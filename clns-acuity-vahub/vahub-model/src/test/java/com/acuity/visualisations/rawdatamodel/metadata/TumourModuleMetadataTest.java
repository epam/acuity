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
import com.acuity.visualisations.rawdatamodel.dataproviders.AssessedTargetLesionDatasetsDataProvider;
import com.acuity.visualisations.rawdatamodel.service.dod.DoDCommonService;
import com.acuity.visualisations.rawdatamodel.vo.AssessedTargetLesionRaw;
import com.acuity.visualisations.rawdatamodel.vo.Subject;
import com.acuity.visualisations.rawdatamodel.vo.wrappers.AssessedTargetLesion;
import org.assertj.core.api.SoftAssertions;
import org.assertj.core.api.junit.jupiter.InjectSoftAssertions;
import org.assertj.core.api.junit.jupiter.SoftAssertionsExtension;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.HashMap;

import static com.acuity.visualisations.config.util.TestConstants.DUMMY_DETECT_DATASETS;
import static com.acuity.visualisations.rawdatamodel.util.Column.DatasetType;
import static com.google.common.collect.Lists.newArrayList;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyCollection;
import static org.mockito.Mockito.when;

@ExtendWith(SoftAssertionsExtension.class)
public class TumourModuleMetadataTest {

    @InjectMocks
    private TumourModuleMetadata tumourModuleMetadata;
    @Mock
    private AssessedTargetLesionDatasetsDataProvider tumourDatasetsDataProvider;
    @Mock
    private DoDCommonService doDCommonService;

    @InjectSoftAssertions
    private SoftAssertions softly;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.initMocks(this);
        tumourModuleMetadata.datasetsDataProvider = newArrayList(tumourDatasetsDataProvider);
    }

    @Test
    public void shouldGetMetadata() {
        when(tumourDatasetsDataProvider.loadDataByVisit(DUMMY_DETECT_DATASETS)).thenReturn(newArrayList(
                new AssessedTargetLesion(new AssessedTargetLesionRaw(), new Subject()),
                new AssessedTargetLesion(new AssessedTargetLesionRaw(), new Subject()),
                new AssessedTargetLesion(new AssessedTargetLesionRaw(), new Subject())));
        when(doDCommonService.getDoDColumns(any(DatasetType.class), anyCollection())).thenReturn(new HashMap());

        MetadataItem metadataItem = tumourModuleMetadata.getMetadataItem(DUMMY_DETECT_DATASETS);
        softly.assertThat(metadataItem.getKey()).isEqualTo("tumour");
        softly.assertThat(metadataItem.build()).contains("\"count\": 3");
        softly.assertThat(metadataItem.build()).contains("\"hasData\": true");
    }

    @Test
    public void shouldGetMetadataNoData() {
        when(tumourDatasetsDataProvider.loadDataByVisit(DUMMY_DETECT_DATASETS)).thenReturn(newArrayList());
        when(doDCommonService.getDoDColumns(any(DatasetType.class), anyCollection())).thenReturn(new HashMap());

        MetadataItem metadataItem = tumourModuleMetadata.getMetadataItem(DUMMY_DETECT_DATASETS);
        softly.assertThat(metadataItem.getKey()).isEqualTo("tumour");
        softly.assertThat(metadataItem.build()).contains("\"count\": 0");
        softly.assertThat(metadataItem.build()).contains("\"hasData\": false");
    }
}
