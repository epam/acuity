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


import com.acuity.sdtm.transform.processor.SDTM_base.BaseWithdrawalProcessor
import com.acuity.sdtm.transform.common.Version
import com.acuity.sdtm.transform.util.Util
import com.mongodb.client.MongoCollection
import com.mongodb.client.model.Filters
import org.bson.Document
import org.bson.conversions.Bson
import org.springframework.stereotype.Component

import static com.acuity.sdtm.transform.common.Study.*
import static com.acuity.sdtm.transform.util.Util.*
import static com.mongodb.client.model.Filters.*

@Component
class SDTM_1_1_WithdrawalProcessor extends BaseWithdrawalProcessor implements SDTM_1_1_StudyCommonProcessor {

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
    Bson getDomainFilter() {
        if (studyIn(DR9)) {
            return and(
                    eq('DSCAT', 'DISPOSITION EVENT'),
                    eq('EPOCH', 'TREATMENT'),
                    not(regex('DSSCAT', '^END OF TREATMENT.*')),
            )
        } else if (studyIn(DR15)) {
            return and(
                    eq('DSCAT', 'DISPOSITION EVENT'),
                    or(
                            eq('DSTERM', 'COMPLETED'),
                            regex('DSTERM', '^WITHDRAWN FROM STUDY DUE TO .*')
                    ),
            )
        } else if (studyIn(DR16, DR13)) {
            return and(eq('DSCAT', 'DISPOSITION EVENT'), or(eq('DSSCAT', 'EARLY TERMINATION'), eq('DSSCAT', 'SUBJECT COMPLETION')),
            )
        } else if (studyIn(DR12, DR11, DR17, DR20, DR21)) {
            return and(
                    eq('DSCAT', 'DISPOSITION EVENT'),
                    eq('EPOCH', 'STUDY')
            )
        } else if (studyIn(DR10, DR6)) {
            return and(
                    eq('DSCAT', 'DISPOSITION EVENT'),
                    eq('VISIT', 'END OF STUDY')
            )
        } else if (studyIn(DR18)) {
            return and(
                    eq('DSCAT', 'DISPOSITION EVENT'),
                    Filters.in('EPOCH', 'TREATMENT', 'STUDY')
            )
        } else if (studyIn(DR22)) {
            return and(eq('DSCAT', 'DISPOSITION EVENT'),
                    eq('EPOCH', 'TREATMENT'))
        } else if (studyIn(DR23)) {
            return and(eq('DSCAT', 'DISPOSITION EVENT'),
                    eq('EPOCH', 'STUDY'),
                    eq('DSSCAT', 'STUDY EXIT'))
        } else if (studyIn(DR25)) {
            return and(eq('DSCAT', 'DISPOSITION EVENT'),
                    eq('DSSCAT', 'STUDY EXIT'))
        } else return super.getDomainFilter()
    }

    @Override
    protected String getPrematurelyWithdrawn(Document dsRow) {
        if (studyIn(DR9, DR16, DR13)) {
            return dsRow.DSSCAT == 'EARLY TERMINATION' ? Util.YES : Util.NO
        } else if (studyIn(DR15)) {
            return dsRow.DSTERM == 'COMPLETED' ? Util.NO : Util.YES
        } else if (studyIn(DR12, DR11, DR18)) {
            return 'COMPLETED'.equalsIgnoreCase(dsRow.DSDECOD as String) ? Util.NO : Util.YES
        } else if (studyIn(DR10, DR6)) {
            return dsRow.EPOCH == 'END OF STUDY' && !'COMPLETED'.equalsIgnoreCase(dsRow.DSDECOD as String) ? Util.YES : Util.NO
        } else {
            return super.getPrematurelyWithdrawn(dsRow)
        }
    }

    @Override
    protected String getPrematureWithdrawalSpecification(Document dsRow, MongoCollection<Document> suppds) {
        if (studyIn(DR9, DR16, DR12, DR13, DR11, DR17, DR20,
                DR21)) {
            return 'OTHER' == dsRow.DSDECOD ? dsRow.DSTERM : null
        } else if (studyIn(DR15)) {
            return 'COMPLETED' == dsRow.DSTERM ? null : super.getPrematureWithdrawalSpecification(dsRow, suppds)
        } else if (studyIn(DR10, DR6)) {
            return dsRow.EPOCH == 'END OF STUDY' && 'OTHER'.equalsIgnoreCase(dsRow.DSDECOD as String) ? dsRow.DSTERM : null
        } else if (studyIn(DR23, DR25)) {
            return null
        }
        return super.getPrematureWithdrawalSpecification(dsRow, suppds)
    }

    @Override
    protected String getCompletionDate(Document dsRow) {
        if (studyIn(DR18, DR25)) {
            dsRow.DSSTDTC
        } else if (studyIn(DR22)) {
            return parseDateTrimDash(dsRow?.DSSTDTC as String) ?: parseDateTrimDash(dsRow?.DSDTC as String)
        } else {
            return super.getCompletionDate(dsRow)
        }
    }

    @Override
    protected String getMainReasonForPrematureWithdrawal(Document dsRow) {
        if (studyIn(DR3)) {
            return dsRow.DSTERM ?: dsRow.DSDECOD
        } else if (studyIn(DR9, DR16)) {
            return dsRow.DSDECOD
        } else if (studyIn(DR15)) {
            if (dsRow.DSTERM == 'COMPLETED') {
                return null
            } else {
                if (dsRow.DSTERM.startsWith('WITHDRAWN FROM STUDY DUE TO ')) {
                    return dsRow.DSTERM.substring(28)
                } else {
                    return dsRow.DSDECOD
                }

            }
        } else if (studyIn(DR12)) {
            return !'COMPLETED'.equalsIgnoreCase(dsRow.DSDECOD as String) ? dsRow.DSDECOD : null
        } else if (studyIn(DR10, DR6)) {
            return dsRow.EPOCH == "END OF STUDY" && !'COMPLETED'.equalsIgnoreCase(dsRow.DSDECOD as String) ? dsRow.DSDECOD : null
        } else if (studyIn(DR13)) {
            return 'EARLY TERMINATION'.equalsIgnoreCase(dsRow.DSSCAT as String) ? dsRow.DSDECOD : null
        } else if (studyIn(DR18)) {
            return dsRow.DSTERM
        }
        return super.getMainReasonForPrematureWithdrawal(dsRow)
    }

    @Override
    public Version getVersion() {
        return Version.SDTM_1_1
    }

}
