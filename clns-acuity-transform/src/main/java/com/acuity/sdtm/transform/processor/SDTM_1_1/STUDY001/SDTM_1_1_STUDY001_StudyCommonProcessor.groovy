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

package com.acuity.sdtm.transform.processor.SDTM_1_1.STUDY001


import com.acuity.sdtm.transform.common.Version
import com.acuity.sdtm.transform.processor.SDTM_1_1.SDTM_1_1_StudyCommonProcessor

trait SDTM_1_1_STUDY001_StudyCommonProcessor extends SDTM_1_1_StudyCommonProcessor {

    @Override
    Version getVersion() {
        return Version.SDTM_1_1_STUDY001
    }

}
