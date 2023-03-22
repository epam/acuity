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
import com.acuity.sdtm.transform.processor.SDTM_base.BasePatientGroupInformationProcessor
import com.acuity.sdtm.transform.common.Version
import com.mongodb.client.MongoCollection
import com.mongodb.client.MongoDatabase
import org.bson.Document
import org.springframework.stereotype.Component

import static com.acuity.sdtm.transform.common.Study.*
import static com.acuity.sdtm.transform.util.Util.parseSubjectBySlash
import static com.acuity.sdtm.transform.util.Util.parseSubjectSpecific
import static com.mongodb.client.model.Filters.and
import static com.mongodb.client.model.Filters.eq

@Component
class SDTM_1_1_PatientGroupInformationProcessor extends BasePatientGroupInformationProcessor implements SDTM_1_1_StudyCommonProcessor {

    @Override
    Version getVersion() {
        Version.SDTM_1_1
    }

    @Override
    boolean isStudyProducesThisTypeOfEntity() {
        studyIn(DR9, DR16, DR12, DR13, DR6, DR17, DR20, DR18,
                DR21, DR22, DR23, DR24, DR25)
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
    protected String getGroupName(Document dmRow, MongoCollection<Document> supp) {
        if (studyIn(DR9, DR16, DR13, DR6)) {
            return getSuppFirstQval(dmRow, supp, 'COHORT')
        } else if (studyIn(DR12)) {
            return dmRow.ARMCD
        } else if (studyIn(DR11)) {
            return dmRow.ARM
        } else if (studyIn(DR17, DR23, DR25)) {
            return dmRow.ACTARM
        } else if (studyIn(DR20)) {
            def m = dmRow.ACTARM =~ /PHASE (.+?) COHORT (.+?)$/
            if (m) {
                return m[0][2]
            }
        } else if (studyIn(DR18)) {
            Document taRow = (Document) supp.find(and(
                    eq('ARM', dmRow.ACTARM),
                    eq('EPOCH', 'TREATMENT')))
                    .first()
            return taRow?.ELEMENT
        } else if (studyIn(DR21)) {
            def m = dmRow.ACTARM =~ /COHORT (.+?)/
            if (m) {
                return m[0][1]
            } else {
                return null
            }
        } else if (studyIn(DR22)) {
            Document taRow = supp.find(and(eq('ARM', dmRow.ARM),
                    eq('ELEMENT', 'Screen')))
                    .first()
            def reg = ~/^Assigned to /
            return taRow?.TABRANCH ? taRow.TABRANCH - reg : null
        } else if (studyIn(DR24)) {
            switch (dmRow.ARM) {
                case 'PART 1 A COHORT 1':
                    return '2mg QD Continuous'
                case 'PART 1 A COHORT 2':
                    return '5mg QD Continuous'
                case 'PART 1 A COHORT 3':
                    return '10mg QD Continuous'
                case 'PART 1 A COHORT 4':
                    return '20mg QD Continuous'
                case 'PART 1 A COHORT 5':
                    return '40mg QD Continuous'
                case 'PART 1 A COHORT 6A':
                    return '80mg QD Continuous'
                case 'PART 1 A COHORT 6B':
                    return '40mg BID 3on4off'
                default:
                    return null
            }
        }
        return super.getGroupName(dmRow, supp)
    }

    @Override
    protected String getGroupingName(Document dmRow, MongoCollection suppdm) {
        if (studyIn(DR9, DR16, DR12, DR13, DR6, DR11, DR17, DR20,
                DR21, DR22)) {
            return 'Cohort'
        } else if (studyIn(DR18)) {
            return 'Treatment Arm'
        } else if (studyIn(DR24)) {
            return 'TREATMENT ARM'
        } else if (studyIn(DR23)) {
            return 'Cohort - Dose'
        } else if (studyIn(DR25)) {
            return 'Cohort (Dose)'
        } else {
            super.getGroupingName(dmRow, suppdm)
        }
    }

    @Override
    void process(MongoDatabase mongo, Writer writer) throws IOException {
        if (studyIn(DR18, DR22, DR24)) {
            MongoCollection<Document> dm = mongo.getCollection("${sessionId}_DM")
            MongoCollection<Document> suppdm = mongo.getCollection("${sessionId}_SUPPDM")
            MongoCollection<Document> ta = mongo.getCollection("${sessionId}_TA")

            try {
                writer.open(study, 'patient_group_information', HEADER)

                dm.find().each { row ->

                    row.groupName = getGroupName(row, ta)
                    row.groupingName = getGroupingName(row, suppdm)

                    writer.write(map(row))
                }
            } finally {
                writer.close()
            }
        } else {
            super.process(mongo, writer)
        }
    }
}
