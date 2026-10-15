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

package com.acuity.visualisations.rawdatamodel.util;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import static com.acuity.visualisations.rawdatamodel.util.Constants.DEFAULT_GROUP;

/**
 * Alphanumeric comparator that always sorts the sentinel "Default group" value last,
 * regardless of its alphabetical position. All other values keep their natural
 * alphanumeric order. Used to keep "Default group" entries at the end of subject
 * grouping filter options (Wide/Short, Long/Thin, and custom/Alternative subject
 * groupings all share this ordering since they converge on the same {@code SetFilter}
 * values list).
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class AlphanumDefaultGroupLastComparator extends AlphanumComparator<String> {

    private static final AlphanumDefaultGroupLastComparator INSTANCE = new AlphanumDefaultGroupLastComparator();

    public static AlphanumDefaultGroupLastComparator getInstance() {
        return INSTANCE;
    }

    @Override
    public int compare(String o1, String o2) {
        boolean default1 = DEFAULT_GROUP.equalsIgnoreCase(o1);
        boolean default2 = DEFAULT_GROUP.equalsIgnoreCase(o2);
        if (default1 && default2) {
            return 0;
        }
        if (default1) {
            return 1;
        }
        if (default2) {
            return -1;
        }
        return super.compare(o1, o2);
    }
}
