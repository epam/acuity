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
import com.acuity.sdtm.transform.processor.SDTM_1_1.SDTM_1_1_LaboratoryProcessor
import com.acuity.sdtm.transform.util.Util
import com.mongodb.client.MongoCollection
import com.mongodb.client.MongoDatabase
import org.bson.Document
import org.springframework.stereotype.Component

import java.util.stream.Collectors

import static com.mongodb.client.model.Filters.*

@Component
class SDTM_1_1_STUDY001_LaboratoryProcessor extends SDTM_1_1_LaboratoryProcessor implements SDTM_1_1_STUDY001_StudyCommonProcessor {

    def CHEM1_LABCODE = ['LBNA', 'LBK', 'LBCA', 'LBGLUC', 'LBCREAT', 'LBBUN', 'LBALB', 'LBTBILI', 'LBAST', 'LBALT',
                         'LBPROT', 'LBMAG', 'LBPHOS', 'LBPHOS1', 'LBCL', 'LBCRP', 'LBCPK', 'LBGGT', 'LBROTH']

    def URIN1_LABCODE = ['LBUSG', 'LBUPH', 'LBUPRT', 'LBUGLUC', 'LBUROBIL', 'LBUHB', 'LBURBC', 'LBUWBC', 'LBKET', 'LBBIL',
                         'LBNITR', 'LBALB', 'LBCREAT']

    def HEMA1_LABCODE = ['HGB', 'PLAT', 'RETI', 'RETIRBC', 'HCT', 'MCV', 'RBC', 'WBC', 'BNEUT', 'NEUTLE', 'LYM', 'LYMLE',
                         'MONO', 'MONOLE', 'BASO', 'BASOLE', 'EOS', 'EOSLE', 'APTT', 'INR']

    def URIN_LABCODE_SUBSTITUTION_MAP = [LBALB:'LBUALB', LBCREAT:'LBUCREAT']

    @Override
    Map<String, String> map(Document row) {
        [
                STUDY   : 'Carrick_data',
                PART    : 'B',
                SUBJECT : row.SUBNUM as String,
                VISIT   : row.VISNAME as String,
                SAMP_DAT: row.LBDAT instanceof String ? Util.parseDate(row.LBDAT as String) : Util.parseDate(row.LBDAT as Date) as String,
                LABCODE : row.LABCODE as String,
                LABVALUE: row.LABVALUE as String,
                LAB_UNIT: row.LABUNIT as String,
                REFLOW  : row.LBSTNRLO as String,
                REFHIGH : row.LBSTNRHI as String,
        ]
    }

    @Override
    public void process(MongoDatabase mongo, Writer writer) throws IOException {
        MongoCollection<Document> chemLab = mongo.getCollection("${sessionId}_CHEM1")
        MongoCollection<Document> urinLab = mongo.getCollection("${sessionId}_URIN1")
        MongoCollection<Document> hemaLab = mongo.getCollection("${sessionId}_HEMA1")

        MongoCollection<Document> dem = mongo.getCollection("${sessionId}_DM")

        MongoCollection<Document> labRefRanges = mongo.getCollection("${sessionId}_LABREFRANGES")

        MongoCollection<Document> lbsn = mongo.getCollection("${sessionId}_DAT_LBSN")
        Map<String, String> lbsr = mongo.getCollection("${sessionId}_DAT_LBSR")
                .find()
                .collectEntries { [it.SITENUM, it.LABNUM ] };
        Map<String, String> labLookUpCodes = mongo.getCollection("${sessionId}_LABLOOKUPNAMES")
                .find()
                .collectEntries { [ it.SAS, it['Test Name'] ] };


        try {
            writer.open(study, module, "laboratory", HEADER)

            chemLab.find().each { row ->
                Document subject = extractSubject(row, dem);
                List<Map<String, String>> labs = subject == null
                        ? Collections.<Map<String, String>> emptyList()
                        : labRefRanges.count() > 0
                        ? splitRowByLab(row, CHEM1_LABCODE, subject, labRefRanges)
                        : splitRowByLab(row, CHEM1_LABCODE, subject, lbsn, lbsr, labLookUpCodes)
                labs.each { lab ->
                    writer.write(lab)
                }
            }

            urinLab.find().each { row ->
                List<Map<String, String>> labs = splitRowByLabUrine(row, URIN1_LABCODE)
                labs.each { lab ->
                    writer.write(lab)
                }
            }

            hemaLab.find().each { row ->
                Document subject = extractSubject(row, dem);
                List<Map<String, String>> labs = subject == null
                        ? Collections.<Map<String, String>> emptyList()
                        : labRefRanges.count() > 0
                        ? splitRowByLab(row, HEMA1_LABCODE, subject, labRefRanges)
                        : splitRowByLab(row, HEMA1_LABCODE, subject, lbsn, lbsr, labLookUpCodes)
                labs.each { lab ->
                    writer.write(lab)
                }
            }
        } finally {
            writer.close()
        }
    }

