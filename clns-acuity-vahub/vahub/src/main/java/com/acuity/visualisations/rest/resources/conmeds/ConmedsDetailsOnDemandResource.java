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

package com.acuity.visualisations.rest.resources.conmeds;

import com.acuity.visualisations.rawdatamodel.service.event.ConmedsService;
import com.acuity.visualisations.rest.model.request.conmeds.ConmedsRequest;
import com.acuity.visualisations.rest.model.response.DetailsOnDemandResponse;
import com.acuity.visualisations.rest.model.request.DetailsOnDemandRequest;
import com.acuity.visualisations.rest.util.Constants;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import java.io.IOException;

@RestController
@RequestMapping("/resources/conmeds/details-on-demand")
@PreAuthorize(Constants.PRE_AUTHORISE_VISUALISATION)
@RequiredArgsConstructor
@CacheConfig(keyGenerator = "datasetsKeyGenerator", cacheResolver = "refreshableCacheResolver")
public class ConmedsDetailsOnDemandResource {

    private final ConmedsService conmedsService;

    @PostMapping("data")
    @Cacheable
    public DetailsOnDemandResponse getConmedsDetailsOnDemandData(
            @RequestBody @Valid DetailsOnDemandRequest requestBody) {
        return new DetailsOnDemandResponse(conmedsService.getDetailsOnDemandData(
                requestBody.getDatasetsObject(),
                requestBody.getEventIds(),
                requestBody.getSortAttrs(),
                requestBody.getStart(),
                (long) requestBody.getEnd() - requestBody.getStart()));
    }

    @PostMapping("all-csv")
    @Cacheable
    public void downloadAllConmedsDetailsOnDemandData(@RequestBody @Valid ConmedsRequest requestBody,
                                                      HttpServletResponse response) throws IOException {
        Constants.setDownloadHeader(response);
        conmedsService.writeAllDetailsOnDemandCsv(requestBody.getDatasetsObject(), response.getWriter(),
                requestBody.getConmedsFilters(), requestBody.getPopulationFilters());
    }

    @PostMapping("selected-csv")
    @Cacheable
    public void downloadSelectedConmedsDetailsOnDemandData(@RequestBody @Valid DetailsOnDemandRequest requestBody,
                                                           HttpServletResponse response) throws IOException {
        Constants.setDownloadHeader(response);
        conmedsService.writeSelectedDetailsOnDemandCsv(requestBody.getDatasetsObject(), requestBody.getEventIds(), response.getWriter());
    }
}
