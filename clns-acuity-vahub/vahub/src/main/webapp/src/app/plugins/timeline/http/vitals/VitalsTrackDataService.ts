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
import {HttpClient} from '@angular/common/http';
import {Observable} from 'rxjs/Observable';

import {getServerPath} from '../../../../common/utils/Utils';
import {PopulationFiltersModel, VitalsFiltersModel} from '../../../../filters/dataTypes/module';
import {TrackData, TrackRequest} from '../IDataService';
import {SessionEventService} from '../../../../session/event/SessionEventService';
import {VitalsTrackDataTransformer} from './VitalsTrackDataTransformer';
import {BaseTrackDataService} from '../BaseTrackDataService';

@Injectable()
export class VitalsTrackDataService extends BaseTrackDataService {

    constructor(protected http: HttpClient,
                private vitalsTrackDataTransformer: VitalsTrackDataTransformer,
                private sessionEventService: SessionEventService,
                private populationFiltersModel: PopulationFiltersModel,
                private vitalsFiltersModel: VitalsFiltersModel) {
        super(http);
    }

    fetchData(request: TrackRequest): Observable<TrackData[]> {
        let result: Observable<TrackData[]>;
        switch (request.expansionLevel) {
            case 3:
                result = this.fetchDataForLevel2(request);
                break;
            case 2:
                result = this.fetchDataForLevel2(request);
                break;
            case 1:
                result = this.fetchDataForLevel1(request);
                break;
            default:
                break;
        }
        return result.map(trackData => this.extendTrackData(trackData, request));

    }

    fetchDataForLevel1(request: TrackRequest): Observable<TrackData[]> {
        const path = getServerPath('timeline', 'vitals', 'summaries');
        const postData: any = this.getRequestPayload(request);
        return this.fetchTimelineTrackData(request, path, postData)
            .map((response) => {
                return this.vitalsTrackDataTransformer.transformVitalsSummaryTrackData(response);
            });
    }

    fetchDataForLevel2(request: TrackRequest): Observable<TrackData[]> {
        const path = getServerPath('timeline', 'vitals', 'details');
        const postData: any = this.getRequestPayload(request);
        return this.fetchTimelineTrackData(request, path, postData)
            .map((response) => {
                return this.vitalsTrackDataTransformer.transformVitalsDetailsTrackData(response);
            });
    }

    private getRequestPayload(request: TrackRequest): any {
        const populationFilters = this.populationFiltersModel.transformFiltersToServer();
        populationFilters.subjectId = {values: request.trackSubjects};
        return {
            populationFilters,
            vitalsFilters: this.vitalsFiltersModel.transformFiltersToServer(),
            dayZero: request.dayZero,
            subjectIds: request.subjectIds,
            datasets: this.sessionEventService.currentSelectedDatasets
        };
    }
}
