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
export class CerebrovascularColumnModel extends AbstractColumnModel {
    _columnDefs: List<ColDef> = List([
        {field: 'studyId', headerName: 'Study ID'},
        {field: 'studyPart', headerName: 'Study part'},
        {field: 'subjectId', headerName: 'Subject ID'},
        {field: 'eventType', headerName: 'Type of Event'},
        {field: 'aeNumber', headerName: 'Associated AE No.'},
        {field: 'startDate', headerName: 'Event Start Date'},
        {field: 'term', headerName: 'Event Term'},
        {field: 'primaryIschemicStroke', headerName: 'If Primary Ischemic Stroke'},
        {field: 'traumatic', headerName: 'If Traumatic'},
        {field: 'intraHemorrhageLoc', headerName: 'Loc. of Primary Intracranial Hemorrhage'},
        {field: 'intraHemorrhageOtherLoc', headerName: 'Primary Intra. Hemorrhage Other, Specify'},
        {field: 'symptomsDuration', headerName: 'Duration of Symptoms'},
        {field: 'mrsPriorToStroke', headerName: 'MRS Prior to Stroke'},
        {field: 'mrsDuringStrokeHosp', headerName: 'MRS During Stroke Hospitalisation'},
        {field: 'mrsCurrVisitOr90dAfter', headerName: 'MRS at Current Visit or 90D After Stroke'},
        {field: 'comment', headerName: 'Comment'}
    ]);
}
