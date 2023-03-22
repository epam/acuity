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


import com.acuity.sdtm.transform.processor.SDTM_base.BaseLaboratoryProcessor
import com.acuity.sdtm.transform.common.Version
import org.bson.Document
import org.bson.conversions.Bson
import org.springframework.stereotype.Component

import static com.acuity.sdtm.transform.common.Study.*
import static com.acuity.sdtm.transform.util.Util.*
import static com.mongodb.client.model.Filters.nin

/**
 * Created by knml167 on 08/06/2015.
 */
@Component
public class SDTM_1_1_LaboratoryProcessor extends BaseLaboratoryProcessor implements SDTM_1_1_StudyCommonProcessor {
    @Override
    Bson getDomainFilter() {
        if (studyIn(DR23, DR25)) {
            return nin('LBSTRESN', null, '')
        }
        return super.getDomainFilter()
    }

    @Override
    boolean isStudyProducesThisTypeOfEntity() {
        return !studyIn(DR15, STUDY002, STUDY003, STUDY004)
    }

    @Override
    public Version getVersion() {
        return Version.SDTM_1_1
    }

    @Override
    String getSubject(String val) {
        if (studyIn(DR22)) {
            parseSubjectSpecific(val)
        } else if (studyIn(DR23, DR25)) {
            parseSubjectBySlash(val)
        } else {
            return super.getSubject(val)
        }
    }

    @Override
    protected String getLabValue(Document lbRow) {
        if (studyIn(DR1, DR2, DR9)) {
            return lbRow.LBORRES
        } else if (studyIn(DR24)) {
            return lbRow.LBSTRESN ?: lbRow.LBORRES
        }
        return super.getLabValue(lbRow)
    }

    @Override
    protected String getLabUnit(Document lbRow) {
        if (studyIn(DR1, DR2, DR9)) {
            return lbRow.LBORRESU
        } else if (studyIn(DR24)) {
            return lbRow.LBSTRESU ?: lbRow.LBORRESU
        }
        return super.getLabUnit(lbRow)
    }

    @Override
    protected String getLabRefLo(Document lbRow) {
        if (studyIn(DR1, DR2, DR9)) {
            return lbRow.LBORNRLO
        }
        return super.getLabRefLo(lbRow)
    }

    @Override
    protected String getLabRefHi(Document lbRow) {
        if (studyIn(DR1, DR2, DR9)) {
            return lbRow.LBORNRHI
        }
        return super.getLabRefHi(lbRow)
    }

    @Override
    protected String getMeasurementDate(Document lbRow) {
        if (studyIn(DR22)) {
            return parseDateTrimDash(lbRow?.LBDTC as String)
        } else {
            return super.getMeasurementDate(lbRow)
        }
    }
}
