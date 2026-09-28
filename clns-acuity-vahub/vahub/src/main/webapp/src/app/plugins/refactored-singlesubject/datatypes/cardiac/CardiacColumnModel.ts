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

import {AbstractColumnModel} from '../AbstractColumnModel';
import {ColDef} from 'ag-grid-community';
import {List} from 'immutable';
import {Injectable} from '@angular/core';

@Injectable()
export class CardiacColumnModel extends AbstractColumnModel {
    _columnDefs: List<ColDef> = List([
        {field: 'studyId'},
        {field: 'studyPart'},
        {field: 'subjectId'},
        {field: 'measurementCategory'},
        {field: 'measurementName'},
        {field: 'measurementTimePoint'},
        {field: 'daysOnStudy'},
        {field: 'analysisVisit'},
        {field: 'visitNumber'},
        {field: 'protocolScheduleTimepoint'},
        {field: 'method'},
        {field: 'resultValue'},
        {field: 'resultUnit'},
        {field: 'baselineValue'},
        {field: 'changeFromBaseline'},
        {field: 'percentChangeFromBaseline'},
        {field: 'baselineFlag'},
        {field: 'clinicallySignificant'},
        {field: 'dateOfLastDose', headerName: 'Date of last drug dose'},
        {field: 'lastDoseAmount', headerName: 'Last drug dose amount'},
        {field: 'atrialFibrillation', headerName: ''},
        {field: 'sinusRhythm', headerName: ''},
        {field: 'reasonNoSinusRhythm', headerName: 'Reason, no sinus rhythm'},
        {field: 'heartRhythm', headerName: ''},
        {field: 'heartRhythmOther', headerName: 'Heart rhythm, other'},
        {field: 'extraSystoles', headerName: ''},
        {field: 'specifyExtraSystoles', headerName: ''},
        {field: 'typeOfConduction', headerName: ''},
        {field: 'conduction', headerName: ''},
        {field: 'reasonAbnormalConduction', headerName: 'Reason, abnormal conduction'},
        {field: 'sttChanges', headerName: 'ST-T changes'},
        {field: 'stSegment', headerName: 'ST segment'},
        {field: 'wave', headerName: 'T-wave'},
        {field: 'beatGroupNumber', headerName: 'Beat group number'},
        {field: 'beatNumberWithinBeatGroup', headerName: 'Beat number within beat group'},
        {field: 'numberOfBeatsInAverageBeat', headerName: 'Number of beats in average beat'},
        {field: 'beatGroupLengthInSec', headerName: 'Beat group length (sec)'},
        {field: 'comment', headerName: 'Cardiologist comment'}
    ]);
}
