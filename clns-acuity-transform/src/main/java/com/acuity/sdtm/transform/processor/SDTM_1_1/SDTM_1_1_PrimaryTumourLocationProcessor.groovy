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
import com.acuity.sdtm.transform.processor.SDTM_base.BasePrimaryTumourLocationProcessor
import com.acuity.sdtm.transform.common.Version
import com.mongodb.BasicDBObject
import com.mongodb.client.MongoCollection
import com.mongodb.client.MongoDatabase
import com.mongodb.client.model.Filters
import com.mongodb.client.model.Sorts
import org.apache.commons.lang3.StringUtils
import org.bson.Document
import org.bson.conversions.Bson
import org.springframework.stereotype.Component

import static com.acuity.sdtm.transform.common.Study.*
import static com.acuity.sdtm.transform.util.Util.*
import static com.mongodb.client.model.Accumulators.*
import static com.mongodb.client.model.Aggregates.*
import static com.mongodb.client.model.Filters.*

/**
 * Created by knml167 on 08/06/2015.
 */
@Component
class SDTM_1_1_PrimaryTumourLocationProcessor extends BasePrimaryTumourLocationProcessor implements SDTM_1_1_StudyCommonProcessor {

    @Override
    boolean isStudyProducesThisTypeOfEntity() {
        return !studyIn(DR15, DR19, DR23, STUDY001, STUDY002, STUDY003, STUDY004)
    }

    @Override
    Version getVersion() {
        return Version.SDTM_1_1
    }

    @Override
    String getSubject(String val) {
        if (studyIn(DR22)) {
            return parseSubjectSpecific(val)
        } else if (studyIn(DR25)) {
            return parseSubjectBySlash(val)
        } else {
            return super.getSubject(val)
        }
    }

    @Override
    Bson getDomainFilter() {
        return null
    }

