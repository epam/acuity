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
import com.acuity.sdtm.transform.processor.SDTM_base.BaseDeathProcessor
import com.acuity.sdtm.transform.common.Version
import com.mongodb.client.MongoCollection
import com.mongodb.client.MongoDatabase
import org.bson.Document
import org.bson.conversions.Bson
import org.springframework.stereotype.Component

import static com.acuity.sdtm.transform.common.Study.*
import static com.acuity.sdtm.transform.util.Util.*
import static com.mongodb.client.model.Filters.eq
import static com.mongodb.client.model.Filters.nin

/**
 * Created by knml167 on 08/06/2015.
 */
@Component
public class SDTM_1_1_DeathProcessor extends BaseDeathProcessor implements SDTM_1_1_StudyCommonProcessor {

    @Override
    boolean isStudyProducesThisTypeOfEntity() {
        return !studyIn(STUDY001, STUDY002, STUDY003, STUDY004)
    }

    @Override
    public Version getVersion() {
        return Version.SDTM_1_1
    }

    @Override
    Map<String, String> map(Document row) {
        if (studyIn(DR22)) {
            [
                    *       : super.map(row),
                    DTHCAUSE: row.CETERM as String
            ]
        } else {
            return super.map(row)
        }
    }

    @Override
    String getSubject(String val) {
        if (studyIn(DR22)) {
            return parseSubjectSpecific(val)
        } else if (studyIn(DR23, DR25)) {
            return parseSubjectBySlash(val)
        } else {
            return super.getSubject(val)
        }
    }

    @Override
    Bson getDomainFilter() {
        if (studyIn(DR1, DR2)) {
            return eq('CECAT', 'DEATH')
        } else if (studyIn(DR11)) {
            return nin('DDDTC', null, '')
        } else if (studyIn(DR18, DR22)) {
            return nin('CESTDTC', null, '')
        }
        return super.getDomainFilter()
    }

    @Override
    protected String getDateOfDeath(Document row) {
        if (studyIn(DR1, DR2, DR18)) {
            return row.CESTDTC
        } else if (studyIn(DR11)) {
            return row.DDDTC
        } else if (studyIn(DR22)) {
            return parseDateTrimDash(row?.CESTDTC as String)
        }
        return super.getDateOfDeath(row)
    }

    @Override
    public void process(MongoDatabase mongo, Writer writer) throws IOException {
        MongoCollection<Document> rows
        if (studyIn(DR1, DR2, DR18, DR22)) {
            rows = mongo.getCollection("${sessionId}_CE")
        } else if (studyIn(DR11)) {
            rows = mongo.getCollection("${sessionId}_DD")
        } else {
            rows = mongo.getCollection("${sessionId}_DM")
        }
        super.performProcess(rows, writer)
    }
}
