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

/**
 * Created by knml167 on 08/06/2015.
 */

@Slf4j
public abstract class BaseVisitProcessor extends BaseEntityProcessor {
    def HEADER = ['STUDY', 'PART', 'SUBJECT', 'VIS_NUMBER', 'VIS_DAT']

    @Override
    Map<String, String> map(Document row) {
        [
                STUDY     : row.STUDYID as String,
                PART      : 'A',
                SUBJECT   : getSubject(row.USUBJID as String),
                VIS_NUMBER: row.VISITNUM as String,
                VIS_DAT   : row.visdat as String,
        ]
    }

    @Override
    public EntityType getEntityType() {
        return EntityType.Visit
    }

    protected String getVisitDate(Document row) {
        return row.SVSTDTC
    }


    @Override
    public void process(MongoDatabase mongo, Writer writer) throws IOException {

        MongoCollection<Document> sv = mongo.getCollection("${sessionId}_SV")

        try {
            writer.open(study, 'visit', HEADER)

            sv.find().each { row ->
                row.visdat = getVisitDate(row)
                writer.write(map(row))
            }
        } finally {
            writer.close()
        }
    }
}
