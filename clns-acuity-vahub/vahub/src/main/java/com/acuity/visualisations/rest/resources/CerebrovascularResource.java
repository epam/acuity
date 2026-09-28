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

package com.acuity.visualisations.rest.resources;

import com.acuity.visualisations.rawdatamodel.axes.AxisOptions;
import com.acuity.visualisations.rawdatamodel.filters.CerebrovascularFilters;
import com.acuity.visualisations.rawdatamodel.filters.Filters;
import com.acuity.visualisations.rawdatamodel.service.event.CerebrovascularService;
import com.acuity.visualisations.rawdatamodel.trellis.TrellisOptions;
import com.acuity.visualisations.rawdatamodel.trellis.grouping.CerebrovascularGroupByOptions;
import com.acuity.visualisations.rawdatamodel.vo.compatibility.TrellisedBarChart;
import com.acuity.visualisations.rawdatamodel.vo.compatibility.TrellisedOvertime;
import com.acuity.visualisations.rawdatamodel.vo.plots.SelectionDetail;
import com.acuity.visualisations.rawdatamodel.vo.wrappers.Cerebrovascular;
import com.acuity.visualisations.rest.model.request.cerebrovascular.CerebrovascularBarChartRequest;
import com.acuity.visualisations.rest.model.request.cerebrovascular.CerebrovascularBarChartSelectionRequest;
import com.acuity.visualisations.rest.model.request.cerebrovascular.CerebrovascularBarLineChartRequest;
import com.acuity.visualisations.rest.model.request.cerebrovascular.CerebrovascularRequest;
import com.acuity.visualisations.rest.model.request.DetailsOnDemandRequest;
import com.acuity.visualisations.rest.model.request.SingleSubjectRequest;
import com.acuity.visualisations.rest.util.Constants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

