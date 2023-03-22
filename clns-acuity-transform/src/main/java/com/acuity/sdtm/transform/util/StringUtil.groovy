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

package com.acuity.sdtm.transform.util

import org.apache.commons.lang3.ArrayUtils

import static java.nio.charset.StandardCharsets.UTF_8

class StringUtil {

    public static final int MAX_ACUITY_LENGTH = 255
    // 5 because 3 bytes go for the three dots and 2 bytes go for the last probably corrupted char because of cutting
    // the 2 bytes because corrupted char while cutting is replaced with � which takes 3 bytes
    public static final int TWO_EXTRA_BYTES_INCLUDING_THREE_DOTS = 5
    public static final String THREE_DOTS = "..."

    static String cutUpToBytesOfUTF8(String string, int maxLength) {
        if (string != null && string.getBytes(UTF_8).length > maxLength && maxLength > TWO_EXTRA_BYTES_INCLUDING_THREE_DOTS) {
            byte[] cutString = Arrays.copyOfRange(string.getBytes(UTF_8), 0, maxLength - TWO_EXTRA_BYTES_INCLUDING_THREE_DOTS)
            string = new String(ArrayUtils.addAll(cutString, THREE_DOTS.getBytes(UTF_8)), UTF_8)
        }
        return string
    }

    /**
     * Method cuts up string to the length {@code length}
     */
    static String cutUpToChars(String str, int length) {
        boolean cut = str != null && str.length() >= length
        return !cut || str.length() < 3 ? str :  (str.substring(0, length - 3) + THREE_DOTS)
    }

}
