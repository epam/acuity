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


import com.acuity.sdtm.transform.io.writer.Writer
import com.acuity.sdtm.transform.processor.SDTM_base.BaseDoseDiscontinuationProcessor
import com.acuity.sdtm.transform.common.Version
import com.mongodb.client.MongoCollection
import com.mongodb.client.MongoDatabase
import com.mongodb.client.model.Filters
import org.bson.Document
import org.bson.conversions.Bson
import org.springframework.stereotype.Component

import static com.acuity.sdtm.transform.common.Study.*
import static com.acuity.sdtm.transform.util.Util.*
import static com.mongodb.client.model.Filters.*
import static org.apache.commons.lang3.StringUtils.startsWithIgnoreCase
import static org.apache.commons.lang3.StringUtils.substring

/**
 * Created by knml167 on 08/06/2015.
 */
@Component
public class SDTM_1_1_DoseDiscontinuationProcessor extends BaseDoseDiscontinuationProcessor implements SDTM_1_1_StudyCommonProcessor {

    @Override
    boolean isStudyProducesThisTypeOfEntity() {
        return !studyIn(STUDY001)
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
    Bson getDomainFilter() {
        if (studyIn(DR7, DR8, DR5, DR14, DR4)) {
            or(super.getDomainFilter(), Filters.in('VISIT', 'END OF TREATMENT', 'END OF STUDY TREATMENT',
                    'End of Treatment'))
        } else if (studyIn(DR9, DR16, DR13)) {
            and(eq('DSCAT', 'DISPOSITION EVENT'), regex('DSSCAT', '^END OF TREATMENT .+'))
        } else if (studyIn(DR15)) {
            eq('DSCAT', 'DISPOSITION EVENT')
        } else if (studyIn(DR10)) {
            and(eq('DSCAT', 'DISPOSITION EVENT'), eq('EPOCH', 'END OF TREATMENT'))
        } else if (studyIn(DR3)) {
            and(eq('DSCAT', 'DISPOSITION EVENT'), eq('DSDECOD', 'TREATMENT STOPPED'))
        } else if (studyIn(DR12, DR11, DR17, DR20, DR18, DR21,
                DR22, DR23)) {
            and(eq('DSCAT', 'DISPOSITION EVENT'), eq('EPOCH', 'TREATMENT'))
        } else if (studyIn(DR25)) {
            and(eq('DSCAT', 'DISPOSITION EVENT'), eq('EPOCH', 'TREATMENT'),
                    eq('DSSCAT', 'END OF STUDY TREATMENT'))
        } else {
            super.getDomainFilter()
        }
    }

    @Override
    protected String getDrugName(Document dsRow, MongoCollection<Document> suppDsCollection) {
        String dsscat = dsRow.DSSCAT

        if (studyIn(DR7, DR8)) {
            if (dsscat.endsWith('END OF TREATMENT')) {
                return dsscat.getAt([0..-18])
            }
        } else if (studyIn(DR9, DR13)) {
            if (dsscat.startsWith('END OF TREATMENT ')) {
                return dsscat.substring(17)
            }
        } else if (studyIn(DR14)) {
            if (dsscat.startsWith('END OF TREATMENT - ')) {
                return dsscat.substring(19)
            }
        } else if (studyIn(DR5)) {
            return dsscat?.toUpperCase()
        } else if (studyIn(DR11)) {
            dsscat = dsRow.DSREFID
            return startsWithIgnoreCase(dsscat, 'EOT') ? substring(dsscat, 4) : dsscat
        } else if (studyIn(DR17, DR18)) {
            return dsscat
        } else if (studyIn(DR20)) {
            switch (dsRow.DSREFID) {
                case 'EOTD':
                    return 'DURVALUMAB'
                default:
                    return null
            }
        } else if (studyIn(DR23)) {
            switch (dsscat) {
                case 'END OF STUDY TREATMENT - VISTUSERTIB':
                    return 'VISTUSERTIB'
                case 'END OF STUDY - ACALBRUTINIB':
                    return 'ACALABRUTINIB'
                default:
                    return null
            }
        }
        super.getDrugName(dsRow, suppDsCollection)
    }

    @Override
    protected String getMainReasonForPrematureWithdrawal(Document dsRow) {
        if (studyIn(DR3)) {
            dsRow.DSTERM
        } else {
            super.getMainReasonForPrematureWithdrawal(dsRow)
        }
    }

    @Override
    protected String getFreeTextWhereReasonSpecified(Document dsRow, MongoCollection suppds) {
        if (studyIn(DR10, DR6, DR11, DR17, DR20, DR18, DR21,
                DR22, DR23)) {
            dsRow.DSTERM
        } else if (studyIn(DR3)) {
            null
        } else if (studyIn(DR25)) {
            return getSuppFirstQval(dsRow, suppds, 'DSSPFY', 'DSSEQ')
        } else {
            super.getFreeTextWhereReasonSpecified(dsRow, suppds)
        }
    }

    @Override
    void process(MongoDatabase mongo, Writer writer) throws IOException {
        if (studyIn(DR18)) {
            MongoCollection<Document> ds = mongo.getCollection("${sessionId}_DS")
            MongoCollection<Document> suppdm = mongo.getCollection("${sessionId}_SUPPDM")
            performProcess(ds, suppdm, writer)
        } else {
            super.process(mongo, writer)
        }
    }

    @Override
    protected String getInvestigationalProductDiscDate(Document dsRow) {
        if (studyIn(DR22)) {
            return parseDateTrimDash(dsRow?.DSSTDTC as String)
        } else {
            return super.getInvestigationalProductDiscDate(dsRow)
        }
    }
}
