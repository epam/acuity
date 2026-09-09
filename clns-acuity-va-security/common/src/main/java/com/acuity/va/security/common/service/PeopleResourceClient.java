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

package com.acuity.va.security.common.service;

import com.acuity.va.security.acl.domain.ActiveDirectoryRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

/**
 * Azure AD user lookup — no-ops when Azure SSO profile is not active.
 */
@Service
public class PeopleResourceClient {

    private static final Logger LOG = LoggerFactory.getLogger(PeopleResourceClient.class);

    public String getFullName(String userId) {
        if (userId == null) {
            return null;
        }
        LOG.debug("Azure profile is not enabled, unable to look up full name for {}", userId);
        return userId;
    }

    public List<ActiveDirectoryRecord> searchUsersByName(String name) {
        LOG.debug("Azure profile is not enabled, unable to search users by name {}", name);
        return Collections.emptyList();
    }
}
