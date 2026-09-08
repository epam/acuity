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
export class CvotColumnModel extends AbstractColumnModel {
    _columnDefs: List<ColDef> = List([
        {field: 'studyId', headerName: 'Study ID'},
        {field: 'studyPart', headerName: 'Study Part'},
        {field: 'subjectId', headerName: 'Subject ID'},
        {field: 'aeNumber', headerName: 'Associated AE No.'},
        {field: 'startDate', headerName: 'Event Start Date'},
        {field: 'term', headerName: 'Event Term'},
        {field: 'category1', headerName: 'Event Category 1'},
        {field: 'category2', headerName: 'Event Category 2'},
        {field: 'category3', headerName: 'Event Category 3'},
        {field: 'description1', headerName: 'Event Description 1'},
        {field: 'description2', headerName: 'Event Description 2'},
        {field: 'description3', headerName: 'Event Description 3'}
    ]);
}
