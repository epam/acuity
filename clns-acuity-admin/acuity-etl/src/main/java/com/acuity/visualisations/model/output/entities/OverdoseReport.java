
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

package com.acuity.visualisations.model.output.entities;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.acuity.visualisations.model.acuityfield.AcuityField;
import com.acuity.visualisations.model.acuityfield.AcuityFieldTransformation;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
public class OverdoseReport extends TimestampedEntity {
    protected String part;
    protected String subject;

    private String patientGuid;

    private String drug;
    private String route;
    @AcuityField(transform = AcuityFieldTransformation.INITIATING_EVENT_00_00_01)
	private LocalDateTime startDate;
    @AcuityField(transform = AcuityFieldTransformation.MEASUREMENT_EVENT_12_00_00)
	private LocalDateTime stopDate;
    private String intentFlag;
    private String overdoseAeFlag;
    private Integer numberForAe;
    private String aeOverdoseAss;
    private BigDecimal totalDose;
    private String totalDoseUnit;
    private String furtherInfo;
}
