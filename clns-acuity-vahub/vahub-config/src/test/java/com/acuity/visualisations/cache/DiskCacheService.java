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

package com.acuity.visualisations.cache;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

/**
 * Compile-only stub. The real DiskCacheService was removed when EhCache was replaced
 * by Caffeine (Caffeine has no disk persistence). WhenRunningDiskCacheServicesITCase
 * tests that deleted feature and needs rewriting; this stub keeps it compile-clean.
 */
public class DiskCacheService {

    public static final String LOCKFILE_NAME = ".lock";

    public enum CACHETYPE { ALL, ACUITY, DETECT }

    public String getDiskLocation() { return ""; }

    public List<Path> listDiskCaches(CACHETYPE type) throws IOException { return List.of(); }

    public List<String> deleteDiskCaches(CACHETYPE type) throws IOException { return List.of(); }
}
