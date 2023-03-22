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

package com.acuity.visualisations.rest.model.request.patient.data;

import com.acuity.visualisations.rawdatamodel.filters.PatientDataFilters;
import com.acuity.visualisations.rest.model.request.EventFilterRequestPopulationAware;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class PatientDataRequest extends EventFilterRequestPopulationAware<PatientDataFilters> {

    private PatientDataFilters patientDataFilters;

    @Override
    public PatientDataFilters getEventFilters() {
        return patientDataFilters;
    }
}
