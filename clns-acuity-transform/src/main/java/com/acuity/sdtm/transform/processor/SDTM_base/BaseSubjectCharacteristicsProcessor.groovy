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

@Slf4j
public abstract class BaseSubjectCharacteristicsProcessor extends BaseEntityProcessor {
    def HEADER = ['STUDY', 'PART', 'SUBJECT', 'VISITNUM', 'ETHPOP', 'S_ETHPOP']

    @Override
    Map<String, String> map(Document row) {
        [
                STUDY   : row.STUDYID as String,
                PART    : 'A',
                SUBJECT : getSubject(row.USUBJID as String),
                VISITNUM: row.visitNum as String,
                ETHPOP  : row.ethnicPopulation as String,
                S_ETHPOP: row.otherEthnicPopulation as String
        ]
    }

    @Override
    Bson getDomainFilter() {
        return null
    }

    @Override
    public EntityType getEntityType() {
        return EntityType.SubjectCharacteristics
    }

    protected String getEthnicPopulation(Document scRow, MongoCollection<Document> suppsc) {
        return null
    }

    protected String getOtherEthnicPopulation(Document scRow, MongoCollection<Document> suppsc) {
        return null
    }

    protected String getVisitNumber(Document row, MongoCollection<Document> supp) {
        row.VISITNUM
    }

    @Override
    public void process(MongoDatabase mongo, Writer writer) throws IOException {

        MongoCollection<Document> sc = mongo.getCollection("${sessionId}_SC")
        MongoCollection<Document> suppsc = mongo.getCollection("${sessionId}_SUPPSC")

        try {
            writer.open(study, 'subject_characteristics', HEADER)

            sc.find().each { row ->

                row.ethnicPopulation = getEthnicPopulation(row, suppsc)
                row.otherEthnicPopulation = getOtherEthnicPopulation(row, suppsc)
                row.visitNum=getVisitNumber(row,suppsc)
                writer.write(map(row))
            }
        } finally {
            writer.close()
        }

    }
}
