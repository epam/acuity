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

package com.acuity.visualisations.rest.config.properties;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

//build properties are generated by maven plugin
@PropertySource(value = "classpath:build.properties")
@Component
public class BuildInfoProperties {

    @Value("${git.build.time}")
    @JsonProperty
    private String buildTime;
    @Value("${git.commit.id.abbrev}")
    @JsonProperty
    private String lastCommitId;
    @Value("${git.commit.time}")
    @JsonProperty
    private String lastCommitTime;
    @Value("${git.branch}")
    @JsonProperty
    private String builtBranch;

}



