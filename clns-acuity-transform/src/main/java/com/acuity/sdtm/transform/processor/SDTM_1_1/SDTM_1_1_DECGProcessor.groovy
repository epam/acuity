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


import com.acuity.sdtm.transform.processor.SDTM_base.BaseDECGProcessor
import com.acuity.sdtm.transform.common.Version
import com.mongodb.client.MongoCollection
import org.bson.Document
import org.bson.conversions.Bson
import org.springframework.stereotype.Component

import static com.acuity.sdtm.transform.common.Study.*
import static com.acuity.sdtm.transform.util.Util.*
import static com.mongodb.client.model.Filters.nin

@Component
public class SDTM_1_1_DECGProcessor extends BaseDECGProcessor implements SDTM_1_1_StudyCommonProcessor {

    private final SHORT_NAME_MAP = ['QRSAG' : 'QRSDUR',
                                    'QTAG'  : 'QTMEAN',
                                    'QTCFAG': 'QTCF',
                                    'RRAG'  : 'RRMEAN',
                                    'QRSSB' : 'QRS',
                                    'QTCBSB': 'QTCB',
                                    'QTCFSB': 'QTCF',
                                    'QTSB'  : 'QT',
                                    'RRSM'  : 'RR']

    @Override
    Map<String, String> map(Document row) {
        if (studyIn(DR22)) {
            [*    : super.map(row),
             EGTIM: getMeasurementTime(row)
            ]
        } else {
            return super.map(row)
        }
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
    boolean isStudyProducesThisTypeOfEntity() {
        return !studyIn(DR15, DR12, DR10, DR6, DR19, DR20, DR21,
                DR24, STUDY001, STUDY002, STUDY003, STUDY004)
    }

    @Override
    public Version getVersion() {
        return Version.SDTM_1_1
    }

    @Override
    String getTestResult(Document row) {
        if (studyIn(DR6, DR11, DR23, DR25)) {
            return row.EGSTRESN
        } else if (studyIn(DR22)) {
            return row.EGORRES
        }
        return super.getTestResult(row)

    }

    @Override
    Bson getDomainFilter() {
        if (studyIn(DR25)) {
            return nin('EGDRVFL', 'Y')
        } else {
            return super.getDomainFilter()
        }
    }

    @Override
    protected String getTestOrExaminationShortName(String name) {
        if (studyIn(DR13, DR11, DR17, DR16, DR18, DR9,
                DR22)) {
            SHORT_NAME_MAP.get(name) ?: super.getTestOrExaminationShortName(name)
        } else {
            super.getTestOrExaminationShortName(name)
        }
    }

    @Override
    protected String getReasonAbnormalOverallEcgEvaluation(Document egRow, MongoCollection suppeg, def suppRows) {
        if (studyIn(DR13)) {
//            Bson joinFilter = and(eq('USUBJID', egRow.USUBJID), eq('QNAM', 'EGSPFY'), eq('IDVARVAL', egRow.EGSEQ))
//            getSuppFirstQval(egRow, suppeg, 'EGSPFY', 'EGSEQ')
            suppRows.find { it['QNAM'] == 'EGSPFY' }?.QVAL
        } else if (studyIn(DR23)) {
            return getSuppFirstQval(egRow, suppeg, 'EGSPFY', 'EGSEQ')
        } else {
            super.getReasonAbnormalOverallEcgEvaluation(egRow, suppeg, suppRows)
        }
    }

    @Override
    protected String getMeasurementDate(Document row) {
        if (studyIn(DR22)) {
            return parseDateTrimTime(row?.EGDTC as String)
        } else {
            return super.getMeasurementDate(row)
        }
    }

    protected String getMeasurementTime(Document row) {
        if (studyIn(DR22)) {
            String val = row.EGDTC as String
            def matcher = val =~ /^(\d{4}\-\d{2}\-\d{2})+\D+(.+)?/
            if (matcher.size() > 0) {
                return matcher[0][2]
            }
        }
    }

    @Override
    protected String getClinicallySignificant(Document egRow, MongoCollection<Document> suppeg, List<Document> suppRows) {
        if (studyIn(DR25)) {
            return getSuppFirstQval(egRow, suppeg, 'EGCLSIG', 'EGSEQ')
        }
        return super.getClinicallySignificant(egRow, suppeg, suppRows)
    }
}
