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

package com.acuity.visualisations.rest.resources.timeline;


import com.acuity.visualisations.rawdatamodel.filters.Filters;
import com.acuity.visualisations.rawdatamodel.filters.PatientDataFilters;
import com.acuity.visualisations.rawdatamodel.service.timeline.TimelinePatientDataService;
import com.acuity.visualisations.rawdatamodel.service.timeline.data.patient.SubjectPatientDataDetail;
import com.acuity.visualisations.rawdatamodel.service.timeline.data.patient.SubjectPatientDataSummary;
import com.acuity.visualisations.rawdatamodel.vo.wrappers.PatientData;
import com.acuity.visualisations.rest.model.request.patient.data.PatientDataRequest;
import com.acuity.visualisations.rest.model.request.patient.data.TimelinePatientDataRequest;
import com.acuity.visualisations.rest.util.Constants;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import java.util.List;

import static com.acuity.visualisations.rawdatamodel.trellis.grouping.ChartGroupByOptions.ChartGroupBySetting.X_AXIS;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

@RestController
@RequestMapping(value = "/resources/timeline/patientdata/",
        consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
@PreAuthorize(Constants.PRE_AUTHORISE_VISUALISATION)
@CacheConfig(keyGenerator = "datasetsKeyGenerator", cacheResolver = "refreshableCacheResolver")
@RequiredArgsConstructor
public class TimelinePatientDataResource {

    private final TimelinePatientDataService timelinePatientDataService;

    /**
     * Gets available patient data filters for the specified detect VA security id
     *
     * @param requestBody selected population filters and patient data filters by client
     * @return list of available filters
     */
    @RequestMapping(value = "/filters", method = POST)
    @Cacheable(condition = Constants.EMPTY_EVENT_AND_POPULATION_FILTER)
    public Filters<PatientData> getAvailableFilters(
            @RequestBody PatientDataRequest requestBody) {

        return timelinePatientDataService.getAvailableFilters(
                requestBody.getDatasetsObject(), requestBody.getPatientDataFilters(), requestBody.getPopulationFilters());
    }

    @RequestMapping(value = "/patientdatasummaries", method = POST)
    @Cacheable(condition = Constants.EMPTY_EVENT_AND_POPULATION_FILTER)
    public List<SubjectPatientDataSummary> getPatientDataSummaries(
            @RequestBody @Valid TimelinePatientDataRequest requestBody) {

        return timelinePatientDataService.
                getPatientDataSummaries(requestBody.getDatasetsObject(),
                        requestBody.getOptions().get(X_AXIS).getParams(),
                        requestBody.getPatientDataFilters(),
                        requestBody.getPopulationFilters());
    }

    /**
     * Gets list of patient data details
     *
     * @param requestBody selected population filters and patient data filters by client
     * @return list of patient details
     */
    @RequestMapping(value = "/patientdatadetails", method = POST)
    @Cacheable(condition = Constants.EMPTY_EVENT_AND_POPULATION_FILTER)
    public List<SubjectPatientDataDetail> getPatientDataDetails(
            @RequestBody TimelinePatientDataRequest requestBody) {

        return timelinePatientDataService.
                getPatientDataDetails(requestBody.getDatasetsObject(), requestBody.getOptions().get(X_AXIS).getParams(),
                        requestBody.getSubjectIds(),
                        requestBody.getPatientDataFilters(),
                        requestBody.getPopulationFilters());
    }
}
