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

package com.acuity.visualisations.rest.resources.alcohol;

import static com.acuity.visualisations.config.util.TestConstants.DUMMY_ACUITY_DATASETS;
import static org.hamcrest.Matchers.hasKey;
import static org.hamcrest.Matchers.hasValue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.acuity.visualisations.rawdatamodel.filters.AlcoholFilters;
import com.acuity.visualisations.rawdatamodel.filters.PopulationFilters;
import com.acuity.visualisations.rawdatamodel.service.event.AlcoholService;
import com.acuity.visualisations.rest.model.response.DetailsOnDemandResponse;
import com.acuity.visualisations.rest.model.request.SingleSubjectRequest;
import com.acuity.visualisations.rest.model.request.alcohol.AlcoholRequest;
import com.acuity.va.security.acl.domain.Datasets;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

@RunWith(SpringRunner.class)
@WebMvcTest(controllers = AlcoholResource.class, secure = false)
public class AlcoholResourceTest {
    private static final String BASE_URL = "/resources/alcohol";

    @Autowired
    private MockMvc mvc;

    @MockBean
    private AlcoholService alcoholService;

    private ObjectMapper mapper = new ObjectMapper();

    @Test
    public void shouldGetFilters() throws Exception {
        AlcoholRequest request = new AlcoholRequest();
        request.setAlcoholFilters(AlcoholFilters.empty());
        request.setPopulationFilters(PopulationFilters.empty());
        request.setDatasets(DUMMY_ACUITY_DATASETS.getDatasetsList());

        when(alcoholService.getAvailableFilters(any(Datasets.class),
                any(AlcoholFilters.class),
                any(PopulationFilters.class)
        )).thenReturn(AlcoholFilters.empty());

        mvc.perform(post(BASE_URL + "/filters")
                .content(mapper.writeValueAsString(request))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    public void shouldGetSingleSubjectData() throws Exception {
        SingleSubjectRequest<AlcoholFilters> requestBody = new SingleSubjectRequest();
        requestBody.setEventFilters(AlcoholFilters.empty());
        requestBody.setSubjectId("subject1");
        requestBody.setDatasets(DUMMY_ACUITY_DATASETS.getDatasetsList());

        Map<String, String> dodMap = new HashMap<>();
        dodMap.put("column_1", "value_1");
        dodMap.put("column_2", "value_2");

        DetailsOnDemandResponse responseToReturn = new DetailsOnDemandResponse(Arrays.asList(dodMap));

        when(alcoholService.getDetailsOnDemandData(any(Datasets.class), anyString(), any(AlcoholFilters.class)))
                .thenReturn(responseToReturn.getDodData());

        mvc.perform(post(BASE_URL + "/single-subject").
                content(mapper.writeValueAsString(requestBody)).
                contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.dodData[0]", hasKey("column_2")))
                .andExpect(jsonPath("$.dodData[0]", hasValue("value_2")))
                .andExpect(jsonPath("$.dodData[0]", hasKey("column_1")))
                .andExpect(jsonPath("$.dodData[0]", hasValue("value_1")))
                .andReturn();
    }
}