    private static Document extractSubject(Document row,
                                    MongoCollection<Document> dem) {
        return dem.find(and(
                eq('SUBID', row.SUBID),
                eq('SUBNUM', row.SUBNUM),
                eq('SITENUM', row.SITENUM))).first()
    }

    private List<Map<String, String>> splitRowByLab(Document row,
                                                    List labcodes,
                                                    Document subject,
                                                    MongoCollection<Document> ranges) {
        Long age = subject.'AGE' as Long
        String sex = subject.'SEX'

        return labcodes.stream()
                .sequential()
                .filter({ labcode -> row.get(labcode) != null })
                .map({ labcode ->
                    row.LABCODE = labcode
                    row.LABVALUE = row.get(labcode)
                    row.LABUNIT = row.get(labcode + 'U')
                    Document rangeRow = ranges.find(and(
                            eq('Lab #', row.SITENUM as String),
                            eq('SAS NAME', row.LABCODE as String),
                            or(eq('Gender', null), eq('Gender', sex)),
                            or(eq('Age Low', null), lte('Age Low', age)),
                            or(eq('Age High', null), gte('Age High', age))
                    )).first()

                    row.LBSTNRLO = rangeRow?.'Range Low'
                    row.LBSTNRHI = rangeRow?.'Range High'

                    return map(row)
                })
                .collect(Collectors.toList())
    };

    private List<Map<String, String>> splitRowByLab(Document row,
                                                    List labcodes,
                                                    Document subject,
                                                    MongoCollection<Document> lbsn,
                                                    Map<String, String> lbsr,
                                                    Map<String, String> labLookUpCodes) {
        Long age = subject.'AGE' as Long
        String sex = subject.'SEX'

        return labcodes.stream()
                .sequential()
                .filter({ labcode -> row.get(labcode) != null })
                .map({ labcode ->
                    row.LABCODE = labcode
                    row.LABVALUE = row.get(labcode)
                    row.LABUNIT = row.get(labcode + 'U')


                    Document rangeRow = lbsn.find(and(
                            // some type troubles are possible here; in fact, lbsr values can be numeric or string,
                            // and it does matter for the equality being checked. if unable to find the required row
                            // with the value as is, try to cast them to, e. g., Long of String forcedly (or just
                            // retrieve the actual column type from lbsn if you know how)
                            eq('LABNUM', lbsr.get(row.SITENUM)),
                            eq('TESTC_DEC', labLookUpCodes.get(row.LABCODE as String)),
                            or(eq('GENDERC', null), eq('GENDERC', sex)),
                            or(eq('AGELOW', null), lte('AGELOW', age)),
                            or(eq('AGEHIGH', null), gte('AGEHIGH', age))
                    )).first()

                    row.LBSTNRLO = rangeRow?.'RNGLOW'
                    row.LBSTNRHI = rangeRow?.'RNGHIGH'

                    return map(row)
                })
                .collect(Collectors.toList())
    };

    private List<Map<String, String>> splitRowByLabUrine(Document row, List<String> labcodes) {

        List<Map<String, String>> result = new ArrayList<>()
        labcodes.each { labcode ->
            if (row.get(labcode) != null) {
                row.LABCODE = getUrinalysisLabcode(labcode)
                row.LABVALUE = row.get(labcode)
                row.LABUNIT = row.get(labcode + 'U')
                result.add(map(row))
            }
        }
        return result
    };

    private String getUrinalysisLabcode(String labcode) {
        String substitutedLabcode = URIN_LABCODE_SUBSTITUTION_MAP.get(labcode)
        return substitutedLabcode != null ? substitutedLabcode : labcode
    }
}
