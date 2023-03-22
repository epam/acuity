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

import static com.acuity.sdtm.transform.util.Util.asDouble
import static com.mongodb.client.model.Aggregates.match
import static com.mongodb.client.model.Filters.*

@Slf4j
abstract class BaseRecist2Processor extends BaseEntityProcessor {

    def HEADER = ['STUDY', 'PART', 'SUBJECT', 'R3IOREC', 'INVOPRES', 'VISIT',
                  'RNLESSIT', 'RNASMDAT', 'RNNEWLES', 'REASDIFF', 'INVAWREC', 'VIS_DAT',]

    Map<String, String> map(Document row) {
        [
                STUDY   : row.STUDYID as String,
                PART    : 'A',
                SUBJECT : getSubject(row.USUBJID as String),
                VISIT   : row.VISITNUM as String,
                RNLESSIT: row.TULOC as String,
                RNASMDAT: row.TUDTC as String,
                RNNEWLES: row.newLesion ? Util.YES : Util.NO,
                R3IOREC : row.RSSTRESC as String,
                INVOPRES: row.investigatorOpinionOfPatientStatus as String,
                REASDIFF: row.RSSPFY as String,
                INVAWREC: row.invawrec as String,
                VIS_DAT : row.VISITDTC as String
        ]
    }

    @Override
    EntityType getEntityType() {
        return EntityType.Recist2
    }

    @Override
    Bson getDomainFilter() {
        eq('RSTESTCD', 'OVRLRESP')
    }

    protected String getReasonRECISTAndInvestigatorAssessmentsDiffer(Document rsRow, Document rsInvopresRow, MongoCollection supprs) {
        return rsInvopresRow ? getSuppFirstQval(rsInvopresRow, supprs, 'RSSPFY', 'RSSEQ') : null
    }

    protected String getInvestigatorOpinionOfPatientStatus(Document rsRow, Document rsInvopresRow, MongoCollection supprs) {
        return rsInvopresRow?.RSSTRESC
    }

    protected String getVisitDate(Document rsRow, Document svRow, Document tuRow) {
        return svRow?.SVSTDTC
    }

    protected String getRecistResponse(Document row, MongoCollection rs) {
        return row?.RSSTRESC
    }

    protected getInvawrec(Document rsRow, Document rsInvopresRow, MongoCollection supprs) {
        rsRow.RSSPFY ? Util.NO : Util.YES
    }

    protected String getLesionScanDate(Document row) {
        return row.TUDTC
    }

    protected Bson getTuFilter(Document row, Map<String, String> tuSchema) {
        and(
                eq(tuSchema.USUBJID, row.USUBJID),
                eq(tuSchema.TUTESTCD, 'TUMIDENT'),
                eq(tuSchema.TUSTRESC, 'NEW'),
                or(eq(tuSchema.VISITNUM, asDouble(row.VISITNUM)), eq(tuSchema.VISITNUM, row.VISITNUM)))
    }

    @Override
    public void process(MongoDatabase mongo, Writer writer) throws IOException {
        MongoCollection<Document> rs = mongo.getCollection("${sessionId}_RS")
        MongoCollection<Document> tu = mongo.getCollection("${sessionId}_TU")
        MongoCollection<Document> sv = mongo.getCollection("${sessionId}_SV")

        MongoCollection<Document> supprs = mongo.getCollection("${sessionId}_SUPPRS")

        def rsSchema = schema(rs)
        def tuSchema = schema(tu)
        def svSchema = schema(sv)

        try {
            writer.open(study, 'recist2', HEADER)

            rs.aggregate([upper(rs, [:]), match(getDomainFilter())]).each { row ->
                Document rsInvopresRow = rs.find(getRsInvopresFilter(row, rsSchema)).first()
                remap(rsInvopresRow, rsSchema)

                row.RSSPFY = getReasonRECISTAndInvestigatorAssessmentsDiffer(row, rsInvopresRow, supprs)
                row.invawrec = getInvawrec(row, rsInvopresRow, supprs)
                row.investigatorOpinionOfPatientStatus = getInvestigatorOpinionOfPatientStatus(row, rsInvopresRow, supprs)
                row.RSSTRESC = getRecistResponse(row, rs)

                Document tuRow = tu.find(getTuFilter(row, tuSchema)).first()

                remap(tuRow, tuSchema)

                if (tuRow) {
                    row.newLesion = true
                    row.TULOC = tuRow.TULOC
                    row.TUDTC = getLesionScanDate(tuRow)
                }

                Document svRow = sv
                        .find(and(eq(svSchema.USUBJID, row.USUBJID), nin('VISITNUM', null, '')))
                        .toList()
                        .find { Math.abs(asDouble(it.VISITNUM) - asDouble(row.VISITNUM)) < 1e-5 }
                row.VISITDTC = getVisitDate(row, svRow, tuRow)

                writer.write(map(row))
            }
        } finally {
            writer.close()
        }
    }

    protected Bson getRsInvopresFilter(Document row, Map<String, String> rsSchema) {
        and(
                eq(rsSchema.USUBJID, row.USUBJID),
                eq(rsSchema.RSTESTCD, 'INVOPRES'),
                eq(rsSchema.VISITNUM, row.VISITNUM))
    }
}
