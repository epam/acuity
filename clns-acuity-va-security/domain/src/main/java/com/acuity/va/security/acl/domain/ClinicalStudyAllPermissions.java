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

package com.acuity.va.security.acl.domain;

import com.acuity.va.security.acl.permissions.AcuityPermissions;
import lombok.experimental.Delegate;

public class ClinicalStudyAllPermissions extends ClinicalStudy {

    private final ClinicalStudy clinicalStudy;

    public ClinicalStudyAllPermissions(ClinicalStudy clinicalStudy) {
        this.clinicalStudy = clinicalStudy;
    }

    @Delegate(excludes = PermissionInfo.class)
    ClinicalStudy getClinicalStudy() {
        return clinicalStudy;
    }

    @Override
    public Integer getViewPermissionMask() {
        return AcuityPermissions.getAllPermissionsMask();
    }

    @Override
    public Integer getRolePermissionMask() {
        return AcuityPermissions.getAllPermissionsMask();
    }

    @Override
    public boolean hasRolePermission() {
        return true;
    }

    @Override
    public boolean getCanView() {
        return true;
    }

}
