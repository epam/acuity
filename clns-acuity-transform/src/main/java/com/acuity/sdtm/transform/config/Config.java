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

package com.acuity.sdtm.transform.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Scope;
import org.springframework.core.convert.ConversionService;
import org.springframework.core.convert.support.DefaultConversionService;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author adavliatov.
 * @since 26.12.2016.
 */
@Configuration
@Import({StudiesConfig.class, EmailConfig.class, MongoConfig.class})
public class Config {
    /**
     * Helps convert string representation to enum entries.
     *
     * @return conversion service
     */
    @Bean
    public ConversionService conversionService() {
        return new DefaultConversionService();
    }

    /**
     * Application pool for study execution.
     *
     * @return application pool
     */
    @Bean(value = "appPool", destroyMethod = "shutdown")
    public ExecutorService applicationPool() {
        return Executors.newSingleThreadExecutor();
    }

    /**
     * Studies execution pool.
     * @return study pool
     */
    @Bean(value = "studyPool", destroyMethod = "shutdown")
    @Scope("prototype")
    public ExecutorService studyPool() {
        return Executors.newWorkStealingPool();
    }
}
