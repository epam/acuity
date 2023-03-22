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

import com.acuity.sdtm.transform.common.Version
import com.acuity.sdtm.transform.processor.SDTM_base.BaseAdverseEventProcessor
import com.acuity.sdtm.transform.util.Util
import com.mongodb.BasicDBObject
import com.mongodb.client.MongoCollection
import com.mongodb.client.model.Sorts
import org.apache.commons.lang3.StringUtils
import org.bson.Document
import org.bson.conversions.Bson
import org.springframework.stereotype.Component

import static com.acuity.sdtm.transform.common.Study.*
import static com.acuity.sdtm.transform.util.Util.*
import static com.mongodb.client.model.Aggregates.match
import static com.mongodb.client.model.Aggregates.sort
import static com.mongodb.client.model.Filters.eq

/**
 * Created by knml167 on 08/06/2015.
 */
@Component
public class SDTM_1_1_AdverseEventProcessor extends BaseAdverseEventProcessor implements SDTM_1_1_StudyCommonProcessor {

    @Override
    boolean isStudyProducesThisTypeOfEntity() {
        return !studyIn(STUDY001, STUDY002, STUDY003, STUDY004)
    }

    @Override
    Map<String, String> map(Document row) {
        if (studyIn(DR18)) {
            [
                    *       : super.map(row),
                    IMMUNE  : row.IMMUNE as String,
                    COMMENT : row.COMMENT as String,
                    AESPINFL: row.AESPINFL as String
            ]
        } else if (studyIn(DR22)) {
            [*     : super.map(row),
             AEINFR: row.infreact as String,
             AEOUT : row.AEOUT as String
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
            parseSubjectBySlash(val)
        } else {
            return super.getSubject(val)
        }
    }

    @Override
    public Version getVersion() {
        return Version.SDTM_1_1
    }

    @Override
    Bson getDomainFilter() {
        super.getDomainFilter()
    }

    @Override
    Bson getGrouping() {
        super.getGrouping()
    }

    @Override
    protected String getHLT(Document aeRow) {
        if (studyIn(DR15)) {
            return aeRow.AEHLGT
        }
        return super.getHLT(aeRow)
    }

    @Override
    protected String getLLT(Document aeRow) {
        if (studyIn(DR15)) {
            return aeRow.AEHLT
        }
        return super.getLLT(aeRow)
    }

    protected String getInfusionReaction(Document aeRow, MongoCollection<Document> suppae) {
        if (studyIn(DR22)) {
            return getSuppFirstQval(aeRow, suppae, 'AEINFR', 'AESEQ')
        } else {
            return super.getInfusionReaction(aeRow, suppae)
        }
    }

    @Override
    protected String getStartDate(Document row) {
        if (studyIn(DR22)) {
            return parseDateTrimDash(row?.AESTDTC as String)
        } else {
            return super.getStartDate(row)
        }
    }

    @Override
    protected String getEndDate(Document row) {
        if (studyIn(DR22)) {
            return parseDateTrimDash(row?.AEENDTC as String)
        } else {
            return super.getEndDate(row)
        }
    }

    @Override
    protected String getSeverityGradeChanges(Document row) {
        if (studyIn(DR22)) {
            return null
        } else {
            return super.getSeverityGradeChanges(row)
        }
    }

    @Override
    protected String getSeverityChangeDates(Document row) {
        if (studyIn(DR22)) {
            return null
        } else {
            return super.getSeverityChangeDates(row)
        }
    }

    void fillDrugs(Document aeRow, MongoCollection<Document> suppae, Map<String, List<Document>> exUsubjectDrugMap) {
        if (studyIn(DR23)) {
            aeRow.IP1 = 'Acalabrutinib'
            aeRow.IP2 = 'Vistusertib'
        }
    }

    void fillCausality(Document aeRow, MongoCollection<Document> suppae, Map<String, List<Document>> exUsubjectDrugMap,
                       List<BasicDBObject> aes = null) {

        if (studyIn(DR7)) {
            aeRow.AEREL1 = getSuppFirstQval(aeRow, suppae, 'AERELAZP', 'AESEQ')
            aeRow.AECAAD1 = getSuppFirstQval(aeRow, suppae, 'AERELDOC', 'AESEQ')

        } else if (studyIn(DR8)) {
            aeRow.AEREL1 = getSuppFirstQval(aeRow, suppae, 'AERELAZP', 'AESEQ')
            aeRow.AECAAD1 = getSuppFirstQval(aeRow, suppae, 'AERELCAB', 'AESEQ')
            aeRow.AECAAD2 = getSuppFirstQval(aeRow, suppae, 'AERELPEM', 'AESEQ')

        } else if (studyIn(DR9)) {
            aeRow.AEREL1 = aeRow.AEREL

        } else if (studyIn(DR5)) {
            aeRow.AEREL1 = getSuppFirstQval(aeRow, suppae, 'AERELAZD', 'AESEQ')
            aeRow.AECAAD1 = getSuppFirstQval(aeRow, suppae, ['AERELCAR', 'AERELGEM', 'AERELPAC', 'AERELPLD'] as String[], 'AESEQ')

        } else if (studyIn(DR6)) {
            aeRow.AEREL1 = getSuppFirstQval(aeRow, suppae, 'RELAZD', 'AESEQ')
            aeRow.AECAAD1 = getSuppFirstQval(aeRow, suppae, 'RELOLA', 'AESEQ')
        } else if (studyIn(DR14)) {
            aeRow.AEREL1 = getSuppFirstQval(aeRow, suppae, 'AERELAZD', 'AESEQ')

        } else if (studyIn(DR1, DR2)) {
            aeRow.AEREL1 = aeRow.AEREL
            aeRow.AEREL2 = getSuppFirstQval(aeRow, suppae, 'AEREL2', 'AESEQ')

        } else if (studyIn(DR15)) {
            aeRow.AEREL1 = aeRow.AEREL

        } else if (studyIn(DR11)) {
            aeRow.AEREL1 = getSuppFirstQval(aeRow, suppae, 'AEREL1', 'AESEQ')
            aeRow.AEREL2 = getSuppFirstQval(aeRow, suppae, 'AEREL2', 'AESEQ')
        } else if (studyIn(DR13)) {
            aeRow.AEREL1 = aeRow.AEREL
            if ("UNLIKELY AE CAUSED BY IP".equalsIgnoreCase(aeRow.AEREL as String)) {
                aeRow.AEREL1 = Util.NO
            } else if ("REASONABLE POSSIBILITY AE RELATED TO IP".equalsIgnoreCase(aeRow.AEREL as String)) {
                aeRow.AEREL1 = Util.YES
            }
        } else if (studyIn(DR17)) {
            aeRow.AEREL1 = getSuppFirstQval(aeRow, suppae, 'AEREL1', 'AESEQ')
            aeRow.AECAAD1 = getSuppFirstQval(aeRow, suppae, 'AEREL2', 'AESEQ')
            aeRow.AECAAD2 = getSuppFirstQval(aeRow, suppae, 'AEREL3', 'AESEQ')

        } else if (studyIn(DR20)) {
            def aerel1 = aes
                    .collect { getSuppFirstQval(it as Document, suppae, 'AEREL1', 'AESEQ') }
                    .unique()
            def aecaad1 = aes
                    .collect { getSuppFirstQval(it as Document, suppae, 'AEREL2', 'AESEQ') }
                    .unique()

            aeRow.AEREL1 = aerel1.contains("RELATED") ? "RELATED" : getSuppFirstQval(aeRow, suppae, 'AEREL1', 'AESEQ')
            aeRow.AECAAD1 = aecaad1.contains("RELATED") ? "RELATED" : getSuppFirstQval(aeRow, suppae, 'AEREL2', 'AESEQ')
        } else if (studyIn(DR18)) {
            aeRow.AEREL1 = aeRow.AEREL
            aeRow.AEREL2 = getSuppFirstQval(aeRow, suppae, 'AEREL2', 'AESEQ')
            aeRow.AECAAD1 = getSuppFirstQval(aeRow, suppae, 'AEREL3', 'AESEQ')
            aeRow.AECAAD2 = getSuppFirstQval(aeRow, suppae, 'AEREL4', 'AESEQ')
        } else if (studyIn(DR21)) {
            aeRow.AEREL1 = getSuppFirstQval(aeRow, suppae, 'AEREL1', 'AESEQ')
            aeRow.AECAAD1 = getSuppFirstQval(aeRow, suppae, 'AEREL2', 'AESEQ')
        } else if (studyIn(DR23)) {
            aeRow.AEREL1 = getSuppFirstQval(aeRow, suppae, 'IP', 'AESEQ')
            aeRow.AEREL2 = getSuppFirstQval(aeRow, suppae, 'IP2', 'AESEQ')
        } else {
            aeRow.AEREL1 = aeRow.AEREL
        }
    }

    void fillActionTaken(Document aeRow, MongoCollection suppae) {
        if (studyIn(DR7)) {
            aeRow.AEACN1 = getSuppFirstQval(aeRow, suppae, 'AEACNAZP', 'AESEQ')
            aeRow.AEACNAD1 = getSuppFirstQval(aeRow, suppae, 'AEACNDOC', 'AESEQ')

        } else if (studyIn(DR8)) {
            aeRow.AEACN1 = getSuppFirstQval(aeRow, suppae, 'AEACNAZP', 'AESEQ')
            aeRow.AEACNAD1 = getSuppFirstQval(aeRow, suppae, 'AEACNCAB', 'AESEQ')
            aeRow.AEACNAD2 = getSuppFirstQval(aeRow, suppae, 'AEACNPEM', 'AESEQ')

        } else if (studyIn(DR9)) {
            aeRow.AEACN1 = aeRow.AEACN
            aeRow.AEACNAD1 = getSuppFirstQval(aeRow, suppae, 'ACTCAR', 'AESEQ')
            aeRow.AEACNAD2 = getSuppFirstQval(aeRow, suppae, 'ACTPAC', 'AESEQ')

        } else if (studyIn(DR5)) {
            aeRow.AEACN1 = getSuppFirstQval(aeRow, suppae, 'AEACNAZD', 'AESEQ')
            aeRow.AEACNAD1 = getSuppFirstQval(aeRow, suppae, ['AEACNCAR', 'AEACNGEM', 'AEACNPAC', 'AEACNPLD'] as String[], 'AESEQ')
        } else if (studyIn(DR14)) {
            aeRow.AEACN1 = getSuppFirstQval(aeRow, suppae, 'AEACNAZD', 'AESEQ')

        } else if (studyIn(DR1, DR2, DR23)) {
            aeRow.AEACN1 = aeRow.AEACN
            aeRow.AEACN2 = getSuppFirstQval(aeRow, suppae, 'AEACN2', 'AESEQ')


        } else if (studyIn(DR16)) {
            aeRow.AEACN1 = aeRow.AEACN
            aeRow.AEACN2 = getSuppFirstQval(aeRow, suppae, 'ACT9150', 'AESEQ')
            aeRow.AEACN3 = getSuppFirstQval(aeRow, suppae, 'ACT5069', 'AESEQ')
        } else if (studyIn(DR6)) {
            aeRow.AEACN1 = getSuppFirstQval(aeRow, suppae, 'ACNAZD', 'AESEQ')
            aeRow.AEACNAD1 = getSuppFirstQval(aeRow, suppae, 'ACNOLA', 'AESEQ')
        } else if (studyIn(DR11)) {
            aeRow.AEACN1 = getSuppFirstQval(aeRow, suppae, 'AEACN1', 'AESEQ')
            aeRow.AEACN2 = getSuppFirstQval(aeRow, suppae, 'AEACN2', 'AESEQ')
        } else if (studyIn(DR17)) {
            aeRow.AEACN1 = getSuppFirstQval(aeRow, suppae, 'AEACN1', 'AESEQ')
            aeRow.AEACNAD1 = getSuppFirstQval(aeRow, suppae, 'AEACN2', 'AESEQ')
            aeRow.AEACNAD2 = getSuppFirstQval(aeRow, suppae, 'AEACN3', 'AESEQ')
        } else if (studyIn(DR20, DR21)) {
            aeRow.AEACN1 = getSuppFirstQval(aeRow, suppae, 'AEACN1', 'AESEQ')
            aeRow.AEACNAD1 = getSuppFirstQval(aeRow, suppae, 'AEACN2', 'AESEQ')
        } else if (studyIn(DR18)) {
            aeRow.AEACN1 = aeRow.AEACN
            aeRow.AEACN2 = getSuppFirstQval(aeRow, suppae, 'AEACN2', 'AESEQ')
            aeRow.AEACN3 = getSuppFirstQval(aeRow, suppae, 'AEACN3', 'AESEQ')
            aeRow.AEACN4 = getSuppFirstQval(aeRow, suppae, 'AEACN4', 'AESEQ')
            aeRow.AEACNAD1 = getSuppFirstQval(aeRow, suppae, 'AEACN3', 'AESEQ')
            aeRow.AEACNAD2 = getSuppFirstQval(aeRow, suppae, 'AEACN4', 'AESEQ')
        } else {
            aeRow.AEACN1 = aeRow.AEACN
        }
    }

    void fillDoseLimitingToxicity(Document aeRow, MongoCollection<Document> suppae) {
        aeRow.DSLTTOX = null
        if (studyIn(DR13)) {
            aeRow.DSLTTOX = getSuppFirstQval(aeRow, suppae, 'DLT', 'AESEQ')
            if (aeRow.DSLTTOX) {
                aeRow.DSLTTOX = StringUtils.containsIgnoreCase(aeRow.DSLTTOX as String, "Y") ? 'DLT - Yes' : 'DLT - No'
            }
        } else if (studyIn(DR20, DR18, DR22)) {
            aeRow.DSLTTOX = getSuppFirstQval(aeRow, suppae, 'AEDLTFL', 'AESEQ')
        } else if (studyIn(DR21)) {
            aeRow.DSLTTOX = getSuppFirstQval(aeRow, suppae, 'AEDLT', 'AESEQ')
        }
    }

    void fillTreatmentEmergentFlag(Document aeRow, MongoCollection<Document> suppae, MongoCollection<Document> ex,
                                   List<BasicDBObject> aes) {
        aeRow.TREMERGFL = null
        if (studyIn(DR13)) {
            def count = aes.stream().filter { ae ->
                StringUtils.containsIgnoreCase(getSuppFirstQval(ae as Document, suppae, 'AETRTEM', 'AESEQ'), "Y")
            }.count()
            aeRow.TREMERGFL = count > 0 ? 'Treatment Emergent - Yes' : 'Treatment Emergent - No'
        } else if (studyIn(DR20, DR18, DR21)) {
            Document exRow = ex.aggregate([
                    match(eq('USUBJID', aeRow.USUBJID as String)),
                    sort(Sorts.ascending('EXSTDTC'))])
                    .first()
            aeRow.TREMERGFL = (exRow && aeRow.AESTDTC >= exRow.EXSTDTC) ? 'Y' : null
        }
    }

    @Override
    void fillMeddraVersion(Document aeRow, MongoCollection<Document> suppae) {
        if (studyIn(DR10, DR6, DR21)) {
            aeRow.MEDDRAV = getSuppFirstQval(aeRow, suppae, 'DICTVER', 'AESEQ')
        } else if (studyIn(DR17)) {
            //empty
        } else {
            aeRow.MEDDRAV = getSuppFirstQval(aeRow, suppae, 'MEDDRAV', 'AESEQ')
        }
    }

    @Override
    void fillSystemOrganClass(Document aeRow, MongoCollection<Document> suppae) {
        if (studyIn(DR11, DR17, DR20, DR18, DR21, DR22, DR23, DR25)) {
            aeRow.evtSoc = aeRow.AESOC
        } else {
            aeRow.evtSoc = aeRow.AEBODSYS
        }
    }

    @Override
    void fillImmune(Document aeRow, MongoCollection<Document> suppae,
                    List<BasicDBObject> aes) {
        if (studyIn(DR18)) {
            def immune = aes.collect { getSuppFirstQval(it as Document, suppae, 'AEIMRLFL', 'AESEQ') }
            aeRow.IMMUNE = immune.contains("Y") ? "Y" : getSuppFirstQval(aeRow, suppae, 'AEIMRLFL', 'AESEQ')
        }
    }

    @Override
    void fillComment(Document aeRow, MongoCollection<Document> suppae,
                     List<BasicDBObject> aes) {
    }

    @Override
    void fillAeOfSpecialInterest(Document aeRow, MongoCollection<Document> suppae,
                                 List<BasicDBObject> aes) {
        if (studyIn(DR18)) {
            def aespinfl = aes.collect { getSuppFirstQval(it as Document, suppae, 'AESPINFL', 'AESEQ') }
            aeRow.AESPINFL = aespinfl.contains("Y") ? 'Y' : 'N'
        }
    }

    @Override
    protected String getActionTakenSeverityChanges1(Document row) {
        if (studyIn(DR22)) {
            null
        } else if (studyIn(DR23)) {
            return row.AEACN
        } else {
            return super.getActionTakenSeverityChanges1(row)
        }
    }

    @Override
    protected String getActionTakenSeverityChanges2(Document row, MongoCollection<Document> suppae) {
        if (studyIn(DR22)) {
            null
        } else {
            return super.getActionTakenSeverityChanges2(row, suppae)
        }
    }

    @Override
    protected String getMeddraPreferredTerm(Document row) {
        if (studyIn(DR23)) {
            return row.AEDECOD ?: row.AETERM
        } else {
            return super.getMeddraPreferredTerm(row)
        }
    }
}



