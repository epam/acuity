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

package com.acuity.sdtm.transform.util.date;

import com.acuity.sdtm.transform.util.date.UnknownDate;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class UnknownDateTest {

    @Test
    public void convertDateFromUnkownFormat() {
        String targetPattern = "MM/dd/yyyy";
        assertEquals("01/03/2015", new UnknownDate("1/3/2015").convertNormalizedTo(targetPattern, true));
        assertEquals("01/03/2015", new UnknownDate("01/03/2015").convertNormalizedTo(targetPattern, true));
        assertEquals("01/01/2015", new UnknownDate("2015-unk-unk").convertNormalizedTo(targetPattern, true));
        assertEquals("12/31/2015", new UnknownDate("2015-unk-unk").convertNormalizedTo(targetPattern, false));
        assertEquals("02/28/2015", new UnknownDate("2015-02-unk").convertNormalizedTo(targetPattern, false));
        assertEquals("11/30/2015", new UnknownDate("2015-11-unk").convertNormalizedTo(targetPattern, false));
        assertEquals("12/20/2015", new UnknownDate("2015-unk-20").convertNormalizedTo(targetPattern, false));
        assertEquals("01/01/2015", new UnknownDate("unk/unk/2015").convertNormalizedTo(targetPattern, true));
        assertEquals("01/01/2014", new UnknownDate("1-Jan-14").convertNormalizedTo(targetPattern, true));
        assertEquals("09/01/2014", new UnknownDate("01-sEP-14").convertNormalizedTo(targetPattern, true));
        assertEquals("01/14/2014", new UnknownDate("2014-1-14").convertNormalizedTo(targetPattern, true));
        assertEquals("01/14/2014", new UnknownDate("2014-01-14").convertNormalizedTo(targetPattern, true));
        assertEquals("01/04/2014", new UnknownDate("2014-01-4").convertNormalizedTo(targetPattern, true));
        assertEquals("01/01/2014", new UnknownDate("Wed Jan 01 00:00:00 GMT 2014").convertNormalizedTo(targetPattern, true));
    }

}
