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

package com.acuity.sdtm.transform.processor.SDTM_1_1


import com.acuity.sdtm.transform.processor.SDTM_base.BaseLvefProcessor
import com.acuity.sdtm.transform.common.Version
import org.bson.Document
import org.springframework.stereotype.Component

import static com.acuity.sdtm.transform.common.Study.*

/**
 * Created by knml167 on 08/06/2015.
 */
@Component
public class SDTM_1_1_LvefProcessor extends BaseLvefProcessor implements SDTM_1_1_StudyCommonProcessor {

    @Override
    boolean isStudyProducesThisTypeOfEntity() {
        return !studyIn(DR15, DR16, DR12, DR13, DR10, DR6, DR11, DR19,
                DR21, DR22, STUDY001, STUDY002, STUDY003, STUDY004)
    }

    @Override
    public Version getVersion() {
        Version.SDTM_1_1
    }

    @Override
    protected fillDate(Document row) {
        row.efdat = row.EGDTC

    }

    @Override
    protected fillLvef(Document row) {
        row.lvef = row.EGSTRESN
    }

    @Override
    protected fillMethod(Document row) {
        row.lvefmth = row.EGMETHOD
    }

}
