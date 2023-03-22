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
public abstract class BaseLvefProcessor extends BaseEntityProcessor {
    def HEADER = ['STUDY', 'PART', 'SUBJECT', 'VISIT', 'EFDAT', 'LVEF', 'LVEFMTH', 'LVEFMTHO']

    Map<String, String> map(Document row) {
        [
                STUDY   : getStudyId(row.STUDYID as String),
                PART    : 'A',
                SUBJECT : getSubject(row.USUBJID as String),
                VISIT   : row.VISITNUM as String,
                EFDAT   : row.efdat as String,
                LVEF    : row.lvef as String,
                LVEFMTH : row.lvefmth as String,
                LVEFMTHO: null
        ]
    }

    @Override
    public EntityType getEntityType() {
        return EntityType.Lvef
    }

    String getStudyId(String studyId) {
        return studyId
    }

    @Override
    Bson getDomainFilter() {
        return Filters.eq('EGTESTCD', 'LVEF')
    }

    protected abstract fillDate(Document row)

    protected abstract fillLvef(Document row)

    protected abstract fillMethod(Document row)


    @Override
    void process(MongoDatabase mongo, Writer writer) throws IOException {
        MongoCollection<Document> eg = mongo.getCollection("${sessionId}_EG")
        performProcess(eg, writer)
    }


    protected void performProcess(MongoCollection<Document> mongoColl, Writer writer) throws IOException {
        try {
            writer.open(study, 'lvef', HEADER)

            mongoColl.find(getDomainFilter()).each { row ->
                fillDate(row)
                fillLvef(row)
                fillMethod(row)
                writer.write(map(row))
            }
        } finally {
            writer.close()
        }
    }
}
