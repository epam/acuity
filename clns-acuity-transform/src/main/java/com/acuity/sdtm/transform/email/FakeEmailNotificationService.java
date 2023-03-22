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

import com.acuity.sdtm.transform.Application;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class FakeEmailNotificationService {
    private static final Logger LOG = LoggerFactory.getLogger(FakeEmailNotificationService.class);

    public void sendFailureEmail(final Stream<Application.StudyExecutionResult> failedStudies) {
        String failedStudiesString = failedStudies.map(result -> result.getStudy().name()).collect(Collectors.joining("\n"));
        if (!failedStudiesString.isEmpty()) {
            LOG.info("Should send alert email due to failure of conversion job. Failed studies:\n{}", failedStudiesString);
        }
    }
}
