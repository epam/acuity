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

package com.acuity.visualisations.model.output.entities;

import com.acuity.visualisations.data.util.Util;
import org.apache.commons.lang.builder.ToStringBuilder;


public class Study extends TimestampedEntity {

    private String projectGuid;
    private String studyGroupGuid;
    private String studyName;
    private String studyDisplay;

    public Study() {
        initId();
    }

    public String getProjectGuid() {
        return projectGuid;
    }

    public void setProjectGuid(String projectGuid) {
        this.projectGuid = projectGuid;
    }

    public String getStudyGroupGuid() {
        return studyGroupGuid;
    }

    public void setStudyGroupGuid(String studyGroupGuid) {
        this.studyGroupGuid = studyGroupGuid;
    }

    @Override
    public String getStudyName() {
        return studyName;
    }

    @Override
    public void setStudyName(String studyName) {
        this.studyName = studyName;
    }

    public String getStudyDisplay() {
        return studyDisplay;
    }

    public void setStudyDisplay(String studyDisplay) {
        this.studyDisplay = studyDisplay;
    }

    @Override
    public String uniqueFieldsToString() {
        return new ToStringBuilder(this, Util.getToStringStyle()).
                append("projectName", getProjectName()).
                append("studyName", studyName).
                toString();
    }

    @Override
    public String allFieldsToString() {
        return new ToStringBuilder(this, Util.getToStringStyle()).
                append("projectName", getProjectName()).
                append("studyName", studyName).
                append("studyDisplay", studyDisplay).
                toString();
    }

}