@RestController
@RequestMapping(value = "/resources/cerebrovascular", consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
@CacheConfig(keyGenerator = "datasetsKeyGenerator", cacheResolver = "refreshableCacheResolver")
@PreAuthorize(Constants.PRE_AUTHORISE_VISUALISATION)
public class CerebrovascularResource {

    @Autowired
    private CerebrovascularService cerebrovascularService;

    private static void setDownloadHeaders(HttpServletResponse response) {
        response.addHeader("Content-disposition", "attachment;filename=details_on_demand.csv");
        response.setContentType("txt/csv");
    }

    @RequestMapping(value = "/countsbarchart", method = POST)
    @Cacheable(condition = Constants.EMPTY_EVENT_AND_POPULATION_FILTER)
    public List<TrellisedBarChart<Cerebrovascular, CerebrovascularGroupByOptions>> getBarChartData(
            @RequestBody CerebrovascularBarChartRequest requestBody) {
        return cerebrovascularService.getBarChart(requestBody.getDatasetsObject(), requestBody.getSettings(), requestBody.getEventFilters(),
                requestBody.getPopulationFilters(), requestBody.getCountType());
    }

    @RequestMapping(value = "/overtime", method = POST)
    @Cacheable(condition = Constants.EMPTY_EVENT_AND_POPULATION_FILTER)
    public List<TrellisedOvertime<Cerebrovascular, CerebrovascularGroupByOptions>> getLineBarChartData(
            @RequestBody CerebrovascularBarLineChartRequest requestBody) {

        return cerebrovascularService.getLineBarChart(requestBody.getDatasetsObject(), requestBody.getSettings(), requestBody.getEventFilters(),
                requestBody.getPopulationFilters());
    }

    @RequestMapping(value = "/trellising", method = POST)
    @Cacheable(condition = Constants.EMPTY_EVENT_AND_POPULATION_FILTER)
    public List<TrellisOptions<CerebrovascularGroupByOptions>> getAvailableTrellising(
            @RequestBody @Valid CerebrovascularRequest requestBody) {
        return cerebrovascularService.getTrellisOptions(requestBody.getDatasetsObject(), requestBody.getEventFilters(),
                requestBody.getPopulationFilters());
    }

    @RequestMapping(value = "/colorby-options", method = POST)
    @Cacheable(condition = Constants.EMPTY_EVENT_AND_POPULATION_FILTER)
    public List<TrellisOptions<CerebrovascularGroupByOptions>> getAvailableColorBy(
            @RequestBody @Valid CerebrovascularRequest requestBody) {
        return cerebrovascularService.getBarChartColorBy(requestBody.getDatasetsObject(), requestBody.getEventFilters(),
                requestBody.getPopulationFilters());
    }

    @RequestMapping(value = "/filters", method = POST)
    @Cacheable(condition = Constants.EMPTY_EVENT_AND_POPULATION_FILTER)
    public Filters<Cerebrovascular> getAvailableFilters(@RequestBody @Valid CerebrovascularRequest requestBody) {
        return cerebrovascularService.getAvailableFilters(requestBody.getDatasetsObject(), requestBody.getEventFilters(),
                requestBody.getPopulationFilters());
    }

    @RequestMapping(value = "/countsbarchart-xaxis", method = POST)
    @Cacheable(condition = Constants.EMPTY_EVENT_AND_POPULATION_FILTER)
    public AxisOptions<CerebrovascularGroupByOptions> getBarChartXAxis(@RequestBody @Valid CerebrovascularRequest requestBody) {
        return cerebrovascularService.getAvailableBarChartXAxis(requestBody.getDatasetsObject(), requestBody.getEventFilters(),
                requestBody.getPopulationFilters());
    }

    @RequestMapping(value = "/overtime-xaxis", method = POST)
    @Cacheable(condition = Constants.EMPTY_EVENT_AND_POPULATION_FILTER)
    public AxisOptions<CerebrovascularGroupByOptions> getOvertimeXAxis(@RequestBody @Valid CerebrovascularRequest requestBody) {
        return cerebrovascularService.getAvailableOverTimeChartXAxis(requestBody.getDatasetsObject(),
                requestBody.getEventFilters(),
                requestBody.getPopulationFilters());
    }

    @RequestMapping(value = "/selection", method = POST)
    public SelectionDetail getSelectionDetailWithinBarChart(
            @RequestBody @Valid CerebrovascularBarChartSelectionRequest requestBody) {
        return cerebrovascularService.getSelectionDetails(requestBody.getDatasetsObject(), requestBody.getEventFilters(),
                requestBody.getPopulationFilters(), requestBody.getSelection());
    }

    @RequestMapping(value = "/details-on-demand", method = POST)
    public List<Map<String, String>> getDetailsOnDemandData(
            @RequestBody @Valid DetailsOnDemandRequest requestBody) {

        return cerebrovascularService.getDetailsOnDemandData(
                requestBody.getDatasetsObject(), requestBody.getEventIds(), requestBody.getSortAttrs(),
                requestBody.getStart(), (long) requestBody.getEnd() - requestBody.getStart());
    }

    @RequestMapping(value = "/download-details-on-demand", method = POST)
    public void downloadAllDetailsOnDemandData(@RequestBody @Valid CerebrovascularRequest requestBody,
                                               HttpServletResponse response) throws IOException {
        setDownloadHeaders(response);
        cerebrovascularService.writeAllDetailsOnDemandCsv(requestBody.getDatasetsObject(), response.getWriter(),
                requestBody.getEventFilters(), requestBody.getPopulationFilters());
    }

    @RequestMapping(value = "/download-selected-details-on-demand", method = POST)
    public void downloadSelectedDetailsOnDemandData(@RequestBody @Valid DetailsOnDemandRequest requestBody,
                                                    HttpServletResponse response) throws IOException {
        setDownloadHeaders(response);
        cerebrovascularService.writeSelectedDetailsOnDemandCsv(requestBody.getDatasetsObject(), requestBody.getEventIds(), response.getWriter());
    }

    @RequestMapping(value = "/single-subject", method = POST)
    @Cacheable(condition = "#requestBody.getEventFilters().isEmpty()")
    public List<Map<String, String>> getSingleSubjectData(
            @RequestBody @Valid SingleSubjectRequest<CerebrovascularFilters> requestBody) {

        return cerebrovascularService.getSingleSubjectData(
                requestBody.getDatasetsObject(), requestBody.getSubjectId(), requestBody.getEventFilters());
    }

    @RequestMapping(value = "/filters-subjects", method = POST)
    @Cacheable(condition = Constants.EMPTY_EVENT_AND_POPULATION_FILTER)
    public List<String> getSubjects(
            @RequestBody CerebrovascularRequest requestBody) {
        return cerebrovascularService.getSubjects(requestBody.getDatasetsObject(), requestBody.getEventFilters(), requestBody.getPopulationFilters());
    }

}
