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

package com.acuity.visualisations.rawdatamodel.trellis.grouping;

import com.acuity.visualisations.rawdatamodel.vo.GroupByOption;
import com.acuity.visualisations.rawdatamodel.vo.Subject;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class PopulationGroupByOptionsTest {

    @ParameterizedTest
    @CsvSource({"Treatment cohort,Group A", "Region cohort,North"})
    public void shouldResolveNamedSubjectGrouping(String groupingName, String groupName) {
        Subject subject = Subject.builder().subjectGroupings(Map.of(
                "Treatment cohort", "Group A",
                "Region cohort", "North")).build();
        GroupByOption.Params params = GroupByOption.Params.builder()
                .with(GroupByOption.Param.SUBJECT_GROUPING_NAME, groupingName)
                .build();

        Object value = PopulationGroupByOptions.SUBJECT_GROUPING.getAttribute(params).getFunction().apply(subject);

        assertThat(value).isEqualTo(groupName);
    }

    @Test
    public void shouldRequireSubjectGroupingName() {
        assertThatThrownBy(() -> PopulationGroupByOptions.SUBJECT_GROUPING.getAttribute(null))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("Cannot use SUBJECT_GROUPING without a subject grouping name");
    }
}
