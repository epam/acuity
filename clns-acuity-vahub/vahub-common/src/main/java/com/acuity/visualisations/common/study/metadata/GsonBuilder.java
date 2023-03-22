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

package com.acuity.visualisations.common.study.metadata;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;

/**
 *
 * @author ksnd199
 */
public final class GsonBuilder {

    private GsonBuilder() {
    }
    
    public static final Gson GSON = new com.google.gson.GsonBuilder().
        setPrettyPrinting().
        serializeNulls().
        setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES).
        create();
}
