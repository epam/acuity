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
import com.acuity.sdtm.transform.processor.SDTM_base.BaseSeriousAdverseEventProcessor
import com.acuity.sdtm.transform.common.Version
import com.acuity.sdtm.transform.util.Util
import com.mongodb.client.MongoCollection
import com.mongodb.client.MongoDatabase
import org.bson.Document
import org.springframework.stereotype.Component

import static com.acuity.sdtm.transform.common.Study.*

/**
 * Created by knml167 on 08/06/2015.
 */
@Component
class SDTM_1_1_SeriousAdverseEventProcessor extends BaseSeriousAdverseEventProcessor implements SDTM_1_1_StudyCommonProcessor {
    @Override
    Version getVersion() {
        return Version.SDTM_1_1
    }

    @Override
    boolean isStudyProducesThisTypeOfEntity() {
        return !studyIn(DR16, DR13, DR12, DR10, DR6, DR11, DR17,
                DR19, DR18, DR21, DR22, DR23, DR25, STUDY001, STUDY002,
                STUDY003, STUDY004)
    }

    @Override
    protected String getCausedByOtherMedication(Document aeRow, MongoCollection suppae) {
        if (studyIn(DR15)) {
            if (aeRow.AERELNST?.startsWith('CAUSED BY OTHER MEDICATION ')) {
                return Util.YES
            } else {
                return Util.NO
            }
        }
        return super.getCausedByOtherMedication(aeRow, suppae)
    }

    @Override
    protected String getOtherMedicationOrDrugThatCausedAE(Document aeRow, MongoCollection suppae) {
        if (studyIn(DR15)) {
            if (aeRow.AERELNST?.startsWith('CAUSED BY OTHER MEDICATION ')) {
                return aeRow.AERELNST.substring(27)
            } else {
                return null
            }
        }
        return super.getOtherMedicationOrDrugThatCausedAE(aeRow, suppae)
    }

    private String getDeathResults(Document row) {
        if (studyIn(DR24)) {
            return null
        } else if (studyIn(DR20)) {
            return row.AEOUT == 'FATAL' ? Util.YES : Util.NO
        }
    }

    @Override
    void process(MongoDatabase mongo, Writer writer) throws IOException {
        if (studyIn(DR20, DR24)) {
            MongoCollection<Document> ae = mongo.getCollection("${sessionId}_AE")
            MongoCollection<Document> suppae = mongo.getCollection("${sessionId}_SUPPAE")

            try {
                writer.open(study, 'serious_adverse_event', HEADER)

                ae.find(getDomainFilter()).each { row ->

                    row.AEDESC01 = null
                    row.SAEIADTC = null
                    row.SAEDTC = null
                    row.SAEHODTC = null
                    row.SAEDIDTC = null
                    row.AESLIFE = null
                    row.AESHOSP = null
                    row.AESDISAB = null
                    row.AESCONG = null
                    row.AESMIE = null
                    row.AESDTH = getDeathResults(row)

                    row.studyProcedureThatCausedAE = null
                    row.causedByStudyProcedure = null
                    row.causedByOtherMedication = getCausedByOtherMedication(row, suppae)
                    row.otherMedicationOrDrugThatCausedAE = getOtherMedicationOrDrugThatCausedAE(row, suppae)

                    writer.write(map(row))
                }
            } finally {
                writer.close()
            }
        } else {
            super.process(mongo, writer)
        }
    }
}
