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

@Slf4j
public abstract class BasePatientGroupInformationProcessor extends BaseEntityProcessor {
    def HEADER = ['STUDY', 'PART', 'SUBJECT', 'GROUP_NAME', 'GROUPING_NAME']

    @Override
    Map<String, String> map(Document row) {
        [
                STUDY        : row.STUDYID as String,
                PART         : 'A',
                SUBJECT      : getSubject(row.USUBJID as String),
                GROUP_NAME   : row.groupName as String,
                GROUPING_NAME: row.groupingName as String,
        ]
    }

    @Override
    EntityType getEntityType() {
        return EntityType.PatientGroupInformation
    }

    protected String getGroupName(Document dmRow, MongoCollection<Document> suppdm) {
        return null
    }

    protected String getGroupingName(Document dmRow, MongoCollection<Document> suppdm) {
        return null
    }

    @Override
    public void process(MongoDatabase mongo, Writer writer) throws IOException {

        MongoCollection<Document> dm = mongo.getCollection("${sessionId}_DM")
        MongoCollection<Document> suppdm = mongo.getCollection("${sessionId}_SUPPDM")

        try {
            writer.open(study, 'patient_group_information', HEADER)

            dm.find().each { row ->

                row.groupName = getGroupName(row, suppdm)
                row.groupingName = getGroupingName(row, suppdm)

                writer.write(map(row))
            }
        } finally {
            writer.close()
        }

    }
}
