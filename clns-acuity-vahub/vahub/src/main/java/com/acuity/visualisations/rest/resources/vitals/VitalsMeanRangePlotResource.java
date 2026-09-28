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

import com.acuity.visualisations.rawdatamodel.axes.AxisOptions;
import com.acuity.visualisations.rawdatamodel.service.event.VitalService;
import com.acuity.visualisations.rawdatamodel.trellis.TrellisOptions;
import com.acuity.visualisations.rawdatamodel.trellis.grouping.VitalGroupByOptions;
import com.acuity.visualisations.rawdatamodel.vo.compatibility.StatType;
import com.acuity.visualisations.rawdatamodel.vo.compatibility.TrellisedRangePlot;
import com.acuity.visualisations.rawdatamodel.vo.plots.SelectionDetail;
import com.acuity.visualisations.rawdatamodel.vo.wrappers.Vital;
import com.acuity.visualisations.rest.model.request.vitals.VitalsMeanRangeSelectionRequest;
import com.acuity.visualisations.rest.model.request.vitals.VitalsMeanRangeValuesRequest;
import com.acuity.visualisations.rest.model.request.vitals.VitalsRequest;
import com.acuity.visualisations.rest.model.request.vitals.VitalsTrellisRequest;
import com.acuity.visualisations.rest.util.Constants;
import org.springframework.beans.factory.annotation.Autowired;
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
@RequestMapping(value = "/resources/vitals/mean-range-plot",
        consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
@PreAuthorize(Constants.PRE_AUTHORISE_VISUALISATION)
@CacheConfig(keyGenerator = "datasetsKeyGenerator", cacheResolver = "refreshableCacheResolver")
public class VitalsMeanRangePlotResource {
    @Autowired
    private VitalService vitalService;

    @PostMapping("x-axis")
    public AxisOptions<VitalGroupByOptions> getXAxis(@RequestBody @Valid VitalsRequest requestBody) {
        return vitalService.getAvailableRangePlotXAxis(requestBody.getDatasetsObject(),
                requestBody.getVitalsFilters(), requestBody.getPopulationFilters());
    }

    @PostMapping("trellising")
    @Cacheable(condition = Constants.EMPTY_VITALS_AND_POPULATION_FILTER)
    public List<TrellisOptions<VitalGroupByOptions>> getAvailableTrellising(@RequestBody @Valid VitalsTrellisRequest requestBody) {
        return vitalService.getMeanRangeTrellisOptions(
                requestBody.getDatasetsObject(),
                requestBody.getVitalsFilters(),
                requestBody.getPopulationFilters(),
                requestBody.getYAxisOption());
    }

    @PostMapping("values")
    @Cacheable(condition = Constants.EMPTY_VITALS_AND_POPULATION_FILTER)
    public List<TrellisedRangePlot<Vital, VitalGroupByOptions>> getValues(
            @RequestBody @Valid VitalsMeanRangeValuesRequest requestBody) {
        return vitalService.getRangePlot(
                requestBody.getDatasetsObject(),
                requestBody.getSettings(),
                requestBody.getVitalsFilters(),
                requestBody.getPopulationFilters(),
                StatType.MEDIAN);
    }

    @PostMapping("selection")
    public SelectionDetail getSelection(
            @RequestBody @Valid VitalsMeanRangeSelectionRequest requestBody) {
        return vitalService.getSelectionDetails(
                requestBody.getDatasetsObject(),
                requestBody.getVitalsFilters(),
                requestBody.getPopulationFilters(),
                requestBody.getSelection());
    }
}
