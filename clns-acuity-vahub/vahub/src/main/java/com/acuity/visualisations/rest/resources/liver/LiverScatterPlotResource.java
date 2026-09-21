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

package com.acuity.visualisations.rest.resources.liver;

import com.acuity.visualisations.rawdatamodel.service.event.LiverService;
import com.acuity.visualisations.rawdatamodel.trellis.TrellisOptions;
import com.acuity.visualisations.rawdatamodel.trellis.grouping.LiverGroupByOptions;
import com.acuity.visualisations.rawdatamodel.vo.SelectionBox;
import com.acuity.visualisations.rawdatamodel.vo.compatibility.TrellisedScatterPlot;
import com.acuity.visualisations.rawdatamodel.vo.plots.SelectionDetail;
import com.acuity.visualisations.rawdatamodel.vo.wrappers.Liver;
import com.acuity.visualisations.rest.model.request.liver.HysRequest;
import com.acuity.visualisations.rest.model.request.liver.HysSelectionRequest;
import com.acuity.visualisations.rest.model.request.liver.LiverRequest;
import com.acuity.visualisations.rest.util.Constants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@RestController
@RequestMapping(value = "/resources/liver/", consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
@PreAuthorize(Constants.PRE_AUTHORISE_VISUALISATION)
@CacheConfig(keyGenerator = "datasetsKeyGenerator", cacheResolver = "refreshableCacheResolver")
public class LiverScatterPlotResource {
    @Autowired
    private LiverService liverService;

    @PostMapping("trellising")
    @Cacheable
    public List<TrellisOptions<LiverGroupByOptions>> getAvailableTrellising(
            @RequestBody LiverRequest requestBody) {

        return liverService.getTrellisOptions(
                requestBody.getDatasetsObject(),
                requestBody.getLiverFilters(),
                requestBody.getPopulationFilters());
    }

    @PostMapping("hysscatter")
    @Cacheable
    public List<TrellisedScatterPlot<Liver, LiverGroupByOptions>> getDataForScatterChart(
            @RequestBody HysRequest requestBody) {

        return liverService.getPlotValues(
                requestBody.getDatasetsObject(),
                requestBody.getSettings().getFilterByTrellisOptions(),
                requestBody.getLiverFilters(),
                requestBody.getPopulationFilters());
    }

    @PostMapping("hysscatter-selection")
    @Cacheable
    public SelectionDetail getSelectionDetailsWithinHysLawPlot(
            @RequestBody HysSelectionRequest requestBody) {

        return liverService.getPlotSelection(
                requestBody.getDatasetsObject(),
                requestBody.getSettings().getFilterByTrellisOptions(),
                requestBody.getLiverFilters(),
                requestBody.getPopulationFilters(),
                new SelectionBox(
                        requestBody.getMinX(),
                        requestBody.getMaxX(),
                        requestBody.getMinY(),
                        requestBody.getMaxY()
                ));
    }
}
