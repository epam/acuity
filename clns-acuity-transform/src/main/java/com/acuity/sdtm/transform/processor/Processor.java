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

import static java.nio.charset.StandardCharsets.UTF_8;
import static java.util.stream.Collectors.toSet;

import com.acuity.sdtm.data.provider.DataProvider;
import com.acuity.sdtm.data.provider.UniFile;
import com.acuity.sdtm.data.util.FileTypeUtil;
import com.acuity.sdtm.transform.common.EntityType;
import com.acuity.sdtm.transform.common.SdtmStudyProcessor;
import com.acuity.sdtm.transform.common.Study;
import com.acuity.sdtm.transform.common.Version;
import com.acuity.sdtm.transform.config.StudiesConfig;
import com.acuity.sdtm.transform.config.StudyConfig;
import com.acuity.sdtm.transform.exception.FileProcessingException;
import com.acuity.sdtm.transform.exception.ProcessorFailedException;
import com.acuity.sdtm.transform.exception.ProcessorNotFoundException;
import com.acuity.sdtm.transform.exception.SdtmException;
import com.epam.parso.Column;
import com.epam.parso.SasFileReader;
import com.epam.parso.impl.SasFileReaderImpl;
import com.mongodb.Block;
import com.mongodb.MongoNamespace;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.opencsv.CSVReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.*;
import javax.annotation.PostConstruct;
import org.apache.commons.lang3.StringUtils;
import org.apache.maven.internal.commons.io.input.BOMInputStream;
import org.bson.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Created by knml167 on 08/06/2015.
 * What the fuck? Now every class has hame "Processor"?
 */
@Service
public class Processor {
    protected static final Logger LOG = LoggerFactory.getLogger(Processor.class);

    private StudiesConfig config;
    private MongoDatabase mongoDatabase;
    private List<SdtmStudyProcessor> studyProcessors;

    @Autowired
    public Processor(StudiesConfig config, MongoDatabase mongoDatabase, List<SdtmStudyProcessor> studyProcessors) {
        this.config = config;
        this.mongoDatabase = mongoDatabase;
        this.studyProcessors = studyProcessors;
    }

    @Autowired
    private DataProvider dataProvider;

    private Map<Version, SdtmStudyProcessor> studyProcessorMap = new HashMap<>();

    @PostConstruct
    public void init() {
        for (SdtmStudyProcessor processor : studyProcessors) {
            studyProcessorMap.put(processor.getVersion(), processor);
        }
    }

    public Map<EntityType, SdtmException> process(final StudyConfig config) throws SdtmException {
        final Study study = config.getStudy();
        final Version version = config.getVersion();
        final String source = config.getSource();
        final String domains = config.getDomain();
        final String module = config.getModule();
        try {
            uploadFilesToDb(study, module, source, domains);
            SdtmStudyProcessor processor = studyProcessorMap.get(version);
            //recursive lookup parent versions
            Version studyVersion = version;
            while (processor == null && Version.SDTM_1_1 != studyVersion) {
                processor = studyProcessorMap.get(version.getParent());
                studyVersion = version.getParent();
            }
            if (processor != null) {
                return processor.process(study, studyVersion, module);
            } else {
                final String message = "Study[" + study + "], Version[" + version + "]: Fail while upload to database";
                LOG.error("[ {} ] Version[ {} ]: Fail while upload to database", study, version);
                throw new ProcessorNotFoundException(message);
            }
        } catch (IOException ex) {
            LOG.error("Fail while upload to database: <br>", ex);
            throw new FileProcessingException("Fail while upload to database: <br>" + ex.getMessage(), ex);
        } catch (Exception ex) {
            LOG.error("Fail while processor execution", ex);
            throw new ProcessorFailedException("Fail while processor execution: <br>" + ex.getMessage(), ex);
        } finally {
            cleanup(study, module);
        }
    }

