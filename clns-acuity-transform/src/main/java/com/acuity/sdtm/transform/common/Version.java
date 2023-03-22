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
 * Version of the study.
 * Give the ability to choose concrete implementation.
 */
public enum Version {
    /**
     * Sdtm protocol compatible version.
     */
    SDTM_1_1 {
        @Override
        public Version getParent() {
            return null;
        }
    },
    /**
     * Drug Version v1 drug version.
     */
    DrugVersion_v1 {
        @Override
        public Version getParent() {
            return SDTM_1_1;
        }
    },
    /**
     * STUDY001 drug version
     */
    SDTM_1_1_STUDY001 {
        @Override
        public Version getParent() {
            return SDTM_1_1;
        }
    },
    /**
     * STUDY002 drug version
     */
    SDTM_1_1_STUDY002 {
        @Override
        public Version getParent() {
            return SDTM_1_1;
        }
    };

    /**
     * Parent version.
     * In case of the missing implementation chooses parent's.
     *
     * @return current version parent
     */
    public abstract Version getParent();
}
