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
import com.acuity.visualisations.rawdatamodel.vo.wrappers.DiseaseExtent;
import com.googlecode.cqengine.query.Query;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.util.Collection;

import static com.acuity.visualisations.rawdatamodel.vo.wrappers.DiseaseExtent.Attributes.RECENT_PROGRESSION_DATE;

@Data
@EqualsAndHashCode(callSuper = false)
@ToString(callSuper = true)
public class DiseaseExtentFilters extends Filters<DiseaseExtent> {

    public static DiseaseExtentFilters empty() {
        return new DiseaseExtentFilters();
    }

    protected DateRangeFilter recentProgressionDate = new DateRangeFilter();

    @Override
    public Query<DiseaseExtent> getQuery(Collection<String> subjectIds) {
        CombinedQueryBuilder<DiseaseExtent> cqb = new CombinedQueryBuilder<>(DiseaseExtent.class);
        if (subjectIds != null && !subjectIds.isEmpty()) {
            cqb.add(getFilterQuery(DiseaseExtent.Attributes.SUBJECT_ID, new SetFilter<>(subjectIds)));
        }
        return cqb
                .add(getFilterQuery(RECENT_PROGRESSION_DATE, recentProgressionDate))
                .build();
    }
}
