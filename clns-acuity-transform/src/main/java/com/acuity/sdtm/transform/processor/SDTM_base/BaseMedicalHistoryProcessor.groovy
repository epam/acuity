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
public abstract class BaseMedicalHistoryProcessor extends BaseEntityProcessor {

    def HEADER = ['STUDY', 'PART', 'SUBJECT', 'MEDICATION', 'MEDICATION_GROUP', 'STATUS', 'START', 'END', 'LLTNAME',
                  'PT_TERM', 'HLT_TERM', 'SOC_TERM', 'CURRENT_MEDICATION']

    Map<String, String> map(Document row) {
        return [
                STUDY             : row.STUDYID as String,
                PART              : row.part as String,
                SUBJECT           : row.SUBNUM as String,
                MEDICATION        : row.MHCAT_DEC as String,
                MEDICATION_GROUP  : row.MHTERM as String,
                STATUS            : row.MHONGO as String,
                START             : row.MHSTDAT as String,
                END               : row.MHENDAT as String,
                LLTNAME           : row.LLT_NAME as String,
                PT_TERM           : row.PT_TERM as String,
                HLT_TERM          : row.HLT_TERM as String,
                SOC_TERM          : row.SOC_TERM as String,
                CURRENT_MEDICATION: row.MHCURM as String
        ]
    }

    @Override
    public EntityType getEntityType() {
        return EntityType.MedicalHistory
    }

    @Override
    public void process(MongoDatabase mongo, Writer writer) throws IOException {
        MongoCollection<Document> mh = mongo.getCollection("${sessionId}_MH")

        try {
            writer.open(study, 'med_hist', HEADER)
            mh.find(getDomainFilter()).each { row ->
                writer.write(map(row))
            }
        } finally {
            writer.close()
        }
    }
}
