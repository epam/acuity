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
import com.mongodb.client.model.Sorts
import groovy.util.logging.Slf4j
import org.bson.Document
import org.bson.conversions.Bson

import static com.mongodb.client.model.Aggregates.match
import static com.mongodb.client.model.Aggregates.sort
import static com.mongodb.client.model.Filters.and
import static com.mongodb.client.model.Filters.nin

/**
 * Created by knml167 on 08/06/2015.
 */

@Slf4j
public abstract class BaseAdverseEventProcessor extends BaseEntityProcessor {


    def HEADER = ['STUDY', 'PART', 'SUBJECT', 'AEV_TEXT', 'AEV_START_DATE', 'AEV_END_DATE',
                  'AEV_SERIOUS', 'AEV_MEDDRA_VERSION',
                  'AEC1DAT', 'AEC2DAT', 'AEC3DAT', 'AEC4DAT', 'AEC5DAT', 'AEC6DAT', 'AEC7DAT', 'AEC8DAT', 'AEC9DAT',
                  'CTCG',
                  'CTCGMAX1', 'CTCGMAX2', 'CTCGMAX3', 'CTCGMAX4', 'CTCGMAX5', 'CTCGMAX6', 'CTCGMAX7', 'CTCGMAX8', 'CTCGMAX9',
                  'AEACN1',
                  'AEC1ACN1', 'AEC2ACN1', 'AEC3ACN1', 'AEC4ACN1', 'AEC5ACN1', 'AEC6ACN1', 'AEC7ACN1', 'AEC8ACN1', 'AEC9ACN1',
                  'AEACN2',
                  'AEC1ACN2', 'AEC2ACN2', 'AEC3ACN2', 'AEC4ACN2', 'AEC5ACN2', 'AEC6ACN2', 'AEC7ACN2', 'AEC8ACN2', 'AEC9ACN2',
                  'AEACN3',
                  'AEC1ACN3', 'AEC2ACN3', 'AEC3ACN3', 'AEC4ACN3', 'AEC5ACN3', 'AEC6ACN3', 'AEC7ACN3', 'AEC8ACN3', 'AEC9ACN3',
                  'AEACNAD1',
                  'AEC1ACA1', 'AEC2ACA1', 'AEC3ACA1', 'AEC4ACA1', 'AEC5ACA1', 'AEC6ACA1', 'AEC7ACA1', 'AEC8ACA1', 'AEC9ACA1',
                  'AEACNAD2',
                  'AEC1ACA2', 'AEC2ACA2', 'AEC3ACA2', 'AEC4ACA2', 'AEC5ACA2', 'AEC6ACA2', 'AEC7ACA2', 'AEC8ACA2', 'AEC9ACA2',
                  'EVT_PT', 'EVT_HLT', 'EVT_LLT', 'EVT_SOC',
                  'IP1', 'IP2', 'IP3', 'AD1', 'AD2', 'AEREL1', 'AEREL2', 'AEREL3', 'AECAAD1', 'AECAAD2', 'AENO',
                  'DSLTTOX', 'TREMERGFL', 'IMMUNE', 'COMMENT', 'AESPINFL', 'AEINFR', 'AEOUT'
    ]

