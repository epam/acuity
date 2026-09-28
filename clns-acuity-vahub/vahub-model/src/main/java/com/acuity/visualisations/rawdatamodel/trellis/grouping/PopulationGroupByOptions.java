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

package com.acuity.visualisations.rawdatamodel.trellis.grouping;

import com.acuity.visualisations.rawdatamodel.trellis.grouping.annotations.AcceptsAttributeContext;
import com.acuity.visualisations.rawdatamodel.trellis.grouping.annotations.BinableOption;
import com.acuity.visualisations.rawdatamodel.trellis.grouping.annotations.HasDrugOption;
import com.acuity.visualisations.rawdatamodel.trellis.grouping.annotations.RangeOption;
import com.acuity.visualisations.rawdatamodel.trellis.grouping.annotations.TimestampOption;
import com.acuity.visualisations.rawdatamodel.vo.EntityAttribute;
import com.acuity.visualisations.rawdatamodel.vo.GroupByOption;
import com.acuity.visualisations.rawdatamodel.vo.Subject;
import com.acuity.visualisations.rawdatamodel.vo.plots.BarChartOptionRange;
import org.apache.commons.collections.MapUtils;

import java.util.Map;

import static com.acuity.visualisations.rawdatamodel.util.Attributes.getBinnedAttribute;

public enum PopulationGroupByOptions implements GroupByOption<Subject> {

