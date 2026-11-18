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
    /** Service blocks from the database, matched by id: 1 = 'Omics', 2 = Patient, 3 = AI and ML. */
    serviceLinks: ServiceLinkItem[];
    feedbackEmail: string;
    hasCompanyLogo = false;
    pathToImages: string;

    private readonly DEFAULT_TITLES: { [id: number]: string } = {
        1: "'Omics' Services",
        2: "Patient Services",
        3: "AI and Machine Learning Services"
    };
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

    /** Service block with the given id (1 = 'Omics', 2 = Patient, 3 = AI and ML). */
    serviceById(id: number): ServiceLinkItem | undefined {
        return this.serviceLinks.find(link => link.id === id);
    }

    /**
     * Makes sure the page always has the three blocks (ids 1..3): missing ones get the default title and an empty URL.
     */
    private normalizeServiceLinks(links: ServiceLinkItem[]): ServiceLinkItem[] {
        return [1, 2, 3].map(id => {
            const existing = (links || []).find(link => link.id === id);
            return {
                id,
                title: existing?.title || this.DEFAULT_TITLES[id],
                url: existing?.url || '',
                sortOrder: existing?.sortOrder || id
            };
        });
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
