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

package com.acuity.sdtm.transform.io.writer.azure;

import com.acuity.sdtm.data.provider.azure.AzureFileStorageDataProvider;
import com.acuity.sdtm.data.provider.azure.AzureStorageUtils;
import com.acuity.sdtm.data.util.StoragePrefix;
import com.acuity.sdtm.transform.common.Study;
import com.acuity.sdtm.transform.io.writer.DestinationMatcher;
import com.microsoft.azure.storage.file.CloudFile;
import com.microsoft.azure.storage.file.CloudFileClient;
import com.microsoft.azure.storage.file.CloudFileDirectory;
import com.microsoft.azure.storage.file.CloudFileShare;
import com.opencsv.CSVWriter;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Profile;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.OutputStreamWriter;

@Profile("azure-storage")
@Service("azureStorageCsvWriter")
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class AzureStorageCsvWriter implements DestinationMatcher {

    @Autowired
    private AzureFileStorageDataProvider azureFileStorageDataProvider;

    private ByteArrayOutputStream content;

    private String fileName;

    @Override
    public boolean match(String destination) {
        return destination.startsWith(StoragePrefix.AZURE_FILE_PROTO_PREFIX.getPrefix());
    }

    @Override
    @SneakyThrows
    public CSVWriter getWriter(String destination, String fileName, Study study) {
        this.fileName = fileName;
        content = new ByteArrayOutputStream();
        return new CSVWriter(new OutputStreamWriter(content));
    }

    @Override
    @SneakyThrows
    public void uploadFile(String destination) {
        String[] tokens = AzureStorageUtils.trim(destination).split("/");
        CloudFileClient client = azureFileStorageDataProvider.getClient();
        CloudFileShare share = client.getShareReference(tokens[0]);
        CloudFileDirectory root = share.getRootDirectoryReference();
        CloudFileDirectory cloudFileDirectory = (CloudFileDirectory) AzureStorageUtils.walk(root, tokens, 1);
        CloudFile file = cloudFileDirectory.getFileReference(fileName + ".csv");
        file.uploadFromByteArray(content.toByteArray(), 0, content.toByteArray().length);
    }
}
