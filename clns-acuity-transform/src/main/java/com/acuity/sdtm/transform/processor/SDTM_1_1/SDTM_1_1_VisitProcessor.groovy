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


import com.acuity.sdtm.transform.processor.SDTM_base.BaseVisitProcessor
import com.acuity.sdtm.transform.common.Version
import org.bson.Document
import org.springframework.stereotype.Component

import static com.acuity.sdtm.transform.common.Study.*
import static com.acuity.sdtm.transform.util.Util.*

/**
 * Created by knml167 on 08/06/2015.
 */
@Component
public class SDTM_1_1_VisitProcessor extends BaseVisitProcessor implements SDTM_1_1_StudyCommonProcessor {

    @Override
    boolean isStudyProducesThisTypeOfEntity() {
        return !studyIn(STUDY001, STUDY002, STUDY003, STUDY004)
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
    protected String getVisitDate(Document row) {
        if (studyIn(DR22)) {
            return parseDateTrimDash(row?.SVSTDTC as String)
        } else {
            return super.getVisitDate(row)
        }
    }
}
