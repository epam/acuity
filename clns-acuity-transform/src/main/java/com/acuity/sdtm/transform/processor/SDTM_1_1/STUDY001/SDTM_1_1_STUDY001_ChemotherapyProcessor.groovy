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
import com.acuity.sdtm.transform.processor.SDTM_1_1.SDTM_1_1_ChemotherapyProcessor
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
class SDTM_1_1_STUDY001_ChemotherapyProcessor extends SDTM_1_1_ChemotherapyProcessor
        implements SDTM_1_1_STUDY001_StudyCommonProcessor {

    def NEW_HEADER = ['STUDY', 'PART', 'SUBJECT', 'THERAPY', 'START', 'END', 'CXCANCER', 'BEST_RESPONSE']

    @Override
    Map<String, String> map(Document row) {
        [
                STUDY        : row.STUDYID as String,
                PART         : row.PART as String,
                SUBJECT      : row.SUBJECT as String,
                THERAPY      : row.THERAPY as String,
                START        : row.START as String,
                END          : row.END as String,
                BEST_RESPONSE: row.BEST_RESPONSE as String,
                CXCANCER     : row.CXCANCER as String
        ]
    }

    @Override
    boolean isStudyProducesThisTypeOfEntity() {
        return studyIn(STUDY001)
    }

    @Override
    Bson getDomainFilter() {
        or(
                or(eq('CHTX1', '1'), eq('CHTX1', 1)),
                or(eq('CHTX2T', '1'), eq('CHTX2T', 1)),
                or(eq('CHTX3T', '1'), eq('CHTX3T', 1))
        )
    }

    private static List<Document> processRow(Document row) {
        List<Document> rows = new ArrayList<>()
        if ('1' == (row.CHTX1 as String)) {
            rows.add(createChemo(row, row.CHTX1D as String, row.CHTDAT as String, row.CHTEDAT as String))
        }
        if ('1' == (row.CHTX2T as String)) {
            rows.add(createChemo(row, row.CHTX2D as String, row.CHTDAT2 as String, row.CHTEDAT2 as String))
        }
        if ('1' == (row.CHTX3T as String)) {
            rows.add(createChemo(row, row.CHTX3D as String, row.CHTDT3 as String, row.CHTEDAT3 as String))
        }
        return rows
    }

    private static Document createChemo(Document row, String therapy, String start, String end) {
        Document chemo = new Document()
        chemo.STUDYID = row.STUDYID as String
        chemo.PART = row.PART as String
        chemo.SUBJECT = row.SUBNUM as String
        chemo.THERAPY = cutUpToBytesOfUTF8(therapy, MAX_ACUITY_LENGTH)
        chemo.START = new UnknownDate(start).convertNormalizedTo(Util.MM_DD_YYYY_SLASHED, true)
        chemo.END = new UnknownDate(end).convertNormalizedTo(Util.MM_DD_YYYY_SLASHED, false)
        chemo.BEST_RESPONSE = cutUpToBytesOfUTF8(row.CHRSP_DEC as String, MAX_ACUITY_LENGTH)
        chemo.CXCANCER = SDTM_1_1_STUDY001_Util.getCxCancer(chemo.START as String, chemo.END as String, row.EXSTDAT as String, row.DSSTDAT as String)
        return chemo
    }

    private static Bson getJoinTableFilter(Document row) {
        return eq('SUBNUM', row.SUBNUM)
    }

    @Override
    void process(MongoDatabase mongo, Writer writer) throws IOException {
        MongoCollection<Document> mh = mongo.getCollection("${sessionId}_MH1")
        MongoCollection<Document> ex = mongo.getCollection("${sessionId}_EX")
        MongoCollection<Document> ds = mongo.getCollection("${sessionId}_DS")

        try {
            writer.open(study, module, 'caprx', NEW_HEADER)
        } finally {
            mh.find(getDomainFilter()).each { row ->
                Document exRow = ex.find(getJoinTableFilter(row)).first()
                Document dsRow = ds.find(getJoinTableFilter(row)).first()
                row.EXSTDAT = exRow?.EXSTDAT
                row.DSSTDAT = dsRow?.DSSTDAT
                processRow(row).each { r -> writer.write(map(r)) }
            }
            writer.close()
        }
    }
}
