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

package com.acuity.visualisations.transform.function;

public class StringAssembler extends AbstractFunction<String> {

    @Override
    public String function(Object[] params) {
        if (params == null || params.length == 0) {
            return null;
        }
        StringBuilder builder = new StringBuilder();
        for (Object param : params) {
            if (param == null) {
                continue;
            }
            if (!(param instanceof String)) {
                continue;
            }
            String pStr = (String) param;
            if (pStr.isEmpty()) {
                continue;
            }
            builder.append(pStr);
            builder.append("; ");
        }
        if (builder.length() != 0) {
            builder.setLength(builder.length() - 2);
        }
        return builder.toString();
    }
}