    @Override
    Map<String, String> map(Document row) {

        Map<String, String> out = [
                STUDY             : row.STUDYID as String,
                PART              : 'A',
                SUBJECT           : getSubject(row.USUBJID as String),
                AEV_TEXT          : row.AETERM as String,
                AEV_START_DATE    : row.AESTDTC as String,
                AEV_END_DATE      : row.AEENDTC as String,
                AEV_SERIOUS       : row.AESER as String,
                AEV_MEDDRA_VERSION: row.MEDDRAV as String,
                CTCG              : row.AETOXGR as String,
                AEACN1            : row.AEACN1 as String,
                AEACN2            : row.AEACN2 as String,
                AEACN3            : row.AEACN3 as String,
                AEACNAD1          : row.AEACNAD1 as String,
                AEACNAD2          : row.AEACNAD2 as String,
                EVT_PT            : row.PT as String,
                EVT_HLT           : row.HLT as String,
                EVT_LLT           : row.LLT as String,
                EVT_SOC           : row.evtSoc as String,
                IP1               : row.IP1 as String,
                IP2               : row.IP2 as String,
                IP3               : row.IP3 as String,
                AD1               : row.AD1 as String,
                AD2               : row.AD2 as String,
                AEREL1            : row.AEREL1 as String,
                AEREL2            : row.AEREL2 as String,
                AEREL3            : row.AEREL3 as String,
                AECAAD1           : row.AECAAD1 as String,
                AECAAD2           : row.AECAAD2 as String,
                AENO              : row.AESPID as String,
                DSLTTOX           : row.DSLTTOX as String,
                TREMERGFL         : row.TREMERGFL as String
        ]

        for (int i = 1; i < 10; i++) {
            String key = 'CTCGMAX' + i
            out.put(key, row.get(key) as String)

            key = 'AEC' + i + 'DAT'
            out.put(key, row.get(key) as String)

            key = 'AEC' + i + 'ACN1'
            out.put(key, row.get(key) as String)

            key = 'AEC' + i + 'ACN2'
            out.put(key, row.get(key) as String)

            key = 'AEC' + i + 'ACN3'
            out.put(key, row.get(key) as String)

            key = 'AEC' + i + 'ACA1'
            out.put(key, row.get(key) as String)

            key = 'AEC' + i + 'ACA2'
            out.put(key, row.get(key) as String)
        }
        return out

    }

    @Override
    Bson getDomainFilter() {
        and(nin('AESTDTC', null, ''))
    }

    @Override
    EntityType getEntityType() {
        return EntityType.AdverseEvent
    }

    protected String getSerious(Document row) {
        if (row.AESER) {
            String aeser = (row.AESER as String).toUpperCase()
            if (aeser in ['1', 'AESER', 'Y', 'YES']) {
                return Util.YES
            }
        }
        return Util.NO
    }

    protected String getHLT(Document aeRow) {
        return aeRow.AEHLT
    }

    protected String getLLT(Document aeRow) {
        return aeRow.AELLT
    }

    protected String getSeverityGradeChanges(Document row) {
        return row.AETOXGR
    }

    protected String getSeverityChangeDates(Document row) {
        return row.AESTDTC
    }

    protected String getActionTakenSeverityChanges1(Document row) {
        return row.AEACN1
    }

    protected String getActionTakenSeverityChanges2(Document row, MongoCollection<Document> suppae) {
        return row.AEACN2
    }

    protected String getInfusionReaction(Document aeRow, MongoCollection<Document> suppae) {
        return null
    }

    protected String getStartDate(Document row) {
        return row.AESTDTC
    }

    protected String getEndDate(Document row) {
        return row.AEENDTC
    }

    protected String getMeddraPreferredTerm(Document row) {
        return row.AEDECOD
    }

    abstract void fillDrugs(Document aeRow, MongoCollection<Document> suppae, Map<String, List<Document>> exUsubjectDrugMap)

    abstract void fillCausality(Document aeRow, MongoCollection<Document> suppae, Map<String,
            List<Document>> exUsubjectDrugMap, List<BasicDBObject> aes)

    abstract void fillActionTaken(Document aeRow, MongoCollection<Document> suppae)

    abstract void fillDoseLimitingToxicity(Document aeRow, MongoCollection<Document> suppae)

    abstract void fillTreatmentEmergentFlag(Document aeRow, MongoCollection<Document> suppae, MongoCollection<Document> ex,
                                            List<BasicDBObject> aes)

    abstract void fillMeddraVersion(Document aeRow, MongoCollection<Document> suppae)

    abstract void fillSystemOrganClass(Document aeRow, MongoCollection<Document> suppae)

