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

import com.acuity.sdtm.transform.processor.SDTM_base.BaseVitalProcessor
import com.mongodb.client.MongoCollection
import org.bson.Document
import org.springframework.stereotype.Component

import static com.acuity.sdtm.transform.common.Study.*
import static com.acuity.sdtm.transform.util.Util.*

/**
 * Created by knml167 on 08/06/2015.
 */
@Component
public class SDTM_1_1_VitalProcessor extends BaseVitalProcessor implements SDTM_1_1_StudyCommonProcessor {

    @Override
    boolean isStudyProducesThisTypeOfEntity() {
        return !studyIn(STUDY001, STUDY002, STUDY003, STUDY004)
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
    Map<String, String> map(Document row) {
        if (studyIn(DR22)) {
            def shortenMap = super.map(row)
            super.map(row).keySet().removeAll(['ANATOM_LOC', 'ANATOM_SITE', 'PHYS_POSITION', 'CLI_SIGN'])
            return shortenMap
        } else {
            return super.map(row)
        }
    }

    @Override
    protected fillAnatomicalLocation(Document row) {
        if (studyIn(DR17, DR20, DR18, DR21, DR23, DR24, DR25)) {
            row.VSLOC = null
        } else {
            row.VSLOC = row.VSLOC
        }
    }


    @Override
    protected fillAnatomicalSideOfInterest(Document row) {
        if (studyIn(DR17, DR20, DR18, DR21, DR23, DR24, DR25)) {
            row.VSLAT = null
        } else {
            row.VSLAT = row.VSLAT
        }
    }


    @Override
    protected fillPhysicalPosition(Document row) {
        if (studyIn(DR17, DR20, DR18, DR21, DR23, DR24, DR25)) {
            row.VSPOS = null
        } else {
            row.VSPOS = row.VSPOS
        }
    }


    @Override
    protected fillClinicallySignificant(Document row, MongoCollection<Document> supp) {
        if (studyIn(DR17, DR20, DR18, DR21, DR23, DR24, DR25)) {
            row.CLI_SIGN = null
        } else {
            row.CLI_SIGN = getSuppFirstQval(row, supp, 'VSCLSIG', 'VSSEQ')
        }
    }

    @Override
    protected fillMeasurementDate(Document row) {
        if (studyIn(DR22)) {
            row.VSDTC = parseDateTrimDash(row?.VSDTC as String)
        } else {
            row.VSDTC = row.VSDTC
        }
    }
}
