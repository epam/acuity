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

import java.io.IOException;
import java.util.List;
import java.util.Map;

import com.acuity.sdtm.transform.common.Study;

/**
 * Created by knml167 on 11/06/2015.
 */
public interface Writer {
    void open(Study study, String fileName, List<String> columns) throws IOException;
    void open(Study study, String module, String fileName, List<String> columns) throws IOException;

    void write(List<String> line);

    void write(String[] row);
    void write(Map<String, String> row);

    void close() throws IOException;
}
