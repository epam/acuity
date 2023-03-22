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

import static com.acuity.sdtm.transform.util.Util.parseSex

/**
 * Created by knml167 on 08/06/2015.
 */

@Slf4j
public abstract class BaseDemographyProcessor extends BaseEntityProcessor {
    def HEADER = ['STUDY', 'PART', 'SUBJECT', 'CENTER', 'SEX', 'RACE', 'VIS_DAT', 'BIRTHDAT']

    @Override
    Map<String, String> map(Document row) {
        [
                STUDY   : row.STUDYID as String,
                PART    : row.part as String,
                SUBJECT : getSubject(row.USUBJID as String),
                CENTER  : row.SITEID as String ?: getCenter(row.USUBJID as String),
                SEX     : parseSex(row.SEX as String),
                RACE    : row.race as String,
                VIS_DAT : row.visitDate as String,
                BIRTHDAT: row.BRTHDTC as String
        ]
    }

    @Override
    public EntityType getEntityType() {
        return EntityType.Demography
    }

    protected List<String> getHeader(){
        return HEADER
    }

    protected String getVisitDate(Document dmRow, MongoCollection suppdm) {
        return getSuppFirstQval(dmRow, suppdm, 'VISITDTC') ?: dmRow.DMDTC
    }

    protected String getRace(Document row, MongoCollection<Document> supp) {
        return row.RACE
    }

    protected String getPart(Document row, MongoCollection<Document> supp) {
        return 'A'
    }

    protected String getBirthDate(Document row, MongoCollection<Document> supp) {
        return row.BRTHDTC
    }

    @Override
    public void process(MongoDatabase mongo, Writer writer) throws IOException {

        MongoCollection<Document> dm = mongo.getCollection("${sessionId}_DM")
        MongoCollection<Document> suppdm = mongo.getCollection("${sessionId}_SUPPDM")

        try {
            writer.open(study, 'dem', getHeader())

            dm.find().each { row ->
                row.part = getPart(row, suppdm)
                row.visitDate = getVisitDate(row, suppdm)
                row.race = getRace(row, suppdm)
                row.BRTHDTC = getBirthDate(row, suppdm)
                writer.write(map(row))
            }
        } finally {
            writer.close()
        }
    }
}
