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

import org.bson.Document

import java.text.SimpleDateFormat
import java.time.LocalDateTime

import static org.apache.commons.lang3.math.NumberUtils.createDouble

/**
 * Created by kkjn755 on 6/29/2015.
 */
class Util {
    static final String NO = 'No'
    static final String YES = 'Yes'
    static final String MM_DD_YYYY_SLASHED = 'MM/dd/yyyy'
    static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat('EEE MMM dd kk:mm:ss z yyyy', Locale.ENGLISH)

    static String parseSubject(String val) {
        def matcher = val =~ /^\w+\W+(.+)?/
        if (matcher.size() > 0) {
            matcher[0][1]
        } else {
            val
        }
    }

    static String parseSubjectSpecific(String val) {
        def matcher = val =~ /^\w+\W+[a-zA-Z]*(\d\d\d\d\W)+(.+)?/
        if (matcher.size() > 0) {
            matcher[0][2]
        } else {
            val
        }
    }

    static String parseSubjectBySlash(String val) {
        return val.split('/')[1]
    }

    static String parseCentre(String val) {
        def matcher = val =~ /^\w+\W+[a-zA-Z]*(\d\d\d\d).+?/
        if (matcher.size() > 0) {
            matcher[0][1]
        } else {
            null
        }
    }

    static String parseCentreSpecific(String val) {
        def matcher = val =~ /^\w+\W\w+\W\d+\W(\d+)\W.+/
        if (matcher.size() > 0) {
            matcher[0][1]
        } else {
            null
        }
    }

    static String parseSex(String val) {
        if (val == null) {
            return null
        }

        String upperVal = val.toUpperCase()

        if (upperVal.equals('F') || upperVal.equals('FEMALE')) {
            return 'Female'
        } else if (upperVal.equals('M') || upperVal.equals('MALE')) {
            return 'Male'
        }

        return val
    }

    /**
     * Translates dose frequency to CDASH standard
     * @param val
     * @return
     */
    static String parseDoseFrequencyToCdashString(String dosingFrequencyName) {
        if (dosingFrequencyName == null) {
            return null
        }

        switch (dosingFrequencyName) {
            case 'QD':
            case 'QH':
            case 'QM':
            case 'BID':
            case 'TID':
            case 'QID':
            case 'Every Week':
            case '2 Times per Week':
            case '3 Times per Week':
            case '4 Times per Week':
                return dosingFrequencyName
            case 'QS':
                return 'Every Week'
            case 'BIS':
                return '2 Times per Week'
            case 'TIS':
                return '3 Times per Week'
            case 'QIS':
                return '4 Times per Week'
            default:
                return 'Other'
        }
    }

    static String parseDoseFrequency(String val) {
        switch (val) {
            case 'QD':
            case 'QH':
            case 'QM':
            case 'QS':
                return '1'

            case 'BID':
            case 'BIM':
            case 'BIS':
                return '2'

            case 'TID':
            case 'TIS':
                return '3'

            case 'QID':
            case 'QIS':
                return '4'

            default:
                return null
        }
    }

    static String parseDoseFrequencyUnit(String val) {
        switch (val) {
            case 'QD':
            case 'BID':
            case 'TID':
            case 'QID':
                return 'Day'

            case 'QH':
                return 'Hour'

            case 'QM':
            case 'BIM':
                return 'Month'

            case 'QS':
            case 'BIS':
            case 'TIS':
            case 'QIS':
                return 'Week'

            default:
                return null
        }
    }

    static String parseYesNo(String val) {
        switch (val) {
            case '0':
            case 'N':
            case 'NO':
                return 'No'
            case '1':
            case 'Y':
            case 'YES':
                return 'Yes'
            default:
                return val
        }
    }

    static String parseDate(String date) {
        def matcher = date =~ /(\d)+/
        matcher.collect { it[0] }.join('-')
    }

    static String parseDate(Date date) {
        date ? date.format('yyyy-MM-dd') : null
    }

    static String parseDateWithFormat(String date) {
        date ? ((date.contains('/') || date.contains('-')) ? date : DATE_FORMAT.parse(date).format('yyyy-MM-dd')) : null
    }

    static String parseDateWithFormatPlusAddOneMinuteIfDatesAreEqual(String processedDate, String comparedDate) {
        if (!processedDate) {
            return null
        }
        LocalDateTime parsedDate = LocalDateTime.parse(processedDate)
        if (LocalDateTime.parse(comparedDate) == parsedDate) {
            return parsedDate.plusMinutes(1) as String
        } else {
            return parsedDate as String
        }
    }

    static String parseDateTrimTime(String date) {
        if (!date) {
            return null
        }
        def matcher = date =~ /^(\d{4}\-\d{2}\-\d{2})/
        if (matcher.size() > 0) {
            return matcher[0][1]
        } else {
            return date
        }
    }

    static String parseDateTrimDash(String date) {
        if (!date) {
            return null
        }
        if (date.charAt(date.length() - 1) == '-') {
            int lastOccurrenceIndex = 0
            for (int i = date.length() - 1; i > 1; i--) {
                lastOccurrenceIndex = i
                if (date.charAt(i - 1) != date.charAt(i)) {
                    break
                }
            }
            return date.substring(0, lastOccurrenceIndex - 1)
        } else {
            return date
        }
    }

    static boolean isSuitableNum(String num) {
        if (!num) {
            return false
        }
        def dose
        (dose = createDouble(num as String)) != null && dose > 0 ? YES : NO
    }

//TODO: investigate MongoDB mapping to String instead of Number
// surprisingly, sometimes Mongo parses VISITNUM as text, sometimes as number
// so we need to convert explicitly
//(actually we could use eq('VISITNUM', NumberUtils.parseNumber(row.VISITNUM, Double.class)) but this behaviour is unpredictable
    static Double asDouble(Object num) {
        if (num instanceof String) {
            Double.parseDouble(num)
        } else if (num instanceof Number) {
            (num as Number).doubleValue()
        } else {
            0D
        }
    }

    static Document transformColumnsToUpperCase(Document inputDoc) {
        Document doc = new Document()
        inputDoc.each { prop -> doc.put(prop.getKey().toUpperCase(), inputDoc.get(prop.getKey())) }
        return doc
    }
}
