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
import { HttpClient } from '@angular/common/http';
import {Observable} from 'rxjs/Observable';
import {getServerPath} from '../common/utils/Utils';
import ConfigurationResponse = Request.ConfigurationResponse;
import BrandingProperties = Request.BrandingProperties;
import QnaMakerProperties = Request.QnaMakerProperties;

/** Service block shown on the home page: title and URL managed in the admin application. */
export interface ServiceLinkItem {
    id: number;
    title: string;
    url: string;
    sortOrder: number;
}

/** Additional properties for the configuration response: home page content from the database. */
export interface ConfigurationResponseExtended {
    serviceLinks?: ServiceLinkItem[];
    feedbackEmail?: string;
}

@Injectable()
export class ConfigurationService {
    public brandingProperties: BrandingProperties;
    public cbioportalUrl: string;
    public pathToStatic: string;
    public qnaMakerProperties: QnaMakerProperties;
    public serviceLinks: ServiceLinkItem[] = [];
    public feedbackEmail: string;

    constructor(private http: HttpClient) {
    }

    getConfigurationProperties(): Observable<ConfigurationResponse> {
        return this.http.get(getServerPath('configuration', 'properties')).map((res: any) => {
            this.setProperties(res);
            return res;
        });
    }

    setProperties(properties: any): void {
        this.brandingProperties = properties.branding;
        this.pathToStatic = this.brandingProperties.pathToImages || 'images';
        this.cbioportalUrl = properties.cbioportalUrl;
        this.qnaMakerProperties = properties.qnaMaker;
        this.serviceLinks = properties.serviceLinks || [];
        this.feedbackEmail = properties.feedbackEmail || '';
    }
}
