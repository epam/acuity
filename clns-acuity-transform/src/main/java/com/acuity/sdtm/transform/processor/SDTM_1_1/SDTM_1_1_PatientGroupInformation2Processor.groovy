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

package com.acuity.sdtm.transform.processor.SDTM_1_1


import com.acuity.sdtm.transform.io.writer.Writer
import com.acuity.sdtm.transform.processor.SDTM_base.BasePatientGroupInformation2Processor
import com.acuity.sdtm.transform.common.Version
import com.mongodb.client.MongoCollection
import com.mongodb.client.MongoDatabase
import org.bson.Document
import org.springframework.stereotype.Component

import static com.acuity.sdtm.transform.common.Study.DR23
import static com.acuity.sdtm.transform.common.Study.DR25
import static com.acuity.sdtm.transform.util.Util.parseSubjectBySlash
import static com.mongodb.client.model.Filters.and
import static com.mongodb.client.model.Filters.eq

@Component
class SDTM_1_1_PatientGroupInformation2Processor extends BasePatientGroupInformation2Processor
        implements SDTM_1_1_StudyCommonProcessor {

    @Override
    boolean isStudyProducesThisTypeOfEntity() {
        return studyIn(DR23, DR25)
    }

    @Override
    String getSubject(String val) {
        if (studyIn(DR23, DR25)) {
            return parseSubjectBySlash(val)
        }
    }

    @Override
    Version getVersion() {
        Version.SDTM_1_1
    }

    protected String getGroupName(Document dmRow, MongoCollection<Document> mh) {
        if (studyIn(DR23, DR25)) {
            Document mhRow = mh.find(and(eq('MHCAT', 'DISEASE HISTORY'),
                    eq('USUBJID', dmRow.USUBJID)))?.first()
            return mhRow?.MHTERM
        }
    }

    protected String getGroupingName(Document dmRow) {
        if (studyIn(DR23)) {
            'Cohort'
        } else if (studyIn(DR25)) {
            'Cohort (Other)'
        }
    }

    @Override
    void process(MongoDatabase mongo, Writer writer) throws IOException {
        if (studyIn(DR23, DR25)) {
            MongoCollection<Document> dm = mongo.getCollection("${sessionId}_DM")
            MongoCollection<Document> mh = mongo.getCollection("${sessionId}_MH")
            try {
                writer.open(study, 'patient_group_information_2', HEADER)

                dm.find().each { row ->

                    row.groupName = getGroupName(row, mh)
                    row.groupingName = getGroupingName(row)

                    writer.write(map(row))
                }
            } finally {
                writer.close()
            }
        }
    }
}
