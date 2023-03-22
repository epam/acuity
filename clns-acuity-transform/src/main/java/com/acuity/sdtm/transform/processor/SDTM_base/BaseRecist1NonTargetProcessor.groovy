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
import com.acuity.sdtm.transform.util.Util
import com.mongodb.client.MongoCollection
import com.mongodb.client.MongoDatabase
import groovy.util.logging.Slf4j
import org.bson.Document
import org.bson.conversions.Bson

import static com.mongodb.client.model.Aggregates.match
import static com.mongodb.client.model.Filters.and
import static com.mongodb.client.model.Filters.eq

@Slf4j
public abstract class BaseRecist1NonTargetProcessor extends BaseEntityProcessor {

    def HEADER = ['STUDY', 'PART', 'SUBJECT', 'VIS_DAT', 'VISIT', 'R2ASMDAT', 'R2LESSIT', 'R2PRES']

    Map<String, String> map(Document row) {
        [
                STUDY   : row.STUDYID as String,
                PART    : 'A',
                SUBJECT : getSubject(row.USUBJID as String),
                VISIT   : row.visitNumber as String,
                VIS_DAT : row.visitDate as String,
                R2ASMDAT: row.lesionScanDate as String,
                R2LESSIT: row.newLesionSite as String,
                R2PRES  : row.anyNonTargetLesionPresent as String
        ]
    }

    @Override
    public EntityType getEntityType() {
        return EntityType.Recist1NonTarget
    }

    @Override
    Bson getDomainFilter() {
        and(eq('TUSTRESC', 'NON-TARGET'), eq('TUTESTCD', 'TUMIDENT'))
    }

    protected String getVisitDate(Document row, MongoCollection<Document> supp) {
        getSuppFirstQval(row, supp, 'VISITDTC', 'TUSEQ')
    }

    protected String getVisitNumber(Document row, MongoCollection<Document> supp) {
        row.VISITNUM
    }

    protected String getNewLesionSite(Document tuRow, MongoCollection<Document> supptu) {
        tuRow.TULOC
    }

    protected String getLesionScanDate(Document row) {
        row.TUDTC
    }

    @Override
    public void process(MongoDatabase mongo, Writer writer) throws IOException {
        MongoCollection<Document> tu = mongo.getCollection("${sessionId}_TU")
        MongoCollection<Document> supptu = mongo.getCollection("${sessionId}_SUPPTU")

        performProcess(tu, supptu, writer)
    }

    protected performProcess(MongoCollection<Document> main,
                             MongoCollection<Document> supp, Writer writer) {
        try {
            writer.open(study, 'recist1_nontarget', HEADER)

            main.aggregate([upper(main, [:]), match(getDomainFilter())]).each { row ->
                row.anyNonTargetLesionPresent = isAnyNonTargetLesionPresent(row)
                row.visitDate = getVisitDate(row, supp)
                row.visitNumber = getVisitNumber(row, supp)
                row.newLesionSite = getNewLesionSite(row, supp)
                row.lesionScanDate = getLesionScanDate(row)
                writer.write(map(row))
            }
        } finally {
            writer.close()
        }
    }

    protected String isAnyNonTargetLesionPresent(Document row) {
        return Util.YES
    }
}
