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

package com.acuity.sdtm.transform.processor.behaviour

import com.mongodb.BasicDBObject
import com.mongodb.client.MongoCollection
import com.mongodb.client.model.Aggregates
import org.bson.Document
import org.bson.conversions.Bson

import static org.apache.commons.lang3.StringUtils.upperCase
import static org.apache.commons.lang3.StringUtils.upperCase

/**
 * Converts file and makes fields case insensitive
 *
 * @author adavliatov.
 * @since 01.09.2016.
 */
trait CaseInsensitivy {

    /**
     * Returns the transformation to convert documents fields to UPPERCASE format
     *
     * @param source documents collection
     * @return remapping transformation
     */
    Bson upper(MongoCollection<Document> source, Map<String, String> initial) {
        def init = [_id: 1, *: initial].withDefault { it }
        if (source == null || source.count() == 0) {
            return Aggregates.project(new BasicDBObject(init));
        }
        final Document first = source.find().first();
        final Map<String, String> remap = first
                .keySet()
                .inject(init) { map, s ->
            if (s ==~ /[a-zA-Z0-9]+/) {
                String upperField = upperCase(s);
                map[upperField] = '$' + s
            } else {
                map[s] = '$' + s
            }
            return map
        }
        return Aggregates.project(new BasicDBObject(remap));
    }

    /**
     * Returns mongo collection schema, maps fields to uppercase and provides default accessors
     *
     * @param source the schema owner
     * @return changed collection schema
     */
    Map<String, String> schema(MongoCollection<Document> source) {
        def init = [:].withDefault { it }
        if (source == null || source.count() == 0) {
            return init
        }
        final Document first = source.find().first();
        first
                .keySet()
                .inject(init) { map, field ->

            String upperField = upperCase(field)
            if (field ==~ /[a-zA-Z0-9]+/ && upperField != field) {
                map[upperField] = field
            }
            map
        } as Map<String, String>
    }

    /**
     * Maps document fields to fulfill provided schema
     *
     * @param source document to convert
     * @param schema required schema
     * @return document after transformation
     */
    Document remap(Document source, Map<String, String> schema) {
        if (source == null || source.size() == 0) {
            return source
        }
        schema.each { k, v -> source[k] = source[v] }
        return source
    }

}
