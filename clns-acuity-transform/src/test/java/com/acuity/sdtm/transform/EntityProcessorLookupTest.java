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

package com.acuity.sdtm.transform;

import com.acuity.sdtm.transform.processor.EntityProcessorLookup;
import com.acuity.sdtm.transform.processor.SDTM_1_1.SDTM_1_1_DemographyProcessor;
import com.acuity.sdtm.transform.common.BaseEntityProcessor;
import com.acuity.sdtm.transform.common.EntityType;
import com.acuity.sdtm.transform.common.Version;
import com.mongodb.MongoClient;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.AnnotationConfigContextLoader;

import java.io.IOException;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.when;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(loader=AnnotationConfigContextLoader.class)
public class EntityProcessorLookupTest {

    private static MongoClient mongoClient;

    @Configuration
    static class ContextConfiguration {
        @Bean
        public SDTM_1_1_DemographyProcessor sdtm_1_1_demographyProcessor() {
            return new SDTM_1_1_DemographyProcessor();
        }

        @Bean(destroyMethod="close")
        public MongoClient mongo() throws IOException {
            return mongoClient;
        }

        @Bean
        public String sessionId() {
            return UUID.randomUUID().toString().replaceAll("-", "");
        }

        @Bean
        public EntityProcessorLookup processorLookup() {
            return new EntityProcessorLookup();
        }
    }

    @Before
    public void init() {
        mongoClient = Mockito.mock(MongoClient.class);
        when(mongoClient.getDatabase(any())).thenReturn(null);
    }

    @Autowired
    EntityProcessorLookup entityProcessorLookup;

    @Test
    public void testProcessorLookup1() throws Exception {
        BaseEntityProcessor processor = entityProcessorLookup.getEntityProcessor(EntityType.Demography, Version.DrugVersion_v1);
        assertThat(processor.getClass()).isEqualTo(SDTM_1_1_DemographyProcessor.class);

    }
}
