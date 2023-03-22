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


import com.acuity.sdtm.transform.processor.SDTM_base.BaseRecist1TargetProcessor
import com.acuity.sdtm.transform.common.Version
import com.mongodb.client.model.Filters
import org.bson.Document
import org.bson.conversions.Bson
import org.springframework.stereotype.Component

import static com.acuity.sdtm.transform.common.Study.*
import static com.acuity.sdtm.transform.util.Util.*
import static com.mongodb.client.model.Filters.*

@Component
public class SDTM_1_1_Recist1TargetProcessor extends BaseRecist1TargetProcessor implements SDTM_1_1_StudyCommonProcessor {

    @Override
    boolean isStudyProducesThisTypeOfEntity() {
        return !studyIn(DR15, DR19, DR23, DR25, STUDY001, STUDY002, STUDY003, STUDY004)
    }

    @Override
    String getSubject(String val) {
        if (studyIn(DR22)) {
            return parseSubjectSpecific(val)
        } else {
            return super.getSubject(val)
        }
    }

    @Override
    Bson getDomainFilter() {
        if (studyIn(DR9)) {
            and(eq('TRGRPID', 'TARGET'), eq('TRTESTCD', 'DIAMETER'))
        } else if (studyIn(DR12)) {
            eq('TRREFID', 'TLRECIST-01')
        } else if (studyIn(DR10, DR11, DR18, DR22)) {
            Filters.in('TRTESTCD', 'LDIAM', 'SAXIS')
        } else if (studyIn(DR17)) {
            and(eq('TRREFID', 'TLRECIST-01'), Filters.in('TRTESTCD', 'LDIAM', 'SAXIS'))
        } else if (studyIn(DR16)) {
            and(eq('TRGRPID', 'TARGET'), Filters.in('TRTESTCD', 'LDIAM', 'AXIAL'),
                    nin('TRSTRESN', null, ''))
        } else if (studyIn(DR20)) {
            and(Filters.in('TRTESTCD', 'LDIAM', 'SAXIS'),
                    nin('TRSTRESN', null, ''))
        } else if (studyIn(DR21)) {
            and(regex('TRLNKID', '^TL.*'), Filters.in('TRTESTCD', 'LDIAM', 'SAXIS'),
                    nin('TRSTRESN', null, ''))
        } else {
            super.getDomainFilter()
        }
    }

    @Override
    protected String getVisitDate(Document trRow, Document svRow) {
        if (studyIn(DR12, DR9, DR5, DR6, DR7, DR8, DR11)) {
            return trRow?.TRDTC
        } else if (studyIn(DR22)) {
            return parseDateTrimDash(svRow?.SVSTDTC as String)
        }
        return super.getVisitDate(trRow, svRow)
    }

    @Override
    protected String getLesionNumber(Document trRow) {
        if (studyIn(DR6, DR11, DR17, DR21)) {
            String lesionNumber = trRow.TRLINKID ?: trRow.TRLNKID
            return lesionNumber != null && lesionNumber.startsWith("TL") ? lesionNumber.substring(2) : lesionNumber
        }
        return super.getLesionNumber(trRow)
    }

    @Override
    protected String isLesionPresent(Document row) {
        if (studyIn(DR18)) {
            row.TRSTRESN ? YES : NO
        } else if (studyIn(DR22)) {
            row.TRTESTCD == 'LDIAM' ? YES : null
        } else {
            return super.isLesionPresent(row)
        }
    }

    @Override
    protected String getLesionScanDate(Document row) {
        if (studyIn(DR22)) {
            return parseDateTrimDash(row?.TRDTC as String)
        } else {
            return super.getLesionScanDate(row)
        }
    }

    @Override
    public Version getVersion() {
        return Version.SDTM_1_1
    }
}