    abstract void fillImmune(Document aeRow, MongoCollection<Document> suppae,
                             List<BasicDBObject> aes)

    abstract void fillComment(Document aeRow, MongoCollection<Document> suppae,
                              List<BasicDBObject> aes)

    abstract void fillAeOfSpecialInterest(Document aeRow, MongoCollection<Document> suppae,
                                          List<BasicDBObject> aes)

    Bson getGrouping() {
        new BasicDBObject('$group',
                new BasicDBObject('_id',
                        new BasicDBObject('USUBJID', '$USUBJID').
                                append('AESPID', '$AESPID').
                                append('AEDECOD', '$AEDECOD')).
                        append('aes', new BasicDBObject('$push', '$$ROOT')))

    }

    protected List<Bson> getAggregationPipeline(MongoCollection<Document> rows = null) {
        [match(getDomainFilter()), getGrouping()]
    }

    @Override
    public void process(MongoDatabase mongo, Writer writer) throws IOException {

        MongoCollection<Document> ae = mongo.getCollection("${sessionId}_AE")
        MongoCollection<Document> ex = mongo.getCollection("${sessionId}_EX")

        MongoCollection<Document> suppae = mongo.getCollection("${sessionId}_SUPPAE")

        try {
            writer.open(study, 'adverse_event', getHeader())
            // Get list of drugs per Subject
            Map<String, List<Document>> exUsubjectDrugMap =
                    ex.aggregate(
                            Arrays.asList(new BasicDBObject('$group',
                                    new BasicDBObject('_id',
                                            new BasicDBObject([USUBJID: '$USUBJID', EXTRT: '$EXTRT']))))
                    ).collect { grp -> grp._id }.groupBy { subjectDose -> subjectDose.USUBJID }
            ae.aggregate(getAggregationPipeline(ae) << sort(Sorts.ascending('AESTDTC'))).each { grp ->
                Document row
                List<BasicDBObject> aes = grp.aes
                if (aes.size() > 1) {
                    def firstAe = aes.first()
                    def lastAe = aes.last()

                    row = firstAe

                    row.AEENDTC = lastAe.AEENDTC

                    aes.eachWithIndex { aeRow, i ->
                        fillActionTaken(aeRow, suppae)

                        row.AESER = getSerious(aeRow)

                        if (i > 0) {
                            row["AEC${i}DAT"] = getSeverityChangeDates(aeRow)
                            row["CTCGMAX${i}"] = getSeverityGradeChanges(aeRow)
                            row["AEC${i}ACN1"] = getActionTakenSeverityChanges1(aeRow)
                            row["AEC${i}ACN2"] = getActionTakenSeverityChanges2(aeRow, suppae)
                            row["AEC${i}ACN3"] = aeRow.AEACN3
                            row["AEC${i}ACA1"] = aeRow.AEACNAD1
                            row["AEC${i}ACA2"] = aeRow.AEACNAD2
                        }
                    }
                } else {
                    row = aes.first()
                    row.AESER = getSerious(row)
                }
                fillDrugs(row, suppae, exUsubjectDrugMap)
                fillCausality(row, suppae, exUsubjectDrugMap, aes)
                fillActionTaken(row, suppae)
                fillDoseLimitingToxicity(row, suppae)
                fillTreatmentEmergentFlag(row, suppae, ex, aes)
                fillMeddraVersion(row, suppae)
                fillSystemOrganClass(row, suppae)
                fillImmune(row, suppae, aes)
                fillComment(row, suppae, aes)
                fillAeOfSpecialInterest(row, suppae, aes)
                row.PT = getMeddraPreferredTerm(row)
                row.HLT = getHLT(row)
                row.LLT = getLLT(row)
                row.infreact = getInfusionReaction(row, suppae)
                row.aestdtc = getStartDate(row)
                row.aeendtc = getEndDate(row)
                writer.write(map(row))
            }
        } finally {
            writer.close()
        }
    }

    protected List<String> getHeader() {
        return HEADER
    }
}
