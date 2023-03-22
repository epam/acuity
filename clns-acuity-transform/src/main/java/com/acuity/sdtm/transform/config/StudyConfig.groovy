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

package com.acuity.sdtm.transform.config

import com.acuity.sdtm.transform.common.Study
import com.acuity.sdtm.transform.common.Version

/**
 * @author adavliatov.
 * @since 28.12.2016.
 */
class StudyConfig {
    Study study
    String source
    String destination
    String domain
    /**
     * The {@code module} allows SDTM runs over one study with different input and output directories at the same moment.
     * For instance, you can configure running SDTM over several inputs of STUDY001 study. In this case you need to add into
     * config file two studies with same {@code study}, {@code domain}, {@code version}, but different {@code source},
     * {@code destination} and {@code module}. Session for particalar processing stored with study+module key
     */
    String module = ""
    Version version


    @Override
    public String toString() {
        return "StudyConfig{" +
                "study=" + study +
                ", source='" + source + '\'' +
                ", destination='" + destination + '\'' +
                ", domain='" + domain + '\'' +
                ", module='" + module + '\'' +
                ", version=" + version +
                '}';
    }
}