    @Override
    void process(MongoDatabase mongo, Writer writer) {
        try {
            writer.open(study, "primary_tumour_location", HEADER)

            MongoCollection<Document> main = null
            MongoCollection<Document> supplementary = null
            List<? extends Bson> pipeline = []
            def nullSupplier = { r, s -> null }
            def rowSupplier = { it }
            def primaryTumorLocationSupplier = nullSupplier
            def originalDiagnosisDateSupplier = { r, s -> getOriginalDiagnosisDate(r, s) }
            def specifyPrimaryTumorLocationSupplier = nullSupplier
            if (studyIn(DR7, DR8)) {
                MongoCollection<Document> dm = mongo.getCollection("${sessionId}_DM")

                dm.find().each { row ->
                    row.primaryTumorLocation = 'Lung'
                    writer.write(map(row))
                }
            } else if (studyIn(DR3)) {
                main = mongo.getCollection("${sessionId}_FA")
                supplementary = mongo.getCollection("${sessionId}_SUPPFA")
                pipeline = [match(eq('FATESTCD', 'PTUMLOC'))]
                primaryTumorLocationSupplier = { Document r, MongoCollection<Document> s -> r['FALOC'] }
                originalDiagnosisDateSupplier = { Document r, MongoCollection s -> getOriginalDiagnosisDate(r, s) }
            } else if (studyIn(DR5)) {
                MongoCollection<Document> dm = mongo.getCollection("${sessionId}_DM")

                dm.find().each { row ->
                    row.primaryTumorLocation = 'Ovary'
                    writer.write(map(row))
                }
            } else if (studyIn(DR14)) {
                MongoCollection<Document> fa = mongo.getCollection("${sessionId}_FA")

                fa.find(eq('FATESTCD', 'DIAGP')).each { row ->
                    row.specifyPrimaryTumorLocation = null
                    row.primaryTumorLocation = row.FASTRESC
                    writer.write(map(row))
                }
            } else if (studyIn(DR4)) {
                MongoCollection<Document> fa = mongo.getCollection("${sessionId}_FA")

                fa.find(eq('FATESTCD', 'PHPRDIAG')).each { row ->
                    row.specifyPrimaryTumorLocation = null
                    row.primaryTumorLocation = row.FASTRESC
                    writer.write(map(row))
                }
            } else if (studyIn(DR9)) {
                MongoCollection<Document> mh = mongo.getCollection("${sessionId}_MH")
                MongoCollection<Document> suppmh = mongo.getCollection("${sessionId}_SUPPMH")

                Bson filter = and(
                        eq('MHCAT', 'CANCER DIAGNOSIS'),
                        not(
                                Filters.in('MHTERM', 'METASTATIC DIAGNOSIS', 'CYTOLOGICAL OR HISTO-PATHOLOGICAL DIAGNOSIS')
                        )
                )

                List<Bson> aggregationPipeline = [
                        match(filter),
                        sort(Sorts.ascending('MHDTC')),
                        group(new BasicDBObject('USUBJID', '$USUBJID'),
                                first('first', '$$ROOT')
                        )
                ]

                mh.aggregate(aggregationPipeline).each { grp ->
                    Document row = grp.first

                    row.primaryTumorLocation = row.MHTERM
                    row.originalDiagnosisDate = row.MHDTC
                    row.specifyPrimaryTumorLocation = getSuppFirstQval(row, suppmh, 'CDTOTH', 'MHSEQ')

                    writer.write(map(row))
                }
            } else if (studyIn(DR16)) {
                MongoCollection<Document> mh = mongo.getCollection("${sessionId}_MH")
                MongoCollection<Document> suppmh = mongo.getCollection("${sessionId}_SUPPMH")

                mh.aggregate([
                        match(eq('MHCAT', 'CANCER DIAGNOSIS')),
                        sort(Sorts.ascending('MHDTC')),
                        group(new BasicDBObject('USUBJID', '$USUBJID'), first('first', '$$ROOT'), sum('count', 1)),
                ])
                        .each { grp ->
                            Document row = grp.first
                            row.primaryTumorLocation = grp.count > 1 ? 'MULTIPLE' : getSuppFirstQval(row, suppmh, 'SITEMETA', 'MHSEQ')
                            row.originalDiagnosisDate = row.MHDTC
                            writer.write(map(row))
                        }
            } else if (studyIn(DR1, DR2)) {
                MongoCollection<Document> mh = mongo.getCollection("${sessionId}_MH")
                MongoCollection<Document> fa = mongo.getCollection("${sessionId}_FA")

                fa.find(eq('FATESTCD', 'PTUMLOC')).each { row ->
                    row.specifyPrimaryTumorLocation = null
                    row.primaryTumorLocation = row.FASTRESC


                    Document mhRow = (Document) mh.find(and(
                            eq('USUBJID', row.USUBJID),
                            eq('MHSCAT', 'FIRST DIAGNOSIS'),
                            eq('MHTERM', row.FAOBJ))).first()

                    row.originalDiagnosisDate = mhRow?.MHSTDTC

                    writer.write(map(row))
                }
            } else if (studyIn(DR12, DR11)) {
                MongoCollection<Document> mh = mongo.getCollection("${sessionId}_MH")

                mh.find(
                        and(
                                eq('MHCAT', 'DISEASE HISTORY'),
                                Filters.nin('MHTERM', 'Local or Regional Recurrence', 'Distant Metastases')
                        )
                ).each { row ->
                    row.primaryTumorLocation = row.MHTERM
                    row.originalDiagnosisDate = row.MHSTDTC

                    writer.write(map(row))
                }
            } else if (studyIn(DR13)) {
                MongoCollection<Document> fa = mongo.getCollection("${sessionId}_FA")
                MongoCollection<Document> suppfa = mongo.getCollection("${sessionId}_SUPPFA")

                Bson filter = eq('FATESTCD', 'LOCADMET')
                List<Bson> aggregationPipeline = [
                        match(filter),
                        sort(Sorts.ascending('FATDTC')),
                        group(new BasicDBObject('USUBJID', '$USUBJID'),
                                push('recs', '$$ROOT')
                        )
                ]


                fa.aggregate(aggregationPipeline).each { grp ->
                    List<Document> recs = grp.recs

                    Optional<Document> first = recs.stream().filter { row -> StringUtils.isNotBlank(row.FADTC as String) }.findFirst()
                    def row = first.orElse(recs.first())

                    String primaryTumorLocation = row.FALOC
                    row.primaryTumorLocation = recs.size() > 1 ? 'Multiple locations' : primaryTumorLocation
                    row.originalDiagnosisDate = row.FADTC
                    row.specifyPrimaryTumorLocation = null
                    if (recs.size() == 1) {
                        if (StringUtils.startsWithIgnoreCase(primaryTumorLocation, 'OTHER METASTATIC SITES')) {
                            row.specifyPrimaryTumorLocation = getSuppFirstQval(row, suppfa, 'FAMEOTH', 'FASEQ')
                        } else if (StringUtils.startsWithIgnoreCase(primaryTumorLocation, 'OTHER LOCALLY ADVANCED SITES')) {
                            row.specifyPrimaryTumorLocation = getSuppFirstQval(row, suppfa, 'FALOCOTH', 'FASEQ')
                        }
                    }
                    writer.write(map(row))
                }
            } else if (studyIn(DR10, DR6)) {
                MongoCollection<Document> fa = mongo.getCollection("${sessionId}_FA")
                MongoCollection<Document> suppfa = mongo.getCollection("${sessionId}_SUPPFA")

                Bson filter = eq('FATESTCD', 'PHPRDIAG')

                fa.find(filter).each { row ->

                    row.primaryTumorLocation = row.FASTRESC
                    row.originalDiagnosisDate = getOriginalDiagnosisDate(row, suppfa)

                    writer.write(map(row))
                }
            } else if (studyIn(DR17)) {
                main = mongo.getCollection("${sessionId}_MH")
                //filter
                pipeline = [
                        match(eq('MHCAT', 'DISEASE HISTORY')),
                        sort(Sorts.ascending('MHSTDTC')),
                        group(new BasicDBObject(USUBJID: '$USUBJID'),
                                first('first', '$$ROOT'))]
                rowSupplier = { it.first }
                primaryTumorLocationSupplier = { Document r, MongoCollection<Document> s -> r['MHTERM'] }
                originalDiagnosisDateSupplier = { Document r, MongoCollection s -> getOriginalDiagnosisDate(r, s) }
            } else if (studyIn(DR18)) {
                main = mongo.getCollection("${sessionId}_MH")
                pipeline = [
                        match(and(eq('MHCAT', 'ONCOLOGY DISEASE'),
                                eq('MHSCAT', 'FIRST POSITIVE BIOPSY'))),
                        sort(Sorts.ascending('MHSTDTC')),
                        group(new BasicDBObject(USUBJID: '$USUBJID'),
                                first('first', '$$ROOT'))]
                rowSupplier = { it.first }
                primaryTumorLocationSupplier = { Document r, MongoCollection<Document> s -> r['MHTERM'] }
                originalDiagnosisDateSupplier = { Document r, MongoCollection s -> getOriginalDiagnosisDate(r, s) }
            } else if (studyIn(DR20)) {
                MongoCollection<Document> fa = mongo.getCollection("${sessionId}_FA")
                fa
                        .aggregate(
                                [
                                        match(or(eq('FAOBJ', 'First Biopsy'),
                                                eq('FATESTCD', 'DIAGP'))),
                                        group(new BasicDBObject(USUBJID: '$USUBJID'),
                                                push('recs', '$$ROOT'))
                                ])
                        .each {
                            grp ->
                                def groupedRows = (grp.recs as List<Document>).groupBy { it.USUBJID }
                                groupedRows.each { key, rows ->
                                    def row = rows.first()
                                    row.primaryTumorLocation = rows.find {
                                        it.FATESTCD == 'DIAGP' && 'Primary Diagnosis' == it.FAOBJ
                                    }?.FASTRESC
                                    row.originalDiagnosisDate = rows.find { it.FAOBJ == 'First Biopsy' }?.FASTRESC
                                    writer.write(map(row))
                                }
                        }
            } else if (studyIn(DR21)) {
                main = mongo.getCollection("${sessionId}_MH")

                pipeline = [
                        match(and(eq('MHCAT', 'DISEASE HISTORY'),
                                nin('MHBODSYS', null, '')))]
                primaryTumorLocationSupplier = { Document r, MongoCollection<Document> s -> r['MHTERM'] }
                originalDiagnosisDateSupplier = { Document r, MongoCollection s -> getOriginalDiagnosisDate(r, s) }
            } else if (studyIn(DR22)) {
                main = mongo.getCollection("${sessionId}_MH")
                supplementary = mongo.getCollection("${sessionId}_FA")
                pipeline = [match(or(eq('MHCAT', 'ONCOLOGY DISEASE'),
                        eq('MHSCAT', 'FIRST POSITIVE BIOPSY'))),
                            sort(Sorts.ascending('MHSTDTC')),
                            group(new BasicDBObject(USUBJID: '$USUBJID'),
                                    first('first', '$$ROOT'))]
                rowSupplier = { it.first }
                primaryTumorLocationSupplier = { Document r, MongoCollection<Document> s ->
                    def consolidatedRows = s.find(and(eq('USUBJID', r.USUBJID),
                            eq('FATESTCD', 'PTUMLOC')))
                    return consolidatedRows.size() > 1 ? 'multiple' : consolidatedRows.first()?.FASTRESC
                }
                originalDiagnosisDateSupplier = { Document r, MongoCollection s -> parseDateTrimDash(r?.MHSTDTC as String) }

            } else if (studyIn(DR24)) {
                main = mongo.getCollection("${sessionId}_FA")
                supplementary = mongo.getCollection("${sessionId}_FA")
                primaryTumorLocationSupplier = { Document r, MongoCollection<Document> s ->
                    def ptumlocFilter = s.find(and(eq('USUBJID', r.USUBJID),
                            eq('FATESTCD', 'PTUMLOC'),
                            eq('FATEST', 'Primary Tumor Location'))).first()
                    return ptumlocFilter?.FAORRES
                }
                originalDiagnosisDateSupplier = { Document r, MongoCollection s -> getOriginalDiagnosisDate(r, s) }
                specifyPrimaryTumorLocationSupplier = { Document r, MongoCollection s -> r['FAORRES'] }
            } else if (studyIn(DR25)) {
                MongoCollection<Document> fa = mongo.getCollection("${sessionId}_FA")

                fa.find(eq('FATESTCD', 'DIAGDTC')).each { row ->
                    row.specifyPrimaryTumorLocation = null
                    row.originalDiagnosisDate = row.FASTRESC
                    writer.write(map(row))
                }
            }
            performProcess(main, supplementary, pipeline, rowSupplier, primaryTumorLocationSupplier,
                    originalDiagnosisDateSupplier, specifyPrimaryTumorLocationSupplier, writer)
        } finally {
            writer.close()
        }
    }

