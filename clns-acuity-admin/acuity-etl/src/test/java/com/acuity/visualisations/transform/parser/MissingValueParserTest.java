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

package com.acuity.visualisations.transform.parser;

import com.acuity.visualisations.exception.InvalidDataFormatException;
import com.acuity.visualisations.transform.rule.ParserRule;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.temporal.Temporal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

public class MissingValueParserTest {

    @Test
    public void shouldTreatDotAsEmptyForNumericParsers() throws InvalidDataFormatException {
        DoubleParser doubleParser = new DoubleParser();
        BigDecimalParser bigDecimalParser = new BigDecimalParser();
        IntegerParser integerParser = new IntegerParser();
        LabvalueParser labvalueParser = new LabvalueParser();

        assertNull(doubleParser.parse("."));
        assertNull(bigDecimalParser.parse("."));
        assertNull(integerParser.parse("."));
        assertNull(labvalueParser.parse("."));
    }

    @Test
    public void shouldStillParseRealDecimalValues() throws InvalidDataFormatException {
        DoubleParser doubleParser = new DoubleParser();
        BigDecimalParser bigDecimalParser = new BigDecimalParser();
        IntegerParser integerParser = new IntegerParser();
        LabvalueParser labvalueParser = new LabvalueParser();

        assertEquals(12.5d, doubleParser.parse("12.5"));
        assertEquals(new BigDecimal("12.50"), bigDecimalParser.parse("12.5"));
        assertEquals(13, integerParser.parse("12.5"));
        assertEquals(new BigDecimal("12.50"), labvalueParser.parse("12.5"));
    }

    @Test
    public void shouldTreatDotAsEmptyForDateParser() throws InvalidDataFormatException {
        DateParser dateParser = new DateParser(ParserRule.DAY_FIRST);

        Temporal parsed = dateParser.parse(".");

        assertNull(parsed);
    }
}
