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

package com.acuity.visualisations.rawdatamodel.service.event;

import com.acuity.visualisations.rawdatamodel.filters.Filters;
import com.acuity.visualisations.rawdatamodel.filters.PopulationFilters;
import com.acuity.visualisations.rawdatamodel.filters.RadiotherapyFilters;
import com.acuity.visualisations.rawdatamodel.service.BaseEventService;
import com.acuity.visualisations.rawdatamodel.service.ssv.OncologyPermission;
import com.acuity.visualisations.rawdatamodel.service.ssv.SsvSummaryTableService;
import com.acuity.visualisations.rawdatamodel.trellis.grouping.RadiotherapyGroupByOptions;
import com.acuity.visualisations.rawdatamodel.vo.FilterResult;
import com.acuity.visualisations.rawdatamodel.vo.RadiotherapyRaw;
import com.acuity.visualisations.rawdatamodel.vo.wrappers.Radiotherapy;
import com.acuity.va.security.acl.domain.Datasets;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.acuity.visualisations.rawdatamodel.util.Column.DatasetType;

@Service
@OncologyPermission
public class RadiotherapyService extends BaseEventService<RadiotherapyRaw, Radiotherapy, RadiotherapyGroupByOptions> implements SsvSummaryTableService {

    private static final String PREVIOUS = "Previous";
    @Override
    public List<Map<String, String>> getSingleSubjectData(Datasets datasets, String subjectId) {
        return getSingleSubjectData(datasets, subjectId, RadiotherapyFilters.empty());
    }

    @Override
    public List<Map<String, String>> getSingleSubjectData(Datasets datasets, String subjectId, Filters<Radiotherapy> filters) {
        final FilterResult<Radiotherapy> filteredData = getFilteredData(datasets, filters,
                PopulationFilters.empty(), null, s -> s.getSubjectId().equals(subjectId));
        Collection<Radiotherapy> pastRadiotherapies = filteredData.stream()
                .filter(r -> PREVIOUS.equalsIgnoreCase(r.getEvent().getTimeStatus()))
                .collect(Collectors.toList());
        return ssvCommonService.getColumnData(DatasetType.fromDatasets(datasets), pastRadiotherapies);
    }

    @Override
    public Map<String, String> getSingleSubjectColumns(DatasetType datasetType) {
        return ssvCommonService.getColumns(datasetType, Radiotherapy.class, RadiotherapyRaw.class);
    }

    @Override
    public List<Map<String, String>> getSingleSubjectData(Datasets datasets, String subjectId, boolean hasTumourAccess) {
        return hasTumourAccess ? getSingleSubjectData(datasets, subjectId, RadiotherapyFilters.empty())
                : Collections.emptyList();
    }

    @Override
    public String getSsvTableName() {
        return "radiotherapy";
    }

    @Override
    public String getSsvTableDisplayName() {
        return "PRIOR ANTI-CANCER RADIOTHERAPY (CAPRX R)";
    }

    @Override
    public String getSubheaderName() {
        return "PRIOR THERAPIES";
    }

    @Override
    public String getHeaderName() {
        return "PATIENT HISTORY";
    }

    @Override
    public double getOrder() {
        return 10;
    }
}
