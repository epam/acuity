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

package com.acuity.sdtm.transform.common;

/**
 * SDTM study identifier.
 */
public enum Study {
    /**
     * DR1 study.
     */
    DR1,
    /**
     * DR2 study.
     */
    DR2,
    /**
     * DR3 study.
     */
    DR3,
    /**
     * DR4 study.
     */
    DR4,
    /**
     * DR5 study.
     */
    DR5,
    /**
     * DR6 study.
     */
    DR6,
    /**
     * DR7 study.
     */
    DR7,
    /**
     * DR8 study.
     */
    DR8,
    /**
     * DR9 study.
     */
    DR9,
    /**
     * DR10 study.
     */
    DR10,
    /**
     * DR11 study.
     */
    DR11,
    /**
     * DR12 study.
     */
    DR12,
    /**
     * DR13 study.
     */
    DR13,
    /**
     * DR14 study.
     */
    DR14,
    /**
     * DR15 study.
     */
    DR15,
    /**
     * DR16 study.
     */
    DR16,
    /**
     * DR17 study.
     */
    DR17,
    /**
     * DR18 study.
     */
    DR18,
    /**
     * DR19 study.
     */
    DR19,
    /**
     * DR20 study.
     */
    DR20,
    /**
     * DR21 study.
     */
    DR21,
    /**
     * DR22 study.
     */
    DR22,
    /**
     * DR23 study.
     */
    DR23,
    /**
     * DR24 study.
     */
    DR24,
    /**
     * DR25 study.
     */
    DR25,
    /**
     * STUDY001 study
     */
    STUDY001,
    /**
     * STUDY002 study
     */
    STUDY002,
    /**
     * STUDY003 study
     */
    STUDY003,
    /**
     * STUDY004 study
     */
    STUDY004;
    /**
     * Check if study is from the given list.
     *
     * @param study study name to find
     * @param studies the list where study should be found
     * @return true if studies names contains study
     */
    public boolean isStudyIn(final String study, final Study... studies) {
        for (Study s : studies) {
            if (study.equals(s.name())) {
                return true;
            }
        }
        return false;
    }
}
