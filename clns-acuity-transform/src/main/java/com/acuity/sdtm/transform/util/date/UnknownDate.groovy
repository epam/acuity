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

package com.acuity.sdtm.transform.util.date

import java.text.SimpleDateFormat
import java.time.YearMonth

class UnknownDate {

    private String date
    private DateFormatter format

    UnknownDate(String date) {
        this.date = date
        this.format = date == null ? DateFormatter.NULLFORMAT :
                DateFormatter.defineDateFormat(date.replaceAll('(?i)UNK', '01'))
    }

    /**
     * This method convert date if it contains unknown day or month (i.e. 2014-unk-01, 2014-02-unk, 2014-unk-unk)
     * The way it replaces values depends on whether the date is start of period or end.
     * If it is start then unk will be replaced with 01 for day and month.
     * If it is end then unk month will be replaced with 12 (december) and day will be replaced with last day of the month.
     */
    public String normalize(boolean isStartDate) {
        String normalized = '' // normalized means date with replaced UNK by values
        if (DateFormatter.NULLFORMAT.equals(format)) {
            return null
        }
        if (isStartDate) {
            normalized = date.replaceAll('(?i)UNK', '01')
        } else {
            if (isMonthUnknown()) {
                normalized = format.replaceMonth(date, '12')
            }
            if (isDayUnknown()) {
                normalized = normalized.isEmpty() ? date : normalized
                int daysInMonth = YearMonth
                        .of(format.extractYear(normalized).toInteger(), format.extractMonth(normalized).toInteger())
                        .lengthOfMonth()
                normalized = format.replaceDay(normalized, String.valueOf(daysInMonth))
            }
        }
        return normalized
    }

    public String convertNormalizedTo(String targetPattern, boolean isStartDate) {
        String normalized = isUnknown() ? normalize(isStartDate) : date
        return convertDateToFormat(targetPattern, normalized)
    }

    public String convertToTargetFormat(String targetPattern) {
        return convertDateToFormat(targetPattern, date)
    }

    private String convertDateToFormat(String targetPattern, String date) {
        if (DateFormatter.NULLFORMAT.equals(format)) {
            return null
        }

        SimpleDateFormat sdf = new SimpleDateFormat(format.getFormat())
        Date d = sdf.parse(date)
        sdf.applyPattern(targetPattern)
        return sdf.format(d)
    }

    private boolean isUnknown() {
        return Optional.ofNullable(date)
                .map({ s -> s.toLowerCase() })
                .orElse('unk').contains('unk');
    }

    private boolean isDayUnknown() {
        String day = format.extractDay(date)
        return 'unk'.equalsIgnoreCase(day)
    }

    private void replaceDay(String day) {
        date = format.replaceDay(date, day)
    }

    private void replaceMonth(String month) {
        date = format.replaceMonth(date, month)
    }

    private boolean isMonthUnknown() {
        String month = format.extractMonth(date)
        return 'unk'.equalsIgnoreCase(month)

    }
}
