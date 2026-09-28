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

package com.acuity.va.security.auth.common;

import com.acuity.va.security.acl.permissions.AcuityPermissions;
import com.acuity.va.security.auth.local.nosecurity.LocalNoSecurityAclPermissionEvaluator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.access.PermissionEvaluator;
import org.springframework.security.acls.domain.DefaultPermissionFactory;
import org.springframework.security.acls.domain.PermissionFactory;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;

/**
 * Enables method security. All permission checks unconditionally permit access.
 */
@Configuration
@EnableMethodSecurity(prePostEnabled = true, securedEnabled = true)
public class MethodSecurityConfiguration {

    @Bean
    public PermissionFactory permissionFactory() {
        return new DefaultPermissionFactory(AcuityPermissions.class);
    }

    @Bean(name = "permissionEvaluator")
    public PermissionEvaluator permissionEvaluator() {
        LocalNoSecurityAclPermissionEvaluator evaluator = new LocalNoSecurityAclPermissionEvaluator();
        evaluator.setPermissionFactory(permissionFactory());
        return evaluator;
    }
}
