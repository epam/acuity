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
import com.mongodb.BasicDBObject
import com.mongodb.client.MongoCollection
import com.mongodb.client.MongoDatabase
import com.mongodb.client.model.Filters
import groovy.util.logging.Slf4j
import org.bson.Document
import org.bson.conversions.Bson

import static com.acuity.sdtm.transform.util.Util.asDouble
import static com.mongodb.client.model.Accumulators.push
import static com.mongodb.client.model.Aggregates.group
import static com.mongodb.client.model.Aggregates.match
import static com.mongodb.client.model.Filters.*
import static org.springframework.util.StringUtils.trimWhitespace

@Slf4j
public abstract class BaseRecist1TargetProcessor extends BaseEntityProcessor {

    def HEADER = ['STUDY', 'PART', 'SUBJECT', 'VIS_DAT', 'VISIT', 'R1LESNO', 'R1ASMDAT', 'R1LESSIT', 'R1LONDIA', 'R1PRES']


    Map<String, String> map(Document row) {
        [
                STUDY   : row.STUDYID as String,
                PART    : 'A',
                SUBJECT : getSubject(row.USUBJID as String),
                VISIT   : row.VISITNUM as String,
                VIS_DAT : row.visitdtc as String,
                R1LESNO : row.lesionNumber as String,
                R1ASMDAT: row.lesionScanDate as String,
                R1LESSIT: row.TULOC as String,
                R1LONDIA: trimWhitespace(row?.TRSTRESN as String),
                R1PRES  : row.lesionPresent as String
        ]
    }

    @Override
    public EntityType getEntityType() {
        return EntityType.Recist1Target
    }

    protected String getLesionNumber(Document trRow) {
        String lesionNumber = trRow.TRLINKID ?: trRow.TRLNKID
        // The following rule was outdated in scope of RCT-5935
        /*if (lesionNumber != null && lesionNumber.startsWith('T')) {
            return lesionNumber.substring(1)
        }*/
        if (lesionNumber != null) {
            def matcher = lesionNumber =~ /^[a-zA-Z]0*(.+)?/
            if (matcher.size() > 0) {
                lesionNumber = matcher[0][1]
            }
        }
        return lesionNumber
    }

    protected String isLesionPresent(Document row) {
        Util.YES
    }

    @Override
    Bson getDomainFilter() {
        and(eq('TRGRPID', 'TARGET'), Filters.in('TRTESTCD', 'LDIAM', 'SAXIS'))
    }

    List<? extends Bson> getPipeline(MongoCollection<Document> tr) {
        Map<String, String> trSchema = schema(tr)
        [
                upper(tr, ['TRLNKID_FOR_GROUP': new BasicDBObject('$ifNull', ['$' + (trSchema.TRLNKID as String), '$' + (trSchema.TRLINKID as String)])]),
                match(getDomainFilter()),
                group(new BasicDBObject('USUBJID', '$USUBJID')
                        .append('TRLNKID', '$TRLNKID_FOR_GROUP'),
                        push('elems', '$$ROOT')
                )
        ]
    }

    protected String getVisitDate(Document trRow, Document svRow) {
        return svRow?.SVSTDTC
    }

    protected String getLesionScanDate(Document row) {
        return row?.TRDTC
    }

    @Override
    public void process(MongoDatabase mongo, Writer writer) throws IOException {
        MongoCollection<Document> tr = mongo.getCollection("${sessionId}_TR")
        MongoCollection<Document> tu = mongo.getCollection("${sessionId}_TU")
        MongoCollection<Document> sv = mongo.getCollection("${sessionId}_SV")

        Map<String, String> tuSchema = schema(tu)
        Map<String, String> svSchema = schema(sv)
        try {
            writer.open(study, 'recist1_target', HEADER)

            tr.aggregate(getPipeline(tr)).each { grp ->
                List<Document> ldiam, others
                (ldiam, others) = (grp.elems as List<Document>).split { it.TRTESTCD == 'LDIAM' }
                (ldiam ?: others).groupBy { row -> row?.TRDTC }
                        .each { grp2 ->
                            long notNullCount = grp2.value.stream()
                                    .filter { row -> row.TRSTRESN != null }
                                    .count()
                            def checkForNullFilter = notNullCount > 0 ? { row -> row.TRSTRESN != null } : { row -> true }
                            grp2.value
                                    .findAll(checkForNullFilter)
                                    .each { row ->
                                        Document tuRow = (Document) tu.find(and(
                                                eq(tuSchema.USUBJID, row.USUBJID),
                                                eq(tuSchema.TUTESTCD, 'TUMIDENT'),
                                                eq(tuSchema.TUSTRESC, 'TARGET'),
                                                or(
                                                        and(exists(tuSchema.TULINKID), eq(tuSchema.TULINKID, row.TRLINKID)),
                                                        and(exists(tuSchema.TULNKID), eq(tuSchema.TULNKID, row.TRLNKID))
                                                ))).first()
                                        remap(tuRow, tuSchema)

                                        if (tuRow) {
                                            row.TULOC = tuRow[tuSchema.TULOC]
                                        }

                                        row.lesionNumber = getLesionNumber(row)
                                        row.lesionPresent = isLesionPresent(row)
                                        row.lesionScanDate = getLesionScanDate(row)

                                        Document svRow = sv
                                                .find(and(eq(svSchema.USUBJID, row.USUBJID), nin('VISITNUM', null, '')))
                                                .toList()
                                                .find { Math.abs(asDouble(it.VISITNUM) - asDouble(row.VISITNUM)) < 1e-5 }

                                        remap(svRow, svSchema)

                                        row.visitdtc = getVisitDate(row, svRow)

                                        writer.write(map(row))
                                    }
                        }
            }
        } finally {
            writer.close()
        }
    }
}
