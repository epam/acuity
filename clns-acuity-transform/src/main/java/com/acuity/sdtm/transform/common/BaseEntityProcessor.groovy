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

package com.acuity.sdtm.transform.common

import com.acuity.sdtm.transform.processor.SDTM_base.BaseStudyCommonProcessor
import com.acuity.sdtm.transform.processor.behaviour.Joinable
import com.mongodb.BasicDBObject
import org.bson.conversions.Bson

/**
 * @author adavliatov.
 * @since 19.08.2016.
 */
abstract class BaseEntityProcessor implements BaseStudyCommonProcessor, Joinable {
    Study study
    String sessionId
    String module

    /**
     * Return domain filter for this type of Entity, or null
     *
     * @return
     */
    Bson getDomainFilter() {
        new BasicDBObject()
    }

    /**
     * Check if the current study is in the given list
     *
     * @param studies
     * @return
     */
    boolean studyIn(Study... studies) {
        return study in studies
    }

    /**
     * Return true if this Entity type is supported by the Study
     *
     * @return
     */
    boolean isStudyProducesThisTypeOfEntity() {
        return true
    }

}

