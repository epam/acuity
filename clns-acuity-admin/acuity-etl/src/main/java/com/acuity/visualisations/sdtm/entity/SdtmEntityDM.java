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

package com.acuity.visualisations.sdtm.entity;


import com.acuity.visualisations.batch.reader.tablereader.TableRow;

/**
 * Demography
 */
public class SdtmEntityDM extends SdtmEntity {

    private String sex;
    private String race;
    private String dmdtc;
    private String brthdtc;

    public String getSex() {
        return sex;
    }

    public String getRace() {
        return race;
    }

    public String getDmdtc() {
        return dmdtc;
    }

    public String getBrthdtc() {
        return brthdtc;
    }

    public void read(TableRow row) {
        sex = (String) row.getValue("SEX");
        race = (String) row.getValue("RACE");
        dmdtc = (String) row.getValue("DMDTC");
        brthdtc = (String) row.getValue("BRTHDTC");
    }
}
