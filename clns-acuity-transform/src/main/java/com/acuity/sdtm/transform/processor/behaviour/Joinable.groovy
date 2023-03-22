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

import com.mongodb.client.MongoCollection
import com.mongodb.client.model.Filters
import org.bson.Document
import org.bson.conversions.Bson

import static com.mongodb.client.model.Filters.*

/**
 * Adds the ability to extracts values from supplementary files
 *
 * @author adavliatov.
 * @since 01.09.2016.
 */
trait Joinable {

    /**
     * Filter to join supplemental collection.
     *
     * @param mainRecord - record from the main collection, i.e. CM record
     * @param outKey - key for the output record, i.e.: "CMGROUP"
     * @param seqKey - name of the sequence column, i.e. "CMSEQ"
     * @param suppCollection - supp collection, i.e. SUPPCM
     * @return value first found value after merging
     */
    Bson getSuppJoinFilter(Document mainRecord, MongoCollection suppCollection, String outKey) {
        String usubjid = mainRecord.USUBJID ?: mainRecord.usubjid
        return and(
                or(eq("USUBJID", usubjid), eq("usubjid", usubjid)),
                or(eq("QNAM", outKey), eq("qnam", outKey)),
        )
    }

    /**
     * Filter to join supplemental collection.
     *
     * @param mainRecord - record from the main collection, i.e. CM record
     * @param outKey - key for the output record, i.e.: "CMGROUP"
     * @param seqKey - name of the sequence column, i.e. "CMSEQ"
     * @param suppCollection - supp collection, i.e. SUPPCM
     * @return value first found value after merging
     */
    Bson getSuppJoinFilter(Document mainRecord, MongoCollection suppCollection, String outKey, String seqKey) {
        String usubjid = mainRecord.USUBJID ?: mainRecord.usubjid
        String idvarval = String.valueOf(mainRecord[seqKey.toUpperCase()] ?: (mainRecord[seqKey.toLowerCase()] ?: ''))
        return and(
                or(eq("USUBJID", usubjid), eq("usubjid", usubjid)),
                or(eq("QNAM", outKey), eq("qnam", outKey)),
                or(eq("IDVARVAL", idvarval), eq("idvarval", idvarval)))
    }

    /**
     * Returns first value from the supplemental collection.
     * For example, we need a CMGROUP value from the SUPPCM collection for the given record from the CM collection.
     * It is possible to "join" these main and supp collections using this clause:
     * CM.USUBJID = SUPPCM.USUBJID and CM.CMSEQ = SUPPCM.IDVARVAL
     *
     * @param mainRecord - record from the main collection, i.e. CM record
     * @param outKey - key for the output record, i.e.: "CMGROUP"
     * @param seqKey - name of the sequence column, i.e. "CMSEQ"
     * @param suppCollection - supp collection, i.e. SUPPCM
     * @return value first found value after merging
     */
    String getSuppFirstQval(Document mainRecord, MongoCollection suppCollection, String outKey, String seqKey) {
        return getSuppFirstQval(mainRecord, suppCollection, outKey, seqKey, null)
    }

    /**
     * Returns first value from the supplemental collection.
     * For example, we need a CMGROUP value from the SUPPCM collection for the given record from the CM collection.
     * It is possible to "join" these main and supp collections using this clause:
     * CM.USUBJID = SUPPCM.USUBJID and CM.CMSEQ = SUPPCM.IDVARVAL
     *
     * @param mainRecord - record from the main collection, i.e. CM record
     * @param outKey - key for the output record, i.e.: "CMGROUP"
     * @param seqKey - name of the sequence column, i.e. "CMSEQ"
     * @param suppCollection - supp collection, i.e. SUPPCM
     * @return value first found value after merging
     */
    String getSuppFirstQval(Document mainRecord, MongoCollection suppCollection, String outKey, String seqKey, Bson joinFilter) {
        joinFilter = joinFilter ?: getSuppJoinFilter(mainRecord, suppCollection, outKey, seqKey)
        Document first = (Document) suppCollection.find(joinFilter).first()
        return first?.QVAL ?: first?.qval
    }

    /**
     * Do the same as getSuppFirstQval(Document mainRecord, MongoCollection suppCollection, String outKey, String seqKey)
     * function, but select first available record for the given outKeys list.
     * Returns the first matching record
     *
     * @param mainRecord
     * @param suppCollection
     * @param outKeys
     * @param seqKey
     * @return
     */
    String getSuppFirstQval(Document mainRecord, MongoCollection suppCollection, String[] outKeys, String seqKey) {
        Bson suppFilter = and(eq("USUBJID", mainRecord.getString("USUBJID")),
                Filters.in("QNAM", outKeys), eq("IDVARVAL", "" + mainRecord.get(seqKey)))

        Document first = (Document) suppCollection.find(suppFilter).first()
        return first?.QVAL ?: first?.qval
    }

    /**
     * Demography supp file doesn't use SEQ (DMSEQ) column, the relation between DM and SUPPDM (by QNAM) is 1:1,
     *
     * @param mainRecord
     * @param suppCollection
     * @param outKey
     * @return
     */
    String getSuppFirstQval(Document mainRecord, MongoCollection suppCollection, String outKey) {
        Document first = (Document) suppCollection.find(getSuppJoinFilter(mainRecord, suppCollection, outKey)).first()
        return first?.QVAL ?: first?.qval
    }

}
