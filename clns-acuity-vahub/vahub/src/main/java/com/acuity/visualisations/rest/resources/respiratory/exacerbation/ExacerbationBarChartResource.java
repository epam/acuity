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

package com.acuity.visualisations.rest.resources.respiratory.exacerbation;

import com.acuity.visualisations.rawdatamodel.service.event.ExacerbationService;
import com.acuity.visualisations.rawdatamodel.vo.plots.SelectionDetail;
import com.acuity.visualisations.rest.model.request.respiratory.exacerbation.ExacerbationBarChartRequest;
import com.acuity.visualisations.rest.model.request.respiratory.exacerbation.ExacerbationRequest;
import com.acuity.visualisations.rest.model.request.respiratory.exacerbation.ExacerbationSelectionRequest;
import com.acuity.visualisations.rest.model.response.respiratory.exacerbation.ExacerbationBarChartResponse;
import com.acuity.visualisations.rest.model.response.respiratory.exacerbation.ExacerbationBarChartXAxisResponse;
import com.acuity.visualisations.rest.model.response.respiratory.exacerbation.ExacerbationColorByOptionsResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;

import static com.acuity.visualisations.rest.util.Constants.PRE_AUTHORISE_VISUALISATION;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@RestController
@RequestMapping(value = "/resources/respiratory/exacerbation/bar-chart",
        consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
@PreAuthorize(PRE_AUTHORISE_VISUALISATION)
@CacheConfig(keyGenerator = "datasetsKeyGenerator", cacheResolver = "refreshableCacheResolver")
@RequiredArgsConstructor
public class ExacerbationBarChartResource {

    private final ExacerbationService exacerbationService;

    @PostMapping("x-axis")
    @Cacheable
    public ExacerbationBarChartXAxisResponse getXAxis(@RequestBody @Valid ExacerbationRequest requestBody) {
        return new ExacerbationBarChartXAxisResponse(exacerbationService.getAvailableBarChartXAxis(requestBody.getDatasetsObject(),
                requestBody.getExacerbationFilters(),
                requestBody.getPopulationFilters()));
    }

    @PostMapping("color-by-options")
    @Cacheable
    public ExacerbationColorByOptionsResponse getColorByOptions(
            @RequestBody ExacerbationRequest requestBody) {

        return new ExacerbationColorByOptionsResponse(exacerbationService.getBarChartColorByOptions(
                requestBody.getDatasetsObject(),
                requestBody.getExacerbationFilters(),
                requestBody.getPopulationFilters()));
    }

    @PostMapping("values")
    @Cacheable
    public ExacerbationBarChartResponse getBarChartData(
            @RequestBody ExacerbationBarChartRequest requestBody) {

        return new ExacerbationBarChartResponse(exacerbationService.getBarChart(
                requestBody.getDatasetsObject(),
                requestBody.getSettings(),
                requestBody.getExacerbationFilters(),
                requestBody.getPopulationFilters(),
                requestBody.getCountType()));
    }

    @PostMapping("selection")
    @Cacheable
    public SelectionDetail getSelection(
            @RequestBody @Valid ExacerbationSelectionRequest requestBody) {
        return exacerbationService.getSelectionDetails(
                requestBody.getDatasetsObject(),
                requestBody.getExacerbationFilters(),
                requestBody.getPopulationFilters(),
                requestBody.getSelection());
    }
}
