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

import com.acuity.sdtm.data.util.FileTypeUtil;
import com.acuity.sdtm.data.util.StoragePrefix;
import com.microsoft.azure.storage.file.CloudFileDirectory;
import com.microsoft.azure.storage.file.ListFileItem;
import lombok.SneakyThrows;

public class AzureStorageUtils {

    public static String trim(String location) {
        return location.substring(StoragePrefix.AZURE_FILE_PROTO_PREFIX.getPrefix().length());
    }

    @SneakyThrows
    public static ListFileItem walk(CloudFileDirectory directory, String[] tokens, int index) {
        if (tokens.length == 1) {
            return directory;
        }
        if (index < tokens.length - 1) {
            return walk(directory.getDirectoryReference(tokens[index]), tokens, index + 1);
        } else {
            if (FileTypeUtil.isSasFile(tokens[index]) || FileTypeUtil.isCsvFile(tokens[index])) {
                return directory.getFileReference(tokens[index]);
            } else {
                return directory.getDirectoryReference(tokens[index]);
            }
        }
    }
}
