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

import org.springframework.context.annotation.Bean
import org.springframework.mail.javamail.JavaMailSenderImpl

/**
 * @author adavliatov.
 * @since 28.12.2016.
 */
//@Configuration
//@EnableConfigurationProperties
//@ConfigurationProperties(prefix = "mail")
class EmailConfig {
    //Should be configured when email service will be available
    String host = ""
    Integer port = 25
    Integer connectionTimeout = 300000
    Integer timeout = 60000
    Integer writeTimeout = 60000
    String recipient = ""
    String subject = ""
    String body = ""
    String signature = ""

    @Bean
    JavaMailSenderImpl mailSender() {
        JavaMailSenderImpl mailSender = new JavaMailSenderImpl()
        mailSender.setHost(host)
        mailSender.setPort(port)
        Properties properties = new Properties()
        properties.put("mail.smtp.connectiontimeout", connectionTimeout)
        properties.put("mail.smtp.timeout", timeout)
        properties.put("mail.smtp.writetimeout", writeTimeout)
        mailSender.setJavaMailProperties(properties)
        mailSender
    }
}
