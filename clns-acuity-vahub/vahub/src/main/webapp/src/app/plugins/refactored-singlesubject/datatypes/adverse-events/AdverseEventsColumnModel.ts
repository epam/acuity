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
export class AdverseEventsColumnModel extends AbstractColumnModel {
    _columnDefs: List<ColDef> = List([
        {field: 'studyId'},
        {field: 'studyPart'},
        {field: 'subjectId'},
        {field: 'preferredTerm'},
        {field: 'highLevelTerm'},
        {field: 'systemOrganClass'},
        {field: 'specialInterestGroup'},
        {field: 'maxSeverity'},
        {field: 'startDate'},
        {field: 'endDate'},
        {field: 'daysOnStudyAtAEStart'},
        {field: 'daysOnStudyAtAEEnd'},
        {field: 'duration'},
        {field: 'daysFromPreviousDoseToAEStart'},
        {field: 'serious'},
        {field: 'actionTaken'},
        {field: 'requiresOrProlongsHospitalisation'},
        {field: 'treatmentEmergent'},
        {field: 'causality'},
        {field: 'description'},
        {field: 'comment'},
        {field: 'outcome'},
        {field: 'requiredTreatment'},
        {field: 'causedSubjectWithdrawal'},
        {field: 'doseLimitingToxicity'},
        {field: 'timePointOfDoseLimitingToxicity'},
        {field: 'immuneMediatedAE'},
        {field: 'infusionReactionAE'},
        {field: 'aeOfSpecialInterest', headerName: 'Ae of special interest'},
    ]);
}
