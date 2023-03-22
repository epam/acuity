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
import {AesBarLineChartHttpService} from './AesBarLineChartHttpService';
import {AesFiltersModel, PopulationFiltersModel} from '../../filters/module';
import {MockFilterModel, MockHttpClient} from '../../common/MockClasses';
import {Observable} from 'rxjs/Observable';
import Dataset = Request.Dataset;
import ChartGroupByOptionsFiltered = Request.ChartGroupByOptionsFiltered;
import AeGroupByOptions = InMemory.AeGroupByOptions;
import Ae = Request.Ae;
import TrellisedOvertime = InMemory.TrellisedOvertime;


describe('GIVEN AesBarLineChartHttpService', () => {

    const mockStudy = [{
        id: 1,
        type: 'DetectDataset',
        canView: true,
        rolePermissionMask: 3,
        viewPermissionMask: 3,
        autoGeneratedId: true,
        name: '',
        shortNameByType: '',
        supertype: '',
        typeForJackson: ''
    }];

    const mockXAxisOption = 'PT';
    const mockYAxisOption = 'COUNT_OF_SUBJECTS';
    const mockTrellising = [
        {
            category: 'NON_MANDATORY_TRELLIS',

            trellisOptions: ['Placebo', 'SuperDex 10 mg', 'SuperDex 20 mg'],
            trellisedBy: 'ARM'
        },
        {
            category: 'NON_MANDATORY_SERIES',
            trellisOptions: ['Empty', 'Grade 1', 'Grade 2', 'Grade 3', 'Grade 4', 'Grade 5'],
            trellisedBy: 'MAX_SEVERITY_GRADE'
        }];

    const mockSeries = [
        {
            trellisedBy: 'MAX_SEVERITY_GRADE',
            category: 'NON_MANDATORY_SERIES',
            trellisOptions: ['Empty', 'Grade 1', 'Grade 2', 'Grade 3', 'Grade 4', 'Grade 5']
        }];
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
                {provide: AesFiltersModel, useClass: MockFilterModel},
                {
                    provide: AesBarLineChartHttpService,
                    useClass: AesBarLineChartHttpService,
                    deps: [HttpClient, PopulationFiltersModel, AesFiltersModel]
                },
            ]
        });
    });

    describe('WHEN we get trellis options', () => {

        it('THEN the data is returned', async(inject([HttpClient, AesBarLineChartHttpService], (httpClient, httpService) => {
            const mockResponse = [{
                'trellisedBy': 'MAX_SEVERITY_GRADE',
                'category': 'NON_MANDATORY_SERIES',
                'trellisOptions': ['Empty', 'Grade 1', 'Grade 2', 'Grade 3', 'Grade 4', 'Grade 5']
            }, {
                'trellisedBy': 'ARM',
                'category': 'NON_MANDATORY_TRELLIS',
                'trellisOptions': ['Placebo', 'SuperDex 10 mg', 'SuperDex 20 mg']
            }];

            spyOn(httpClient, 'post').and.returnValue(Observable.of(mockResponse));

            httpService.getTrellisOptions(mockStudy, 'yAxisOption').subscribe((res) => {
                expect(res).toEqual(mockResponse);
            });
        })));
    });
});
