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

package com.acuity.visualisations.web.util;

import org.springframework.util.StringUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.regex.Pattern;

public final class Utils {

    public static final Pattern DATE_PATTERN = Pattern.compile("[0-9]{1,2}-[a-zA-Z]{3}-[0-9]{2,4}");

    private Utils() {
    }

    public static String getDateString(Date date) {
        return newDateFormat("dd-MMM-yyyy").format(date);
    }


    public static Date parse(final String date) throws Exception {
        if (!StringUtils.hasText(date)) {
            return null;
        }
        String trimmedDate = date.trim();
        if (DATE_PATTERN.matcher(trimmedDate).matches()) {
            int yearLength = trimmedDate.substring(trimmedDate.lastIndexOf('-') + 1).length();
            return newDateFormat(yearLength == 4 ? "dd-MMM-yyyy" : "dd-MMM-yy").parse(trimmedDate);
        }
        throw new ParseException("Unparseable date: [" + date + "]", 0);
    }

    private static SimpleDateFormat newDateFormat(String pattern) {
        SimpleDateFormat dateFormat = new SimpleDateFormat(pattern, Locale.ENGLISH);
        dateFormat.setLenient(false);
        return dateFormat;
    }

}