    private void cleanup(Study study, String module) {
        final String sessionId = config.studySessions().get(study.toString() + module);
        mongoDatabase.listCollectionNames().forEach((Block<String>) collectionName -> {
            if (collectionName.startsWith(sessionId)) {
                MongoCollection<Document> mongoCollection = mongoDatabase.getCollection(collectionName);
                String lrName = collectionName.replace(sessionId, "last_run");
                MongoCollection<Document> oldLastCollection = mongoDatabase.getCollection(lrName);
                if (oldLastCollection != null) {
                    oldLastCollection.drop();
                }
                mongoCollection.renameCollection(new MongoNamespace(mongoDatabase.getName() + "." + lrName));
            }
        });
    }

    /**
     * Scan the given directory and return a list of files
     *
     * @param sourceFolder
     * @return
     * @throws IOException
     */
    private List<UniFile> scanDirectory(String sourceFolder) throws IOException {
        return dataProvider.getListFiles(sourceFolder);
    }

    private void checkRequiredFiles(String domains, List<UniFile> files) {
        if (!StringUtils.isEmpty(domains)) {

            Set<String> requiredDomains = Arrays.stream(domains.split(",")).
                    map(String::trim).
                    map(String::toLowerCase).
                    collect(toSet());

            Set<String> foundDomains = files.stream().
                    map(UniFile::getName).
                    map(file -> file.substring(0, file.lastIndexOf('.'))).
                    map(String::toLowerCase).
                    collect(toSet());

            requiredDomains.removeAll(foundDomains);
            if (!requiredDomains.isEmpty()) {
                LOG.error("Domains not found: {}", requiredDomains);
            }
        }
    }

    private void uploadFilesToDb(Study study, String module, String sourceFolder, String domains) throws IOException {
        LOG.info("[ {} ] Uploading files", study);

        final String sessionId = config.studySessions().get(study.toString() + module);

        List<UniFile> files = scanDirectory(sourceFolder);
        checkRequiredFiles(domains, files);

        for (UniFile file : files) {
            String fileName = file.getName();
            if (!FileTypeUtil.isSasFile(fileName) && !FileTypeUtil.isCsvFile(fileName)) {
                continue;
            }
            LOG.info("[ {} ] Found file " + fileName + "...", study);
            String collectionName = sessionId + "_" + fileName.toUpperCase().substring(0, fileName.lastIndexOf('.'));

            try {
                MongoDatabase db = mongoDatabase;

                LOG.info("[ {} ] Getting collection " + collectionName + "...", study);
                MongoCollection<Document> mongoCollection = db.getCollection(collectionName);
                if (mongoCollection.count() > 0) {
                    LOG.info("[ {} ] Collection has " + mongoCollection.count() + " entries.", study);
                    mongoCollection.drop();
                }

                LOG.info("[ {} ] Uploading collection " + collectionName + "...", study);

                InputStream is = file.getInputStream();

                if (FileTypeUtil.isSasFile(fileName)) {
                    SasFileReader reader = new SasFileReaderImpl(is);

                    String[] columns = reader.getColumns().stream().map(Column::getName).toArray(String[]::new);

                    Object[] row;
                    while ((row = reader.readNext()) != null) {
                        Document doc = new Document();
                        for (int i = 0; i < columns.length; i++) {
                            if (columns[i] != null) {
                                doc.append(columns[i], row[i]);
                            }
                        }
                        mongoCollection.insertOne(doc);
                    }

                    is.close();
                } else if (FileTypeUtil.isCsvFile(fileName)) {
                    //skip ByteOrderMark of file
                    BOMInputStream bis = new BOMInputStream(is);
                    InputStreamReader isr = new InputStreamReader(bis, UTF_8);
                    //todo: remove OpenCSV dependency
                    CSVReader reader = new CSVReader(isr, ',');

                    String[] columns = reader.readNext();

                    String[] row;
                    int count = 0;
                    while ((row = reader.readNext()) != null) {
                        Document doc = new Document();
                        for (int i = 0; i < columns.length; i++) {
                            if (columns[i] != null) {
                                doc.append(columns[i], row[i].isEmpty() ? null : row[i]);
                            }
                        }
                        mongoCollection.insertOne(doc);
                        count++;
                    }
                    LOG.info("Totally: " + count + " rows");
                    reader.close();
                }
            } catch (IOException e) {
                LOG.error(e.getMessage());
            }
        }
    }
}
