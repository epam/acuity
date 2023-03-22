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


import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Scope

import javax.annotation.PostConstruct

/**
 * @author adavliatov.
 * @since 28.12.2016.
 */
@Configuration
@EnableConfigurationProperties
@ConfigurationProperties(prefix = "studies")
class StudiesConfig {
    List<StudyConfig> list = new ArrayList<>()
    Map<String, String> studySessions = new HashMap<>()

    @PostConstruct
    void init() {
        studySessions = list.collectEntries { [(it.study.toString() + it.module): sessionId()] }
    }

    @Bean
    @Scope("prototype")
    String sessionId() {
        UUID.randomUUID().toString().replaceAll("-", "")
    }

    @Bean
    Map<String, String> studySessions() {
        studySessions
    }

    @Bean
    List<StudyConfig> configs() {
        list
    }
}
