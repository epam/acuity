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
import com.mongodb.client.MongoCollection
import com.mongodb.client.MongoDatabase
import com.mongodb.client.model.Filters
import groovy.util.logging.Slf4j
import org.bson.Document
import org.bson.conversions.Bson

/**
 * Created by knml167 on 08/06/2015.
 */

@Slf4j
public abstract class BaseDoseDiscontinuationProcessor extends BaseEntityProcessor {
    def HEADER = ['STUDY', 'PART', 'SUBJECT', 'SD', 'IPDC_DAT', 'IPDCREAS', 'IPDCSPEC']

    @Override
    Map<String, String> map(Document row) {
        [
                STUDY   : row.STUDYID as String,
                PART    : 'A',
                SUBJECT : getSubject(row.USUBJID as String),
                SD      : row.sd as String,
                IPDC_DAT: row.DSSTDTC as String,
                IPDCREAS: row.dsdecod as String,
                IPDCSPEC: row.dsspfy as String
        ]
    }

    @Override
    public EntityType getEntityType() {
        return EntityType.DoseDiscontinuation
    }

    @Override
    Bson getDomainFilter() {
        Filters.regex('DSTERM', '^TREATMENT STOPPED DUE TO .+')
    }

    protected String getDrugName(Document dsRow, MongoCollection<Document> suppDsCollection) {
        getSuppFirstQval(dsRow, suppDsCollection, 'SD', 'DSSEQ')
    }

    /**
     * IPDCREAS mapping
     *
     * @param dsRow
     * @return
     */
    protected String getMainReasonForPrematureWithdrawal(Document dsRow) {
        return dsRow.DSDECOD
    }

    /**
     * Free text where the reason is specified.
     *
     * @param dsRow
     * @return
     */
    protected String getFreeTextWhereReasonSpecified(Document dsRow, MongoCollection suppds) {
        return getSuppFirstQval(dsRow, suppds, 'DSSPFY', 'DSSEQ')
    }

    protected String getInvestigationalProductDiscDate(Document dsRow) {
        return dsRow.DSSTDTC
    }

    @Override
    public void process(MongoDatabase mongo, Writer writer) throws IOException {

        MongoCollection<Document> ds = mongo.getCollection("${sessionId}_DS")
        MongoCollection<Document> suppds = mongo.getCollection("${sessionId}_SUPPDS")
        performProcess(ds, suppds, writer)
    }

    public void performProcess(MongoCollection<Document> main, MongoCollection<Document> supp,
                               Writer writer) {
        try {
            writer.open(study, 'target_med_dos_disc', getHeader())

            main.find(getDomainFilter()).each { row ->

                row.sd = getDrugName(row, supp)
                row.DSSTDTC = getInvestigationalProductDiscDate(row)
                row.dsspfy = getFreeTextWhereReasonSpecified(row, supp)
                row.dsdecod = getMainReasonForPrematureWithdrawal(row)
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
