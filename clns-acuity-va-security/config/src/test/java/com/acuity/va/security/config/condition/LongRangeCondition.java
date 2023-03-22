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

package com.acuity.va.security.config.condition;

import org.assertj.core.api.Condition;

/**
 *
 * @author Glen
 */
public class LongRangeCondition extends Condition<Long> {

    protected Long from;
    protected Long to;

    public LongRangeCondition(Long from, Long to) {
        this.from = from;
        this.to = to;
    }

    @Override
    public boolean matches(Long value) {
        return (from < value && value < to);
    }
}
