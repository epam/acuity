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

import com.acuity.sdtm.transform.processor.SDTM_base.BaseRecist2Processor
import com.mongodb.client.MongoCollection
import org.bson.Document
import org.bson.conversions.Bson
import org.springframework.stereotype.Component

import static com.acuity.sdtm.transform.common.Study.*
import static com.acuity.sdtm.transform.util.Util.*
import static com.mongodb.client.model.Filters.*

@Component
class SDTM_1_1_Recist2Processor extends BaseRecist2Processor implements SDTM_1_1_StudyCommonProcessor {

    @Override
    String getSubject(String val) {
        if (studyIn(DR22)) {
            return parseSubjectSpecific(val)
        } else if (studyIn(DR23)) {
            return parseSubjectBySlash(val)
        } else {
            return super.getSubject(val)
        }
    }

    @Override
    boolean isStudyProducesThisTypeOfEntity() {
        !studyIn(DR15, DR19, DR25, STUDY001, STUDY002, STUDY003, STUDY004)
    }

    @Override
    protected String getReasonRECISTAndInvestigatorAssessmentsDiffer(Document rsRow, Document rsInvopresRow,
                                                                     MongoCollection supprs) {
        if (studyIn(DR13)) {
            rsInvopresRow ? getSuppFirstQval(rsInvopresRow, supprs, 'RESDIFFE', 'RSSEQ') : null
        } else if (studyIn(DR17)) {
            //rs - rsInvopres
            getSuppFirstQval(rsRow, supprs, 'DIFFSP', 'RSSEQ')
        } else if (studyIn(DR16)) {
            getSuppFirstQval(rsRow, supprs, 'RESDIFFE', 'RSSEQ')
        } else if (studyIn(DR20, DR18, DR21, DR22, DR23, DR24)) {
            null
        } else {
            super.getReasonRECISTAndInvestigatorAssessmentsDiffer(rsRow, rsInvopresRow, supprs)
        }
    }

    @Override
    protected getInvawrec(Document rsRow, Document rsInvopresRow, MongoCollection supprs) {
        if (studyIn(DR17)) {
            parseYesNo(getSuppFirstQval(rsRow, supprs, 'INVAGREE', 'RSSEQ'))
        } else if (studyIn(DR16)) {
            parseYesNo(getSuppFirstQval(rsRow, supprs, 'INVESRES', 'RSSEQ'))
        } else if (studyIn(DR18, DR24)) {
            getSuppFirstQval(rsRow, supprs, 'INVAGREE', 'RSSEQ')
        } else if (studyIn(DR20, DR21, DR22, DR23)) {
            null
        } else {
            super.getInvawrec(rsRow, rsInvopresRow, supprs)
        }
    }

    @Override
    protected String getInvestigatorOpinionOfPatientStatus(Document rsRow, Document rsInvopresRow, MongoCollection supprs) {
        if (studyIn(DR17, DR20, DR18, DR21, DR23, DR24)) {
            null
        } else if (studyIn(DR16)) {
            getSuppFirstQval(rsRow, supprs, 'INVESOPN', 'RSSEQ')
        } else if (studyIn(DR22)) {
            rsInvopresRow?.RSSTRESC
        } else {
            super.getInvestigatorOpinionOfPatientStatus(rsRow, rsInvopresRow, supprs)
        }
    }

    @Override
    protected String getVisitDate(Document rsRow, Document svRow, Document tuRow) {
        if (studyIn(DR9, DR5, DR6, DR7, DR8, DR10, DR11)) {
            rsRow.RSDTC
        } else if (DR22) {
            return parseDateTrimDash(svRow?.SVSTDTC as String)
        } else {
            super.getVisitDate(rsRow, svRow, tuRow)
        }
    }

    @Override
    protected Bson getTuFilter(Document row, Map<String, String> tuSchema) {
        if (studyIn(DR16)) {
            and(
                    eq(tuSchema.USUBJID, row.USUBJID),
                    eq(tuSchema.TUTESTCD, 'TUMIDENT'),
                    eq(tuSchema.TUSTRESC, 'NEW'),
                    eq(tuSchema.VISITNUM, asDouble(row.VISITNUM)),
                    nin(tuSchema.TULOC, null, ''),
                    nin(tuSchema.TUDTC, null, ''),
            )
        } else {
            super.getTuFilter(row, tuSchema)
        }
    }

    @Override
    protected String getRecistResponse(Document row, MongoCollection rs) {
        if (studyIn(DR22)) {
            Document rsRow = rs.find(and(
                    eq('USUBJID', row.USUBJID),
                    eq('RSTESTCD', 'RSALL')
            )).first()
            return rsRow?.RSSTRESC ?: null
        } else {
            return super.getRecistResponse(row, rs)
        }
    }

    @Override
    protected String getLesionScanDate(Document row) {
        if (studyIn(DR22)) {
            return parseDateTrimDash(row?.TUDTC as String)
        } else {
            return super.getLesionScanDate(row)
        }
    }

    @Override
    protected Bson getRsInvopresFilter(Document row, Map<String, String> rsSchema) {
        if (studyIn(DR20, DR18, DR21, DR23)) {
            not(exists("USUBJID"))
        } else if (studyIn(DR22)) {
            and(
                    eq(rsSchema.USUBJID, row.USUBJID),
                    eq(rsSchema.RSTESTCD, 'NRADPROG'),
                    eq(rsSchema.VISITNUM, row.VISITNUM))
        } else {
            super.getRsInvopresFilter(row, rsSchema)
        }
    }
}
