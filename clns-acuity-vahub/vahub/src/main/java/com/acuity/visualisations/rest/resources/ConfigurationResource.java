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

package com.acuity.visualisations.rest.resources;

import com.acuity.visualisations.rawdatamodel.dao.SiteContentRepository;
import com.acuity.visualisations.rawdatamodel.vo.ServiceLink;
import com.acuity.visualisations.rest.config.branding.BrandingProperties;
import com.acuity.visualisations.rest.config.properties.IntegrationProperties;
import com.acuity.visualisations.rest.model.response.ConfigurationResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.info.BuildProperties;
import org.springframework.dao.DataAccessException;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collections;
import java.util.List;
import java.util.regex.Pattern;

import static org.springframework.http.MediaType.ALL_VALUE;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@RestController
@RequestMapping(value = "/resources/configuration/", consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
public class ConfigurationResource {

    private static final Logger LOG = LoggerFactory.getLogger(ConfigurationResource.class);

    @Autowired
    private BrandingProperties brandingProperties;

    @Autowired
    private IntegrationProperties integrationProperties;

    @Autowired
    private BuildProperties buildProperties;

    @Autowired
    private SiteContentRepository siteContentRepository;

    private static final Pattern SHORT_VERSION_EXTRACTOR = Pattern.compile("^([\\d.]+).*$");

    @RequestMapping(value = "properties", consumes = {APPLICATION_JSON_VALUE, ALL_VALUE})
    public ConfigurationResponse getProperties() {
        return ConfigurationResponse.builder()
                .branding(brandingProperties)
                .cbioportalUrl(integrationProperties.getCbioportal().get("url"))
                .qnaMaker(integrationProperties.getQnaMaker())
                .serviceLinks(loadServiceLinks())
                .feedbackEmail(loadFeedbackEmail())
                .build();
    }

    /**
     * Loads service blocks from the database. If the table is not available yet (for example, the migration
     * has not run), the configuration is still returned, just without the blocks.
     */
    private List<ServiceLink> loadServiceLinks() {
        try {
            return siteContentRepository.selectServiceLinks();
        } catch (DataAccessException e) {
            LOG.warn("Service links could not be loaded from the database, returning an empty list", e);
            return Collections.emptyList();
        }
    }

    /**
     * Loads feedback email from the database. If the table is not available yet, returns null.
     */
    private String loadFeedbackEmail() {
        try {
            return siteContentRepository.selectFeedbackEmail();
        } catch (DataAccessException e) {
            LOG.warn("Feedback email could not be loaded from the database, returning null", e);
            return null;
        }
    }
}
