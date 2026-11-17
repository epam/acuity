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

package com.acuity.visualisations.rawdatamodel.dao;

import com.acuity.visualisations.common.lookup.AcuityRepository;
import com.acuity.visualisations.rawdatamodel.vo.ServiceLink;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * Read-only access to the home page content saved by the admin application:
 * service blocks (map_service_link) and the feedback email (map_feedback).
 */
@AcuityRepository
public interface SiteContentRepository {

    /** Service blocks in display order: the first row is 'Omics', the second Patient, the third AI and ML. */
    @Select("SELECT msl_id AS id, msl_title AS title, msl_url AS url, msl_sort_order AS sortOrder"
            + " FROM map_service_link"
            + " ORDER BY msl_sort_order, msl_id")
    List<ServiceLink> selectServiceLinks();

    /** Feedback email, or null if the table is empty. */
    @Select("SELECT mfb_email FROM map_feedback ORDER BY mfb_id LIMIT 1")
    String selectFeedbackEmail();
}
