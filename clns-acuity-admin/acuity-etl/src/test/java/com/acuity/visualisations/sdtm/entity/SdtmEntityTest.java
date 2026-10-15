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

package com.acuity.visualisations.sdtm.entity;

import com.acuity.visualisations.batch.reader.tablereader.TableRow;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

public class SdtmEntityTest {

    @Test
    public void shouldTreatDotAsEmptyForDoubleValues() {
        SdtmEntity entity = new TestSdtmEntity();

        assertNull(entity.readDouble(new MapTableRow(Collections.singletonMap("VISITNUM", ".")), "VISITNUM"));
        assertEquals(12.5d, entity.readDouble(new MapTableRow(Collections.singletonMap("VISITNUM", "12.5")), "VISITNUM"));
    }

    private static final class TestSdtmEntity extends SdtmEntity {
        @Override
        public void read(TableRow row) {
        }
    }

    private static final class MapTableRow extends TableRow {
        private final Map<String, Object> values;

        private MapTableRow(Map<String, Object> values) {
            this.values = values;
        }

        @Override
        public Object getValue(String column) {
            return values.get(column);
        }
    }
}
