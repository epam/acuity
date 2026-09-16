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
import com.acuity.visualisations.rest.model.request.respiratory.exacerbation.ExacerbationRequest;
import com.acuity.visualisations.rest.model.response.DetailsOnDemandResponse;
import com.acuity.visualisations.rest.model.request.DetailsOnDemandRequest;
import com.acuity.visualisations.rest.util.Constants;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import java.io.IOException;

import static com.acuity.visualisations.rest.util.Constants.PRE_AUTHORISE_VISUALISATION;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@RestController
@RequestMapping(value = "/resources/respiratory/exacerbation/details-on-demand",
        consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
@PreAuthorize(PRE_AUTHORISE_VISUALISATION)
@RequiredArgsConstructor
public class ExacerbationDetailsOnDemandResource {

    private final ExacerbationService exacerbationService;

    @PostMapping("data")
    public DetailsOnDemandResponse getExacerbationDetailsOnDemandData(
            @RequestBody @Valid DetailsOnDemandRequest requestBody) throws NoSuchFieldException {
        return new DetailsOnDemandResponse(exacerbationService.getDetailsOnDemandData(
                requestBody.getDatasetsObject(),
                requestBody.getEventIds(),
                requestBody.getSortAttrs(),
                requestBody.getStart(),
                (long) requestBody.getEnd() - requestBody.getStart()));
    }

    @PostMapping("all-csv")
    public void downloadAllExacerbationDetailsOnDemandData(@RequestBody @Valid ExacerbationRequest requestBody,
                                                           HttpServletResponse response) throws IOException {
        Constants.setDownloadHeader(response);
        exacerbationService.writeAllDetailsOnDemandCsv(requestBody.getDatasetsObject(), response.getWriter(),
                requestBody.getExacerbationFilters(), requestBody.getPopulationFilters());
    }

    @PostMapping("selected-csv")
    public void downloadSelectedExacerbationDetailsOnDemandData(@RequestBody @Valid DetailsOnDemandRequest requestBody,
                                                                HttpServletResponse response) throws IOException {
        Constants.setDownloadHeader(response);
        exacerbationService.writeSelectedDetailsOnDemandCsv(requestBody.getDatasetsObject(), requestBody.getEventIds(), response.getWriter());
    }
}
