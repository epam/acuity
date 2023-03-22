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


import com.acuity.sdtm.transform.processor.SDTM_base.BaseConmedProcessor
import com.acuity.sdtm.transform.common.Version
import com.mongodb.BasicDBObject
import com.mongodb.client.MongoCollection
import com.mongodb.client.model.Filters
import org.bson.Document
import org.bson.conversions.Bson
import org.springframework.stereotype.Component

import static com.acuity.sdtm.transform.common.Study.*
import static com.acuity.sdtm.transform.util.Util.*
import static org.apache.commons.lang3.StringUtils.defaultString

/**
 * Created by knml167 on 08/06/2015.
 */
@Component
public class SDTM_1_1_ConmedProcessor extends BaseConmedProcessor implements SDTM_1_1_StudyCommonProcessor {

    @Override
    boolean isStudyProducesThisTypeOfEntity() {
        return !studyIn(STUDY001, STUDY002, STUDY003, STUDY004)
    }

    @Override
    public Version getVersion() {
        Version.SDTM_1_1
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
        if (studyIn(DR15)) {
            Filters.eq('CMCAT', 'GENERAL CONCOMITANT MEDICATION')
        } else if (studyIn(DR13, DR16)) {
            Filters.eq('CMCAT', 'CONCOMITANT MEDICATIONS LOG')
        } else if (studyIn(DR20, DR21)) {
            Filters.in('CMCAT',
                    'CONCOMITANT MEDICATIONS',
                    'OVERDOSE',
                    'SUBSEQUENT SYSTEMIC THERAPY'
            )
        } else if (studyIn(DR18)) {
            Filters.eq('CMCAT', 'GENERAL CONCOMITANT MEDICATIONS')
        } else if (studyIn(DR22)) {
            Filters.in('CMCAT',
                    'GENERAL CONCOMITANT MEDICATION',
                    'GENERAL CONCOMITANT MEDICATIONS',
                    'PREVIOUS AND CONCOMITANT MEDICATION',
                    'GENERAL CONMED',
                    'CONCOMITANT MEDICATIONS LOG',
                    'CONCOMITANT MEDICATIONS',
                    'BLOOD TRANSFUSION')
        } else if (studyIn(DR25)) {
            Filters.in('CMCAT',
                    'GENERAL CONCOMITANT MEDICATION',
                    'GENERAL CONCOMITANT MEDICATIONS',
                    'PREVIOUS AND CONCOMITANT MEDICATION',
                    'GENERAL CONMED',
                    'CONCOMITANT MEDICATIONS LOG',
                    'CONCOMITANT MEDICATIONS')
        } else {
            super.getDomainFilter()
        }
    }

    protected String getConmedAtcCode(Document cmRow, MongoCollection suppcm = null) {
        if (studyIn(DR1, DR2)) {
            getSuppFirstQval(cmRow, suppcm, 'ATCDECO2', 'CMSEQ')
        } else if (studyIn(DR9, DR13)) {
            getSuppFirstQval(cmRow, suppcm, 'CMATC2CD', 'CMSEQ')
        } else if (studyIn(DR15)) {
            getSuppFirstQval(cmRow, suppcm, 'ATC2CODE', 'CMSEQ')
        } else if (studyIn(DR16)) {
            getSuppFirstQval(cmRow, suppcm, 'CMATC1CD', 'CMSEQ')
        } else if (studyIn(DR13)) {
            getSuppFirstQval(cmRow, suppcm, 'CMATC2', 'CMSEQ')
        } else if (studyIn(DR12, DR6, DR11, DR17, DR20, DR18, DR21,
                DR22, DR23, DR24, DR25)) {
            null
        } else {
            super.getConmedAtcCode(cmRow, suppcm)
        }
    }

    @Override
    protected Bson fillMedicationName() {
        if (studyIn(DR18)) {
            new BasicDBObject('$ifNull', ['$CMTRT', '$CMTRT'])
        } else {
            return super.fillMedicationName()
        }
    }

    protected String getMedicationGroup(Document cmRow, MongoCollection suppcm) {
        if (studyIn(DR9, DR12, DR10, DR17, DR19, DR25)) {
            cmRow.CMCLAS
        } else if (studyIn(DR15)) {
            getSuppFirstQval(cmRow, suppcm, 'ATC2TERM', 'CMSEQ')
        } else if (studyIn(DR16, DR13)) {
            getSuppFirstQval(cmRow, suppcm, 'CMATC2', 'CMSEQ')
        } else if (studyIn(DR11, DR20, DR18, DR21, DR23,
                DR24)) {
            null
        } else {
            super.getMedicationGroup(cmRow, suppcm)
        }
    }

    @Override
    protected String getReasonForTreatment(Document row, MongoCollection<Document> suppcm) {
        if (studyIn(DR20)) {
            null
        } else if (studyIn(DR18, DR24)) {
            String suppQval = getSuppFirstQval(row, suppcm, 'CMREAS', 'CMSEQ')
            row.CMINDC ? defaultString(suppQval) + "($row.CMINDC)" : suppQval
        } else {
            super.getReasonForTreatment(row, suppcm)
        }
    }

    @Override
    protected String getDosePerAdministration(Document row, MongoCollection<Document> supp) {
        if (studyIn(DR20)) {
            row.CMCAT == 'OVERDOSE' ? row.CMDOSE : null
        } else {
            super.getDosePerAdministration(row, supp)
        }
    }

    @Override
    protected String getDoseUnit(Document row, MongoCollection<Document> supp) {
        if (studyIn(DR20)) {
            row.CMCAT == 'OVERDOSE' ? row.CMDOSU : null
        } else {
            super.getDoseUnit(row, supp)
        }
    }

    @Override
    protected String getDoseFrequency(Document row) {
        if (studyIn(DR20, DR21, DR22)) {
            null
        } else if (studyIn(DR18)) {
            parseDoseFrequencyToCdashString(row.CMDOSFRQ ? (row.CMDOSFRQ as String).split(";")[0] : null)
        } else {
            super.getDoseFrequency(row)
        }
    }

    @Override
    protected String getMedStartDate(Document row) {
        if (studyIn(DR22)) {
            return parseDateTrimDash(row?.CMSTDTC as String)
        } else {
            return super.getMedStartDate(row)
        }
    }

    @Override
    protected String getMedEndDate(Document row) {
        if (studyIn(DR22)) {
            return parseDateTrimDash(row?.CMENDTC as String)
        } else {
            return super.getMedEndDate(row)
        }
    }
}


