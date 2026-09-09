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

import {Subject} from 'rxjs/Subject';
import {Injectable} from '@angular/core';
import {BaseEventService} from '../../common/event/BaseEventService';
import {Store} from '@ngrx/store';
import {ApplicationState} from '../../common/store/models/ApplicationState';
import {NewEventFiltersWereApplied, UpdateAvailableSubjects} from '../../common/store/actions/SharedStateActions';

/**
 * Sends events/obserables about filter changes
 */
@Injectable()
export class FilterEventService extends BaseEventService {
    /**
     * sends messages about the filter changes
     */
    public populationFilter: Subject<any> = new Subject<any>();
    public aesFilter: Subject<any> = new Subject<any>();
    public cerebrovascularFilter: Subject<any> = new Subject<any>();
    public cvotFilter: Subject<any> = new Subject<any>();
    public seriousAesFilter: Subject<any> = new Subject<any>();
    public labsFilter: Subject<any> = new Subject<any>();
    public exposureFilter: Subject<any> = new Subject<any>();
    public doseProportionalityFilter: Subject<any> = new Subject<any>();
    public pkResultOverallResponseFilter: Subject<any> = new Subject<any>();
    public lungFunctionFilter: Subject<any> = new Subject<any>();
    public exacerbationsFilter: Subject<any> = new Subject<any>();
    public cardiacFilter: Subject<any> = new Subject<any>();
    public vitalsFilter: Subject<any> = new Subject<any>();
    public patientDataFilter: Subject<any> = new Subject<any>();
    public renalFilter: Subject<any> = new Subject<any>();
    public liverFilter: Subject<any> = new Subject<any>();
    public tumourResponseFilter: Subject<any> = new Subject<any>();
    public conmedsFilter: Subject<any> = new Subject<any>();
    public doseFilter: Subject<any> = new Subject<any>();
    public recistFilter: Subject<any> = new Subject<any>();
    public deathFilter: Subject<any> = new Subject<any>();
    public doseDiscontinuationFilter: Subject<any> = new Subject<any>();
    public medicalHistoryFilter: Subject<any> = new Subject<any>();
    public surgicalHistoryFilter: Subject<any> = new Subject<any>();
    public liverDiagnosticInvestigationFilter: Subject<any> = new Subject<any>();
    public liverRiskFactorsFilter: Subject<any> = new Subject<any>();
    public alcoholFilter: Subject<any> = new Subject<any>();
    public nicotineFilter: Subject<any> = new Subject<any>();
    public cieventsFilter: Subject<any> = new Subject<any>();
    public biomarkersFilter: Subject<any> = new Subject<any>();
    public ctDnaFilter: Subject<any> = new Subject<any>();

    public populationFilterSubjectCount: Subject<number> = new Subject<number>();
    public eventFilterEventCount: Subject<number> = new Subject<number>();
    public sidePanelTab: Subject<any> = new Subject<any>();

    constructor(private _store: Store<ApplicationState>) {
        super();
    }

    dispatchNewEventFiltersWereApplied(): void {
        this._store.dispatch(new NewEventFiltersWereApplied());
    }

    setPopulationFilterSubjectCount(subjectCount: number): void {
        this.populationFilterSubjectCount.next(subjectCount);
    }

    setEventFilterEventCount(eventCount: number): void {
        this.eventFilterEventCount.next(eventCount);
    }

    setSidePanelTab(sidePanelTab: any): void {
        this.sidePanelTab.next(sidePanelTab);
    }

    setPopulationFilter(currentPopulationFilter: any): void {
        this.populationFilter.next(currentPopulationFilter);
    }

    setAesFilter(currentAesFilter: any): void {
        this.aesFilter.next(currentAesFilter);
    }

    setCerebrovascularFilter(currentFilter: any): void {
        this.cerebrovascularFilter.next(currentFilter);
    }

    setCvotFilter(currentCvotFilter: any): void {
        this.cvotFilter.next(currentCvotFilter);
    }

