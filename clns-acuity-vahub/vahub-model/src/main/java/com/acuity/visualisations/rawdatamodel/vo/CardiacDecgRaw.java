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

package com.acuity.visualisations.rawdatamodel.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.io.Serializable;
import java.util.Date;

@Getter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
@EqualsAndHashCode(of = "id")
@AcuityEntity(version = 0)
public class CardiacDecgRaw implements Serializable {
    private String id;

    private String subjectId;

    private String measurementName;

    private Date measurementTimePoint;

    private Double visitNumber;

    private String resultValue;

    private String protocolScheduleTimepoint;

    private String ecgEvaluation;

    private String clinicallySignificant;

    private String method;

    private Integer beatGroupNumber;

    private Integer beatNumberWithinBeatGroup;

    private Integer numberOfBeatsInAverageBeat;

    private Double beatGroupLengthInSec;

    private String comment;
}

