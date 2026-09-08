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
import com.acuity.visualisations.rawdatamodel.filters.CvotEndpointFilters;
import com.acuity.visualisations.rawdatamodel.filters.Filters;
import com.acuity.visualisations.rawdatamodel.service.event.CvotEndpointService;
import com.acuity.visualisations.rawdatamodel.trellis.TrellisOptions;
import com.acuity.visualisations.rawdatamodel.trellis.grouping.CvotEndpointGroupByOptions;
import com.acuity.visualisations.rawdatamodel.vo.compatibility.TrellisedBarChart;
import com.acuity.visualisations.rawdatamodel.vo.compatibility.TrellisedOvertime;
import com.acuity.visualisations.rawdatamodel.vo.plots.SelectionDetail;
import com.acuity.visualisations.rawdatamodel.vo.wrappers.CvotEndpoint;
import com.acuity.visualisations.rest.model.request.cvot.endpoint.CvotEndpointBarChartRequest;
import com.acuity.visualisations.rest.model.request.cvot.endpoint.CvotEndpointBarChartSelectionRequest;
import com.acuity.visualisations.rest.model.request.cvot.endpoint.CvotEndpointBarLineChartRequest;
import com.acuity.visualisations.rest.model.request.cvot.endpoint.CvotEndpointRequest;
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
@RequestMapping(value = "/resources/cvotendpoint", consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
@CacheConfig(keyGenerator = "datasetsKeyGenerator", cacheResolver = "refreshableCacheResolver")
@PreAuthorize(Constants.PRE_AUTHORISE_VISUALISATION)
public class CvotEndpointResource {

    @Autowired
    private CvotEndpointService cvotEndpointService;

    private static void setDownloadHeaders(HttpServletResponse response) {
        response.addHeader("Content-disposition", "attachment;filename=details_on_demand.csv");
        response.setContentType("txt/csv");
    }

    @RequestMapping(value = "/countsbarchart", method = POST)
    @Cacheable(condition = Constants.EMPTY_EVENT_AND_POPULATION_FILTER)
    public List<TrellisedBarChart<CvotEndpoint, CvotEndpointGroupByOptions>> getBarChartData(
            @RequestBody CvotEndpointBarChartRequest requestBody) {
        return cvotEndpointService.getBarChart(requestBody.getDatasetsObject(), requestBody.getSettings(), requestBody.getEventFilters(),
                requestBody.getPopulationFilters(), requestBody.getCountType());
    }

    @RequestMapping(value = "/overtime", method = POST)
    @Cacheable(condition = Constants.EMPTY_EVENT_AND_POPULATION_FILTER)
    public List<TrellisedOvertime<CvotEndpoint, CvotEndpointGroupByOptions>> getLineBarChartData(
            @RequestBody CvotEndpointBarLineChartRequest requestBody) {
        return cvotEndpointService.getLineBarChart(requestBody.getDatasetsObject(), requestBody.getSettings(), requestBody.getEventFilters(),
                requestBody.getPopulationFilters());
    }

    @RequestMapping(value = "/trellising", method = POST)
    @Cacheable(condition = Constants.EMPTY_EVENT_AND_POPULATION_FILTER)
    public List<TrellisOptions<CvotEndpointGroupByOptions>> getAvailableTrellising(
            @RequestBody @Valid CvotEndpointRequest requestBody) {
        return cvotEndpointService.getTrellisOptions(requestBody.getDatasetsObject(), requestBody.getEventFilters(),
                requestBody.getPopulationFilters());
    }

    @RequestMapping(value = "/colorby-options", method = POST)
    @Cacheable(condition = Constants.EMPTY_EVENT_AND_POPULATION_FILTER)
    public List<TrellisOptions<CvotEndpointGroupByOptions>> getAvailableColorBy(
            @RequestBody @Valid CvotEndpointRequest requestBody) {
        return cvotEndpointService.getBarChartColorBy(requestBody.getDatasetsObject(), requestBody.getEventFilters(),
                requestBody.getPopulationFilters());
    }

    @RequestMapping(value = "/filters", method = POST)
    @Cacheable(condition = Constants.EMPTY_EVENT_AND_POPULATION_FILTER)
    public Filters<CvotEndpoint> getAvailableFilters(@RequestBody @Valid CvotEndpointRequest requestBody) {
        return cvotEndpointService.getAvailableFilters(requestBody.getDatasetsObject(),
                requestBody.getEventFilters(),
                requestBody.getPopulationFilters());
    }

    @RequestMapping(value = "/countsbarchart-xaxis", method = POST)
    @Cacheable(condition = Constants.EMPTY_EVENT_AND_POPULATION_FILTER)
    public AxisOptions<CvotEndpointGroupByOptions> getBarChartXAxis(@RequestBody @Valid CvotEndpointRequest requestBody) {
        return cvotEndpointService.getAvailableBarChartXAxis(requestBody.getDatasetsObject(),
                requestBody.getEventFilters(),
                requestBody.getPopulationFilters());
    }

    @RequestMapping(value = "/overtime-xaxis", method = POST)
    @Cacheable(condition = Constants.EMPTY_EVENT_AND_POPULATION_FILTER)
    public AxisOptions<CvotEndpointGroupByOptions> getOvertimeXAxis(@RequestBody @Valid CvotEndpointRequest requestBody) {
        return cvotEndpointService.getAvailableOverTimeChartXAxis(requestBody.getDatasetsObject(),
                requestBody.getEventFilters(),
                requestBody.getPopulationFilters());
    }

    @RequestMapping(value = "/selection", method = POST)
    public SelectionDetail getSelectionDetailWithinBarChart(
            @RequestBody @Valid CvotEndpointBarChartSelectionRequest requestBody) {
        return cvotEndpointService.getSelectionDetails(requestBody.getDatasetsObject(), requestBody.getEventFilters(),
                requestBody.getPopulationFilters(), requestBody.getSelection());
    }

    @RequestMapping(value = "/details-on-demand", method = POST)
    public List<Map<String, String>> getDetailsOnDemandData(
            @RequestBody @Valid DetailsOnDemandRequest requestBody) {

        return cvotEndpointService.getDetailsOnDemandData(
                requestBody.getDatasetsObject(), requestBody.getEventIds(), requestBody.getSortAttrs(),
                requestBody.getStart(), (long) requestBody.getEnd() - requestBody.getStart());
    }

    @RequestMapping(value = "/download-details-on-demand", method = POST)
    public void downloadAllDetailsOnDemandData(@RequestBody @Valid CvotEndpointRequest requestBody,
                                               HttpServletResponse response) throws IOException {
        setDownloadHeaders(response);
        cvotEndpointService.writeAllDetailsOnDemandCsv(requestBody.getDatasetsObject(), response.getWriter(),
                requestBody.getEventFilters(), requestBody.getPopulationFilters());
    }

    @RequestMapping(value = "/download-selected-details-on-demand", method = POST)
    public void downloadSelectedDetailsOnDemandData(@RequestBody @Valid DetailsOnDemandRequest requestBody,
                                                    HttpServletResponse response) throws IOException {
        setDownloadHeaders(response);
        cvotEndpointService.writeSelectedDetailsOnDemandCsv(requestBody.getDatasetsObject(), requestBody.getEventIds(), response.getWriter());
    }

    @RequestMapping(value = "/single-subject", method = POST)
    @Cacheable(condition = "#requestBody.getEventFilters().isEmpty()")
    public List<Map<String, String>> getSingleSubjectData(
            @RequestBody @Valid SingleSubjectRequest<CvotEndpointFilters> requestBody) {

        return cvotEndpointService.getSingleSubjectData(
                requestBody.getDatasetsObject(), requestBody.getSubjectId(), requestBody.getEventFilters());
    }

    @RequestMapping(value = "/filters-subjects", method = POST)
    @Cacheable(condition = Constants.EMPTY_EVENT_AND_POPULATION_FILTER)
    public List<String> getSubjects(
            @RequestBody CvotEndpointRequest requestBody) {
        return cvotEndpointService.getSubjects(requestBody.getDatasetsObject(), requestBody.getEventFilters(), requestBody.getPopulationFilters());
    }

}
