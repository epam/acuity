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


import com.acuity.sdtm.transform.processor.SDTM_base.BaseECGProcessor
import com.acuity.sdtm.transform.common.Version
import com.mongodb.client.model.Filters
import org.bson.Document
import org.bson.conversions.Bson
import org.springframework.stereotype.Component

import static com.acuity.sdtm.transform.common.Study.*

@Component
public class SDTM_1_1_ECGProcessor extends BaseECGProcessor implements SDTM_1_1_StudyCommonProcessor {

    @Override
    boolean isStudyProducesThisTypeOfEntity() {
        return !studyIn(DR15, DR13, DR11, DR17, DR18, DR22, DR23, DR25,
                STUDY001, STUDY002, STUDY003, STUDY004)
    }

    @Override
    Bson getDomainFilter() {
        if (studyIn(DR9, DR16)) {
            return Filters.in('EGTESTCD', 'QRSSB', 'RRSM', 'QTSB', 'QTCFSB', 'INTP')
        } else if (studyIn(DR12)) {
            return Filters.and(Filters.ne('EGTESTCD', null), Filters.ne('EGTESTCD', ''))
        } else {
            return super.getDomainFilter()
        }
    }

    @Override
    String getTestName(Document egRow) {
        if (studyIn(DR9, DR16, DR12)) {
            if ((egRow.EGTEST as String)?.toLowerCase() in ['QTcF Interval, Single Beat'.toLowerCase(), 'QTCF Interval, Aggregate'.toLowerCase()]) {
                return "QTcF - Fridericia's Correction Formula"
            }
        }
        return super.getTestName(egRow)
    }

    @Override
    String getTestResult(Document row) {
        if (studyIn(DR6)) {
            return row.EGSTRESN
        } else if (studyIn(DR24)) {
            if ((row.EGTESTCD as String)?.toUpperCase() in ['VRAG', 'RRAG', 'PRAG', 'QRSAG', 'QTAG']) {
                return row.EGORRES
            } else return null
        }
        return super.getTestResult(row)
    }

    @Override
    protected String getEvaluation(Document row) {
        if (studyIn(DR20, DR21, DR24)) {
            null
        } else {
            return super.getEvaluation(row)
        }
    }

    @Override
    protected String getAbnormEvaluation(Document row) {
        if (studyIn(DR20, DR21, DR24)) {
            null
        } else {
            return super.getAbnormEvaluation(row)
        }
    }

    @Override
    public Version getVersion() {
        return Version.SDTM_1_1;
    }
}
