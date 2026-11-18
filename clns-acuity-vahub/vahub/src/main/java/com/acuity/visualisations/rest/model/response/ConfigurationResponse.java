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

package com.acuity.visualisations.rest.model.response;

import com.acuity.visualisations.rawdatamodel.vo.ServiceLink;
import com.acuity.visualisations.rest.config.branding.BrandingProperties;
import com.acuity.visualisations.rest.config.properties.IntegrationProperties;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

@Data
@EqualsAndHashCode(callSuper = false)
@Builder
public class ConfigurationResponse {
    private BrandingProperties branding;
    private String cbioportalUrl;
    private IntegrationProperties.QnaMakerProperties qnaMaker;
    /** Home page service blocks (title and URL) from the database, in display order. */
    private List<ServiceLink> serviceLinks;
    /** Feedback email from the database. */
    private String feedbackEmail;
}
