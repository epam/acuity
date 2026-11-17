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

import {Component} from '@angular/core';
import {Router} from '@angular/router';
import {ConfigurationService, ServiceLinkItem} from '../configuration/ConfigurationService';

@Component({
    selector: 'home',
    templateUrl: 'HomeComponent.html',
    styleUrls: ['./HomeComponent.css'],
    standalone: false
})
export class HomeComponent {
    loading: boolean;
    /** Service blocks from the database in display order: 0 = 'Omics', 1 = Patient, 2 = AI and ML. */
    serviceLinks: ServiceLinkItem[];
    feedbackEmail: string;
    hasCompanyLogo = false;
    pathToImages: string;

    private readonly DEFAULT_TITLES = [
        "'Omics' Services",
        "Patient Services",
        "AI and Machine Learning Services"
    ];
    private readonly DEFAULT_EMAIL = "support@digitalecmt.org";

    constructor(private router: Router,
                private configurationService: ConfigurationService) {
        this.pathToImages = configurationService.pathToStatic;
        this.checkIfHasCompany(configurationService.pathToStatic);
        this.serviceLinks = this.normalizeServiceLinks(configurationService.serviceLinks);
        this.feedbackEmail = configurationService.feedbackEmail || this.DEFAULT_EMAIL;
    }

    public navigate(url: string): void {
        this.router.navigateByUrl(url);
    }

    /** Service block at the given position, or undefined if the table has no such row. */
    serviceAt(index: number): ServiceLinkItem | undefined {
        return this.serviceLinks[index];
    }

    /**
     * Fills missing rows with default titles and empty URLs, so that the page always shows three blocks.
     */
    private normalizeServiceLinks(links: ServiceLinkItem[]): ServiceLinkItem[] {
        const result: ServiceLinkItem[] = [];
        for (let i = 0; i < this.DEFAULT_TITLES.length; i++) {
            const existing = links && links[i];
            result.push({
                id: existing?.id || 0,
                title: existing?.title || this.DEFAULT_TITLES[i],
                url: existing?.url || '',
                sortOrder: i + 1
            });
        }
        return result;
    }

    getTooltipText(hasOption: boolean): string {
        return !hasOption
            ? `You are not subscribed to this service, please contact \n${this.feedbackEmail} for more information` : '';
    }

    private checkIfHasCompany(path: string): void {
        const imageData = new Image();

        imageData.onload = () => {
            if (this.hasCompanyLogo) {
                return;
            }
            this.hasCompanyLogo = true;
        };
        imageData.onerror = () => {
            this.hasCompanyLogo = false;
        };
        imageData.src = `/assets/${path}/company.png`;
    }
}