    NONE(null) {
        @Override
        public EntityAttribute<Subject> getAttribute() {
            return EntityAttribute.attribute("ALL", e -> "All");
        }
    },
    STUDY_CODE(Subject.Attributes.STUDY_CODE),
    STUDY_NAME(Subject.Attributes.STUDY_NAME),
    STUDY_PART_ID(Subject.Attributes.STUDY_PART),
    PLANNED_TREATMENT_ARM(Subject.Attributes.PLANNED_ARM),
    ACTUAL_TREATMENT_ARM(Subject.Attributes.ACTUAL_ARM),
    CENTER_NUMBER(Subject.Attributes.SITE_ID),
    SITE_ID(Subject.Attributes.SITE_ID),
    @AcceptsAttributeContext(required = false)
    @RangeOption(RangeOption.RangeOptionType.DATE)
    RANDOMISATION_DATE(Subject.Attributes.DATE_OF_RANDOMISATION) {
        @Override
        public EntityAttribute<Subject> getAttribute(Params params) {
            return getSubjectRangedAttribute(getAttribute(), "DATE_OF_RANDOMISATION", params);
        }
    },
    @AcceptsAttributeContext(required = false)
    @RangeOption(RangeOption.RangeOptionType.DATE)
    FIRST_TREATMENT_DATE(Subject.Attributes.FIRST_TREATMENT_DATE) {
        @Override
        public EntityAttribute<Subject> getAttribute(Params params) {
            return getSubjectRangedAttribute(getAttribute(), "FIRST_TREATMENT_DATE", params);
        }
    },
    @AcceptsAttributeContext(required = false)
    @RangeOption(RangeOption.RangeOptionType.DATE)
    LAST_TREATMENT_DATE(Subject.Attributes.LAST_TREATMENT_DATE) {
        @Override
        public EntityAttribute<Subject> getAttribute(Params params) {
            return getSubjectRangedAttribute(getAttribute(), "LAST_TREATMENT_DATE", params);
        }
    },
    DEATH(Subject.Attributes.DEATH_FLAG),
    @AcceptsAttributeContext(required = false)
    @RangeOption(RangeOption.RangeOptionType.DATE)
    DATE_OF_DEATH(Subject.Attributes.DATE_OF_DEATH) {
        @Override
        public EntityAttribute<Subject> getAttribute(Params params) {
            return getSubjectRangedAttribute(getAttribute(), "DATE_OF_DEATH", params);
        }
    },
    COUNTRY(Subject.Attributes.COUNTRY),
    COUNTRY_AND_REGION(Subject.Attributes.REGION),
    SEX(Subject.Attributes.SEX),
    RACE(Subject.Attributes.RACE),
    @AcceptsAttributeContext(required = false)
    @RangeOption(RangeOption.RangeOptionType.LONG)
    AGE(Subject.Attributes.AGE) {
        @Override
        public EntityAttribute<Subject> getAttribute(Params params) {
            return getSubjectRangedAttribute(getAttribute(), "AGE", params);
        }
    },
    @AcceptsAttributeContext(required = false)
    @RangeOption(RangeOption.RangeOptionType.LONG)
    DURATION_ON_STUDY(Subject.Attributes.DURATION_ON_STUDY) {
        @Override
        public EntityAttribute<Subject> getAttribute(Params params) {
            return getSubjectRangedAttribute(getAttribute(), "DURATION_ON_STUDY", params);
        }
    },
    WITHDRAWAL(Subject.Attributes.WITHDRAWAL),
    REASON_FOR_WITHDRAWAL(Subject.Attributes.REASON_FOR_WITHDRAWAL),
    CENTRE(Subject.Attributes.CENTER_NUMBER),
    ETHNIC_GROUP(Subject.Attributes.ETHNIC_GROUP),
    SPECIFIED_ETHNIC_GROUP(Subject.Attributes.SPEC_ETHNIC_GROUP),
    @BinableOption
    @TimestampOption
    ON_STUDY(null) {
        @Override
        public EntityAttribute<Subject> getAttribute() {
            throw new IllegalStateException("Cannot use ON_STUDY without params");
        }

        @Override
        public EntityAttribute<Subject> getAttribute(Params params) {
            if (params == null) {
                throw new IllegalStateException("Cannot use ON_STUDY without params");
            }
            final Object axisStart = params.get(Param.AXIS_START);
            return axisStart instanceof EmptyBin
                    ? EntityAttribute.attribute("ON_STUDY", (Subject s) -> Bin.empty())
                    : getBinnedAttribute("ON_STUDY", params, o -> axisStart, Subject::getStudyLeaveDate);
        }
    },
    DOSE_COHORT(Subject.Attributes.DOSE_COHORT),
    OTHER_COHORT(Subject.Attributes.OTHER_COHORT),
    MAX_DOSE_PER_ADMIN_OF_DRUG(Subject.Attributes.MAX_DOSE_MAP) {
        @Override
        public EntityAttribute<Subject> getAttribute() {
            throw new IllegalStateException("Cannot use MAX_DOSE_PER_ADMIN_OF_DRUG without passing the drug name");
        }

        @Override
        public EntityAttribute<Subject> getAttribute(Params params) {
            if (params == null) {
                return getAttribute();
            }
            final Object drugName = params.get(Param.DRUG_NAME);

            return drugName == null ? getAttribute()
                    : EntityAttribute.attribute("MAX_DOSE_PER_ADMIN_OF_DRUG", (Subject s) -> s.getDrugsMaxDoses().get(drugName.toString()));
        }
    },
    @AcceptsAttributeContext(required = false)
    @RangeOption(RangeOption.RangeOptionType.DOUBLE)
    WEIGHT(Subject.Attributes.WEIGHT) {
        @Override
        public EntityAttribute<Subject> getAttribute(Params params) {
            return getSubjectRangedAttribute(getAttribute(), "WEIGHT", params);
        }
    },
    @AcceptsAttributeContext(required = false)
    @RangeOption(RangeOption.RangeOptionType.DOUBLE)
    HEIGHT(Subject.Attributes.HEIGHT) {
        @Override
        public EntityAttribute<Subject> getAttribute(Params params) {
            return getSubjectRangedAttribute(getAttribute(), "HEIGHT", params);
        }
    },
    @HasDrugOption
    DISCONTINUATION(Subject.Attributes.DISC_MAP) {
        @Override
        public EntityAttribute<Subject> getAttribute(Params params) {
            String drugName = params.getStr(Param.DRUG_NAME);
            if (drugName != null) {
                return EntityAttribute.attribute("DISCONTINUATION",
                        s -> s.getDrugsDiscontinued().get(drugName));
            } else {
                return getAttribute();
            }
        }
    },
    @HasDrugOption
    MAIN_REASON_FOR_DISCONTINUATION(Subject.Attributes.DISC_REASONS_MAP) {
        @Override
        public EntityAttribute<Subject> getAttribute(Params params) {
            String drugName = params.getStr(Param.DRUG_NAME);
            if (drugName != null) {
                return EntityAttribute.attribute("MAIN_REASON_FOR_DISCONTINUATION",
                        s -> s.getDrugDiscontinuationMainReason().get(drugName));
            } else {
                return getAttribute();
            }
        }
    };

    private Subject.Attributes attribute;

    PopulationGroupByOptions(Subject.Attributes attribute) {
        this.attribute = attribute;
    }

    @Override
    public EntityAttribute<Subject> getAttribute() {
        return attribute.getAttribute();
    }

    private static EntityAttribute<Subject> getSubjectRangedAttribute(EntityAttribute<Subject> attribute, String name, Params params) {
        final Map<String, BarChartOptionRange<?>> context = params == null ? null
                : (Map<String, BarChartOptionRange<?>>) params.get(Param.CONTEXT);
        return MapUtils.isEmpty(context) ? attribute
                : EntityAttribute.attribute(name, (Subject subj) -> context.get(subj.getSubjectId()));
    }

}


