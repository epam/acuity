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
import com.acuity.sdtm.transform.processor.SDTM_base.BaseSubjectCharacteristicsProcessor
import com.acuity.sdtm.transform.common.Version
import com.mongodb.client.MongoCollection
import com.mongodb.client.MongoDatabase
import org.bson.Document
import org.springframework.stereotype.Component

import static com.acuity.sdtm.transform.common.Study.*
import static com.acuity.sdtm.transform.util.Util.parseSubjectSpecific

@Component
public class SDTM_1_1_SubjectCharacteristicsProcessor extends BaseSubjectCharacteristicsProcessor implements SDTM_1_1_StudyCommonProcessor {

    @Override
    public Version getVersion() {
        return Version.SDTM_1_1
    }

    @Override
    String getSubject(String val) {
        if (studyIn(DR22)) {
            return parseSubjectSpecific(val)
        } else {
            return super.getSubject(val)
        }
    }

    @Override
    boolean isStudyProducesThisTypeOfEntity() {
        return studyIn(DR9, DR12, DR22, DR24)
    }

    @Override
    protected String getEthnicPopulation(Document row, MongoCollection<Document> suppsc) {
        if (studyIn(DR9)) {
            return row.SCSTRESC
        } else if (studyIn(DR22, DR24)) {
            return row.ETHNIC
        } else {
            return super.getEthnicPopulation(row, suppsc)
        }
    }

    @Override
    protected String getOtherEthnicPopulation(Document scRow, MongoCollection<Document> suppsc) {
        if (studyIn(DR9)) {
            return scRow.SCTEST
        } else {
            return super.getEthnicPopulation(scRow, suppsc)
        }
    }

    @Override
    protected String getVisitNumber(Document row, MongoCollection<Document> supp) {
        if (studyIn(DR22, DR24)) {
            return null
        } else {
            return super.getVisitNumber(row, supp)
        }
    }

    @Override
    public void process(MongoDatabase mongo, Writer writer) throws IOException {
        if (studyIn(DR22, DR24)) {

            MongoCollection<Document> dm = mongo.getCollection("${sessionId}_DM")

            try {
                writer.open(study, 'subject_characteristics', getHeader())

                dm.find().each { row ->

                    row.ethnicPopulation = getEthnicPopulation(row, dm)
                    row.otherEthnicPopulation = getOtherEthnicPopulation(row, dm)
                    row.visitNum = getVisitNumber(row, dm)
                    writer.write(map(row))
                }
            } finally {
                writer.close()
            }
        } else super.process(mongo, writer)
    }
}
