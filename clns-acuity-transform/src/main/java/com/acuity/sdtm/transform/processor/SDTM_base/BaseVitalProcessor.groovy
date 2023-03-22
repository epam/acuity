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
public abstract class BaseVitalProcessor extends BaseEntityProcessor {
    def HEADER = ['STUDY', 'PART', 'SUBJECT', 'TEST_NAME', 'TEST_RESULT', 'RESULT_UNIT', 'PHYS_POSITION', 'ANATOM_LOC', 'ANATOM_SITE', 'VISIT', 'TEST_DATE', 'CLI_SIGN']

    @Override
    Map<String, String> map(Document row) {
        [
                STUDY        : row.STUDYID as String,
                PART         : 'A',
                SUBJECT      : getSubject(row.USUBJID as String),
                TEST_NAME    : row.VSTEST as String,
                TEST_RESULT  : row.VSSTRESN as String,
                RESULT_UNIT  : row.VSSTRESU as String,
                PHYS_POSITION: row.VSPOS as String,
                ANATOM_LOC   : row.VSLOC as String,
                ANATOM_SITE  : row.VSLAT as String,
                VISIT        : row.VISITNUM as String,
                TEST_DATE    : row.VSDTC as String,
                CLI_SIGN     : row.CLI_SIGN as String
        ]
    }

    @Override
    public EntityType getEntityType() {
        return EntityType.Vital
    }

    protected abstract fillAnatomicalLocation(Document row)

    protected abstract fillAnatomicalSideOfInterest(Document row)

    protected abstract fillPhysicalPosition(Document row)

    protected abstract fillClinicallySignificant(Document row, MongoCollection<Document> supp)

    protected abstract fillMeasurementDate(Document row)

    @Override
    public void process(MongoDatabase mongo, Writer writer) throws IOException {

        MongoCollection<Document> vs = mongo.getCollection("${sessionId}_VS")
        MongoCollection<Document> suppvs = mongo.getCollection("${sessionId}_SUPPVS")

        try {
            writer.open(study, 'vital', HEADER)

            vs.find().each { row ->
                fillAnatomicalLocation(row)
                fillAnatomicalSideOfInterest(row)
                fillPhysicalPosition(row)
                fillClinicallySignificant(row, suppvs)
                fillMeasurementDate(row)

                writer.write(map(row))
            }
        } finally {
            writer.close()
        }
    }
}
