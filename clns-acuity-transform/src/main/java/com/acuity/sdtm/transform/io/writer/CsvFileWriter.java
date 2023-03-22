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

package com.acuity.sdtm.transform.io.writer;

import com.acuity.sdtm.transform.common.Study;
import com.acuity.sdtm.transform.config.StudiesConfig;
import com.acuity.sdtm.transform.config.StudyConfig;
import com.acuity.sdtm.transform.exception.ProcessorFailedException;
import com.opencsv.CSVWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * Created by knml167 on 10/06/2015.
 */
@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class CsvFileWriter implements Writer {
    protected static final Logger LOG = LoggerFactory.getLogger(CsvFileWriter.class);

    @Autowired
    private StudiesConfig configs;

    @Autowired
    private DestinationMatcher destinationMather;

    private CSVWriter csvWriter = null;

    private List<String> columns;

    private String destination;

    @Override
    public void open(Study study, String fileName, List<String> columns) throws IOException {
        open(study, "", fileName, columns);
    }

    @Override
    public void open(Study study, String module, String fileName, List<String> columns) throws IOException {
        this.columns = columns;
        final StudyConfig config = configs
                .getList()
                .stream()
                .filter(conf -> conf.getStudy() == study && conf.getModule().equals(module))
                .findFirst()
                .orElseThrow(
                        () -> {
                            final String message = "Study[" + study + "]: suitable configuration not found";
                            LOG.error("Study[ {} ]: suitable configuration not found", study);
                            return new RuntimeException(new ProcessorFailedException(message));
                        });
        destination = config.getDestination();
        csvWriter = destinationMather.getWriter(destination, fileName, study);
        write(columns);
    }

    @Override
    public void write(List<String> line) {
        csvWriter.writeNext(line.toArray(new String[line.size()]));
    }

    @Override
    public void write(Map<String, String> row) {
        String[] out = new String[columns.size()];
        for (int i = 0; i < columns.size(); i++) {
            out[i] = row.get(columns.get(i));
        }
        csvWriter.writeNext(out);
    }

    @Override
    public void write(String[] row) {
        csvWriter.writeNext(row);
    }

    @Override
    public void close() throws IOException {
        if (csvWriter != null) {
            csvWriter.flush();
            destinationMather.uploadFile(destination);
            csvWriter.close();
        }
    }
}
