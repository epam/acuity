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

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import com.esotericsoftware.kryo.pool.KryoPool;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;

import static org.assertj.core.api.Assertions.assertThat;

public class DataProviderConfigurationTest {

    private final DataProviderConfiguration configuration = new DataProviderConfiguration();

    @Test
    public void shouldSerializeAtomicWrappersWithoutReflectiveAccess() {
        AtomicBoolean atomicBoolean = roundTrip(new AtomicBoolean(true), AtomicBoolean.class);
        AtomicInteger atomicInteger = roundTrip(new AtomicInteger(42), AtomicInteger.class);
        AtomicLong atomicLong = roundTrip(new AtomicLong(4242L), AtomicLong.class);
        AtomicReference<String> atomicReference = roundTrip(new AtomicReference<>("value"), AtomicReference.class);

        assertThat(atomicBoolean.get()).isTrue();
        assertThat(atomicInteger.get()).isEqualTo(42);
        assertThat(atomicLong.get()).isEqualTo(4242L);
        assertThat(atomicReference.get()).isEqualTo("value");
    }

    private <T> T roundTrip(T value, Class<T> type) {
        KryoPool pool = configuration.kryoPool();
        Kryo kryo = pool.borrow();
        try {
            byte[] bytes = write(kryo, value);
            return read(kryo, bytes, type);
        } finally {
            pool.release(kryo);
        }
    }

    private static <T> byte[] write(Kryo kryo, T value) {
        ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
        try (Output output = new Output(byteStream)) {
            kryo.writeObject(output, value);
        }
        return byteStream.toByteArray();
    }

    private static <T> T read(Kryo kryo, byte[] bytes, Class<T> type) {
        try (Input input = new Input(new ByteArrayInputStream(bytes))) {
            return kryo.readObject(input, type);
        }
    }
}
