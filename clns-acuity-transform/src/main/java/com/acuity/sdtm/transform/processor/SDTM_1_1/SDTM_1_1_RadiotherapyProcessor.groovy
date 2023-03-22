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


import com.acuity.sdtm.transform.processor.SDTM_base.BaseRadiotherapyProcessor
import com.acuity.sdtm.transform.common.Version
import com.acuity.sdtm.transform.util.Util
import com.mongodb.client.MongoCollection
import org.apache.commons.lang3.StringUtils
import org.bson.Document
import org.bson.conversions.Bson
import org.springframework.stereotype.Component

import static com.acuity.sdtm.transform.common.Study.*
import static com.acuity.sdtm.transform.util.Util.*
import static com.mongodb.client.model.Filters.eq

/**
 * Created by knml167 on 08/06/2015.
 */
@Component
public class SDTM_1_1_RadiotherapyProcessor extends BaseRadiotherapyProcessor implements SDTM_1_1_StudyCommonProcessor {

    @Override
    boolean isStudyProducesThisTypeOfEntity() {
        return !studyIn(DR15, DR12, DR19, STUDY001, STUDY002, STUDY003)
    }

    @Override
    public Version getVersion() {
        return Version.SDTM_1_1
    }

    @Override
    String getSubject(String val) {
        if (studyIn(DR22)) {
            return parseSubjectSpecific(val)
        } else if (studyIn(DR25)) {
            return parseSubjectBySlash(val)
        } else {
            return super.getSubject(val)
        }
    }

    @Override
    Bson getDomainFilter() {
        if (studyIn(DR9)) {
            eq('CMCAT', 'PRIOR RADIATION LOG')
        } else if (studyIn(DR16, DR13)) {
            eq('CMCAT', 'RADIATION LOG')
        } else if (studyIn(DR20)) {
            eq('CMCAT', 'RADIATION')
        } else if (studyIn(DR18)) {
            eq('CMSCAT', 'RADIOTHERAPY')
        } else if (studyIn(DR21)) {
            eq('CMCAT', 'PRIOR RADIATION')
        } else if (studyIn(DR25)) {
            eq('CMTRT', 'RADIOTHERAPY')
        } else {
            super.getDomainFilter()
        }
    }

    @Override
    String getNumberOfFractionDoses(Document cmRow, MongoCollection suppcm) {
        if (studyIn(DR9, DR16)) {
            getSuppFirstQval(cmRow, suppcm, 'DOSES', 'CMSEQ')
        } else if (studyIn(DR20)) {
            null
        } else {
            super.getNumberOfFractionDoses(cmRow, suppcm)
        }
    }

    @Override
    String getRadiotherapySiteOrRegion(Document cmRow, MongoCollection suppcm) {
        if (studyIn(DR9)) {
            getSuppFirstQval(cmRow, suppcm, 'REGION', 'CMSEQ')
        } else if (studyIn(DR16)) {
            getSuppFirstQval(cmRow, suppcm, 'RADIOSIT', 'CMSEQ')
        } else if (studyIn(DR13)) {
            getSuppFirstQval(cmRow, suppcm, 'CXRDTXT', 'CMSEQ')
        } else if (studyIn(DR21)) {
            cmRow.CMINDC
        } else {
            super.getRadiotherapySiteOrRegion(cmRow, suppcm)
        }
    }

    @Override
    String getTreatmentStatus(Document cmRow, MongoCollection suppcm) {
        if (studyIn(DR9)) {
            cmRow.CMTRT
        } else if (studyIn(DR16)) {
            cmRow.CMSCAT
        } else if (studyIn(DR20, DR21, DR25)) {
            null
        } else {
            super.getTreatmentStatus(cmRow, suppcm)
        }
    }

    @Override
    String getRadiotherapyGiven(Document cmRow, MongoCollection suppcm) {
        if (studyIn(DR9)) {
            getSuppFirstQval(cmRow, suppcm, 'CHEMORA2', 'CMSEQ')
        } else if (studyIn(DR13, DR16)) {
            Util.YES
        } else if (studyIn(DR20)) {
            StringUtils.isNotBlank(cmRow.CMSTDTC as String) ? Util.YES : Util.NO
        } else {
            super.getRadiotherapyGiven(cmRow, suppcm)
        }
    }

    @Override
    String getDose(Document row, MongoCollection supp) {
        if (studyIn(DR20, DR21)) {
            String fractionDose = row.CMDOSE
            if (fractionDose?.isNumber()) {
                return (fractionDose as Double) / 100
            }
        } else if (studyIn(DR25)) {
            if (row.CMDOSU == 'Gy') {
                return row.CMDOSE
            } else {
                return null
            }
        }
        return super.getDose(row, supp)
    }

    @Override
    String getRadioTherapyStartDate(Document row) {
        if (studyIn(DR22)) {
            return parseDateTrimDash(row?.CMSTDTC as String)
        } else {
            return super.getRadioTherapyStartDate(row)
        }
    }

    @Override
    String getRadioTherapyStopDate(Document row) {
        if (studyIn(DR22)) {
            return parseDateTrimDash(row?.CMENDTC as String)
        } else {
            return super.getRadioTherapyStopDate(row)
        }
    }
}
