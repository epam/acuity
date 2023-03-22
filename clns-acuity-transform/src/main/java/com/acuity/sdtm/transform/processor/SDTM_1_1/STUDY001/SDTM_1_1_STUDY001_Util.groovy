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


import com.acuity.sdtm.transform.util.Util
import com.acuity.sdtm.transform.util.date.UnknownDate

import java.text.SimpleDateFormat

class SDTM_1_1_STUDY001_Util {

    static String getCxCancer(String mhStartDate, String mhEndDate, String exstdat, String dsstdat) {
        String exStartDate = new UnknownDate(exstdat).convertToTargetFormat(Util.MM_DD_YYYY_SLASHED)
        //there is MMM_yy date format so day is set to 01 by default parse method of SimpleDateFormat
        String dsDate = new UnknownDate(dsstdat).convertToTargetFormat(Util.MM_DD_YYYY_SLASHED)

        SimpleDateFormat format = new SimpleDateFormat(Util.MM_DD_YYYY_SLASHED)
        if (mhEndDate && exStartDate && format.parse(mhEndDate).compareTo(format.parse(exStartDate)) < 0) {
            return "Previous"
        } else if (mhStartDate && dsDate && format.parse(mhStartDate).compareTo(format.parse(dsDate)) > 0) {
            return "Post"
        }
        return ""
    }
}
