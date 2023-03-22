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

package com.acuity.sdtm.transform.email;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.acuity.sdtm.transform.Application;
import com.acuity.sdtm.transform.common.EntityType;
import com.acuity.sdtm.transform.config.EmailConfig;
import com.acuity.sdtm.transform.exception.SdtmException;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
public class EmailNotificationService {
    private static final Logger LOG = LoggerFactory.getLogger(EmailNotificationService.class);

    @Autowired
    private EmailConfig emailConfig;

    @Autowired
    private JavaMailSenderImpl mailSender;

    public void sendFailureEmail(final Stream<Application.StudyExecutionResult> failedStudies) {
        MimeMessage message = mailSender.createMimeMessage();
        try {
            MimeMessageHelper messageHelper = new MimeMessageHelper(message, true, "UTF-8");
            messageHelper.setTo(emailConfig.getRecipient());
            messageHelper.setSubject(emailConfig.getSubject());
            final String studiesFeedback = failedStudies.map(studyResult -> {
                        if (studyResult.getExecutionResult().isEmpty()) {
                            return "";
                        }
                        StringBuilder sb = new StringBuilder();
                        sb = sb.append(studyResult)
                                .append(":")
                                .append("<br>");
                        for (Map.Entry<EntityType, SdtmException> entry : studyResult.getExecutionResult().entrySet()) {
                            sb = sb.append(entry.getKey()).append(":").append(entry.getValue().getMessage()).append("<br>");
                        }
                        return sb.toString();
                    }
            ).collect(Collectors.joining());
            if (StringUtils.isBlank(studiesFeedback)) {
                return;
            }
            String text = emailConfig.getBody()
                    + studiesFeedback
                    + emailConfig.getSignature()
                    + "<br>";
            messageHelper.setText(text, true);
        } catch (MessagingException e) {
            LOG.info("Error while email preparation", e);
        }
        mailSender.send(message);
        LOG.info("Sending alert email due to failure of conversion job");
    }
}
