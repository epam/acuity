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

package com.acuity.sdtm.transform.io.reader;

import com.opencsv.CSVReader;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author adavliatov.
 * @since 28.07.2016.
 */
@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class CsvReader implements Reader {
    @Value("${source:}")
    private String sourceFolder;

    private CSVReader reader;

    @Override
    public void open(String testFileName) throws IOException {
//        if (destinationFolder.startsWith("smb://")) {
//            SmbFile file = new SmbFile(destinationFolder + "/" + testFileName + ".csv", Env.NTLM_AUTH);
//            reader = new CSVReader(new OutputStreamWriter(file.getOutputStream()), ',');
//        } else {
        reader = new CSVReader(new FileReader(sourceFolder + File.separator + "test" + File.separator + testFileName + ".csv"), ',');
//        try (CSVReader reader = new CSVReader(new FileReader(destinationFolder + File.separator + testFileName + ".csv"), ',')) {
//            for (Iterator<String[]> iter = reader.iterator(); iter.hasNext(); ) {
//                final String[] next = iter.next();
//            }
//        }
//        }
//        write(columns);
    }

    @Override
    public List<List<String>> readAll() {
        List<List<String>> lines = new ArrayList<>();
        for (String[] aReader : reader) {
            lines.add(Arrays.asList(aReader));
        }
        return lines;
    }

    @Override
    public void close() throws IOException {
        if (reader != null) {
            reader.close();
        }
    }
}
