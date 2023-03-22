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

import com.acuity.sdtm.transform.util.StringUtil;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class StringUtilTest {

    @Test
    public void replaceSupplementaryCharacters() {
        assertEquals(null, StringUtil.cutUpToBytesOfUTF8(null, 10));
        assertEquals("", StringUtil.cutUpToBytesOfUTF8("", 10));
        assertEquals("1234567890", StringUtil.cutUpToBytesOfUTF8("1234567890", 5));
        assertEquals("123456", StringUtil.cutUpToBytesOfUTF8("123456", 7));
        assertEquals("12...", StringUtil.cutUpToBytesOfUTF8("12345678", 7));
//        assertEquals("�...", StringUtil.cutUpToBytesOfUTF8("����������", 7));
//        assertEquals("�...", StringUtil.cutUpToBytesOfUTF8("����������", 8));
//        assertEquals("��...", StringUtil.cutUpToBytesOfUTF8("����������", 9));
//        assertEquals("�...", StringUtil.cutUpToBytesOfUTF8("\uD800\uDC82\uD800\uDC82\uD800\uDC82", 8));
        assertEquals("\uD800\uDC82...", StringUtil.cutUpToBytesOfUTF8("\uD800\uDC82\uD800\uDC82\uD800\uDC82", 9));
//        assertEquals("\uD800\uDC82�...", StringUtil.cutUpToBytesOfUTF8("\uD800\uDC82\uD800\uDC82\uD800\uDC82", 10));
//        assertEquals("\uD800\uDC82�...", StringUtil.cutUpToBytesOfUTF8("\uD800\uDC82\uD800\uDC82\uD800\uDC82", 11));
        assertEquals("\uD800\uDC82\uD800\uDC82\uD800\uDC82", StringUtil.cutUpToBytesOfUTF8("\uD800\uDC82\uD800\uDC82\uD800\uDC82", 12));
    }

}
