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

package com.acuity.visualisations.web.auth;

import org.springframework.web.filter.GenericFilterBean;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

import static com.acuity.visualisations.web.auth.UserInfoHolder.isCurrentUserDeveloper;

public class WebAuthorizationFilter extends GenericFilterBean {
    private static final String UNAUTHORIZED_ENDPOINT = "unauthorized";

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        if (isCurrentUserDeveloper() || doesRequestContainUnauthorizedEndpoint(request)) {
            chain.doFilter(request, response);
        } else {
            ((HttpServletResponse) response).sendRedirect(UNAUTHORIZED_ENDPOINT);
        }
    }

    private boolean doesRequestContainUnauthorizedEndpoint(ServletRequest request) {
        return ((HttpServletRequest) request).getRequestURI().contains(UNAUTHORIZED_ENDPOINT);
    }
}
