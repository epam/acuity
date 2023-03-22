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

import static net.javacrumbs.jsonunit.fluent.JsonFluentAssert.assertThatJson;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyCollection;
import static org.mockito.Mockito.when;

import com.acuity.visualisations.common.study.metadata.MetadataItem;
import com.acuity.visualisations.rawdatamodel.Constants;
import com.acuity.visualisations.rawdatamodel.dataproviders.SeriousAeDatasetsDataProvider;
import com.acuity.visualisations.rawdatamodel.service.dod.DoDCommonService;
import com.acuity.visualisations.rawdatamodel.util.Column;
import com.acuity.visualisations.rawdatamodel.vo.SeriousAeRaw;
import com.acuity.visualisations.rawdatamodel.vo.Subject;
import com.acuity.visualisations.rawdatamodel.vo.wrappers.SeriousAe;
import com.acuity.va.security.acl.domain.Datasets;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import org.assertj.core.api.JUnitSoftAssertions;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

public class SeriousAeModuleMetadataTest {
    @InjectMocks
    private SeriousAeModuleMetadata moduleMetadata;
    @Mock
    private SeriousAeDatasetsDataProvider datasetsDataProvider;
    @Mock
    private DoDCommonService doDCommonService;

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
                new SeriousAe(SeriousAeRaw.builder().build(), Subject.builder().build()),
                new SeriousAe(SeriousAeRaw.builder().build(), Subject.builder().build()),
                new SeriousAe(SeriousAeRaw.builder().build(), Subject.builder().build())));

        final HashMap<String, String> cols = new HashMap<>();
        cols.put("column1", "title 1");
        cols.put("column2", "title 2");
        when(doDCommonService.getDoDColumns(any(Column.DatasetType.class), anyCollection())).thenReturn(cols);

        //When
        MetadataItem result = moduleMetadata.getMetadataItem(Constants.DATASETS);

        //Then
        softly.assertThat(result.getKey()).isEqualTo("seriousAe");
        String json = result.build();
        softly.assertThat(json).contains("\"count\": 3");
        softly.assertThat(json).contains("\"hasData\": true");
        assertThatJson(json).node("seriousAe.detailsOnDemandColumns").isEqualTo(Arrays.asList("column1", "column2"));
    }
}