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

package com.acuity.visualisations.rest.config;

import com.acuity.visualisations.config.ApplicationEhCacheConfig;
import com.acuity.visualisations.config.ApplicationEnableExecutorConfig;
import com.acuity.visualisations.config.ApplicationModelConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.boot.web.servlet.ServletListenerRegistrationBean;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.context.event.EventListener;
import org.springframework.security.web.session.HttpSessionEventPublisher;

@SpringBootApplication(scanBasePackages = {
        "com.acuity.va.security.auth",
        "com.acuity.visualisations.rest.resources",
        "com.acuity.visualisations.rest.util"
})
@EnableAspectJAutoProxy(proxyTargetClass = true)
@Slf4j
public class ApplicationWeb extends SpringBootServletInitializer {

    private static Class<?>[] springResources = new Class<?>[]{ApplicationAuditLoggerConfig.class, ApplicationModelConfig.class,
            ApplicationEhCacheConfig.class, ApplicationEnableExecutorConfig.class, ApplicationWeb.class};

    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
        return application.sources(springResources);
    }

    @Bean
    public ServletListenerRegistrationBean<HttpSessionEventPublisher> httpSessionEventPublisher() {
        return new ServletListenerRegistrationBean<>(new HttpSessionEventPublisher());
    }

    @SuppressWarnings("checkstyle:linelength")
    @EventListener(ApplicationReadyEvent.class)
    public void onStartUp() {
        log.info("\n888     888     d8888 888    888 888     888 888888b.        888    888        d8888  .d8888b.        .d8888b. 88888888888     d8888 8888888b. 88888888888 8888888888 8888888b.  \n"
               + "888     888    d88888 888    888 888     888 888  \"88b       888    888       d88888 d88P  Y88b      d88P  Y88b    888        d88888 888   Y88b    888     888        888  \"Y88b \n"
               + "888     888   d88P888 888    888 888     888 888  .88P       888    888      d88P888 Y88b.           Y88b.         888       d88P888 888    888    888     888        888    888 \n"
               + "Y88b   d88P  d88P 888 8888888888 888     888 8888888K.       8888888888     d88P 888  \"Y888b.         \"Y888b.      888      d88P 888 888   d88P    888     8888888    888    888 \n"
               + " Y88b d88P  d88P  888 888    888 888     888 888  \"Y88b      888    888    d88P  888     \"Y88b.          \"Y88b.    888     d88P  888 8888888P\"     888     888        888    888 \n"
               + "  Y88o88P  d88P   888 888    888 888     888 888    888      888    888   d88P   888       \"888            \"888    888    d88P   888 888 T88b      888     888        888    888 \n"
               + "   Y888P  d8888888888 888    888 Y88b. .d88P 888   d88P      888    888  d8888888888 Y88b  d88P      Y88b  d88P    888   d8888888888 888  T88b     888     8888888888 8888888P\"  ");
    }

    public static void main(String[] args) {
        SpringApplication.run(springResources, args);
    }
}
