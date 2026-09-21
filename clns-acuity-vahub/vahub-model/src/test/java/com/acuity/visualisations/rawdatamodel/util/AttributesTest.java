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

package com.acuity.visualisations.rawdatamodel.util;

import com.acuity.visualisations.rawdatamodel.trellis.grouping.ChartGroupByOptions;
import com.googlecode.cqengine.attribute.SimpleNullableAttribute;
import com.googlecode.cqengine.query.option.QueryOptions;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.junit.jupiter.api.Test;

import java.util.Date;

import static org.assertj.core.api.Assertions.assertThat;

public class AttributesTest {

    @Test
    public void testGetString() {
        SomeEntity e = getEntity();
        SimpleNullableAttribute<SomeEntity, String> stringAttr =
                new SimpleNullableAttribute<SomeEntity, String>(SomeEntity.class, String.class, "stringField") {
                    @Override public String getValue(SomeEntity o, QueryOptions q) { return o.getStringField(); }
                };
        assertThat(Attributes.getString(stringAttr, e)).isEqualTo(e.getStringField());
    }

    @Test
    public void testGetInt() {
        SomeEntity e = getEntity();
        SimpleNullableAttribute<SomeEntity, Integer> intAttr =
                new SimpleNullableAttribute<SomeEntity, Integer>(SomeEntity.class, Integer.class, "intField") {
                    @Override public Integer getValue(SomeEntity o, QueryOptions q) { return o.getIntField(); }
                };
        assertThat(Attributes.getInt(intAttr, e)).isEqualTo(e.getIntField());
    }

    @Test
    public void testGetDouble() {
        SomeEntity e = getEntity();
        SimpleNullableAttribute<SomeEntity, Double> doubleAttr =
                new SimpleNullableAttribute<SomeEntity, Double>(SomeEntity.class, Double.class, "doubleField") {
                    @Override public Double getValue(SomeEntity o, QueryOptions q) { return o.getDoubleField(); }
                };
        assertThat(Attributes.getDouble(doubleAttr, e)).isEqualTo(e.getDoubleField());
    }

    @Test
    public void testGetDate() {
        SomeEntity e = getEntity();
        SimpleNullableAttribute<SomeEntity, Date> dateAttr =
                new SimpleNullableAttribute<SomeEntity, Date>(SomeEntity.class, Date.class, "dateField") {
                    @Override public Date getValue(SomeEntity o, QueryOptions q) { return o.getDateField(); }
                };
        assertThat(Attributes.getDate(dateAttr, e)).isEqualTo(e.getDateField());
    }

    @Test
    public void testGetEmptyValueWhenGroupByOptionIsNull() {
        ChartGroupByOptions.GroupByOptionAndParams groupByOptionAndParams = new ChartGroupByOptions.GroupByOptionAndParams();
        assertThat((String) Attributes.get(groupByOptionAndParams, getEntity())).isEqualTo(Attributes.DEFAULT_EMPTY_VALUE);
    }

    @AllArgsConstructor
    @Data
    private static class SomeEntity {
        private String stringField;
        private int intField;
        private Date dateField;
        private double doubleField;
    }

    private SomeEntity getEntity() {
        return new SomeEntity("str", 2, new Date(5 * 24 * 60 * 60 * 1000), 3.14);
    }

    private SomeEntity getAnotherEntity() {
        return new SomeEntity("str2", 3, new Date(5 * 24 * 60 * 60 * 1000), 5.14);
    }
}
