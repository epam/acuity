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

package com.acuity.visualisations.mapping.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@Setter
@EqualsAndHashCode(of = "code", callSuper = false)
@JsonIgnoreProperties(ignoreUnknown = true)
public class StudyRule extends MappingEntity implements Comparable<StudyRule> {

    public enum Status {
        incomplete, readyToMap, mapped, notInAcuity
    }

    private Long projectId;
    private String drugProgramme;

    @Getter(AccessLevel.NONE)
    @Setter(AccessLevel.NONE)
    private String code;
    @Getter(AccessLevel.NONE)
    @Setter(AccessLevel.NONE)
    private String name;
    private String clinicalStudyName;
    private String clinicalStudyId;
    private String studyComment;

    private boolean blinding;
    private boolean randomisation;
    private boolean regulatory;

    private Status status;
    private boolean enabled;

    private boolean studyCompleted;
    private boolean studyValid;
    private boolean studyEnabled;
    private boolean studyUseAltLabCodes;

    private String createdBy;
    private Date creationDate;
    private Date mappingModifiedDate;

    private String cronExpression;
    private boolean isScheduled;
    private boolean autoAssignedCountry;

    @Getter(AccessLevel.NONE)
    @Setter(AccessLevel.NONE)
    private boolean xAxisLimitedToVisit;

//    cBioPortal profiles represented as bit mask meaning selected profiles
//    0th Mutations
//    1st CNA data (linear)
//    2d CNA data (discrete)
//    3d Putative copy-number alterations from GISTIC
//    4th mRNA Expression (U133 microarray only)
//    5th mRNA Expression z-Scores (U133 microarray only)
//    6th mRNA Expression z-Scores (microarray)
//    7th mRNA Expression (RNA Seq V2 RSEM)
//    8th mRNA Expression z-Scores (RNA Seq V2 RSEM)
//    9th Methylation (HM27)
//    10th Methylation (HM450)
//    11th Protein Expression (RPPA)
//    12th Protein Expression Z-scores (RPPA)
//    13th Protein level Z-scores (mass spectrometry by CPTAC) (RPPA)
    private int profilesMask;
    private String cbioPortalStudyCode;

    private boolean amlEnabled;

    private StudySubjectGrouping studySubjectGrouping;
    private String primarySource;
    @Getter(onMethod = @__(@JsonIgnore))
    private List<FileRule> fileRules = new ArrayList<>();
    @Getter(onMethod = @__(@JsonIgnore))
    private List<ExcludingValue> excludingValues;
    private List<GroupRuleBase> projectGroupRules = new ArrayList<>();
    private boolean useCustomDrugsForBaseline;

    public String getStudyName() {
        return name;
    }

    public void setStudyName(String name) {
        this.name = name;
    }

    public String getStudyCode() {
        return code;
    }

    public void setStudyCode(String code) {
        this.code = code;
    }

    public boolean isxAxisLimitedToVisit() {
        return xAxisLimitedToVisit;
    }

    public void setxAxisLimitedToVisit(boolean xAxisLimitedToVisit) {
        this.xAxisLimitedToVisit = xAxisLimitedToVisit;
    }

    public FileRule getFileRule(Long fileRuleId) {
        for (FileRule rule : fileRules) {
            if (rule.getId().equals(fileRuleId)) {
                return rule;
            }
        }
        throw new IllegalArgumentException();
    }

    public List<AEGroupRule> getAeGroups() {
        return getProjectGroupRules().stream()
                .filter(e -> GroupRuleBase.ProjectGroupType.ae.equals(e.getType()))
                .map(AEGroupRule.class::cast)
                .collect(Collectors.toList());
    }

    public List<LabGroupRule> getLabGroups() {
        return getProjectGroupRules().stream()
                .filter(e -> GroupRuleBase.ProjectGroupType.lab.equals(e.getType()))
                .map(LabGroupRule.class::cast)
                .collect(Collectors.toList());
    }

    @Override
    public int compareTo(StudyRule other) {
        return code.compareTo(other.getStudyCode());
    }

}
