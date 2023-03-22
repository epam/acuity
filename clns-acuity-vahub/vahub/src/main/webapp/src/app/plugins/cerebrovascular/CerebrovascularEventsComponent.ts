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

import {AfterViewInit, Component, ViewChild} from '@angular/core';
import {AEsComponent} from '../aes/AEsComponent';
import {CollapseTabsDirective} from '../../common/directives/CollapseTabsDirective';
import {PluginsService} from '../PluginsService';

@Component({
    templateUrl: '../aes/AEsComponent.html'
})

export class CerebrovascularEventsComponent extends AEsComponent implements AfterViewInit {
    @ViewChild(CollapseTabsDirective) collapseTabsDirective;

    constructor(public pluginsService: PluginsService) {
        super(pluginsService);
    }

    ngAfterViewInit(): void {
        super.ngAfterViewInit();
    }
}
