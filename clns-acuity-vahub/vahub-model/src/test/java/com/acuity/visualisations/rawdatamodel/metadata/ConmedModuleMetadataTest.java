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

package com.acuity.visualisations.rawdatamodel.metadata;


import com.acuity.visualisations.common.study.metadata.MetadataItem;
import com.acuity.visualisations.rawdatamodel.Constants;
import com.acuity.visualisations.rawdatamodel.dataproviders.ConmedDatasetsDataProvider;
import com.acuity.visualisations.rawdatamodel.service.dod.DoDCommonService;
import com.acuity.visualisations.rawdatamodel.util.Column;
import com.acuity.visualisations.rawdatamodel.vo.ConmedRaw;
import com.acuity.visualisations.rawdatamodel.vo.Subject;
import com.acuity.visualisations.rawdatamodel.vo.wrappers.Conmed;
import com.acuity.va.security.acl.domain.Datasets;
import org.assertj.core.api.JUnitSoftAssertions;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;

import static net.javacrumbs.jsonunit.fluent.JsonFluentAssert.assertThatJson;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyCollection;
import static org.mockito.Mockito.when;

public class ConmedModuleMetadataTest {
    @InjectMocks
    private ConmedModuleMetadata moduleMetadata;
    @Mock
    private ConmedDatasetsDataProvider datasetsDataProvider;
    @Mock
    private DoDCommonService tableService;

    @Rule
    public final JUnitSoftAssertions softly = new JUnitSoftAssertions();

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        moduleMetadata.datasetsDataProvider = Collections.singletonList(datasetsDataProvider);
    }

    @Test
    public void shouldGetMetadata() {
        //Given
        when(datasetsDataProvider.loadData(any(Datasets.class))).thenReturn(Arrays.asList(
                new Conmed(ConmedRaw.builder().build(), Subject.builder().build()),
                new Conmed(ConmedRaw.builder().build(), Subject.builder().build()),
                new Conmed(ConmedRaw.builder().build(), Subject.builder().build())));

        final HashMap<String, String> cols = new HashMap<>();
        cols.put("column1", "title 1");
        cols.put("column2", "title 2");
        when(tableService.getDoDColumns(any(Column.DatasetType.class), anyCollection())).thenReturn(cols);

        //When
        MetadataItem result = moduleMetadata.getMetadataItem(Constants.DATASETS);

        //Then
        softly.assertThat(result.getKey()).isEqualTo("conmeds");
        String json = result.build();
        softly.assertThat(json).contains("\"count\": 3");
        softly.assertThat(json).contains("\"hasData\": true");
        assertThatJson(json).node("conmeds.detailsOnDemandColumns").isEqualTo(Arrays.asList("column1", "column2"));
        assertThatJson(json).node("conmeds.availableYAxisOptions").isEqualTo(Arrays.asList(
                "COUNT_OF_SUBJECTS",
                "PERCENTAGE_OF_ALL_SUBJECTS",
                "PERCENTAGE_OF_SUBJECTS_WITHIN_PLOT"));
    }
}
