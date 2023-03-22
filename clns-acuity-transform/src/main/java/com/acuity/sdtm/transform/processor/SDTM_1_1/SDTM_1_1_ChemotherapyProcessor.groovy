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

package com.acuity.sdtm.transform.processor.SDTM_1_1


import com.acuity.sdtm.transform.processor.SDTM_base.BaseChemotherapyProcessor
import com.acuity.sdtm.transform.common.Version
import com.mongodb.client.MongoCollection
import com.mongodb.client.model.Filters
import org.bson.Document
import org.bson.conversions.Bson
import org.springframework.stereotype.Component

import static com.acuity.sdtm.transform.common.Study.*
import static com.acuity.sdtm.transform.util.Util.parseSubjectBySlash
import static com.acuity.sdtm.transform.util.Util.parseSubjectSpecific
import static com.mongodb.client.model.Filters.*

/**
 * Created by knml167 on 08/06/2015.
 */
@Component
public class SDTM_1_1_ChemotherapyProcessor extends BaseChemotherapyProcessor implements SDTM_1_1_StudyCommonProcessor {

    @Override
    boolean isStudyProducesThisTypeOfEntity() {
        return !studyIn(DR15, DR19, STUDY001, STUDY002, STUDY003, STUDY004)
    }

    @Override
    public Version getVersion() {
        return Version.SDTM_1_1
    }

    @Override
    String getSubject(String val) {
        if (studyIn(DR22)) {
            return parseSubjectSpecific(val)
        } else if (studyIn(DR23, DR25)) {
            return parseSubjectBySlash(val)
        } else {
            return super.getSubject(val)
        }
    }

    @Override
    Bson getDomainFilter() {
        if (studyIn(DR9)) {
            eq('CMCAT', 'PRIOR CANCER THERAPY LOG')
        } else if (studyIn(DR16, DR13)) {
            eq('CMCAT', 'CANCER THERAPY LOG')
        } else if (studyIn(DR12, DR21)) {
            eq('CMCAT', 'PRIOR SYSTEMIC THERAPY')
        } else if (studyIn(DR10, DR6)) {
            eq('CMCAT', 'PRIOR SYSTEMIC THERAPIES')
        } else if (studyIn(DR17)) {
            Filters.in('CMCAT', 'PREVIOUS CANCER THERAPY', 'CURRENT CANCER THERAPY')
        } else if (studyIn(DR18)) {
            eq('CMCAT', 'CANCER THERAPY')
        } else if (studyIn(DR22)) {
            and(eq('CMCAT', 'CANCER THERAPY'),
                    eq('CMSCAT', 'SYSTEMIC THERAPY'))
        } else if (studyIn(DR23)) {
            and(Filters.in('CMCAT', 'PRIOR DLBCL RELATED SYSTEMIC TREATMENT', 'CANCER THERAPY'),
                    regex('CMSCAT', '^(?:(?!RADIOTHERAPY).)*$'))
        } else if (studyIn(DR25)) {
            and(
                    Filters.in('CMCAT', 'PRIOR RELATED SYSTEMIC TREATMENT', 'CANCER THERAPY'),
                    or(
                            regex('CMSCAT', '^(?:(?!RADIOTHERAPY).)*$'), Filters.in('CMSCAT', null, '')))
        } else {
            super.getDomainFilter()
        }
    }

    @Override
    protected String getNumOfTreatmentCycles(Document row) {
        if (studyIn(DR20, DR21, DR23, DR25)) {
            null
        } else {
            return super.getNumOfTreatmentCycles(row)
        }
    }

    @Override
    protected String getMedicationName(Document cmRow) {
        if (studyIn(DR16, DR10, DR6)) {
            return cmRow.CMTRT
        }
        return super.getMedicationName(cmRow)
    }

    @Override
    protected String getBestResponseToTreatment(Document cmRow, MongoCollection suppcm) {
        if (studyIn(DR5, DR10, DR6)) {
            getSuppFirstQval(cmRow, suppcm, 'SYSTRESP', 'CMSEQ')
        } else if (studyIn(DR9, DR16)) {
            getSuppFirstQval(cmRow, suppcm, 'RESPONSE', 'CMSEQ')
        } else if (studyIn(DR11, DR17, DR20, DR21, DR24)) {
            getSuppFirstQval(cmRow, suppcm, 'CMBRESP', 'CMSEQ')
        } else if (studyIn(DR18, DR22, DR23, DR25)) {
            getSuppFirstQval(cmRow, suppcm, 'CXBRESP', 'CMSEQ')
        } else {
            super.getBestResponseToTreatment(cmRow, suppcm)
        }
    }

    @Override
    protected String getTreatmentStatus(Document cmRow, MongoCollection suppcm) {
        if (studyIn(DR7, DR8, DR4, DR14, DR10, DR6, DR12, DR11, DR17)) {
            return cmRow.CMSCAT
        } else if (studyIn(DR5)) {
            return getSuppFirstQval(cmRow, suppcm, 'SYSDISET', 'CMSEQ')
        } else if (studyIn(DR9, DR16)) {
            return getSuppFirstQval(cmRow, suppcm, 'STATUS', 'CMSEQ')
        } else if (studyIn(DR6)) {
            return getSuppFirstQval(cmRow, suppcm, 'REGNUM', 'CMSEQ')
        } else if (studyIn(DR20, DR18, DR21, DR23, DR24, DR25)) {
            null
        } else {
            super.getTreatmentStatus(cmRow, suppcm)
        }
    }

    @Override
    protected String getReasonForTherapyFailure(Document cmRow, MongoCollection suppcm) {
        if (studyIn(DR5, DR10, DR6)) {
            getSuppFirstQval(cmRow, suppcm, 'SYSTREAS', 'CMSEQ')
        } else if (studyIn(DR3, DR22)) {
            getSuppFirstQval(cmRow, suppcm, 'CMENREAS', 'CMSEQ')
        } else if (studyIn(DR12, DR11, DR20, DR18, DR21)) {
            getSuppFirstQval(cmRow, suppcm, 'CMTRPEND', 'CMSEQ')
        } else if (studyIn(DR17)) {
            null
        } else if (studyIn(DR23)) {
            getSuppFirstQval(cmRow, suppcm, 'CMTRTSR', 'CMSEQ')
        } else if (studyIn(DR25)) {
            getSuppFirstQval(cmRow, suppcm, 'PROGDTC', 'CMSEQ')
        } else {
            super.getReasonForTherapyFailure(cmRow, suppcm)
        }
    }

    @Override
    protected String getTherapyClass(Document cmRow, MongoCollection suppcm) {
        if (studyIn(DR7, DR8, DR5, DR4, DR14, DR10, DR6,
                DR12, DR11, DR20, DR23, DR25)) {
            return null
        } else if (studyIn(DR9)) {
            return cmRow.CMCLAS
        } else if (studyIn(DR13)) {
            getSuppFirstQval(cmRow, suppcm, 'CMATC2', 'CMSEQ')
        } else if (studyIn(DR17)) {
            getSuppFirstQval(cmRow, suppcm, 'THCLAS', 'CMSEQ')
        } else if (studyIn(DR24)) {
            return cmRow.CMCAT
        } else {
            super.getTherapyClass(cmRow, suppcm)
        }
    }

}
