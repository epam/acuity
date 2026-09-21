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

import {Injectable} from '@angular/core';
import {ColDef} from 'ag-grid-community';
import {List} from 'immutable';
import {AbstractColumnModel} from '../AbstractColumnModel';

@Injectable()
export class SeriousAeColumnModel extends AbstractColumnModel {
    _columnDefs: List<ColDef> = List([
        {field: 'studyId', headerName: 'Study id'},
        {field: 'studyPart', headerName: 'Study part'},
        {field: 'subjectId', headerName: 'Subject Id'},
        {field: 'num', headerName: 'AE number'},
        {field: 'ae', headerName: 'Adverse event'},
        {field: 'startDate', headerName: 'AE start date'},
        {field: 'endDate', headerName: 'AE end date'},
        {field: 'resultInDeath', headerName: 'Results in death'},
        {field: 'hospitalizationRequired', headerName: 'Requires or prolongs hospitalization'},
        {field: 'congenitalAnomaly', headerName: 'Congenital anomaly or birth defect'},
        {field: 'lifeThreatening', headerName: 'Life threatening'},
        {field: 'disability', headerName: 'Persist. or sign. disability/incapacity'},
        {field: 'otherSeriousEvent', headerName: 'Other medically important serious event'},
        {field: 'hospitalizationDate', headerName: 'Date of hospitalization'},
        {field: 'dischargeDate', headerName: 'Date of discharge'},
        {field: 'pt', headerName: 'Preferred term'},
        {field: 'becomeSeriousDate', headerName: 'Date AE met criteria for serious AE'},
        {field: 'daysFromFirstDoseToCriteria', headerName: 'Days from first dose to AE met criteria'},
        {field: 'findOutDate', headerName: 'Date investigator aware of serious AE'},
        {field: 'description', headerName: 'AE description'},
        {field: 'primaryDeathCause', headerName: 'Primary cause of death'},
        {field: 'secondaryDeathCause', headerName: 'Secondary cause of death'},
        {field: 'ad', headerName: 'Additional Drug'},
        {field: 'causedByAD', headerName: 'AE Caused by Additional Drug'},
        {field: 'ad1', headerName: 'Additional Drug 1'},
        {field: 'causedByAD1', headerName: 'AE Caused by Additional Drug 1'},
        {field: 'ad2', headerName: 'Additional Drug 2'},
        {field: 'causedByAD2', headerName: 'AE Caused by Additional Drug 2'},
        {field: 'otherMedication', headerName: 'Other medication'},
        {field: 'causedByOtherMedication', headerName: 'AE caused by other medication'},
        {field: 'studyProcedure', headerName: 'Study procedure(s)'},
        {field: 'causedByStudy', headerName: 'AE caused by study procedure(s)'},
    ]);
}
