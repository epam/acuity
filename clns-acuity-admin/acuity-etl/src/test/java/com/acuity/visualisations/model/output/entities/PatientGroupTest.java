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

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class PatientGroupTest {

    @Test
    public void shouldIncludeGroupingNameInEntityIdentity() {
        PatientGroup firstGrouping = patientGroup("Dose grouping", "100 mg");
        PatientGroup secondGrouping = patientGroup("Region", "Europe");

        assertThat(firstGrouping.uniqueFieldsToString()).isNotEqualTo(secondGrouping.uniqueFieldsToString());
        assertThat(firstGrouping.allFieldsToString()).contains("Dose grouping", "100 mg");
    }

    private PatientGroup patientGroup(String groupingName, String groupName) {
        PatientGroup patientGroup = new PatientGroup();
        patientGroup.setSubject("subject-1");
        patientGroup.setPart("A");
        patientGroup.setGroupingName(groupingName);
        patientGroup.setGroupName(groupName);
        return patientGroup;
    }
}