    setSeriousAesFilter(currentSeriousAesFilter: any): void {
        this.seriousAesFilter.next(currentSeriousAesFilter);
    }

    setLabsFilter(currentLabsFilter: any): void {
        this.labsFilter.next(currentLabsFilter);
    }

    setVitalsFilter(currentVitalsFilter: any): void {
        this.vitalsFilter.next(currentVitalsFilter);
    }

    setPatientDataFilter(currentPatientDataFilter: any): void {
        this.patientDataFilter.next(currentPatientDataFilter);
    }


    setExacerbationsFilter(currentExacerbationsFilter: any): void {
        this.exacerbationsFilter.next(currentExacerbationsFilter);
    }

    setTumourResponseFilter(currentTumourResponseFilter: any): void {
        this.tumourResponseFilter.next(currentTumourResponseFilter);
    }

    setLungFunctionFilter(currentLungFunctionFilter: any): void {
        this.lungFunctionFilter.next(currentLungFunctionFilter);
    }

    setCardiacFilter(currentCardiacFilter: any): void {
        this.cardiacFilter.next(currentCardiacFilter);
    }

    setRenalFilter(currentRenalFilter: any): void {
        this.renalFilter.next(currentRenalFilter);
    }

    setRecistFilter(currentRecistFilter: any): void {
        this.recistFilter.next(currentRecistFilter);
    }

    setLiverFunctionFilter(currentLiverFunctionFilter: any): void {
        this.liverFilter.next(currentLiverFunctionFilter);
    }

    setConmedsFilter(currentConmedsFilter: any): void {
        this.conmedsFilter.next(currentConmedsFilter);
    }

    setDoseFilter(currentDoseFilter: any): void {
        this.doseFilter.next(currentDoseFilter);
    }

    setDeathFilter(currentDeathFilter: any): void {
        this.deathFilter.next(currentDeathFilter);
    }

    setDoseDiscontinuationFilter(currentDoseDiscontinuationFilter: any): void {
        this.doseDiscontinuationFilter.next(currentDoseDiscontinuationFilter);
    }

    setMedicalHistoryFilter(currentMedicalHistoryFilter: any): void {
        this.medicalHistoryFilter.next(currentMedicalHistoryFilter);
    }

    setSurgicalHistoryFilter(currentSurgicalHistoryFilter: any): void {
        this.surgicalHistoryFilter.next(currentSurgicalHistoryFilter);
    }

    setLiverDiagnosticInvestigationFilter(currentLiverDiagnosticInvestigationFilter: any): void {
        this.liverDiagnosticInvestigationFilter.next(currentLiverDiagnosticInvestigationFilter);
    }

    setLiverRiskFactorsFilter(currentLiverRiskFactorsFilter: any): void {
        this.liverRiskFactorsFilter.next(currentLiverRiskFactorsFilter);
    }

    setAlcoholFilter(currentAlcoholFilter: any): void {
        this.alcoholFilter.next(currentAlcoholFilter);
    }

    setNicotineFilter(currentNicotineFilter: any): void {
        this.nicotineFilter.next(currentNicotineFilter);
    }

    setCIEventsFilter(currentCIEventsFilter: any): void {
        this.cieventsFilter.next(currentCIEventsFilter);
    }

    setBiomarkersFilter(currentBiomarkersFilter: any): void {
        this.biomarkersFilter.next(currentBiomarkersFilter);
    }

    setExposureFilter(currentExposureFilter: any): void {
        this.exposureFilter.next(currentExposureFilter);
    }

    setDoseProportionalityFilter(currentDoseProportionalityFilter: any): void {
        this.doseProportionalityFilter.next(currentDoseProportionalityFilter);
    }

    setPkResultOverallResponseFilter(currentPkResultOverallResponseFilter: any): void {
        this.pkResultOverallResponseFilter.next(currentPkResultOverallResponseFilter);
    }

    setCtDnaFilter(currentCtDnaFilter: any): void {
        this.ctDnaFilter.next(currentCtDnaFilter);
    }
}
