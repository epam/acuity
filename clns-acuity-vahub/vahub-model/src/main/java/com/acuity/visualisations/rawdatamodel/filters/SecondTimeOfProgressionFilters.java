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

package com.acuity.visualisations.rawdatamodel.filters;

import com.acuity.visualisations.rawdatamodel.util.CombinedQueryBuilder;
import com.acuity.visualisations.rawdatamodel.vo.wrappers.SecondTimeOfProgression;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.googlecode.cqengine.query.Query;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.util.Collection;

@Data
@EqualsAndHashCode(callSuper = false)
@ToString(callSuper = true)
public class SecondTimeOfProgressionFilters extends Filters<SecondTimeOfProgression> {
    @JsonIgnore
    public static SecondTimeOfProgressionFilters empty() {
        return new SecondTimeOfProgressionFilters();
    }

    @Override
    public Query<SecondTimeOfProgression> getQuery(Collection<String> subjectIds) {
        CombinedQueryBuilder<SecondTimeOfProgression> cqb = new CombinedQueryBuilder<>(SecondTimeOfProgression.class);
        if (subjectIds != null && !subjectIds.isEmpty()) {
            cqb.add(getFilterQuery(SecondTimeOfProgression.Attributes.SUBJECT_ID, new SetFilter<>(subjectIds)));
        }
        return cqb.build();
    }
}
