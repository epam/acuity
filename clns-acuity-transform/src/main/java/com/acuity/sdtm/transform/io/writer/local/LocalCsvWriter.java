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

package com.acuity.sdtm.transform.io.writer.local;

import com.acuity.sdtm.data.util.StoragePrefix;
import com.acuity.sdtm.transform.common.Study;
import com.acuity.sdtm.transform.exception.FileProcessingException;
import com.acuity.sdtm.transform.io.writer.CsvFileWriter;
import com.acuity.sdtm.transform.io.writer.DestinationMatcher;
import com.opencsv.CSVWriter;
import lombok.SneakyThrows;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileWriter;
import java.nio.file.Paths;

@Service("localCsvWriter")
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class LocalCsvWriter implements DestinationMatcher {
    protected static final Logger LOG = LoggerFactory.getLogger(CsvFileWriter.class);

    @Override
    public boolean match(String destination) {
        return !destination.startsWith(StoragePrefix.AZURE_FILE_PROTO_PREFIX.getPrefix())
                && !destination.startsWith(StoragePrefix.SMB_PROTO_PREFIX.getPrefix());
    }

    @Override
    @SneakyThrows
    public CSVWriter getWriter(String destination, String fileName, Study study) {
        final File file = Paths.get(destination,  fileName + ".csv").toFile();
        if (file.getParentFile().mkdirs()) {
            LOG.info("Output folder was created: {}" + file.getParentFile().getCanonicalPath());
        }
        if (file.exists() || file.createNewFile()) {
            return new CSVWriter(new FileWriter(file), ',');
        } else {
            final String message = "Study[" + study + "]: can not create file for [" + file.getCanonicalPath() + "]";
            LOG.error("Study[ {} ]: can not create file for [ {} ]", study, file.getCanonicalPath());
            throw new RuntimeException(new FileProcessingException(message));
        }
    }
}
