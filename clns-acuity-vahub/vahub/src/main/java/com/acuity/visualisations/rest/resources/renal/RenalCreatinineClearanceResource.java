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

package com.acuity.visualisations.rest.resources.renal;

import com.acuity.visualisations.rawdatamodel.service.event.RenalService;
import com.acuity.visualisations.rawdatamodel.vo.plots.SelectionDetail;
import com.acuity.visualisations.rest.model.request.renal.RenalPlotValuesRequest;
import com.acuity.visualisations.rest.model.request.renal.RenalRequest;
import com.acuity.visualisations.rest.model.request.renal.RenalSelectionRequest;
import com.acuity.visualisations.rest.model.request.renal.RenalTrellisRequest;
import com.acuity.visualisations.rest.model.response.renal.RenalBoxPlotResponse;
import com.acuity.visualisations.rest.model.response.renal.RenalTrellisResponse;
import com.acuity.visualisations.rest.model.response.renal.RenalXAxisResponse;
import com.acuity.visualisations.rest.util.Constants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/resources/renal/creatinine-clearance-box-plot")
@PreAuthorize(Constants.PRE_AUTHORISE_VISUALISATION)
public class RenalCreatinineClearanceResource {

    @Autowired
    private RenalService renalService;

    @PostMapping("x-axis")
    public RenalXAxisResponse getAvailableXAxisOptions(@RequestBody RenalRequest requestBody) {
        return new RenalXAxisResponse(renalService.getAvailableBoxPlotXAxis(
                requestBody.getDatasetsObject(),
                requestBody.getRenalFilters(),
                requestBody.getPopulationFilters()));
    }

    @PostMapping("trellising")
    public RenalTrellisResponse getAvailableTrellising(@RequestBody RenalTrellisRequest requestBody) {
        return new RenalTrellisResponse(renalService.getTrellisOptions(
                requestBody.getDatasetsObject(),
                requestBody.getRenalFilters(),
                requestBody.getPopulationFilters(),
                requestBody.getYAxisOption()));
    }

    @PostMapping("boxplot")
    public RenalBoxPlotResponse getBoxPlotData(@RequestBody @Valid RenalPlotValuesRequest requestBody) {
        return new RenalBoxPlotResponse(renalService.getBoxPlot(
                requestBody.getDatasetsObject(),
                requestBody.getSettings(),
                requestBody.getRenalFilters(),
                requestBody.getPopulationFilters()));
    }

    @PostMapping("selection")
    public SelectionDetail getSelection(@RequestBody @Valid RenalSelectionRequest requestBody) {
        return renalService.getRangedSelectionDetails(requestBody.getDatasetsObject(),
                requestBody.getRenalFilters(),
                requestBody.getPopulationFilters(),
                requestBody.getSelection());
    }
}
