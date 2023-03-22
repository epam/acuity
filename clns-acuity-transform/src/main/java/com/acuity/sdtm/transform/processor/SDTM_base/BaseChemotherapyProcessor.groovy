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

import static com.mongodb.client.model.Filters.*

/**
 * Created by knml167 on 08/06/2015.
 */

@Slf4j
public abstract class BaseChemotherapyProcessor extends BaseEntityProcessor {
    def HEADER = ['STUDY', 'PART', 'SUBJECT', 'VISIT', 'VIS_DAT', 'MEDPREF', 'CXSDAT', 'CXEDAT',
                  'CYCLENO', 'CXCLASS', 'TREATSTS', 'CXBRESP', 'RSTRTEND']

    @Override
    public EntityType getEntityType() {
        return EntityType.Chemotherapy
    }

    @Override
    Bson getDomainFilter() {
        or(
                eq('CMCAT', 'PRIOR SYSTEMIC THERAPY'),
                eq('CMSCAT', 'PRIOR SYSTEMIC THERAPIES'),
                and(
                        eq('CMCAT', 'CANCER THERAPY'),
                        ne('CMSCAT', 'RADIOTHERAPY')
                ),
                regex('CMSCAT', '^CHEMOTHERAPY$', 'i')
        )
    }

    @Override
    Map<String, String> map(Document row) {
        [
                STUDY   : row.STUDYID as String,
                PART    : 'A',
                SUBJECT : getSubject(row.USUBJID as String),
                VISIT   : row.VISITNUM as String,
                VIS_DAT : row.VISITDTC as String,
                MEDPREF : row.medicationName as String,
                CXSDAT  : row.CMSTDTC as String,
                CXEDAT  : row.CMENDTC as String,
                CYCLENO : row.cmdose as String,
                CXCLASS : row.therapyClass as String,
                TREATSTS: row.treatmentStatus as String,
                CXBRESP : row.bestResponseToTreatment as String,
                RSTRTEND: row.reasonForTherapyFailure as String
        ]
    }

    protected String getNumOfTreatmentCycles(Document row) {
        return row.CMDOSE
    }

    protected String getMedicationName(Document cmRow) {
        return cmRow.CMDECOD ?: cmRow.CMTRT
    }

    protected String getBestResponseToTreatment(Document row, MongoCollection suppConmed) {
        getSuppFirstQval(row, suppConmed, 'CXBRESP', 'CMSEQ') ?:
                getSuppFirstQval(row, suppConmed, 'BRESP', 'CMSEQ')
    }

    protected String getTreatmentStatus(Document row, MongoCollection suppConmed) {
        getSuppFirstQval(row, suppConmed, 'CXTRTST', 'CMSEQ')
    }

    protected String getReasonForTherapyFailure(Document row, MongoCollection suppConmed) {
        getSuppFirstQval(row, suppConmed, 'RSTRTEND', 'CMSEQ')
    }

    protected String getTherapyClass(Document row, MongoCollection suppConmed) {
        return row.CMSCAT
    }

    @Override
    public void process(MongoDatabase mongo, Writer writer) throws IOException {
        MongoCollection<Document> cm = mongo.getCollection("${sessionId}_CM")
        MongoCollection<Document> suppcm = mongo.getCollection("${sessionId}_SUPPCM")

        try {
            writer.open(study, 'caprx', HEADER)

            cm.find(getDomainFilter()).each { row ->

                row.cmdose = getNumOfTreatmentCycles(row)
                row.medicationName = getMedicationName(row)

                row.treatmentStatus = getTreatmentStatus(row, suppcm)

                row.therapyClass = getTherapyClass(row, suppcm)

                row.bestResponseToTreatment = getBestResponseToTreatment(row, suppcm)

                row.reasonForTherapyFailure = getReasonForTherapyFailure(row, suppcm)

                row.VISITDTC = getSuppFirstQval(row, suppcm, 'VISITDTC', 'CMSEQ')

                writer.write(map(row))
            }
        } finally {
            writer.close()
        }
    }
}