    protected String getOriginalDiagnosisDate(Document row, MongoCollection supp) {
        def date = null
        if (studyIn(DR6, DR24)) {
            date = row.FADTC
        } else if (studyIn(DR10)) {
            date = getSuppFirstQval(row, supp, 'PHFPBDAT', 'FASEQ')
        } else if (studyIn(DR17, DR18, DR21)) {
            date = row.MHSTDTC
        } else if (studyIn(DR3)) {
            date = getSuppFirstQval(row, supp, 'ODIGDATE', 'FASEQ')
        }
        parseDate((date ?: "") as String)
    }

    def performProcess(
            MongoCollection<Document> main, MongoCollection<Document> supp,
            List<? extends Bson> pipeline,
            rowSupplier,
            primaryTumorLocationSupplier,
            originalDiagnosisDateSupplier,
            specifyPrimaryTumorLocationSupplier,
            Writer writer) {

        if (!main) {
            return
        }
        writer.open(study, "primary_tumour_location", HEADER)
        //reverse compatibility
        main.aggregate(pipeline).each { row ->
            row = rowSupplier(row)
            row.primaryTumorLocation = primaryTumorLocationSupplier(row, supp)
            row.originalDiagnosisDate = originalDiagnosisDateSupplier(row, supp)
            row.specifyPrimaryTumorLocation = specifyPrimaryTumorLocationSupplier(row, supp)

            writer.write(map(row))
        }
    }
}
