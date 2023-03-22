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
     import com.mongodb.BasicDBObject
import com.mongodb.client.MongoCollection
import com.mongodb.client.MongoDatabase
import groovy.util.logging.Slf4j
import org.bson.Document
     import org.bson.conversions.Bson

     @Slf4j
public abstract class BaseDECGProcessor extends BaseEntityProcessor {

    def HEADER = ['STUDY', 'PART', 'SUBJECT', 'VISIT', 'EGDAT', 'EGTESTCD', 'EGSTRESC', 'EGCLSIG', 'EGSPFY','EGTIM']

    Map<String, String> map(Document row) {
        [
                STUDY   : row.STUDYID as String,
                PART    : 'A',
                SUBJECT : getSubject(row.USUBJID as String),
                VISIT   : row.VISITNUM as String,
                EGDAT   : row.egdat as String,
                EGTESTCD: row.egtestcd as String,
                EGSTRESC: row.testResult as String,
                EGCLSIG : row.egclsigValue as String,
                EGSPFY  : row.egspfy as String
        ]
    }

    public EntityType getEntityType() {
        return EntityType.Decg
    }

    protected String getTestOrExaminationShortName(String name) {
        return name
    }

    protected String getReasonAbnormalOverallEcgEvaluation(Document egRow, MongoCollection suppeg, def group) {
        return null
    }

    String getTestResult(Document row) {
        return row.EGSTRESC
    }

    protected String getMeasurementDate(Document row) {
        return row.EGDTC
    }

    protected String getClinicallySignificant(Document egRow, MongoCollection<Document> suppeg, List<Document> suppRows) {
        return suppRows.find { it['QNAM'] == 'EGCLSIG' }?.QVAL
    }

    @Override
    Bson getDomainFilter() {
        return super.getDomainFilter()
    }

    @Override
    public void process(MongoDatabase mongo, Writer writer) throws IOException {

        MongoCollection<Document> eg = mongo.getCollection("${sessionId}_EG")
        MongoCollection<Document> suppeg = mongo.getCollection("${sessionId}_SUPPEG")
        suppeg.createIndex(new BasicDBObject([USUBJID: 1, QNAM: 1, IDVARVAL: 1]))
        final group = suppeg
                .find()
                .asList()
                .groupBy { it.USUBJID }
                .collectEntries { outer -> [(outer.key): outer.value.groupBy { inner -> (inner.IDVARVAL as String) }] }
        try {
            writer.open(study, 'decg', HEADER)

            eg.find(getDomainFilter()).each { row ->
                List<Document> suppRows = group[row.USUBJID] ? group[row.USUBJID][row.EGSEQ as String] : []
                row.egtestcd = getTestOrExaminationShortName(row.EGTESTCD as String)
                row.testResult = getTestResult(row)
//                Bson joinFilter = and(eq('USUBJID', row.USUBJID), eq('QNAM', 'EGCLSIG'), eq('IDVARVAL', row.EGSEQ))
//                row.egclsigValue = getSuppFirstQval(row, suppeg, 'EGCLSIG', 'EGSEQ', joinFilter)
                row.egclsigValue = getClinicallySignificant(row, suppeg, suppRows)

                row.egspfy = getReasonAbnormalOverallEcgEvaluation(row, suppeg, suppRows)
                row.egdat = getMeasurementDate(row)

                writer.write(map(row))
            }
        } finally {
            writer.close()
        }
    }
}
