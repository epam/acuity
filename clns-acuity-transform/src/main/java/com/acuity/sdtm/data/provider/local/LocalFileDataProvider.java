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

package com.acuity.sdtm.data.provider.local;

import com.acuity.sdtm.data.provider.DataProvider;
import com.acuity.sdtm.data.provider.UniFile;
import com.acuity.sdtm.data.util.FileTypeUtil;
import com.acuity.sdtm.data.util.StoragePrefix;
import lombok.SneakyThrows;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;

@Service("localFileDataProvider")
public class LocalFileDataProvider implements DataProvider {

    @Override
    public boolean match(String location) {
        return !location.startsWith(StoragePrefix.AZURE_FILE_PROTO_PREFIX.getPrefix())
                && !location.startsWith(StoragePrefix.SMB_PROTO_PREFIX.getPrefix());
    }

    @Override
    @SneakyThrows
    public List<UniFile> getListFiles(String location) {
        return Files.walk(Paths.get(location)).map(Path::toFile).
                filter(file -> file.isFile() && (FileTypeUtil.isCsvFile(file.getName()) || FileTypeUtil.isSasFile(file.getName()))).
                map(LocalFile::new).collect(Collectors.toList());
    }

    private static class LocalFile implements UniFile {

        private File file;

        LocalFile(File file) {
            this.file = file;
        }

        @Override
        public String getName() {
            return file.getName();
        }

        @Override
        public InputStream getInputStream() throws IOException {
            return new FileInputStream(file);
        }
    }
}
