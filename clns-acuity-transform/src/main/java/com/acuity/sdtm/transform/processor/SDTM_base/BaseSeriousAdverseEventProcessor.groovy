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

import static com.acuity.sdtm.transform.util.Util.parseSubject
import static com.mongodb.client.model.Filters.eq

/**
 * Created by knml167 on 08/06/2015.
 */

@Slf4j
abstract class BaseSeriousAdverseEventProcessor extends BaseEntityProcessor {
    def HEADER = ['STUDY', 'PART', 'SUBJECT', 'AETERM', 'AEDESC01', 'AESDAT', 'AESIADAT', 'AESHODAT', 'AESDIDAT', 'AESDTH', 'AESLIFE', 'AESHOSP',
                  'AESDISAB', 'AESCONG', 'AESMIE', 'AESMEDCA', 'AESMED', 'AECAUSSP', 'AESP']

    @Override
    Map<String, String> map(Document row) {
        [
                STUDY   : row.STUDYID as String,
                PART    : 'A',
                SUBJECT : parseSubject(row.USUBJID as String),
                AETERM  : row.AETERM as String,
                AEDESC01: row.AEDESC01 as String,
                AESDAT  : row.SAEDTC as String,
                AESIADAT: row.SAEIADTC as String,
                AESHODAT: row.SAEHODTC as String,
                AESDIDAT: row.SAEDIDTC as String,
                AESDTH  : row.AESDTH as String,
                AESLIFE : row.AESLIFE as String,
                AESHOSP : row.AESHOSP as String,
                AESDISAB: row.AESDISAB as String,
                AESCONG : row.AESCONG as String,
                AESMIE  : row.AESMIE as String,
                AESMEDCA: row.causedByOtherMedication as String,
                AESMED  : row.otherMedicationOrDrugThatCausedAE as String,
                AECAUSSP: row.causedByStudyProcedure as String,
                AESP    : row.studyProcedureThatCausedAE as String,

        ]
    }

    @Override
    Bson getDomainFilter() {
        return eq('AESER', 'Y')
    }

    @Override
    EntityType getEntityType() {
        return EntityType.SeriousAdverseEvent
    }

    protected String getCausedByOtherMedication(Document aeRow, MongoCollection suppae) {
        return null
    }

    protected String getOtherMedicationOrDrugThatCausedAE(Document aeRow, MongoCollection suppae) {
        return null
    }

    protected String getCausedByStudyProcedure(Document aeRow, MongoCollection suppae) {
        return getSuppFirstQval(aeRow, suppae, 'SAECAUSP', 'AESEQ')
    }

    protected String getStudyProcedureThatCausedAE(Document aeRow, MongoCollection suppae) {
        return getSuppFirstQval(aeRow, suppae, 'SAESP', 'AESEQ')
    }

    @Override
    void process(MongoDatabase mongo, Writer writer) throws IOException {
        MongoCollection<Document> ae = mongo.getCollection("${sessionId}_AE")
        MongoCollection<Document> suppae = mongo.getCollection("${sessionId}_SUPPAE")

        try {
            writer.open(study, 'serious_adverse_event', HEADER)

            ae.find(getDomainFilter()).each { row ->

                row.AEDESC01 = getSuppFirstQval(row, suppae, 'AEDESC01', 'AESEQ')
                row.SAEIADTC = getSuppFirstQval(row, suppae, 'SAEIADTC', 'AESEQ')
                row.SAEDTC = getSuppFirstQval(row, suppae, 'SAEDTC', 'AESEQ')
                row.SAEHODTC = getSuppFirstQval(row, suppae, 'SAEHODTC', 'AESEQ')
                row.SAEDIDTC = getSuppFirstQval(row, suppae, 'SAEDIDTC', 'AESEQ')

                row.studyProcedureThatCausedAE = getStudyProcedureThatCausedAE(row, suppae)
                row.causedByStudyProcedure = getCausedByStudyProcedure(row, suppae)
                row.causedByOtherMedication = getCausedByOtherMedication(row, suppae)
                row.otherMedicationOrDrugThatCausedAE = getOtherMedicationOrDrugThatCausedAE(row, suppae)

                writer.write(map(row))
            }
        } finally {
            writer.close()
        }
    }
}
