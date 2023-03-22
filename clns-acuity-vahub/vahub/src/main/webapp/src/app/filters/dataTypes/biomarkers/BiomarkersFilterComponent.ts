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

import {Component, OnInit, OnDestroy, AfterViewInit, Output, EventEmitter} from '@angular/core';
import {FilterEventService} from '../../event/FilterEventService';
import {SessionEventService} from '../../../session/event/SessionEventService';
import {AbstractDataTypeFilter} from '../AbstractDataTypeFilter';
import {BiomarkersFiltersModel} from './BiomarkersFiltersModel';

@Component({
    selector: 'biomarkers-filter',
    template: `<filter-collection [filtersModel]="filtersModel" (clearAll)="onClearAll()"
                                  (exportFilters)="onExportFilters($event)"></filter-collection>`
})
export class BiomarkersFilterComponent extends AbstractDataTypeFilter implements OnInit, OnDestroy, AfterViewInit {
    @Output()
    public clearAll = new EventEmitter<void>();
    @Output()
    public exportFilters = new EventEmitter<MouseEvent>();

    constructor(
        public filtersModel: BiomarkersFiltersModel,
        protected filterEventService: FilterEventService,
        protected sessionEventService: SessionEventService
    ) {
        super();
        this.subKey = 'BiomarkersFilterComponent';
    }

    ngAfterViewInit(): void {
        this.afterViewInit();
    }

    ngOnInit(): void {
        this.onInit();
    }

    ngOnDestroy(): void {
        this.onDestroy();
    }
}
