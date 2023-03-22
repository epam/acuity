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
import com.opencsv.CSVWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import java.util.Collection;

@Primary
@Service
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class CsvDestinationMatcher implements DestinationMatcher {

    @Autowired
    private Collection<DestinationMatcher> destinationMatchers;

    @Override
    public boolean match(String destination) {
        return true;
    }

    @Override
    public CSVWriter getWriter(String destination, String fileName, Study study) {
        return destinationMatchers.stream()
                .filter(p -> p.match(destination))
                .findAny()
                .map(p -> p.getWriter(destination, fileName, study))
                .orElseThrow(UnsupportedOperationException::new);
    }

    @Override
    public void uploadFile(String destination) {
        destinationMatchers.stream()
                .filter(p -> p.match(destination))
                .findAny()
                .map(p -> {
                    p.uploadFile(destination);
                    return p;
                })
                .orElseThrow(UnsupportedOperationException::new);
    }
}
