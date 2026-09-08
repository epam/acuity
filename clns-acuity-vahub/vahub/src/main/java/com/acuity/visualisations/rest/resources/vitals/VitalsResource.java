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

package com.acuity.visualisations.rest.resources.vitals;

import com.acuity.visualisations.rawdatamodel.filters.VitalFilters;
import com.acuity.visualisations.rawdatamodel.service.event.VitalService;
import com.acuity.visualisations.rest.model.request.SingleSubjectRequest;
import com.acuity.visualisations.rest.model.request.vitals.VitalsRequest;
import com.acuity.visualisations.rest.model.response.DetailsOnDemandResponse;
import com.acuity.visualisations.rest.util.Constants;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import java.util.List;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@RestController
@RequestMapping(value = "/resources/vitals",
        consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
@PreAuthorize(Constants.PRE_AUTHORISE_VISUALISATION)
@RequiredArgsConstructor
@CacheConfig(keyGenerator = "datasetsKeyGenerator", cacheResolver = "refreshableCacheResolver")
public class VitalsResource {

    private final VitalService vitalService;

    @PostMapping("filters")
    @Cacheable
    public VitalFilters getFilters(@RequestBody VitalsRequest requestBody) {

        return (VitalFilters) vitalService.getAvailableFilters(
                requestBody.getDatasetsObject(),
                requestBody.getVitalsFilters(),
                requestBody.getPopulationFilters());
    }

    @PostMapping("filtered-subjects")
    @Cacheable
    public List<String> getSubjects(@RequestBody VitalsRequest requestBody) {

        return vitalService.getSubjects(requestBody.getDatasetsObject(),
                        requestBody.getVitalsFilters(),
                        requestBody.getPopulationFilters());
    }

    @PostMapping("single-subject")
    @Cacheable
    public DetailsOnDemandResponse getSingleSubjectData(
            @RequestBody @Valid SingleSubjectRequest<VitalFilters> requestBody) {

        return new DetailsOnDemandResponse(vitalService.getDetailsOnDemandData(
                requestBody.getDatasetsObject(), requestBody.getSubjectId(), requestBody.getEventFilters()));
    }
}
