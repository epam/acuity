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

import static com.mongodb.client.model.Filters.nin

/**
 * Created by knml167 on 08/06/2015.
 */

@Slf4j
public abstract class BaseDeathProcessor extends BaseEntityProcessor {
    def HEADER = ['STUDY', 'PART', 'SUBJECT', 'DTH_DATE','DTHCAUSE']

    @Override
    Map<String, String> map(Document row) {
        [
                STUDY   : row.STUDYID as String,
                PART    : 'A',
                SUBJECT : getSubject(row.USUBJID as String),
                DTH_DATE: row.dateOfDeath as String
        ]
    }

    @Override
    public EntityType getEntityType() {
        return EntityType.Death
    }

    @Override
    Bson getDomainFilter() {
        return nin('DTHDTC', null, '')
    }

    protected String getDateOfDeath(Document row) {
        return row.DTHDTC
    }

    @Override
    public void process(MongoDatabase mongo, Writer writer) throws IOException {
        MongoCollection<Document> dm = mongo.getCollection("${sessionId}_DM")
        performProcess(dm, writer)
    }

    protected void performProcess(MongoCollection<Document> rows, Writer writer) {
        try {
            writer.open(study, "death", HEADER)

            rows.find(getDomainFilter()).each { row ->
                row.dateOfDeath = getDateOfDeath(row)
                writer.write(map(row))
            }
        } finally {
            writer.close()
        }
    }
}
