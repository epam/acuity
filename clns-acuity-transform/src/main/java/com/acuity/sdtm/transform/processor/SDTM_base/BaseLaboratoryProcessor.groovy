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
public abstract class BaseLaboratoryProcessor extends BaseEntityProcessor {
    def HEADER = ['STUDY', 'PART', 'SUBJECT', 'VISIT', 'SAMP_DAT', 'LABCODE', 'LABVALUE', 'LAB_UNIT', 'REFLOW', 'REFHIGH']

    Map<String, String> map(Document row) {
        [
                STUDY   : row.STUDYID as String,
                PART    : 'A',
                SUBJECT : getSubject(row.USUBJID as String),
                VISIT   : row.VISITNUM as String,
                SAMP_DAT: row.LBDTC as String,
                LABCODE : (row.LBTEST ?: row.LBTESTCD) as String,
                LABVALUE: row.LBSTRESN as String,
                LAB_UNIT: row.LBSTRESU as String,
                REFLOW  : row.LBSTNRLO as String,
                REFHIGH : row.LBSTNRHI as String,
        ]
    }

    @Override
    public EntityType getEntityType() {
        return EntityType.Laboratory
    }

    protected String getLabValue(Document lbRow) {
        return lbRow.LBSTRESN
    }

    protected String getLabUnit(Document lbRow) {
        return lbRow.LBSTRESU
    }

    protected String getLabRefLo(Document lbRow) {
        return lbRow.LBSTNRLO
    }

    protected String getLabRefHi(Document lbRow) {
        return lbRow.LBSTNRHI
    }

    protected String getMeasurementDate(Document lbRow){
        return lbRow.LBDTC
    }

    @Override
    public void process(MongoDatabase mongo, Writer writer) throws IOException {
        MongoCollection<Document> lb = mongo.getCollection("${sessionId}_LB")

        try {
            writer.open(study, "laboratory", HEADER)

            lb.find(getDomainFilter()).each { row ->
                String labvalue = getLabValue(row)

                if (labvalue == '���100') {
                    //Hot-hotfix!! Skip some buggy records
                    return
                }
                row.LBSTRESN = getLabValue(row)
                row.LBSTRESU = getLabUnit(row)

                row.LBSTNRLO = getLabRefLo(row)
                row.LBSTNRHI = getLabRefHi(row)
                row.lbdtc=getMeasurementDate(row)
                writer.write(map(row))
            }
        } finally {
            writer.close()
        }
    }
}
