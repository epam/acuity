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

import {async, inject, TestBed} from '@angular/core/testing';
import {HttpClient} from '@angular/common/http';
import {HttpClientTestingModule} from '@angular/common/http/testing';
import {PopulationHttpService} from './PopulationHttpService';
import {PopulationFiltersModel} from '../../filters/module';

import * as utils from '../../common/utils/Utils';
import {MockFilterModel, MockHttpClient} from '../../common/MockClasses';
import {Observable} from 'rxjs/Observable';
import Dataset = Request.Dataset;

describe('GIVEN PopulationHttpService', () => {

    const mockStudy = [{
        id: 1,
        type: 'DetectDataset',
        canView: true,
        rolePermissionMask: 3,
        viewPermissionMask: 3,
        autoGeneratedId: true,
        name: '',
        shortNameByType: '',
        supertype: ''
    }];

    const mockXAxisOption = 'ACTUAL_TREATMENT_ARM';
    const mockYAxisOption = 'COUNT_OF_SUBJECTS';
    const mockTrellising = [{category: 'NON_MANDATORY_SERIES', trellisedBy: 'STUDY_ID', trellisOptions: ['DummyTFL']}];

    const mockSeries = [{trellisedBy: 'STUDY_ID', category: 'NON_MANDATORY_SERIES', trellisOptions: ['DummyTFL']}];
    const mockSelection = {
        maxX: 1.4224283305227656,
        maxY: 22,
        minX: -0.4662731871838111,
        minY: 0
    };
    beforeEach(() => {
        TestBed.configureTestingModule({
            imports: [HttpClientTestingModule],
            providers: [
                {provide: HttpClient, useClass: MockHttpClient},
                {provide: PopulationFiltersModel, useClass: MockFilterModel},
                {
                    provide: PopulationHttpService,
                    useClass: PopulationHttpService,
                    deps: [HttpClient, PopulationFiltersModel]
                },
            ]
        });
    });

    describe('WHEN we get trellis options', () => {

        it('THEN the data is returned', async(inject([HttpClient, PopulationHttpService], (httpClient, httpService) => {
            const mockResponse = [{
                'trellisedBy': 'STUDY_ID',
                'category': 'NON_MANDATORY_SERIES',
                'trellisOptions': ['DummyTFL']
            }, {
                'trellisedBy': 'STUDY_NAME',
                'category': 'NON_MANDATORY_SERIES',
                'trellisOptions': ['DummyTFL']
            }];

            spyOn(httpClient, 'post').and.returnValue(Observable.of(mockResponse));

            httpService.getTrellisOptions(mockStudy, 'yAxisOption').subscribe((res) => {
                expect(res).toEqual(mockResponse);
            });
        })));
    });

    describe('WHEN we get bar chart data', () => {

        it('THEN the data is returned', async(inject([HttpClient, PopulationHttpService], (httpClient, httpService) => {
            const mockResponse = [{
                'trellisedBy': [],
                'data': [{
                    'name': 'DummyTFL',
                    'color': '#88CCEE',
                    'categories': ['Placebo', 'SuperDex 10 mg', 'SuperDex 20 mg'],
                    'series': [{category: 'Placebo', rank: 1, value: 56}]
                }]
            }];

            spyOn(httpClient, 'post').and.returnValue(Observable.of(mockResponse));
            const expectedResponse = [{
                'plotType': 'STACKED_BARCHART',
                'trellising': [],
                'data': [{
                    'name': 'DummyTFL',
                    'color': '#88CCEE',
                    'categories': ['Placebo', 'SuperDex 10 mg', 'SuperDex 20 mg'],
                    'series': [{category: 'Placebo', rank: 1, value: 56}]
                }]
            }];
            httpService.getData(mockStudy, mockXAxisOption, mockYAxisOption, mockTrellising).subscribe((res) => {
                expect(res.toJS()).toEqual(expectedResponse);
            });
        })));
    });

    describe('WHEN we get bar chart selection data', () => {

        it('THEN the data is returned', async(inject([HttpClient, PopulationHttpService], (httpClient, httpService) => {
            const mockResponse = {
                'eventIds': ['DummyTFL-6974311790', 'DummyTFL-2329157638'],
                'subjectIds': ['DummyTFL-6974311790', 'DummyTFL-2329157638'],
                'totalEvents': null,
                'totalSubjects': 197
            };

            spyOn(httpClient, 'post').and.returnValue(Observable.of(mockResponse));

            httpService.getSelectionDetail(mockStudy, mockXAxisOption, mockYAxisOption, mockTrellising, mockSeries, mockSelection)
                .subscribe((res) => {
                    expect(res).toEqual(mockResponse);
                });
        })));
    });

    describe('WHEN we get details on demand data', () => {

        it('THEN the data is returned', async(inject([HttpClient, PopulationHttpService],
            (httpClient, httpService: PopulationHttpService) => {
                const subjectIds = ['1', '2'];
                const mockResponse = [{subjectId: 'Subj-1'}];

                spyOn(httpClient, 'post').and.returnValue(Observable.of(mockResponse));

                httpService.getDetailsOnDemand(<Dataset[]> mockStudy, subjectIds, 0,
                    10, null)
                    .subscribe((res) => {
                        expect(res).toEqual(mockResponse);
                    });
            })));
    });

    describe('WHEN we download all the details on demand data', () => {

        it('THEN the data is returned', async(inject([HttpClient, PopulationHttpService],
            (httpClient, httpService: PopulationHttpService) => {
                spyOn(utils, 'downloadData');
                const mockResponse = [{subjectId: 'Subj-1'}];

                spyOn(httpClient, 'post').and.returnValue(Observable.of(mockResponse));

                httpService.downloadAllDetailsOnDemandData(<Request.AcuityObjectIdentityWithPermission[]> mockStudy);

                expect(utils.downloadData).toHaveBeenCalled();
            })));
    });
});