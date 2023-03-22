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

import static com.acuity.sdtm.transform.util.Util.*

/**
 * Created by knml167 on 08/06/2015.
 */

@Slf4j
public abstract class BaseDoseProcessor extends BaseEntityProcessor {
    def HEADER = ['STUDY', 'PART', 'SUBJECT', 'MDS_DRUG', 'MDS_DOSE', 'MDS_DOSE_UNIT', 'MDS_START_DATE', 'MDS_END_DATE',
                  'MDS_FREQUENCY', 'MDS_FREQUENCY_UNIT', 'MDS_ACTION_TAKEN', 'MDS_REASON_FOR_ACTION_TAKEN', 'MDS_DOSE_CHANGE_SPEC']

    @Override
    Map<String, String> map(Document row) {
        [
                STUDY                      : row.STUDYID as String,
                PART                       : 'A',
                SUBJECT                    : getSubject(row.USUBJID as String),
                MDS_DRUG                   : row.EXTRT as String,
                MDS_DOSE                   : row.EXDOSE as String,
                MDS_DOSE_UNIT              : row.EXDOSU as String,
                MDS_START_DATE             : row.EXSTDTC as String,
                MDS_END_DATE               : row.EXENDTC as String,
                MDS_FREQUENCY              : row.frequency as String,
                MDS_FREQUENCY_UNIT         : row.frequencyUnit as String,
                MDS_ACTION_TAKEN           : row.actionTaken as String,
                MDS_REASON_FOR_ACTION_TAKEN: row.reasonForActionTaken as String,
                MDS_DOSE_CHANGE_SPEC       : row.reasonForDoseChangeSpecification as String,

        ]
    }

    @Override
    public EntityType getEntityType() {
        return EntityType.Dose
    }

    protected String getDrugName(Document exRow) {
        return exRow.EXTRT
    }

    protected String getFrequency(Document exRow) {
        return parseDoseFrequency(exRow.EXDOSFRQ as String)
    }

    protected String getFrequencyUnit(Document exRow) {
        return parseDoseFrequencyUnit(exRow.EXDOSFRQ as String)
    }

    protected String getReasonForActionTaken(Document exRow, MongoCollection<Document> suppex) {
        return getSuppFirstQval(exRow, suppex, 'EXADJ', 'EXSEQ')
    }

    protected String getReasonForDoseChangeSpecification(Document exRow, MongoCollection<Document> suppex) {
        return getSuppFirstQval(exRow, suppex, 'EXADJDSC', 'EXSEQ')
    }

    protected String getActionTaken(Document exRow, MongoCollection<Document> suppex) {
        return getSuppFirstQval(exRow, suppex, 'EXACN', 'EXSEQ')
    }

    protected String getStartDate(Document exRow) {
        exRow.EXSTDTC
    }

    protected String getEndDate(Document exRow) {
        exRow.EXENDTC
    }

    @Override
    public void process(MongoDatabase mongo, Writer writer) throws IOException {

        MongoCollection<Document> ex = mongo.getCollection("${sessionId}_EX")
        MongoCollection<Document> suppex = mongo.getCollection("${sessionId}_SUPPEX")

        try {
            writer.open(study, 'target_med_dosing_schedule', HEADER)

            ex.find().each { row ->

                row.EXTRT = getDrugName(row)
                row.EXSTDTC = getStartDate(row)
                row.EXENDTC = getEndDate(row)

                row.frequency = getFrequency(row)
                row.frequencyUnit = getFrequencyUnit(row)

                row.actionTaken = getActionTaken(row, suppex)

                row.reasonForActionTaken = getReasonForActionTaken(row, suppex)

                row.reasonForDoseChangeSpecification = getReasonForDoseChangeSpecification(row, suppex)

                writer.write(map(row))
            }
        } finally {
            writer.close()
        }
    }
}
