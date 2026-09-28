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
import com.acuity.visualisations.rest.model.request.liver.LiverRequest;
import com.acuity.visualisations.rest.resources.DetailsOnDemandCsvDownloader;
import com.acuity.visualisations.rest.model.request.DetailsOnDemandRequest;
import com.acuity.visualisations.rest.util.Constants;
import org.springframework.beans.factory.annotation.Autowired;
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
import java.util.List;
import java.util.Map;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@RestController
@RequestMapping(value = "/resources/liver/", consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
@PreAuthorize(Constants.PRE_AUTHORISE_VISUALISATION)
@CacheConfig(keyGenerator = "datasetsKeyGenerator", cacheResolver = "refreshableCacheResolver")
public class LiverDetailsOnDemandResource extends DetailsOnDemandCsvDownloader {

    @Autowired
    private LiverService liverService;

    @PostMapping("details-on-demand")
    @Cacheable
    public List<Map<String, String>> getDetailsOnDemandData(
            @RequestBody @Valid DetailsOnDemandRequest requestBody) throws NoSuchFieldException {

        return liverService.getDetailsOnDemandData(
                requestBody.getDatasetsObject(), requestBody.getEventIds(), requestBody.getSortAttrs(),
                requestBody.getStart(), (long) requestBody.getEnd() - requestBody.getStart());
    }

    @PostMapping("download-details-on-demand")
    @Cacheable
    public void getAllDetailsOnDemandData(
            @RequestBody @Valid LiverRequest requestBody,
            HttpServletResponse response) throws IOException {

        setDownloadHeaders(response);
        liverService.writeAllDetailsOnDemandCsv(requestBody.getDatasetsObject(), response.getWriter(),
                requestBody.getEventFilters(), requestBody.getPopulationFilters());
    }


    @PostMapping(value = "download-selected-details-on-demand")
    public void downloadSelectedDetailsOnDemandData(@RequestBody @Valid DetailsOnDemandRequest requestBody,
                                                    HttpServletResponse response) throws IOException {
        setDownloadHeaders(response);
        liverService.writeSelectedDetailsOnDemandCsv(requestBody.getDatasetsObject(), requestBody.getEventIds(), response.getWriter());
    }
}
