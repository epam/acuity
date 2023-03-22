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

import com.acuity.sdtm.transform.processor.SDTM_base.BaseDoseProcessor
import com.mongodb.client.MongoCollection
import org.bson.Document
import org.springframework.stereotype.Component

import static com.acuity.sdtm.transform.common.Study.*
import static com.acuity.sdtm.transform.util.Util.*

/**
 * Created by knml167 on 08/06/2015.
 */
@Component
public class SDTM_1_1_DoseProcessor extends BaseDoseProcessor implements SDTM_1_1_StudyCommonProcessor {

    @Override
    boolean isStudyProducesThisTypeOfEntity() {
        return !studyIn(STUDY001, STUDY002, STUDY003, STUDY004)
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
    protected String getStartDate(Document exRow) {
        if (studyIn(DR16, DR1, DR2)) {
            return parseDateTrimTime(exRow?.EXSTDTC as String)
        } else if (studyIn(DR22)) {
            return parseDateTrimDash(exRow?.EXSTDTC as String)
        } else {
            super.getStartDate(exRow)
        }
    }

    @Override
    protected String getEndDate(Document exRow) {
        if (studyIn(DR16, DR1, DR2)) {
            return parseDateTrimTime(exRow?.EXENDTC as String)
        } else if (studyIn(DR22)) {
            return parseDateTrimDash(exRow?.EXENDTC as String)
        } else {
            super.getEndDate(exRow)
        }
    }

    @Override
    protected String getFrequency(Document exRow) {
        if (studyIn(DR15, DR16, DR22)) {
            return 1
        } else if (studyIn(DR11)) {
            if (exRow.EXTRT == 'MEDI4736') {
                return 1
            }
        } else if (studyIn(DR1, DR2)) {
            return exRow.EXDOSFRQ ? (exRow.EXDOSFRQ as String).split('/')[0] : null
        } else if (studyIn(DR23)) {
            String freqRow = exRow.EXDOSFRQ as String
            if (freqRow) {
                def checkValues = ["QD", "QH", "QM", "QS", "BID", "BIM", "BIS", "TID", "TIS", "QID", "QIS"]
                def foundValue = checkValues.find { value -> freqRow.contains(value) }
                return parseDoseFrequency(foundValue)
            } else {
                return null
            }
        } else if (studyIn(DR25)) {
            return exRow.EXDOSFRQ ? (exRow.EXDOSFRQ as String).split(' ')[0] : null
        }
        return super.getFrequency(exRow)
    }

    @Override
    protected String getFrequencyUnit(Document exRow) {
        if (studyIn(DR15, DR16)) {
            return 'Day'
        } else if (studyIn(DR11)) {
            if (exRow.EXTRT == 'MEDI4736') {
                return 'Day'
            }
        } else if (studyIn(DR22)) {
            return 'day'
        } else if (studyIn(DR1, DR2)) {
            return exRow.EXDOSFRQ ? (exRow.EXDOSFRQ as String).split('/')[1].trim() : null
        } else if (studyIn(DR23, DR18)) {
            String freqRow = exRow.EXDOSFRQ as String
            if (freqRow) {
                def checkValues = ["QD", "BID", "TID", "QID", "QH", "QM", "BIM", "QS", "BIS", "TIS", "QIS"]
                String foundValue = checkValues.find { value -> freqRow.contains(value) }
                return parseDoseFrequencyUnit(foundValue)
            } else {
                return null
            }
        } else if (studyIn(DR25)) {
            if (exRow.EXDOSFRQ != null) {
                def split = (exRow.EXDOSFRQ as String).split(' ');
                return split[split.size() - 1]
            } else {
                return null
            }
        }
        return super.getFrequencyUnit(exRow)
    }

    @Override
    protected String getActionTaken(Document exRow, MongoCollection<Document> suppex) {
        if (studyIn(DR16, DR17)) {
            return getSuppFirstQval(exRow, suppex, 'DOSEADJ', 'EXSEQ')
        } else if (studyIn(DR12, DR10, DR6, DR11, DR20, DR18, DR21)) {
            return getSuppFirstQval(exRow, suppex, 'EXDOSADJ', 'EXSEQ')
        } else if (studyIn(DR23)) {
            return null
        } else if (studyIn(DR25)) {
            return getSuppFirstQval(exRow, suppex, 'EXACN', 'EXSEQ')
        }
        return super.getActionTaken(exRow, suppex)
    }

    @Override
    protected String getReasonForActionTaken(Document exRow, MongoCollection<Document> suppex) {
        if (studyIn(DR15, DR16, DR13, DR12, DR6, DR11, DR17, DR20,
                DR18, DR21, DR22, DR23, DR24, DR25)) {
            return exRow.EXADJ
        } else if (studyIn(DR3)) {
            return getSuppFirstQval(exRow, suppex, 'EXACN', 'EXSEQ')
        }
        return super.getReasonForActionTaken(exRow, suppex)
    }

    @Override
    protected String getReasonForDoseChangeSpecification(Document exRow, MongoCollection<Document> suppex) {
        if (studyIn(DR15)) {
            return getSuppFirstQval(exRow, suppex, 'EXADMREA', 'EXSEQ')
        } else if (studyIn(DR16, DR12, DR13, DR17, DR3)) {
            return exRow.EXADJ
        } else if (studyIn(DR10)) {
            return getSuppFirstQval(exRow, suppex, 'EXADJOTH', 'EXSEQ')
        } else if (studyIn(DR6)) {
            return getSuppFirstQval(exRow, suppex, 'EXPADJOTH', 'EXSEQ')
        } else if (studyIn(DR11, DR20, DR18, DR21, DR24)) {
            return null
        } else if (studyIn(DR25)) {
            return getSuppFirstQval(exRow, suppex, 'EXADJDSC', 'EXSEQ')
        }
        return super.getReasonForDoseChangeSpecification(exRow, suppex)
    }
}
