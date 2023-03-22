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

package com.acuity.sdtm.data.provider.samba;

import com.acuity.sdtm.data.provider.DataProvider;
import com.acuity.sdtm.data.provider.UniFile;
import com.acuity.sdtm.data.util.FileTypeUtil;
import com.acuity.sdtm.data.util.StoragePrefix;
import com.acuity.sdtm.transform.Env;
import jcifs.smb.SmbFile;
import lombok.SneakyThrows;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service("sambaDataProvider")
public class SambaDataProvider implements DataProvider {

    static {
        jcifs.Config.setProperty("jcifs.smb.client.responseTimeout", "750000");
        jcifs.Config.setProperty("jcifs.smb.client.soTimeout", "800000");
    }

    @Override
    public boolean match(String location) {
        return location.startsWith(StoragePrefix.SMB_PROTO_PREFIX.getPrefix());
    }

    @Override
    @SneakyThrows
    public List<UniFile> getListFiles(String location) {
        if (!location.endsWith("/")) {
            location += "/";
        }

        SmbFile dir = new SmbFile(location, Env.NTLM_AUTH);

        return Arrays.stream(dir.listFiles(file -> file.isFile()
                && (FileTypeUtil.isCsvFile(file.getName()) || FileTypeUtil.isSasFile(file.getName())))).
                map(SmbData::new).collect(Collectors.toList());
    }

    private static class SmbData implements UniFile {

        private SmbFile file;

        SmbData(SmbFile file) {
            this.file = file;
        }

        @Override
        public String getName() {
            return file.getName();
        }

        @Override
        public InputStream getInputStream() throws IOException {
            return file.getInputStream();
        }
    }
}
