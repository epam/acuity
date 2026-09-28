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
export class DoseColumnModel extends AbstractColumnModel {
    _columnDefs: List<ColDef> = List([
        {field: 'studyId', headerName: 'Study id'},
        {field: 'studyPart', headerName: 'Study part'},
        {field: 'subjectId', headerName: 'Subject id'},
        {field: 'studyDrug', headerName: 'Study drug'},
        {field: 'studyDrugCategory', headerName: 'Study drug category'},
        {field: 'startDate', headerName: 'Start date'},
        {field: 'endDate', headerName: 'End date'},
        {field: 'dosePerAdmin', headerName: 'Dose per administration'},
        {field: 'doseUnit', headerName: 'Dose unit'},
        {field: 'doseFreq', headerName: 'Dose frequency'},
        {field: 'totalDailyDose', headerName: 'Total daily dose'},
        {field: 'plannedDose', headerName: 'Planned dose'},
        {field: 'plannedDoseUnits', headerName: 'Planned dose units'},
        {field: 'plannedNoDaysTreatment', headerName: 'Planned No. of days treatment'},
        {field: 'formulation', headerName: 'Formulation'},
        {field: 'route', headerName: 'Route'},
        {field: 'actionTaken', headerName: 'Action taken'},
        {field: 'mainReasonForActionTaken', headerName: 'Main reason for action taken'},
        {field: 'mainReasonForActionTakenSpec', headerName: 'Main reason for action taken, Specification'},
        {field: 'aeNumCausedActionTaken', headerName: 'AE number caused action taken'},
        {field: 'aePtCausedActionTaken', headerName: 'AE PT caused action taken'},
        {field: 'reasonForTherapy', headerName: 'Reason for therapy'},
        {field: 'treatmentCycleDelayed', headerName: 'Treatment cycle delayed'},
        {field: 'reasonTreatmentCycleDelayed', headerName: 'Reason treatment cycle delayed'},
        {field: 'reasonTreatmentCycleDelayedOther', headerName: 'Reason treatment cycle delayed, Other'},
        {field: 'aeNumCausedTreatmentCycleDelayed', headerName: 'AE number caused treatment cycle delayed'},
        {field: 'aePtCausedTreatmentCycleDelayed', headerName: 'AE PT caused treatment cycle delayed'},
        {field: 'medicationCode', headerName: 'Medication code'},
        {field: 'medicationDictionaryText', headerName: 'Medication dictionary text'},
        {field: 'atcCode', headerName: 'ATC code'},
        {field: 'atcDictionaryText', headerName: 'ATC dictionary text'},
        {field: 'medicationPt', headerName: 'Medication PT'},
        {field: 'medicationGroupingName', headerName: 'Medication grouping name'},
        {field: 'activeIngredients', headerName: 'Active ingredients'}
    ]);
}
