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

package com.acuity.sdtm.transform.processor.SDTM_base


import com.acuity.sdtm.transform.io.writer.Writer
import com.acuity.sdtm.transform.common.EntityType
import com.acuity.sdtm.transform.common.Version
import com.mongodb.BasicDBObject
import com.mongodb.client.MongoCollection
import com.mongodb.client.MongoDatabase
import com.mongodb.client.model.Aggregates
import org.bson.Document
import org.bson.conversions.Bson

import static com.acuity.sdtm.transform.util.Util.parseCentre
import static com.acuity.sdtm.transform.util.Util.parseSubject
import static org.apache.commons.lang3.StringUtils.upperCase

/**
 * @author adavliatov.
 * @since 26.08.2016.
 */
trait BaseStudyCommonProcessor {

    /**
     * Map method, just maps input row from the mongo collection to the output row for csv
     * replacement for the stupid getMap
     *
     * @param row input row
     * @return output row
     */
    abstract Map<String, String> map(Document row)

    abstract EntityType getEntityType()

    abstract Version getVersion()

    abstract void process(MongoDatabase mongo, Writer writer) throws IOException;

    /**
     * Returns the transformation to convert documents fields to UPPERCASE format
     *
     * @param source documents collection
     * @return remapping transformation
     */
    Bson upper(MongoCollection<Document> source, Map<String, Object> initial) {
        def init = [_id: 1, *: initial].withDefault { it }
        if (source == null || source.count() == 0) {
            return Aggregates.project(new BasicDBObject(init))
        }
        final Document first = source.find().first()
        final Map<String, Object> remap = first
                .keySet()
                .inject(init) { map, s ->
            switch (s) {
                case String:
                    if (s ==~ /[a-zA-Z0-9]+/) {
                        String upperField = upperCase(s);
                        map[upperField] = '$' + s
                    } else {
                        map[s] = '$' + s
                    }
                    break
//                TODO: add specific cases
//                case BasicDBObject:
//                    break
            }
            return map
        }
        return Aggregates.project(new BasicDBObject(remap))
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

    String getSubject(String val) {
        return parseSubject(val)
    }

    String getCenter(String val) {
        return parseCentre(val)
    }

}
