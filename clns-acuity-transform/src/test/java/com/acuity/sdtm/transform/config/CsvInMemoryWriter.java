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

package com.acuity.sdtm.transform.config;

import com.acuity.sdtm.transform.common.Study;
import com.acuity.sdtm.transform.io.writer.CsvFileWriter;

import java.io.IOException;
import java.util.*;

/**
 * Accumulate results of transformation in the Java Map instead of File System
 */
public class CsvInMemoryWriter extends CsvFileWriter {

    private Map<String, List<List<String>>> csvs = new HashMap<>();
    private List<List<String>> csv;

    private List<String> columns;

    @Override
    public void open(Study study, String fileName, List<String> columns) throws IOException {
        open(study, "", fileName, columns);
    }

    @Override
    public void open(Study study, String module, String fileName, List<String> columns) throws IOException {
        this.columns = columns;
        csv = new ArrayList<>();
        csvs.put(fileName, csv);
        write(columns);
    }

    @Override
    public void write(List<String> line) {
        csv.add(line);
    }

    @Override
    public void write(Map<String, String> row) {
        List<String> out = new ArrayList<>(columns.size());
        for (String column : columns) {
            out.add(row.get(column));
        }

        csv.add(out);
    }

    public void write(String[] row) {
        csv.add(Arrays.asList(row));
    }

    public List<List<String>> getCsv(String fileName) {
        return csvs.get(fileName);
    }

    @Override
    public void close() throws IOException {

    }
}
