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

package com.acuity.visualisations.rawdatamodel.dataproviders;

import com.acuity.visualisations.rawdatamodel.dataproviders.common.SubjectAwareDatasetsRegularDataProvider;
import com.acuity.visualisations.rawdatamodel.vo.SecondTimeOfProgressionRaw;
import com.acuity.visualisations.rawdatamodel.vo.Subject;
import com.acuity.visualisations.rawdatamodel.vo.wrappers.SecondTimeOfProgression;
import org.springframework.stereotype.Component;

@Component
public class SecondTimeOfProgressionDatasetsDataProvider extends SubjectAwareDatasetsRegularDataProvider<SecondTimeOfProgressionRaw, SecondTimeOfProgression> {
    @Override
    protected SecondTimeOfProgression getWrapperInstance(SecondTimeOfProgressionRaw event, Subject subject) {
        return new SecondTimeOfProgression(event, subject);
    }

    @Override
    protected Class<SecondTimeOfProgressionRaw> rawDataClass() {
        return SecondTimeOfProgressionRaw.class;
    }
}
