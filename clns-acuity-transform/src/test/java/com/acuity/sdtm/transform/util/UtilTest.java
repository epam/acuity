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

package com.acuity.sdtm.transform.util;

import com.acuity.sdtm.transform.util.Util;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * @author adavliatov.
 * @since 18.08.2016.
 */
public class UtilTest {
    @Test
    public void parseDate() {
        assertEquals("", Util.parseDate((String) null));
        assertEquals("", Util.parseDate(""));
        assertEquals("2016", Util.parseDate("2016"));
        assertEquals("2016", Util.parseDate("2016-"));
        assertEquals("2016", Util.parseDate("2016--"));
        assertEquals("2016-10", Util.parseDate("2016-10-"));
        assertEquals("2016-10", Util.parseDate("2016-10"));
    }

}
