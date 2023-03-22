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
import {HttpClientTestingModule, HttpTestingController} from '@angular/common/http/testing';
import {HttpClientModule} from '@angular/common/http';

import {AesSummaryHttpService} from './AesSummaryHttpService';

describe('GIVEN AEsSummaryAnyDataService', () => {

    beforeEach(() => {
        TestBed.configureTestingModule({
            imports: [HttpClientModule, HttpClientTestingModule],
            providers: [AesSummaryHttpService]
        });
    });

    describe('When get data for any category', () => {
        it('should match /aes/any url', async(inject([AesSummaryHttpService, HttpTestingController],
            (service: AesSummaryHttpService, backend: HttpTestingController) => {
                service.getData(null, 'any').subscribe();
                backend.match('/aes/any');
            })));
    });


});
