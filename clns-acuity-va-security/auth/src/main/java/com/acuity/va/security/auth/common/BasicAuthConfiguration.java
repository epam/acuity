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

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@Order(2)
public class BasicAuthConfiguration {

    public static final String REMOTE_USER = "REMOTE_USER";

    private final BasicAuthProperties properties;

    @Autowired
    public BasicAuthConfiguration(BasicAuthProperties properties) {
        this.properties = properties;
    }

    @Bean
    public SecurityFilterChain basicAuthFilterChain(HttpSecurity http) throws Exception {
        http.securityMatcher("/resources/security/**")
            .authorizeHttpRequests(auth -> auth.anyRequest().hasRole(REMOTE_USER))
            .httpBasic(Customizer.withDefaults())
            .csrf(csrf -> csrf.disable())
            .anonymous(anon -> anon.disable());
        return http.build();
    }

    @Bean
    public InMemoryUserDetailsManager basicAuthUserDetailsManager() {
        InMemoryUserDetailsManager manager = new InMemoryUserDetailsManager();
        for (BasicAuthUser user : properties.getUsers()) {
            String[] roles = user.getRoles() != null ? user.getRoles() : new String[0];
            String[] auths = user.getAuthorities() != null ? user.getAuthorities() : new String[0];
            UserDetails details = User.withUsername(user.getUsername())
                .password("{noop}" + user.getPassword())
                .roles(roles)
                .authorities(auths)
                .build();
            manager.createUser(details);
        }
        return manager;
    }
}
