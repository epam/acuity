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

package com.acuity.visualisations.rawdatamodel.dataproviders.config;

import com.acuity.visualisations.rawdatamodel.dataproviders.common.kryo.KryoContext;
import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.pool.KryoPool;
import de.javakaffee.kryoserializers.CollectionsEmptyListSerializer;
import de.javakaffee.kryoserializers.CollectionsEmptyMapSerializer;
import de.javakaffee.kryoserializers.CollectionsEmptySetSerializer;
import de.javakaffee.kryoserializers.CollectionsSingletonListSerializer;
import de.javakaffee.kryoserializers.CollectionsSingletonMapSerializer;
import de.javakaffee.kryoserializers.CollectionsSingletonSetSerializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Collections;

@Configuration
public class DataProviderConfiguration {

    private Kryo factory() {
        Kryo kryo = new Kryo();
        kryo.setReferences(true);
        kryo.setRegistrationRequired(false);
        registerCustomSerializers(kryo);
        return kryo;
    }

    // NOTE: ArraysAsListSerializer / UnmodifiableCollectionsSerializer / SynchronizedCollectionsSerializer
    // were intentionally removed. They relied on reflective access to private JDK fields
    // (e.g. java.util.Arrays$ArrayList.a / Collections$Unmodifiable*.c / Collections$Synchronized*.mutex),
    // which is blocked on Java 17+/21 by the module system ("module java.base does not opens java.util")
    // - see https://github.com/magro/kryo-serializers/issues/131. They are no longer needed because
    // DataProvider.loadToFile() now normalizes every collection to a plain ArrayList before handing it
    // to Kryo, so these JDK-internal wrapper types never reach the (de)serialization path.
    // The Collections.empty*()/singleton*() serializers below are kept: they do NOT use reflection
    // (verified via javap - they just call the public Collections.emptyList()/singletonList(Object)
    // factory methods), so they remain safe on all JDK versions.
    private void registerCustomSerializers(Kryo kryo) {
        kryo.register(Collections.emptyList().getClass(), new CollectionsEmptyListSerializer());
        kryo.register(Collections.emptyMap().getClass(), new CollectionsEmptyMapSerializer());
        kryo.register(Collections.emptySet().getClass(), new CollectionsEmptySetSerializer());
        kryo.register(Collections.singletonList("").getClass(), new CollectionsSingletonListSerializer());
        kryo.register(Collections.singleton("").getClass(), new CollectionsSingletonSetSerializer());
        kryo.register(Collections.singletonMap("", "").getClass(), new CollectionsSingletonMapSerializer());
    }

    // Build pool with SoftReferences enabled (optional)
    @Bean
    public KryoPool kryoPool() {
        return new KryoPool.Builder(this::factory).softReferences().build();
    }

    @Bean
    public KryoContext kryoContext() {
        return new KryoContext(kryoPool());
    }
}
