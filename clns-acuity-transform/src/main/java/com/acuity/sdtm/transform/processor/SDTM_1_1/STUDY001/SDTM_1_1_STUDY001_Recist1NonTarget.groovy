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
import com.acuity.sdtm.transform.processor.SDTM_1_1.SDTM_1_1_Recist1NonTargetProcessor
import com.acuity.sdtm.transform.util.Util
import com.mongodb.client.MongoCollection
import com.mongodb.client.MongoDatabase
import org.bson.Document
import org.bson.conversions.Bson
import org.springframework.stereotype.Component

import static com.mongodb.client.model.Filters.and
import static com.mongodb.client.model.Filters.eq
import static com.mongodb.client.model.Filters.or

@Component
class SDTM_1_1_STUDY001_Recist1NonTarget extends SDTM_1_1_Recist1NonTargetProcessor
        implements SDTM_1_1_STUDY001_StudyCommonProcessor {
    def NEW_HEADER = ['STUDY', 'PART', 'SUBJECT', 'LSID', 'LSLOC', 'LSMEAS1', 'VISITID', 'VISITDATE', 'LSMTRES']

    Map<String, String> map(Document row) {
        [
                STUDY    : 'STUDY001',
                PART     : 'A',
                SUBJECT  : row.SUBNUM as String,
                LSID     : row.LSID as String,
                LSLOC    : row.LSLOC as String,
                LSMEAS1  : row.LSMEAS1 as String,
                VISITID  : row.VISITID as String,
                VISITDATE: Util.parseDateWithFormat(row.VISITDATE as String),
                LSMTRES  : row.LSMTRES as String,
        ]
    }

    @Override
    Bson getDomainFilter() {
        or(
                and(eq('LSTP', '1'), eq('LSASMCRT', '13')),
                and(eq('LSTP', 1), eq('LSASMCRT', 13))
        )
    }

    private static String getResponse(Document row) {
        if (row.LSMTRES as String == '6') {
            return '4'
        } else if (row.LSMTRES as String == '7') {
            return '3'
        }
        row.LSMTRES
    }

    @Override
    void process(MongoDatabase mongo, Writer writer) throws IOException {
        MongoCollection<Document> ls1 = mongo.getCollection("${sessionId}_LS1")
        MongoCollection<Document> vs = mongo.getCollection("${sessionId}_VISIT")
        try {
            writer.open(study, module, 'recist', NEW_HEADER)
            ls1.find(getDomainFilter()).each { row ->
                row.VISITDATE = getVisitDate(row, vs)
                row.LSMTRES = getResponse(row)
                writer.write(map(row))
            }
        } finally {
            writer.close()
        }
    }
}
