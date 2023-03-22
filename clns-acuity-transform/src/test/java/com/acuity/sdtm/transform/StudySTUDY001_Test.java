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

package com.acuity.sdtm.transform;

import static java.util.stream.Collectors.toList;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import com.acuity.sdtm.transform.config.AutoProcessConfiguration;
import com.acuity.sdtm.transform.config.CsvInMemoryWriter;
import com.acuity.sdtm.transform.config.MongoConfiguration;
import com.acuity.sdtm.transform.config.TestContextConfiguration;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.ConfigFileApplicationContextInitializer;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@TestPropertySource(properties = {
        "source=test_data/STUDY001",
        "destination=",
        "domains=",
        "study=STUDY001",
        "version=SDTM_1_1_STUDY001",
})
@ContextConfiguration(classes = {TestContextConfiguration.class, AutoProcessConfiguration.class, MongoConfiguration.class},
        initializers = ConfigFileApplicationContextInitializer.class)
@ActiveProfiles("embedded-mongo")
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
public class StudySTUDY001_Test {

    @Autowired
    CsvInMemoryWriter writer;

    @Test
    public void testMedicalHistory() {
        List<List<String>> csv = writer.getCsv("med_hist");
        assertThat(csv).hasSize(5);

        List<String> header = csv.get(0);
        assertThat(header).containsOnly("STUDY", "PART", "SUBJECT", "MEDICATION", "MEDICATION_GROUP", "STATUS", "START", "END", "LLTNAME",
                "PT_TERM", "HLT_TERM", "SOC_TERM", "CURRENT_MEDICATION");

        List<List<String>> data = csv.stream().filter(
                row -> "E12345".equals(row.get(header.indexOf("SUBJECT"))) &&
                        "Allergy / Immunology".equals(row.get(header.indexOf("MEDICATION"))) &&
                        "Allergy to adhesive".equals(row.get(header.indexOf("MEDICATION_GROUP"))) &&
                        "X".equals(row.get(header.indexOf("STATUS"))) &&
                        "04/01/2014".equals(row.get(header.indexOf("START"))) &&
                        row.get(header.indexOf("END")) == null &&
                        "Hypersensitivity".equals(row.get(header.indexOf("PT_TERM"))) &&
                        "Allergic conditions NEC".equals(row.get(header.indexOf("HLT_TERM"))) &&
                        "Immune system disorders".equals(row.get(header.indexOf("SOC_TERM"))) &&
                        "1".equals(row.get(header.indexOf("CURRENT_MEDICATION"))) &&
                        "Allergy".equals(row.get(header.indexOf("LLTNAME"))
                        )
        ).collect(toList());

        assertThat(data).hasSize(1);
    }

    @Test
    public void testRadiotherapy() {
        List<List<String>> csv = writer.getCsv("caprxr");
        assertThat(csv).hasSize(8);

        List<String> header = csv.get(0);
        assertThat(header).containsOnly("STUDY", "PART", "SUBJECT", "RADIOTHERAPY", "START", "END", "CXCANCER");
        List<List<String>> data1 = csv.stream().filter(
                row -> "E34567".equals(row.get(header.indexOf("SUBJECT"))) &&
                        "radiotherapy".equals(row.get(header.indexOf("RADIOTHERAPY"))) &&
                        "07/01/2017".equals(row.get(header.indexOf("START"))) &&
                        "09/30/2017".equals(row.get(header.indexOf("END")))
        ).collect(toList());

        assertThat(data1).hasSize(1);

        List<List<String>> data2 = csv.stream().filter(
                row -> "M12345".equals(row.get(header.indexOf("SUBJECT"))) &&
                        "Previous".equals(row.get(header.indexOf("CXCANCER")))
        ).collect(toList());

        assertThat(data2).hasSize(3);

        List<List<String>> data3 = csv.stream().filter(
                row -> "M12347".equals(row.get(header.indexOf("SUBJECT"))) &&
                        "Post".equals(row.get(header.indexOf("CXCANCER")))
        ).collect(toList());

        assertThat(data3).hasSize(3);
    }

    @Test
    public void testChemotherapy() {
        List<List<String>> csv = writer.getCsv("caprx");
        assertThat(csv).hasSize(14);

        List<String> header = csv.get(0);
        assertThat(header).containsOnly("STUDY", "PART", "SUBJECT", "THERAPY", "START", "END", "CXCANCER", "BEST_RESPONSE");

        List<List<String>> data1 = csv.stream().filter(
                row -> "E23456".equals(row.get(header.indexOf("SUBJECT"))) &&
                        "Imatinib".equals(row.get(header.indexOf("THERAPY"))) &&
                        "01/01/2012".equals(row.get(header.indexOf("START"))) &&
                        "12/31/2014".equals(row.get(header.indexOf("END"))) &&
                        "Active disease".equals(row.get(header.indexOf("BEST_RESPONSE")))
        ).collect(toList());

        assertThat(data1).hasSize(1);

        List<List<String>> data2 = csv.stream().filter(
                row -> "M12346".equals(row.get(header.indexOf("SUBJECT"))) &&
                        "".equals(row.get(header.indexOf("CXCANCER")))
        ).collect(toList());

        assertThat(data2).hasSize(3);
    }

}
