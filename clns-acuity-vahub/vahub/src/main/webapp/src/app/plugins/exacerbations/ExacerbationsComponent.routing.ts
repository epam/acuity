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

import { Routes } from '@angular/router';
import {ExacerbationsOverTimeComponent} from './exacerbationsOverTime/ExacerbationsOverTimeComponent';
import {ExacerbationsOnSetComponent} from './exacerbationsonset/ExacerbationsOnSetComponent';
import { ExacerbationsCountComponent } from './exacerbationscounts/ExacerbationsCountComponent';

export const exacerbationsRoutes: Routes = [
    { path: '', pathMatch: 'full', redirectTo: 'exacerbations-counts'},
    { path: 'exacerbations-over-time', component: ExacerbationsOverTimeComponent },
    { path: 'exacerbations-onset', component: ExacerbationsOnSetComponent },
    { path: 'exacerbations-counts', component: ExacerbationsCountComponent}
];

export const exacerbationsRoutingComponents = [
    ExacerbationsOverTimeComponent,
    ExacerbationsOnSetComponent,
    ExacerbationsCountComponent
];
