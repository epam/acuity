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

package com.acuity.sdtm.data.provider.azure;

import com.acuity.sdtm.data.provider.DataProvider;
import com.acuity.sdtm.data.provider.UniFile;
import com.acuity.sdtm.data.util.FileTypeUtil;
import com.acuity.sdtm.data.util.StoragePrefix;
import com.microsoft.azure.storage.file.*;
import lombok.Getter;
import lombok.SneakyThrows;
import lombok.Value;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Getter
public class AzureFileStorageDataProvider implements DataProvider {

    private CloudFileClient client;

    @Autowired
    public AzureFileStorageDataProvider(CloudFileClient client) {
        this.client = client;
    }

    @Override
    public boolean match(String location) {
        return location.startsWith(StoragePrefix.AZURE_FILE_PROTO_PREFIX.getPrefix());
    }

    @Override
    @SneakyThrows
    public List<UniFile> getListFiles(String location) {
        String[] tokens = AzureStorageUtils.trim(location).split("/");

        CloudFileShare share = client.getShareReference(tokens[0]);
        CloudFileDirectory root = share.getRootDirectoryReference();

        CloudFileDirectory cloudFileDirectory = (CloudFileDirectory) AzureStorageUtils.walk(root, tokens, 1);
        Iterable<ListFileItem> listFileItems = cloudFileDirectory.listFilesAndDirectories();
        return StreamSupport.stream(listFileItems.spliterator(), false)
                .filter(t -> FileTypeUtil.isSasFile(t.getUri().getPath()) || FileTypeUtil.isCsvFile(t.getUri().getPath()))
                .map(e -> get(e.getUri().getPath()))
                .collect(Collectors.toList());
    }

    @SneakyThrows
    private UniFile get(String location) {
        if (location.startsWith("/")) {
            location = location.substring(1);
        }
        String[] tokens = location.split("/");

        CloudFileShare share = client.getShareReference(tokens[0]);
        CloudFileDirectory root = share.getRootDirectoryReference();

        CloudFile file = (CloudFile) AzureStorageUtils.walk(root, tokens, 1);

        return new CloudFileData(file);
    }

    @Value
    private static class CloudFileData implements UniFile {

        CloudFile file;

        @Override
        public String getName() {
            return file.getName();
        }

        @Override
        @SneakyThrows
        public InputStream getInputStream() throws IOException {
            return file.openRead();
        }
    }
}
