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

package com.acuity.sdtm.transform.config

import com.acuity.sdtm.data.provider.DataProvider
import com.acuity.sdtm.data.provider.MatchingDataProvider
import com.acuity.sdtm.data.provider.azure.AzureFileStorageDataProvider
import com.acuity.sdtm.data.provider.local.LocalFileDataProvider
import com.acuity.sdtm.data.provider.samba.SambaDataProvider
import com.acuity.sdtm.transform.common.Study
import com.acuity.sdtm.transform.common.Version
import com.acuity.sdtm.transform.io.reader.CsvReader
import com.acuity.sdtm.transform.io.writer.CsvDestinationMatcher
import com.acuity.sdtm.transform.io.writer.CsvFileWriter
import com.acuity.sdtm.transform.io.writer.DestinationMatcher
import com.acuity.sdtm.transform.io.writer.azure.AzureStorageCsvWriter
import com.acuity.sdtm.transform.io.writer.local.LocalCsvWriter
import com.acuity.sdtm.transform.io.writer.samba.SambaCsvWriter
import com.microsoft.azure.storage.CloudStorageAccount
import com.microsoft.azure.storage.file.CloudFileClient
import com.mongodb.MongoClient
import com.mongodb.MongoCredential
import com.mongodb.ServerAddress
import com.mongodb.client.MongoDatabase
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.*

import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

@Configuration
@ComponentScan(value = "com.acuity.sdtm.transform.processor", excludeFilters = [
        @ComponentScan.Filter(value = CsvFileWriter.class, type = FilterType.ASSIGNABLE_TYPE),
        @ComponentScan.Filter(value = StudiesConfig.class, type = FilterType.ASSIGNABLE_TYPE),
        @ComponentScan.Filter(value = ExecutorService.class, type = FilterType.ASSIGNABLE_TYPE)
]
)
class TestContextConfiguration {
    private static final Logger logger = LoggerFactory.getLogger(TestContextConfiguration.class);

    @Value('${study}')
    private Study study
    @Value('${version}')
    private Version version
    @Value('${source}')
    private String source
    @Value('${destination}')
    private String destination
    @Value('${domains}')
    private String domains
    @Value('${module:}')
    private String module


    @Bean
    @Profile("local-mongo")
    public MongoConfig mongoConfig() {
        final MongoConfig mongoConfig = new MongoConfig();
        mongoConfig.setDb("test");
        mongoConfig.setHost("localhost");
        mongoConfig.setPort(27017);
        return mongoConfig;
    }

    @Bean
    @Profile("remote-mongo")
    public MongoConfig remoteMongoConfig() {
        final MongoConfig mongoConfig = new MongoConfig();
        mongoConfig.setDb("test");
        mongoConfig.setHost("localhost");
        mongoConfig.setPort(27017);
        return mongoConfig;
    }

    @Bean
    public CsvInMemoryWriter csvWriter() {
        return new CsvInMemoryWriter();
    }

    @Bean
    public CsvReader csvReader() {
        return new CsvReader();
    }

    @Bean("appPool")
    public ExecutorService appPool() {
        Executors.newSingleThreadExecutor()
    }

    @Bean("studyPool")
    public ExecutorService studyPool() {
        Executors.newSingleThreadExecutor()
    }


    @Bean
    public StudiesConfig studiesConfig() {
        final StudiesConfig configs = new StudiesConfig();
        StudyConfig config = new StudyConfig();
        config.setStudy(study);
        config.setVersion(version);
        config.setSource(source);
        config.setDestination(destination);
        config.setDomain(domains);
        config.setModule(module);
        final List<StudyConfig> list = [config]
        configs.setList(list);
        configs.setStudySessions(list.collectEntries { [(it.study): UUID.randomUUID().toString().replaceAll("-", "")] })
        configs
    }

    @Bean
    @Profile("remote-mongo")
    public MongoDatabase mongoDatabase() throws IOException {
        MongoConfig mongoConfig = remoteMongoConfig();
        MongoClient mongoClient = new MongoClient(
                new ServerAddress(mongoConfig.host, mongoConfig.port),
                Arrays.asList(
                        MongoCredential
                                .createCredential(
                                mongoConfig.user,
                                mongoConfig.db,
                                mongoConfig.pass.toCharArray())
                )
        );
        return mongoClient.getDatabase(mongoDb);
    }

    @Bean
    @Profile("local-mongo")
    public MongoDatabase mongoDatabase1() throws IOException {
        MongoClient mongoClient = new MongoClient();
        return mongoClient.getDatabase(mongoConfig().getDb());
    }

    @Bean
    public DataProvider azureFileStorageDataProvider(CloudFileClient fileClient) {
        return new AzureFileStorageDataProvider(fileClient)
    }

    @Bean
    @Primary
    public DataProvider matchingDataProvider() {
        return new MatchingDataProvider()
    }

    @Bean
    public DataProvider sambaDataProvider() {
        return new SambaDataProvider()
    }

    @Bean
    public DataProvider localFileDataProvider() {
        return new LocalFileDataProvider()
    }

    @Bean
    public CloudFileClient cloudFileClient() {
        String storageConnectionString = "DefaultEndpointsProtocol=http;" +
                "AccountName=;" +
                "AccountKey="
        CloudStorageAccount storageAccount = CloudStorageAccount.parse(storageConnectionString)
        return storageAccount.createCloudFileClient()
    }

    @Bean
    @Primary
    public DestinationMatcher destinationMatcher() {
        return new CsvDestinationMatcher()
    }

    @Bean
    public DestinationMatcher getAzureStorageCsvWriter() {
        return new AzureStorageCsvWriter()
    }

    @Bean
    public DestinationMatcher getLocalCsvWriter() {
        return new LocalCsvWriter()
    }

    @Bean
    public DestinationMatcher getSambaCsvWriter() {
        return new SambaCsvWriter()
    }
}
