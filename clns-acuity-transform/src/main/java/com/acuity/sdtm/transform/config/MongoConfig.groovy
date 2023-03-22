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

import com.mongodb.MongoClient
import com.mongodb.MongoClientOptions
import com.mongodb.MongoCredential
import com.mongodb.ServerAddress
import com.mongodb.client.MongoDatabase
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.util.StringUtils

import static java.util.Collections.singletonList

/**
 * @author adavliatov.
 * @since 28.12.2016.
 */
@Configuration
@EnableConfigurationProperties
@ConfigurationProperties(prefix = "mongo")
class MongoConfig {
    private static final Logger logger = LoggerFactory.getLogger(MongoConfig.class)

    String host
    Integer port
    String db
    String user
    String pass

    @Bean
    MongoDatabase mongoDatabase() throws IOException {
        mongo().getDatabase(db)
    }

    @Bean(destroyMethod = "close")
    MongoClient mongo() throws IOException {
        logger.info("Connecting mongo... {}:{} {} {}", host, port, db, user)

        MongoClientOptions.Builder builder = new MongoClientOptions.Builder();
        //build the connection options
        builder.maxConnectionIdleTime(60000);//set the max wait time in (ms)
        MongoClientOptions opts = builder.build();

        if (StringUtils.isEmpty(user)) {
            new MongoClient(host, port)
        } else {
            MongoCredential credential = MongoCredential.createCredential(user, db, pass.toCharArray())
            new MongoClient(new ServerAddress(host, port), singletonList(credential), opts)
        }
    }
}
