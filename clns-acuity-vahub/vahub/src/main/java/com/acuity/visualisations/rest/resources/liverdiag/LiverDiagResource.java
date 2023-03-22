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

package com.acuity.visualisations.rest.resources.liverdiag;

import com.acuity.visualisations.rawdatamodel.filters.LiverDiagFilters;
import com.acuity.visualisations.rawdatamodel.service.event.LiverDiagService;
import com.acuity.visualisations.rest.model.request.SingleSubjectRequest;
import com.acuity.visualisations.rest.model.request.liverdiag.LiverDiagRequest;
import com.acuity.visualisations.rest.model.response.DetailsOnDemandResponse;
import com.acuity.visualisations.rest.util.Constants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
@RequestMapping("/resources/liver-diag")
@PreAuthorize(Constants.PRE_AUTHORISE_VISUALISATION)
@CacheConfig(keyGenerator = "datasetsKeyGenerator", cacheResolver = "refreshableCacheResolver")
public class LiverDiagResource {

    @Autowired
    private LiverDiagService liverDiagService;

    @PostMapping("filters")
    @Cacheable
    public LiverDiagFilters getFilters(@RequestBody @Valid LiverDiagRequest requestBody) {

        return (LiverDiagFilters) liverDiagService.getAvailableFilters(
                requestBody.getDatasetsObject(),
                requestBody.getLiverDiagFilters(),
                requestBody.getPopulationFilters());
    }

    @PostMapping("single-subject")
    @Cacheable
    public DetailsOnDemandResponse getSingleSubjectData(
            @RequestBody @Valid SingleSubjectRequest<LiverDiagFilters> requestBody) {

        return new DetailsOnDemandResponse(liverDiagService.getDetailsOnDemandData(
                requestBody.getDatasetsObject(), requestBody.getSubjectId(),
                requestBody.getEventFilters()));
    }
}
