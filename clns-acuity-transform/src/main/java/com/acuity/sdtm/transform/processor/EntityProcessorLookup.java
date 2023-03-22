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

package com.acuity.sdtm.transform.processor;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.acuity.sdtm.transform.common.BaseEntityProcessor;
import com.acuity.sdtm.transform.common.EntityType;
import com.acuity.sdtm.transform.common.Version;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Created by knml167 on 09/06/2015.
 */
@Component
public class EntityProcessorLookup {
    protected static final Logger LOG = LoggerFactory.getLogger(EntityProcessorLookup.class);

    private Map<EntityType, Map<Version, BaseEntityProcessor>> lookupMap = new HashMap<>();

    @Autowired
    private List<BaseEntityProcessor> processors;

    @PostConstruct
    public void init() {
        for (BaseEntityProcessor processor : processors) {
            LOG.info("Found class {} for {}, version: {}", processor.getClass().getName(),
                    processor.getEntityType(), processor.getVersion());
            lookupMap.computeIfAbsent(processor.getEntityType(), c -> new HashMap<>())
                    .put(processor.getVersion(), processor);
        }
    }

    public BaseEntityProcessor getEntityProcessor(EntityType entityType, Version version) throws Exception {
        Map<Version, BaseEntityProcessor> entityProcessors = lookupMap.get(entityType);
        if (entityProcessors == null) {
            throw new Exception("No processors found for entity type " + entityType);
        }
        BaseEntityProcessor processor = lookupEntityProcessorImpl(entityProcessors, version);
        if (processor == null) {
            throw new Exception("No processor found for entity type " + entityType
                    + " version " + version.toString());
        }
        return processor;
    }

    private static BaseEntityProcessor lookupEntityProcessorImpl(Map<Version,
            BaseEntityProcessor> entityProessors, Version version) {
        BaseEntityProcessor processor = entityProessors.get(version);
        if (processor == null) {
            if (version.getParent() == null) {
                return null;
            } else {
                return lookupEntityProcessorImpl(entityProessors, version.getParent());
            }
        } else {
            return processor;
        }
    }
}
