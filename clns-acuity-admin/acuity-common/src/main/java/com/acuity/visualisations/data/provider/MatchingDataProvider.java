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

package com.acuity.visualisations.data.provider;

import com.acuity.visualisations.data.Data;
import java.util.List;
import lombok.NonNull;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

@Primary
@Service
public class MatchingDataProvider implements DataProvider {

    @Autowired
    private List<DataProvider> providers;

    @Override
    public boolean match(@NonNull String location) {
        return true;
    }

    @Override
    @SneakyThrows
    public Data get(@NonNull String location) {
        return providers.stream()
                .filter(p -> p.match(location))
                .findFirst()
                .map(p -> p.get(location))
                .orElseThrow(UnsupportedOperationException::new);
    }
}
