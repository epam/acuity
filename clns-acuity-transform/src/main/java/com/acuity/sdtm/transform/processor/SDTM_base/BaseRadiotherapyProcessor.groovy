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
import com.mongodb.client.model.Filters
import groovy.util.logging.Slf4j
import org.bson.Document
import org.bson.conversions.Bson

/**
 * Created by knml167 on 08/06/2015.
 */

@Slf4j
public abstract class BaseRadiotherapyProcessor extends BaseEntityProcessor {
    def HEADER = ['STUDY', 'PART', 'SUBJECT', 'CXRDTEXT', 'CXRDSDAT', 'CXRDEDAT',
                  'TREATSTS', 'FRACGRAY', 'NOFRACRY', 'CXRD']

    @Override
    public EntityType getEntityType() {
        return EntityType.Radiotherapy
    }

    @Override
    Bson getDomainFilter() {
        Filters.regex('CMSCAT', '^RADIOTHERAPY', 'i')
    }

    @Override
    Map<String, String> map(Document row) {
        [
                STUDY   : row.STUDYID as String,
                PART    : 'A',
                SUBJECT : getSubject(row.USUBJID as String),
                CXRDTEXT: row.cmloc as String,
                CXRDSDAT: row.cmstdtc as String,
                CXRDEDAT: row.cmendtc as String,
                TREATSTS: row.cxtrtst as String,
                FRACGRAY: row.cmdose as String,
                NOFRACRY: row.nofracry as String,
                CXRD    : row.cxrd as String
        ]
    }

    String getNumberOfFractionDoses(Document cmRow, MongoCollection suppcm) {
        return getSuppFirstQval(cmRow, suppcm, 'NOFRACRY', 'CMSEQ')
    }

    String getRadiotherapySiteOrRegion(Document cmRow, MongoCollection suppcm) {
        return cmRow.CMLOC
    }

    String getTreatmentStatus(Document cmRow, MongoCollection suppcm) {
        return getSuppFirstQval(cmRow, suppcm, 'CXTRTST', 'CMSEQ')
    }

    String getRadiotherapyGiven(Document cmRow, MongoCollection suppcm) {
        String dose = cmRow.CMDOSE
        if (dose != null && dose.isNumber() && (dose as Double) > 0) {
            return Util.YES
        } else {
            return Util.NO
        }
    }

    String getRadioTherapyStartDate(Document row) {
        row.CMSTDTC
    }

    String getRadioTherapyStopDate(Document row) {
        row.CMENDTC
    }

    String getDose(Document row, MongoCollection supp) {
        row.CMDOSE
    }

    @Override
    public void process(MongoDatabase mongo, Writer writer) throws IOException {
        MongoCollection<Document> cm = mongo.getCollection("${sessionId}_CM")
        MongoCollection<Document> suppcm = mongo.getCollection("${sessionId}_SUPPCM")

        performProcess(cm, suppcm, writer)
    }

    protected performProcess(MongoCollection<Document> main, MongoCollection<Document> supp, Writer writer) {
        try {
            writer.open(study, 'caprxr', HEADER)

            main.find(getDomainFilter()).each { row ->
                row.cxrd = getRadiotherapyGiven(row, supp)
                row.cxtrtst = getTreatmentStatus(row, supp)
                row.cmstdtc = getRadioTherapyStartDate(row)
                row.cmendtc = getRadioTherapyStopDate(row)
                row.cmloc = getRadiotherapySiteOrRegion(row, supp)
                row.nofracry = getNumberOfFractionDoses(row, supp)
                row.cmdose = getDose(row, supp)

                writer.write(map(row))
            }
        } finally {
            writer.close()
        }
    }
}
