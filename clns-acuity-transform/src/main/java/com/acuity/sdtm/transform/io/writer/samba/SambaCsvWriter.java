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

package com.acuity.sdtm.transform.io.writer.samba;

import com.acuity.sdtm.data.util.StoragePrefix;
import com.acuity.sdtm.transform.Env;
import com.acuity.sdtm.transform.common.Study;
import com.acuity.sdtm.transform.io.writer.DestinationMatcher;
import com.opencsv.CSVWriter;
import jcifs.smb.SmbFile;
import lombok.SneakyThrows;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import java.io.OutputStreamWriter;

@Service("sambaCsvWriter")
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class SambaCsvWriter implements DestinationMatcher {

    @Override
    public boolean match(String destination) {
        return destination.startsWith(StoragePrefix.SMB_PROTO_PREFIX.getPrefix());
    }

    @Override
    @SneakyThrows
    public CSVWriter getWriter(String destination, String fileName, Study study) {
        SmbFile file = new SmbFile(destination + "/" + fileName + ".csv", Env.NTLM_AUTH);
        return new CSVWriter(new OutputStreamWriter(file.getOutputStream()), ',');
    }
}
