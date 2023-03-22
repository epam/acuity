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
import com.acuity.sdtm.transform.processor.SDTM_base.BaseRecist1NonTargetProcessor
import com.acuity.sdtm.transform.common.Version
import com.acuity.sdtm.transform.util.Util
import com.mongodb.client.MongoCollection
import com.mongodb.client.MongoDatabase
import org.apache.commons.lang3.StringUtils
import org.bson.Document
import org.bson.conversions.Bson
import org.springframework.stereotype.Component

import static com.acuity.sdtm.transform.common.Study.*
import static com.acuity.sdtm.transform.util.Util.parseDateTrimDash
import static com.acuity.sdtm.transform.util.Util.parseSubjectSpecific
import static com.mongodb.client.model.Aggregates.match
import static com.mongodb.client.model.Filters.*

@Component
public class SDTM_1_1_Recist1NonTargetProcessor extends BaseRecist1NonTargetProcessor implements SDTM_1_1_StudyCommonProcessor {
    @Override
    boolean isStudyProducesThisTypeOfEntity() {
        return !studyIn(DR15, DR19, DR23, DR25, STUDY002, STUDY003, STUDY004)
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
    Bson getDomainFilter() {
        if (studyIn(DR13)) {
            eq('TUSTRESC', 'NON-TARGET')
        } else if (studyIn(DR16)) {
            and(
                    eq('TUSTRESC', 'NON-TARGET'),
                    eq('TUTESTCD', 'TUMIDENT'),
                    nin('TULOC', null, ''),
                    nin('TUDTC', null, ''),
            )
        } else {
            super.getDomainFilter()
        }
    }

    @Override
    public Version getVersion() {
        return Version.SDTM_1_1
    }

    @Override
    protected String getVisitDate(Document row, MongoCollection<Document> supp) {
        if (studyIn(DR17)) {
            def svRow = supp.find(and(
                    eq('USUBJID', row.USUBJID),
                    eq('VISITNUM', row.VISITNUM))).first()
            svRow?.SVSTDTC
        } else if (studyIn(DR20, DR18, DR21)) {
            null
        } else if (studyIn(DR22)) {
            return parseDateTrimDash(getSuppFirstQval(row, supp, 'VISITDTC', 'TUSEQ') as String)
        } else if (studyIn(DR24)) {
            return row.TUDTC
        } else if (studyIn(STUDY001)) {
            def vsRow = supp.find(and(eq('SUBID', row.SUBID), eq('VISITID', row.VISITID))).first()
            vsRow?.VISDAT
        } else {
            super.getVisitDate(row, supp)
        }
    }

    @Override
    protected String getVisitNumber(Document row, MongoCollection<Document> supp) {
        if (studyIn(DR18)) {
            null
        } else {
            return super.getVisitNumber(row, supp)
        }
    }

    @Override
    protected String getNewLesionSite(Document tuRow, MongoCollection<Document> supptu) {
        if (studyIn(DR13)) {
            if (StringUtils.equalsIgnoreCase(tuRow.TULOC as String, 'OTHER')) {
                return getSuppFirstQval(tuRow, supptu, 'TULOCDET', 'TUSEQ')
            }
        }
        return super.getNewLesionSite(tuRow, supptu)
    }

    @Override
    protected String isAnyNonTargetLesionPresent(Document trRow) {
        if (studyIn(DR20)) {
            trRow && trRow.TRSTRESC ? Util.YES : Util.NO
        } else if (studyIn(DR21)) {
            trRow && trRow.TRSTRESC == 'PRESENT' ? Util.YES : Util.NO
        } else if (studyIn(DR22)) {
            trRow?.TRTESTCD == 'TUMSTATE' ? 'YES' : null
        } else {
            return super.isAnyNonTargetLesionPresent(trRow)
        }
    }

    @Override
    protected String getLesionScanDate(Document row) {
        if (studyIn(DR22)) {
            return parseDateTrimDash(row?.TUDTC as String)
        } else {
            return super.getLesionScanDate(row)
        }
    }

    @Override
    void process(MongoDatabase mongo, Writer writer) throws IOException {
        if (studyIn(DR17)) {
            MongoCollection<Document> tu = mongo.getCollection("${sessionId}_TU")
            MongoCollection<Document> sv = mongo.getCollection("${sessionId}_SV")
            performProcess(tu, sv, writer)
        } else if (studyIn(DR20, DR21, DR22)) {
            MongoCollection<Document> tu = mongo.getCollection("${sessionId}_TU")
            MongoCollection<Document> supptu = mongo.getCollection("${sessionId}_SUPPTU")
            MongoCollection<Document> tr = mongo.getCollection("${sessionId}_TR")
            try {
                writer.open(study, 'recist1_nontarget', HEADER)

                tu.aggregate([upper(tu, [:]), match(getDomainFilter())]).each { row ->
                    Document trRow = tr.find(getJoinTableFilter(row)).first()

                    row.anyNonTargetLesionPresent = isAnyNonTargetLesionPresent(trRow)
                    row.visitDate = getVisitDate(row, supptu)
                    row.visitNumber = getVisitNumber(row, supptu)
                    row.newLesionSite = getNewLesionSite(row, supptu)
                    row.lesionScanDate = getLesionScanDate(row)

                    writer.write(map(row))
                }
            } finally {
                writer.close()
            }
        } else {
            super.process(mongo, writer)
        }
    }

    Bson getJoinTableFilter(Document row) {
        if (studyIn(DR21)) {
            and(
                    eq('USUBJID', row.USUBJID),
                    eq('TRLNKID', row.TULNKID),
                    eq('VISITNUM', row.VISITNUM))
        } else if (studyIn(DR20, DR22)) {
            and(
                    eq('USUBJID', row.USUBJID),
                    eq('TRLNKID', row.TULNKID))
        }
    }
}
