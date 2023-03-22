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

package com.acuity.sdtm.transform.processor.SDTM_1_1.STUDY002

import com.acuity.sdtm.transform.io.writer.Writer
import com.acuity.sdtm.transform.processor.SDTM_1_1.SDTM_1_1_DoseDiscontinuationProcessor
import com.mongodb.client.MongoCollection
import com.mongodb.client.MongoDatabase
import org.bson.Document
import org.springframework.stereotype.Component

import static com.acuity.sdtm.transform.common.Study.STUDY004
import static com.acuity.sdtm.transform.common.Study.STUDY003
import static com.acuity.sdtm.transform.util.Util.parseDate
import static com.acuity.sdtm.transform.util.Util.transformColumnsToUpperCase

@Component
class SDTM_1_1_STUDY002_Processor extends SDTM_1_1_DoseDiscontinuationProcessor
        implements SDTM_1_1_STUDY002_StudyCommonProcessor {
    def NEW_HEADER = ['SUBJECT', 'DATE_OF_DISCONTINUATION', 'REASON', 'FREE_TEXT']

    @Override
    Map<String, String> map(Document row) {
        [
                SUBJECT                : row.SUBJECT as String,
                DATE_OF_DISCONTINUATION: parseDate(row.CPOFFDAT_INT as Date),
                REASON                 : row.CPREASON as String,
                FREE_TEXT              : row.CPADDCOM as String
        ]
    }

    @Override
    void process(MongoDatabase mongo, Writer writer) throws IOException {
        MongoCollection<Document> cp = studyIn(STUDY003, STUDY004) ?
                mongo.getCollection("${sessionId}_RD_CP") :
                mongo.getCollection("${sessionId}_CP")
        try {
            writer.open(study, 'target_med_dos_disc', NEW_HEADER)
            cp.find()
                    .map({ doc -> transformColumnsToUpperCase(doc) })
                    .each { row ->
                        if (studyIn(STUDY004) || row.'CPYN' == 'No') {
                            writer.write(map(row))
                        }
                    }
        } finally {
            writer.close()
        }
    }
}
