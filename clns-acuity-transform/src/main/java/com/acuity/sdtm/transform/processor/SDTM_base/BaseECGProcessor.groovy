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
import groovy.util.logging.Slf4j
import org.bson.Document
import org.bson.conversions.Bson

import static com.mongodb.client.model.Filters.and
import static com.mongodb.client.model.Filters.ne

@Slf4j
public abstract class BaseECGProcessor extends BaseEntityProcessor {

    def HEADER = ['STUDY', 'PART', 'SUBJECT', 'DATE', 'VISIT', 'EGTEST', 'EGORRES', 'EGUNIT', 'EVAL', 'ABNORM', 'EGCLSIG']

    Map<String, String> map(Document row) {
        [
                STUDY  : row.STUDYID as String,
                PART   : 'A',
                SUBJECT: getSubject(row.USUBJID as String),
                DATE   : row.EGDTC as String,
                VISIT  : row.VISITNUM as String,
                EGTEST : row.testName as String,
                EGORRES: row.testResult as String,
                EGUNIT : row.EGORRESU as String,
                EVAL   : row.ECGEvaluation as String,
                ABNORM : row.abnormEvaluation as String,
                EGCLSIG: row.egclsigValue as String,
        ]
    }

    public EntityType getEntityType() {
        EntityType.Ecg
    }

    @Override
    Bson getDomainFilter() {
        and(ne('EGTESTCD', null), ne('EGTESTCD', ''))
    }

    protected String getTestName(Document egRow) {
        String testName = egRow.EGTEST
        if (testName in ["QTcF - Friderica Correction Formula", "QTcF - Friderica's Correction Formula"]) {
            "QTcF - Fridericia's Correction Formula"
        } else {
            testName
        }
    }

    protected String getEvaluation(Document row) {
        row.EGEVAL
    }

    protected String getTestResult(Document row) {
        row.EGORRES
    }

    protected String getAbnormEvaluation(Document row) {
        row.ABNORM
    }

    @Override
    public void process(MongoDatabase mongo, Writer writer) throws IOException {

        MongoCollection<Document> eg = mongo.getCollection("${sessionId}_EG")
        MongoCollection<Document> suppeg = mongo.getCollection("${sessionId}_SUPPEG")

        try {
            writer.open(study, 'ecg', HEADER)

            eg.find(getDomainFilter()).each { row ->

                row.ECGEvaluation = getEvaluation(row)
                row.abnormEvaluation = getAbnormEvaluation(row)
                row.testName = getTestName(row)
                row.testResult = getTestResult(row)
                row.egclsigValue = getSuppFirstQval(row, suppeg, 'EGCLSIG', 'EGSEQ')

                writer.write(map(row))
            }
        } finally {
            writer.close()
        }
    }
}
