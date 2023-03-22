/*
 * Copyright 2021 The University of Manchester
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.acuity.sdtm.transform.processor.SDTM_1_1;

import com.acuity.sdtm.transform.common.BaseEntityProcessor;
import com.acuity.sdtm.transform.common.EntityType;
import com.acuity.sdtm.transform.common.SdtmStudyProcessor;
import com.acuity.sdtm.transform.common.Study;
import com.acuity.sdtm.transform.common.Version;
import com.acuity.sdtm.transform.config.StudiesConfig;
import com.acuity.sdtm.transform.exception.SdtmException;
import com.acuity.sdtm.transform.io.writer.CsvFileWriter;
import com.acuity.sdtm.transform.processor.EntityProcessorLookup;
import com.mongodb.client.MongoDatabase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

/**
 * Created by knml167 on 10/06/2015.
 */
@Component
public class StudyProcessor implements SdtmStudyProcessor {
    protected static final Logger LOG = LoggerFactory.getLogger(StudyProcessor.class);

    private final List<EntityType> entityProcessList = Arrays.asList(EntityType.Demography, EntityType.Visit, EntityType.AdverseEvent, EntityType.Laboratory, EntityType.Lvef,
            EntityType.SeriousAdverseEvent, EntityType.Vital, EntityType.Conmed, EntityType.Dose, EntityType.DoseDiscontinuation,
            EntityType.PrimaryTumourLocation, EntityType.Radiotherapy, EntityType.Chemotherapy, EntityType.Death, EntityType.Completion, EntityType.Ecg,
            EntityType.Randomization, EntityType.Recist1Target, EntityType.Recist1NonTarget, EntityType.Recist2, EntityType.PatientGroupInformation, EntityType.PatientGroupInformation2,
            EntityType.SubjectCharacteristics, EntityType.Decg, EntityType.MedicalHistory);

    @Autowired
    private MongoDatabase mongoDatabase;

    @Autowired
    private EntityProcessorLookup entityProcessorLookup;

    @Autowired
    private StudiesConfig config;

    @Override
    public Version getVersion() {
        return Version.SDTM_1_1;
    }

    @Autowired
    private ApplicationContext ctx;

    @Override
    public Map<EntityType, SdtmException> process(Study study, Version version, String module) throws Exception {
        Map<EntityType, SdtmException> failedEntities = new ConcurrentHashMap<>();
        ExecutorService pool = (ExecutorService) ctx.getBean("studyPool");
        final Map<EntityType, Future<Void>> futures = new HashMap<>();
        for (EntityType entity : entityProcessList) {
            if (EntityType.All == entity) {
                continue;
            }
            CsvFileWriter csvFileWriter = ctx.getBean(CsvFileWriter.class);
            //todo: reimplement according to Application.java
            Callable<Void> task = () -> {
                try {
                    //todo adavliatov: reimplement according to [https://github.com/jensalm/strategy]
                    BaseEntityProcessor entityProcessor = entityProcessorLookup.getEntityProcessor(entity, version);
                    entityProcessor.setStudy(study);
                    entityProcessor.setModule(module);
                    entityProcessor.setSessionId(config.studySessions().get(study.toString() + module));
                    if (entityProcessor.isStudyProducesThisTypeOfEntity()) {
                        LOG.info("[ {} of module {} ] Processing: {}", study, module, entityProcessor.getEntityType());
                        entityProcessor.process(mongoDatabase, csvFileWriter);
                    }
                } catch (SdtmException ex) {
                    LOG.error(ex.getMessage());
                    failedEntities.put(entity, ex);
                }
                return null;
            };
            futures.put(entity, pool.submit(task));
        }
        for (Map.Entry<EntityType, Future<Void>> execution : futures.entrySet()) {
            final Future<Void> taskResult = execution.getValue();
            final Void aVoid = taskResult.get();
            if (taskResult.isCancelled()) {
                LOG.error("[ {} ] subtask for [ {} ] was cancelled after 15 minutes awaiting", study, execution.getKey());
            }
        }
        return failedEntities;
    }
}
