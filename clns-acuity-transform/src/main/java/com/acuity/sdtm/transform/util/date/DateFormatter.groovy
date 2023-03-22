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

import java.util.regex.Pattern

enum DateFormatter {
    YYYYMMDD('yyyy-MM-dd', Pattern.compile('^\\d{4}-\\d{1,2}-\\d{1,2}\$')){

        private final String SPLITTER = '-'

        @Override
        String extractMonth(String date) {
            return date.split(SPLITTER)[1]
        }

        @Override
        String extractYear(String date) {
            return date.split(SPLITTER)[0]
        }

        @Override
        String extractDay(String date) {
            return date.split(SPLITTER)[2]
        }

        @Override
        String replaceDay(String date, String day) {
            String[] parts = date.split(SPLITTER)
            return parts[0] + SPLITTER + parts[1] + SPLITTER + day
        }

        @Override
        String replaceMonth(String date, String month) {
            String[] parts = date.split(SPLITTER)
            return parts[0] + SPLITTER + month + SPLITTER + parts[2]
        }

    },
    MMDDYYYY('MM/dd/yyyy', Pattern.compile('^\\d{1,2}/\\d{1,2}/\\d{4}\$')){

        private final String SPLITTER = '/'

        @Override
        String extractMonth(String date) {
            return date.split(SPLITTER)[0]
        }

        @Override
        String extractYear(String date) {
            return date.split(SPLITTER)[2]
        }

        @Override
        String extractDay(String date) {
            return date.split(SPLITTER)[1]
        }

        @Override
        String replaceDay(String date, String day) {
            String[] parts = date.split(SPLITTER)
            return parts[0] + SPLITTER + day + SPLITTER + parts[2]
        }

        @Override
        String replaceMonth(String date, String month) {
            String[] parts = date.split(SPLITTER)
            return month + SPLITTER + parts[1] + SPLITTER + parts[2]

        }

    },
    DDMMMYY('dd-MMM-yy',
            Pattern.compile('^\\d{1,2}-(?i)(jan|feb|mar|apr|may|jun|jul|aug|sep|oct|nov|dec)-\\d{2}\$')){
    },
    EEEMMMDDTIMEZYYYY('EEE MMM dd kk:mm:ss z yyyy',
            Pattern.compile('^(?i)(sun|mon|tue|wed|thu|fri|sat)\\s{1}(?i)(jan|feb|mar|apr|may|jun|jul|aug|sep|oct|nov|dec)\\s{1}\\d{1,2}\\s{1}'
                    + '\\d{1,2}:\\d{1,2}:\\d{1,2}\\s{1}[a-z]{3}\\s{1}\\d{4}')){
    },
    MMMYY('MMM-yy',
            Pattern.compile('^(?i)(jan|feb|mar|apr|may|jun|jul|aug|sep|oct|nov|dec)-\\d{2}\$')){
    },
    NULLFORMAT(null, null){
    }

    private String format
    private Pattern pattern

    DateFormatter(String format, Pattern pattern) {
        this.format = format
        this.pattern = pattern
    }

    String getFormat() {
        return format
    }

    Pattern getPattern() {
        return pattern
    }

    String extractMonth(String date) {
        throw new UnsupportedOperationException()
    }

    String extractYear(String date) {
        throw new UnsupportedOperationException()
    }

    String extractDay(String date) {
        throw new UnsupportedOperationException()
    }

    String replaceDay(String date, String day) {
        throw new UnsupportedOperationException()
    }

    String replaceMonth(String date, String month) {
        throw new UnsupportedOperationException()
    }

    static defineDateFormat(String date) {
        if (date in ['', '.', null]) {
            return NULLFORMAT
        }
        for (DateFormatter frmt : values()) {
            if (!NULLFORMAT.equals(frmt) && frmt.getPattern().matcher(date).matches()) {
                return frmt
            }
        }
        return NULLFORMAT
    }

}
