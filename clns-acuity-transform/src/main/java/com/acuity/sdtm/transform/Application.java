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

import com.acuity.sdtm.transform.common.EntityType;
import com.acuity.sdtm.transform.common.Study;
import com.acuity.sdtm.transform.config.Config;
import com.acuity.sdtm.transform.config.StudiesConfig;
import com.acuity.sdtm.transform.email.FakeEmailNotificationService;
import com.acuity.sdtm.transform.exception.SdtmException;
import com.acuity.sdtm.transform.processor.Processor;

import lombok.Data;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;

import javax.annotation.PostConstruct;
import java.util.Collections;
import java.util.Map;
import java.util.TimeZone;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.stream.Stream;

import static java.util.Arrays.stream;
import static java.util.concurrent.CompletableFuture.allOf;
import static org.slf4j.LoggerFactory.getLogger;

/**
 * @author adavliatov.
 * @since 26.12.2016.
 */
@SpringBootApplication(scanBasePackages = {"com.acuity.sdtm.data", "com.acuity.sdtm.transform"})
@EnableAutoConfiguration(exclude = {Config.class})
public class Application {
    private static final Logger LOG = getLogger(Application.class);

    @Autowired
    private StudiesConfig studiesConfig;

    //Use EmailNotificationService when email service will be available
    @Autowired
    private FakeEmailNotificationService emailService;

    @Autowired
    private Processor processor;

    @PostConstruct
    void setProperties() {
        TimeZone.setDefault(TimeZone.getTimeZone("GMT"));
    }

    /**
     * Java app entry point.
     *
     * @param args cmd args
     */
    public static void main(final String[] args) {
        SpringApplication.run(Application.class, args);
    }

    @Bean
    public CommandLineRunner commandLineRunner(final ApplicationContext ctx) {
        return args -> {
            LOG.info("Processing SDTM transformation");
            LOG.info("Configuration: {}", studiesConfig.getList());
            ExecutorService pool = (ExecutorService) ctx.getBean("appPool");
            final Stream<CompletableFuture<StudyExecutionResult>> tasks = studiesConfig
                    .configs()
                    .stream()
                    .map(
                            config -> {
                                final Study study = config.getStudy();
                                return CompletableFuture.supplyAsync(() -> {
                                    try {
                                        LOG.info("Start processing study:[ {} ]", study);
                                        LOG.info("Study configuration: {}", config);
                                        return new StudyExecutionResult(study, processor.process(config));
                                    } catch (SdtmException ex) {
                                        LOG.error("Processor error", ex);
                                        return new StudyExecutionResult(study, Collections.singletonMap(EntityType.All, ex));
                                    }
                                }, pool);
                            }
                    );
            final CompletableFuture[] taskResults = tasks.toArray(CompletableFuture[]::new);
            allOf()
                    .thenApply(ignore ->
                            stream(taskResults)
                                    .map(task -> (StudyExecutionResult) task.join())
                                    .filter(execution -> !execution.getExecutionResult().isEmpty())
                    )
                    .thenAccept(executions -> emailService.sendFailureEmail(executions))
                    .whenComplete((aVoid, throwable) -> {
                        if (throwable != null) {
                            LOG.error("Unhandled exception", throwable);
                        }
                        ((ConfigurableApplicationContext) ctx).close();
                    });
        };
    }

    /**
     * The study processing execution result holder.
     */
    @Data
    public static final class StudyExecutionResult {
        /**
         * Study reference.
         */
        private final Study study;

        /**
         * Study failed types and corresponding executions.
         */
        private final Map<EntityType, SdtmException> executionResult;
    }
}
