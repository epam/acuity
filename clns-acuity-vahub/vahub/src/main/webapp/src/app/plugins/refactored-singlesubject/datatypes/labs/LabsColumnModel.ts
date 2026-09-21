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
import {List} from 'immutable';
import {AbstractColumnModel} from '../AbstractColumnModel';

@Injectable()
export class LabsColumnModel extends AbstractColumnModel {
    _columnDefs: List<any> = List([
        {
            headerName: 'Study',
            groupId: 'study',
            children: [
                // anchor column - always visible, including when the group is collapsed
                {headerName: 'Subject id', field: 'subjectId', columnGroupShow: null},
                {headerName: 'Study id', field: 'studyId', columnGroupShow: 'open'},
                {headerName: 'Study Part id', field: 'studyPart', columnGroupShow: 'open'},
                {
                    headerName: 'Visit number', field: 'visitNumber',
                    comparator: this.numericComparator, filter: 'agNumberColumnFilter', columnGroupShow: 'open'
                },
                {headerName: 'Measurement time point', field: 'measurementTimePoint', columnGroupShow: 'open'},
                {
                    headerName: 'Days on study', field: 'daysOnStudy',
                    comparator: this.numericComparator, filter: 'agNumberColumnFilter', columnGroupShow: 'open'
                }
            ]
        },
        {
            headerName: 'Lab Results',
            groupId: 'labResults',
            children: [
                // anchor column - always visible, including when the group is collapsed
                {headerName: 'Measurement name', field: 'measurementName', columnGroupShow: null},
                {headerName: 'Measurement category', field: 'measurementCategory', columnGroupShow: 'open'},
                {
                    headerName: 'Analysis visit', field: 'analysisVisit',
                    comparator: this.numericComparator, filter: 'agNumberColumnFilter', columnGroupShow: 'open'
                },
                {headerName: 'Protocol schedule timepoint', field: 'protocolScheduleTimepoint', columnGroupShow: 'open'},
                {
                    headerName: 'Result value', field: 'resultValue', cellStyle: this.colourIfOutOfRange,
                    comparator: this.numericComparator, filter: 'agNumberColumnFilter', columnGroupShow: 'open'
                },
                {headerName: 'Result unit', field: 'resultUnit', columnGroupShow: 'open'},
                {headerName: 'Value dipstick', field: 'valueDipstick', columnGroupShow: 'open'},
                {
                    headerName: 'Baseline value', field: 'baselineValue',
                    comparator: this.numericComparator, filter: 'agNumberColumnFilter', columnGroupShow: 'open'
                },
                {
                    headerName: 'Change from baseline', field: 'changeFromBaseline',
                    comparator: this.numericComparator, filter: 'agNumberColumnFilter', columnGroupShow: 'open'
                },
                {
                    headerName: 'Percent change from baseline', field: 'percentChangeFromBaseline',
                    comparator: this.numericComparator, filter: 'agNumberColumnFilter', columnGroupShow: 'open'
                },
                {headerName: 'Baseline flag', field: 'baselineFlag', columnGroupShow: 'open'},
                {
                    headerName: 'Ref range norm value', field: 'refRangeNormValue',
                    comparator: this.numericComparator, filter: 'agNumberColumnFilter', columnGroupShow: 'open'
                },
                {
                    headerName: 'Times upper ref value', field: 'timesUpperRefValue',
                    comparator: this.numericComparator, filter: 'agNumberColumnFilter', columnGroupShow: 'open'
                },
                {
                    headerName: 'Times lower ref value', field: 'timesLowerRefValue',
                    comparator: this.numericComparator, filter: 'agNumberColumnFilter', columnGroupShow: 'open'
                },
                {
                    headerName: 'Lower ref range value', field: 'lowerRefRangeValue',
                    comparator: this.numericComparator, filter: 'agNumberColumnFilter', columnGroupShow: 'open'
                },
                {
                    headerName: 'Upper ref range value', field: 'upperRefRangeValue',
                    comparator: this.numericComparator, filter: 'agNumberColumnFilter', columnGroupShow: 'open'
                },
                {headerName: 'Device name', field: 'deviceName', columnGroupShow: 'open'},
                {headerName: 'Device version', field: 'deviceVersion', columnGroupShow: 'open'},
                {headerName: 'Device type', field: 'deviceType', columnGroupShow: 'open'},
                {headerName: 'Source type', field: 'sourceType', columnGroupShow: 'open'}
            ]
        }
    ]);

    private colourIfOutOfRange(params: any): any {
        if (params.data && (+params.value < +params.data.lowerRefRangeValue || +params.value > +params.data.upperRefRangeValue)) {
            return {backgroundColor: AbstractColumnModel.WARNING_COLOUR};
        }
        return {};
    }

    // The DoD backend serialises every value (including numeric Double/Integer fields) to a
    // formatted String, so ag-Grid's default comparator sorts them lexicographically
    // (e.g. "10" before "2"). Parse to float for these numeric columns instead.
    private numericComparator(valueA: any, valueB: any): number {
        const a = parseFloat(valueA);
        const b = parseFloat(valueB);
        const aIsNumber = !isNaN(a);
        const bIsNumber = !isNaN(b);
        if (!aIsNumber && !bIsNumber) {
            return 0;
        }
        if (!aIsNumber) {
            return -1;
        }
        if (!bIsNumber) {
            return 1;
        }
        return a - b;
    }
}
