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

package com.acuity.visualisations.data.provider.samba;

import org.assertj.core.api.SoftAssertions;
import org.assertj.core.api.junit.jupiter.InjectSoftAssertions;
import org.assertj.core.api.junit.jupiter.SoftAssertionsExtension;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith({SpringExtension.class, SoftAssertionsExtension.class})
public class SambaDataProviderTest {

    @InjectMocks
    private SambaDataProvider sambaDataProvider;

    @InjectSoftAssertions
    private SoftAssertions softly;

    @Test
    public void testMatch() {

        softly.assertThat(sambaDataProvider.match("smb://a/b/c")).isTrue();
        softly.assertThat(sambaDataProvider.match("\\\\a\\b\\c")).isTrue();
        softly.assertThat(sambaDataProvider.match("//a/b/c")).isTrue();
        softly.assertThat(sambaDataProvider.match("a/b/c")).isFalse();
        softly.assertThat(sambaDataProvider.match("other://a/b/c")).isFalse();

        try {
            sambaDataProvider.match(null);
            softly.fail("NPE expected");
        } catch(NullPointerException e) {
            softly.assertThat(e.getMessage().equals("location"));
        }
    }
}
