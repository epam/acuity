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
import com.mongodb.client.model.Accumulators
import com.mongodb.client.model.Filters
import com.mongodb.client.model.Sorts
import groovy.util.logging.Slf4j
import org.bson.Document
import org.bson.conversions.Bson

import static com.acuity.sdtm.transform.util.Util.parseDoseFrequencyToCdashString
import static com.mongodb.client.model.Aggregates.*

/**
 * Created by knml167 on 08/06/2015.
 */

@Slf4j
public abstract class BaseConmedProcessor extends BaseEntityProcessor {
    def HEADER = ['STUDY', 'PART', 'SUBJECT', 'MEDPREF', 'MEDGROUP', 'MEDTDDOS', 'MEDDOSU', 'MDOSFRQ',
                  'ATCCODE', 'MED_SDAT', 'MED_EDAT', 'TRREAS']

    @Override
    Map<String, String> map(Document row) {
        [
                STUDY   : row.STUDYID as String,
                PART    : 'A',
                SUBJECT : getSubject(row.USUBJID as String),
                MEDPREF : row.medicationName as String,
                MEDGROUP: row.cmGroup as String,
                MEDTDDOS: row.dosePerAdministration as String,
                MEDDOSU : row.doseUnit as String,
                MDOSFRQ : row.doseFrequency as String,
                ATCCODE : row.atcCode as String,
                MED_SDAT: row.medStartDate as String,
                MED_EDAT: row.medEndDate as String,
                TRREAS  : row.reasonForTreatment as String
        ]
    }

    @Override
    public EntityType getEntityType() {
        return EntityType.Conmed
    }

    @Override
    Bson getDomainFilter() {
        return Filters.in('CMCAT',
                'GENERAL CONCOMITANT MEDICATION',
                'GENERAL CONCOMITANT MEDICATIONS',
                'PREVIOUS AND CONCOMITANT MEDICATION',
                'GENERAL CONMED',
                'CONCOMITANT MEDICATIONS LOG',
                'CONCOMITANT MEDICATIONS'
        )
    }


    protected String getConmedAtcCode(Document cmRow, MongoCollection suppcm) {
        return getSuppFirstQval(cmRow, suppcm, 'ATCCD', 'CMSEQ')
    }

    protected String getMedicationGroup(Document cmRow, MongoCollection suppcm) {
        return getSuppFirstQval(cmRow, suppcm, 'CMGROUP', 'CMSEQ')
    }

    protected String getDosePerAdministration(Document row, MongoCollection<Document> supp) {
        row.CMDOSE
    }

    protected String getReasonForTreatment(Document row, MongoCollection<Document> supp) {
        return row.CMINDC
    }

    protected String getDoseUnit(Document row, MongoCollection<Document> documentMongoCollection) {
        row.CMDOSU
    }

    protected String getDoseFrequency(Document row) {
        parseDoseFrequencyToCdashString(row.CMDOSFRQ as String)
    }

    protected String getMedStartDate(Document row) {
        return row.CMSTDTC
    }

    protected String getMedEndDate(Document row) {
        return row.CMENDTC
    }

    protected Bson fillMedicationName() {
        return new BasicDBObject('$ifNull', ['$CMDECOD', '$CMTRT'])
    }

    protected List<Bson> pipeline() {
        [
                //filter
                match(getDomainFilter()),
                //medicationName = row.CMDECOD ?: row.CMTRT
                project(new BasicDBObject([
                        CUR           : '$$CURRENT',
                        medicationName: fillMedicationName()])
                ),
                //sort by row.CMENDTC
                sort(Sorts.ascending('CUR.CMENDTC')),
                //group + save last values
                group(
                        new BasicDBObject([
                                USUBJID       : '$CUR.USUBJID',
                                CMSTDTC       : '$CUR.CMSTDTC',
                                medicationName: '$medicationName']),
                        Accumulators.last('last', '$$ROOT'), Accumulators.push('elems', '$$ROOT'))
        ]
    }

    @Override
    public void process(MongoDatabase mongo, Writer writer) throws IOException {

        MongoCollection<Document> cm = mongo.getCollection("${sessionId}_CM")
        MongoCollection<Document> suppcm = mongo.getCollection("${sessionId}_SUPPCM")

        try {
            writer.open(study, 'conmed', HEADER)
            cm.aggregate(pipeline()).each { grp ->

                Document last = grp.last
                (grp.elems as List).stream().filter { elem -> elem.CUR.CMENDTC == null }.findAny().ifPresent { elem -> last = elem }
                Document row = last.CUR

                row.medicationName = last.medicationName
                row.cmGroup = getMedicationGroup(row, suppcm)
                row.dosePerAdministration = getDosePerAdministration(row, suppcm)
                row.doseUnit = getDoseUnit(row, suppcm)
                row.doseFrequency = getDoseFrequency(row)
                row.atcCode = getConmedAtcCode(row, suppcm)
                row.medStartDate = getMedStartDate(row)
                row.medEndDate = getMedEndDate(row)
                row.reasonForTreatment = getReasonForTreatment(row, suppcm)
                writer.write(map(row))
            }
        } finally {
            writer.close()
        }
    }
}
