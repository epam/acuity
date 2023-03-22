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

import com.acuity.sdtm.transform.processor.SDTM_base.BaseDemographyProcessor
import com.mongodb.client.MongoCollection
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
public class SDTM_1_1_DemographyProcessor extends BaseDemographyProcessor implements SDTM_1_1_StudyCommonProcessor {

    @Override
    boolean isStudyProducesThisTypeOfEntity() {
        return !studyIn(STUDY001, STUDY002, STUDY003, STUDY004)
    }

    @Override
    Bson getDomainFilter() {
        return super.getDomainFilter()
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
    protected String getPart(Document row, MongoCollection<Document> supp) {
        if (studyIn(DR10)) {
            return getSuppFirstQval(row, supp, 'PHASE')
        } else if (studyIn(DR16)) {
            return row.ARM
        } else if (studyIn(DR20)) {
            if (row.ACTARM == 'SCREEN FAILURE') {
                return 'SCREEN FAIL'
            }
            def m = row.ACTARM =~ /PHASE (.+?) COHORT/
            if (m) {
                return m[0][1]
            }
            return null
        } else if (studyIn(DR18)) {
            return row.ACTARM
        } else if (studyIn(DR21)) {
            def m = row.ACTARM =~ /PART A/
            if (m) {
                return 'A'
            } else if (row.ACTARM == 'SCREEN FAILURE') {
                return 'SCREEN FAIL'
            } else {
                return null
            }
        } else if (studyIn(DR22)) {
            return 'A / B'
        } else if (studyIn(DR23)) {
            return getSuppFirstQval(row, supp, 'PHASE')
        }
        return super.getPart(row, supp)
    }

    @Override
    protected String getVisitDate(Document dmRow, MongoCollection suppdm) {
        if (studyIn(DR9, DR15, DR12, DR11, DR17, DR20, DR18,
                DR3, DR21, DR23, DR24, DR25)) {
            return dmRow.RFICDTC
        } else if (studyIn(DR16)) {
            return getSuppFirstQval(dmRow, suppdm, 'ENROLL')
        } else if (DR22) {
            return parseDateTrimDash(dmRow?.RFICDTC as String)
        }
        return super.getVisitDate(dmRow, suppdm)
    }

    @Override
    protected String getRace(Document row, MongoCollection<Document> supp) {
        if (studyIn(DR22)) {
            if ((row.RACE as String)?.toUpperCase() == 'OTHER') {
                Document first = supp.find(eq('USUBJID', row.USUBJID)).first()
                return first?.RACEOTH
            } else {
                return row.RACE
            }
        } else {
            return super.getRace(row, supp)
        }
    }

    @Override
    protected String getBirthDate(Document row, MongoCollection<Document> supp) {
        if (studyIn(DR22, DR25)) {
            return parseDateTrimDash(row?.BRTHDTC as String)
        } else {
            return super.getBirthDate(row, supp)
        }
    }

    @Override
    String getCenter(String val) {
        if (studyIn(DR25)) {
            return parseCentreSpecific(val)
        }
        return super.getCenter(val);
    }
}
