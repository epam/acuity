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

package com.acuity.sdtm.transform.processor.SDTM_1_1.STUDY001

import com.acuity.sdtm.transform.io.writer.Writer
import com.acuity.sdtm.transform.processor.SDTM_1_1.SDTM_1_1_MedicalHistoryProcessor
import com.acuity.sdtm.transform.util.Util
import com.acuity.sdtm.transform.util.date.UnknownDate
import com.mongodb.client.MongoCollection
import com.mongodb.client.MongoDatabase
import org.bson.Document
import org.bson.conversions.Bson
import org.springframework.stereotype.Component

import static com.acuity.sdtm.transform.common.Study.STUDY001
import static com.acuity.sdtm.transform.util.StringUtil.MAX_ACUITY_LENGTH
import static com.acuity.sdtm.transform.util.StringUtil.cutUpToBytesOfUTF8
import static com.mongodb.client.model.Filters.eq
import static com.mongodb.client.model.Filters.or

@Component
class SDTM_1_1_STUDY001_MedicalHistoryProcessor extends SDTM_1_1_MedicalHistoryProcessor implements SDTM_1_1_STUDY001_StudyCommonProcessor {

    @Override
    boolean isStudyProducesThisTypeOfEntity() {
        return studyIn(STUDY001)
    }

    @Override
    Bson getDomainFilter() {
        or(eq('MHYN', '2'), eq('MHYN', 2))
    }

    @Override
    void process(MongoDatabase mongo, Writer writer) throws IOException {
        MongoCollection<Document> mh = mongo.getCollection("${sessionId}_MH1")

        try {
            writer.open(study, module, 'med_hist', HEADER)
            mh.find(getDomainFilter()).each { row ->
                row.MHSTDAT = new UnknownDate(row.MHSTDAT as String)
                        .convertNormalizedTo(Util.MM_DD_YYYY_SLASHED, true)
                row.MHENDAT = new UnknownDate(row.MHENDAT as String)
                        .convertNormalizedTo(Util.MM_DD_YYYY_SLASHED, false)
                row.MHCAT_DEC = cutUpToBytesOfUTF8(row.MHCAT_DEC as String, MAX_ACUITY_LENGTH)
                row.MHTERM = cutUpToBytesOfUTF8(row.MHTERM as String, MAX_ACUITY_LENGTH)
                row.MHONGO = cutUpToBytesOfUTF8(row.MHONGO as String, MAX_ACUITY_LENGTH)
                row.LLT_NAME = cutUpToBytesOfUTF8(row.LLT_NAME as String, MAX_ACUITY_LENGTH)
                row.PT_TERM = cutUpToBytesOfUTF8(row.PT_TERM as String, MAX_ACUITY_LENGTH)
                row.HLT_TERM = cutUpToBytesOfUTF8(row.HLT_TERM as String, MAX_ACUITY_LENGTH)
                row.SOC_TERM = cutUpToBytesOfUTF8(row.SOC_TERM as String, MAX_ACUITY_LENGTH)
                row.MHCURM = cutUpToBytesOfUTF8(row.MHCURM as String, MAX_ACUITY_LENGTH)
                writer.write(map(row))
            }
        } finally {
            writer.close()
        }
    }
}
