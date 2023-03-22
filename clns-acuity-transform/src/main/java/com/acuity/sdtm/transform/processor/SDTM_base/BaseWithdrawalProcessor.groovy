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

package com.acuity.sdtm.transform.processor.SDTM_base


import com.acuity.sdtm.transform.io.writer.Writer
import com.acuity.sdtm.transform.common.BaseEntityProcessor
import com.acuity.sdtm.transform.common.EntityType
import com.acuity.sdtm.transform.util.Util
import com.mongodb.client.MongoCollection
import com.mongodb.client.MongoDatabase
import groovy.util.logging.Slf4j
import org.bson.Document
import org.bson.conversions.Bson

import static com.mongodb.client.model.Filters.or
import static com.mongodb.client.model.Filters.regex

@Slf4j
public abstract class BaseWithdrawalProcessor extends BaseEntityProcessor {

    def HEADER = ['STUDY', 'PART', 'SUBJECT', 'TERM_DAT', 'TERMPREM', 'TERMREAS', 'TERMSPEC']

    Map<String, String> map(Document row) {
        return [
                STUDY   : row.STUDYID as String,
                PART    : 'A',
                SUBJECT : getSubject(row.USUBJID as String),
                TERM_DAT: row.completionDate as String,
                TERMPREM: row.prematurelyWithdrawn as String,
                TERMREAS: row.mainReasonForPrematureWithdrawal as String,
                TERMSPEC: row.prematureWithdrawalSpecification as String
        ]
    }

    @Override
    public EntityType getEntityType() {
        return EntityType.Completion
    }

    @Override
    Bson getDomainFilter() {
        or(
                regex('DSSCAT', '^REASON STUDY DISCONTINUED$', 'i'),
                regex('VISIT', '^END OF STUDY$', 'i'),
                regex('DSDECOD', '^STUDY COMPLETED$', 'i'),
                regex('DSDECOD', '^STUDY DISCONTINUED$', 'i'),
                regex('DSTERM', '^WITHDRAWN FROM STUDY DUE TO.*', 'i')
        )
    }

    protected String getPrematurelyWithdrawn(Document dsRow) {
        boolean isStudyCompleted = 'COMPLETED'.equalsIgnoreCase(dsRow.DSDECOD as String) ||
                'STUDY COMPLETED'.equalsIgnoreCase(dsRow.DSDECOD as String) ||
                'COMPLETED'.equalsIgnoreCase(dsRow.DSTERM as String)
        return isStudyCompleted ? Util.NO : Util.YES
    }

    protected String getCompletionDate(Document dsRow) {
        return dsRow.DSSTDTC ?: dsRow.DSDTC
    }

    protected String getMainReasonForPrematureWithdrawal(Document dsRow) {
        return dsRow.DSDECOD ?: dsRow.DSTERM
    }

    protected String getPrematureWithdrawalSpecification(Document dsRow, MongoCollection<Document> suppds) {
        getSuppFirstQval(dsRow, suppds, 'DSSPFY', 'DSSEQ')
    }

    @Override
    public void process(MongoDatabase mongo, Writer writer) throws IOException {
        MongoCollection<Document> ds = mongo.getCollection("${sessionId}_DS")

        MongoCollection<Document> suppds = mongo.getCollection("${sessionId}_SUPPDS")

        try {
            writer.open(study, 'withdrawal_completion', getHeader())
            ds.find(getDomainFilter()).each { row ->

                row.completionDate = getCompletionDate(row)
                row.prematurelyWithdrawn = getPrematurelyWithdrawn(row)

                if (row.prematurelyWithdrawn == Util.YES) {
                    row.mainReasonForPrematureWithdrawal = getMainReasonForPrematureWithdrawal(row)
                    row.prematureWithdrawalSpecification = getPrematureWithdrawalSpecification(row, suppds)
                }

                writer.write(map(row))
            }
        } finally {
            writer.close()
        }
    }

    protected List<String> getHeader() {
        return HEADER
    }
}
