ALTER TABLE ONLY acl_class
    ADD CONSTRAINT acl_class_class_key UNIQUE (class);

ALTER TABLE ONLY acl_class
    ADD CONSTRAINT acl_class_pkey PRIMARY KEY (id);

ALTER TABLE ONLY acl_entry
    ADD CONSTRAINT acl_entry_acl_object_identity_ace_order_key UNIQUE (acl_object_identity, ace_order);

ALTER TABLE ONLY acl_entry
    ADD CONSTRAINT acl_entry_pkey PRIMARY KEY (id);

ALTER TABLE ONLY acl_object_identity
    ADD CONSTRAINT acl_object_identity_object_id_class_object_id_identity_key UNIQUE (object_id_class, object_id_identity);

ALTER TABLE ONLY acl_object_identity
    ADD CONSTRAINT acl_object_identity_pkey PRIMARY KEY (id);

ALTER TABLE ONLY acl_object
    ADD CONSTRAINT acl_object_pkey PRIMARY KEY (id);

ALTER TABLE ONLY acl_remote
    ADD CONSTRAINT acl_remote_pkey PRIMARY KEY (id);

ALTER TABLE ONLY acl_sid
    ADD CONSTRAINT acl_sid_pkey PRIMARY KEY (id);

ALTER TABLE ONLY acl_sid
    ADD CONSTRAINT acl_sid_sid_principal_key UNIQUE (sid, principal);

ALTER TABLE ONLY batch_job_execution_context
    ADD CONSTRAINT batch_job_execution_context_pkey PRIMARY KEY (job_execution_id);

ALTER TABLE ONLY batch_job_execution
    ADD CONSTRAINT batch_job_execution_pkey PRIMARY KEY (job_execution_id);

ALTER TABLE ONLY batch_job_instance
    ADD CONSTRAINT batch_job_instance_job_name_job_key_key UNIQUE (job_name, job_key);

ALTER TABLE ONLY batch_job_instance
    ADD CONSTRAINT batch_job_instance_pkey PRIMARY KEY (job_instance_id);

ALTER TABLE ONLY batch_step_execution_context
    ADD CONSTRAINT batch_step_execution_context_pkey PRIMARY KEY (step_execution_id);

ALTER TABLE ONLY batch_step_execution
    ADD CONSTRAINT batch_step_execution_pkey PRIMARY KEY (step_execution_id);

ALTER TABLE ONLY filter_aes
    ADD CONSTRAINT filter_aes_pkey PRIMARY KEY (filter_id);

ALTER TABLE ONLY filter_alcohol
    ADD CONSTRAINT filter_alcohol_pkey PRIMARY KEY (filter_id);

ALTER TABLE ONLY filter_conmeds
    ADD CONSTRAINT filter_conmeds_pkey PRIMARY KEY (filter_id);

ALTER TABLE ONLY filter_death
    ADD CONSTRAINT filter_death_pkey PRIMARY KEY (filter_id);

ALTER TABLE ONLY filter_dose
    ADD CONSTRAINT filter_dose_pkey PRIMARY KEY (filter_id);

ALTER TABLE ONLY filter_dosedisc
    ADD CONSTRAINT filter_dosedisc_pkey PRIMARY KEY (filter_id);

ALTER TABLE ONLY filter_exacerbation
    ADD CONSTRAINT filter_exacerbation_pkey PRIMARY KEY (filter_id);

ALTER TABLE ONLY filter_hce
    ADD CONSTRAINT filter_hce_pkey PRIMARY KEY (filter_id);

ALTER TABLE ONLY filter_labs
    ADD CONSTRAINT filter_labs_pkey PRIMARY KEY (filter_id);

ALTER TABLE ONLY filter_liver_diag
    ADD CONSTRAINT filter_liver_diag_pkey PRIMARY KEY (filter_id);

ALTER TABLE ONLY filter_liver
    ADD CONSTRAINT filter_liver_pkey PRIMARY KEY (filter_id);

ALTER TABLE ONLY filter_liver_risk
    ADD CONSTRAINT filter_liver_risk_pkey PRIMARY KEY (filter_id);

ALTER TABLE ONLY filter_lungfunction
    ADD CONSTRAINT filter_lungfunction_pkey PRIMARY KEY (filter_id);

ALTER TABLE ONLY filter_lvef
    ADD CONSTRAINT filter_lvef_pkey PRIMARY KEY (filter_id);

ALTER TABLE ONLY filter_mh
    ADD CONSTRAINT filter_mh_pkey PRIMARY KEY (filter_id);

ALTER TABLE ONLY filter_nicotine
    ADD CONSTRAINT filter_nicotine_pkey PRIMARY KEY (filter_id);

ALTER TABLE ONLY filter_population
    ADD CONSTRAINT filter_population_pkey PRIMARY KEY (filter_id);

ALTER TABLE ONLY filter_qtcf
    ADD CONSTRAINT filter_qtcf_pkey PRIMARY KEY (filter_id);

ALTER TABLE ONLY filter_recist
    ADD CONSTRAINT filter_recist_pkey PRIMARY KEY (filter_id);

ALTER TABLE ONLY filter_renal
    ADD CONSTRAINT filter_renal_pkey PRIMARY KEY (filter_id);

ALTER TABLE ONLY filter_sae
    ADD CONSTRAINT filter_sae_pkey PRIMARY KEY (filter_id);

ALTER TABLE ONLY filter_safety_population
    ADD CONSTRAINT filter_safety_population_pkey PRIMARY KEY (filter_id);

ALTER TABLE ONLY filter_sh
    ADD CONSTRAINT filter_sh_pkey PRIMARY KEY (filter_id);

ALTER TABLE ONLY filter_vitals
    ADD CONSTRAINT filter_vitals_pkey PRIMARY KEY (filter_id);

ALTER TABLE ONLY dual
    ADD CONSTRAINT firstkey PRIMARY KEY (code);

ALTER TABLE ONLY group_members
    ADD CONSTRAINT group_members_pkey PRIMARY KEY (id);

ALTER TABLE ONLY groups
    ADD CONSTRAINT groups_pkey PRIMARY KEY (id);

ALTER TABLE ONLY log_arg
    ADD CONSTRAINT log_arg_pkey PRIMARY KEY (log_arg_id);

ALTER TABLE ONLY log_operation
    ADD CONSTRAINT log_operation_pkey PRIMARY KEY (log_operation_id);

ALTER TABLE ONLY map_ae_group_rule
    ADD CONSTRAINT map_ae_group_rule_pkey PRIMARY KEY (magr_id);

ALTER TABLE ONLY map_ae_group_value_rule
    ADD CONSTRAINT map_ae_group_value_rule_pkey PRIMARY KEY (magv_id);

ALTER TABLE ONLY map_aggr_fun
    ADD CONSTRAINT map_aggr_fun_pkey PRIMARY KEY (maf_id);

ALTER TABLE ONLY map_audit
    ADD CONSTRAINT map_audit_pkey PRIMARY KEY (mau_id);

ALTER TABLE ONLY map_clinical_study
    ADD CONSTRAINT map_clinical_study_pkey PRIMARY KEY (mcs_study_id, mcs_mpr_id);

ALTER TABLE ONLY map_column_rule
    ADD CONSTRAINT map_column_rule_pkey PRIMARY KEY (mcr_id);

ALTER TABLE ONLY map_custom_labcode_lookup
    ADD CONSTRAINT map_custom_labcode_lookup_pkey PRIMARY KEY (cll_id);

ALTER TABLE ONLY map_dashboard_col_options
    ADD CONSTRAINT map_dashboard_col_options_mdco_code_key UNIQUE (mdco_code);

ALTER TABLE ONLY map_dashboard_col_options
    ADD CONSTRAINT map_dashboard_col_options_pkey PRIMARY KEY (mdco_id);

ALTER TABLE ONLY map_dashboard_row_options
    ADD CONSTRAINT map_dashboard_row_options_mdro_code_key UNIQUE (mdro_code);

ALTER TABLE ONLY map_dashboard_row_options
    ADD CONSTRAINT map_dashboard_row_options_pkey PRIMARY KEY (mdro_id);

ALTER TABLE ONLY map_description_entity
    ADD CONSTRAINT map_description_entity_mde_men_id_mde_mfd_id_key UNIQUE (mde_men_id, mde_mfd_id);

ALTER TABLE ONLY map_description_entity
    ADD CONSTRAINT map_description_entity_pkey PRIMARY KEY (mde_id);

ALTER TABLE ONLY map_description_file
    ADD CONSTRAINT map_description_file_mdf_mfr_id_mdf_mfd_id_key UNIQUE (mdf_mfr_id, mdf_mfd_id);

ALTER TABLE ONLY map_description_file
    ADD CONSTRAINT map_description_file_pkey PRIMARY KEY (mdf_id);

ALTER TABLE ONLY map_dynamic_field
    ADD CONSTRAINT map_dynamic_field_mdfi_mmr_id_key UNIQUE (mdfi_mmr_id);

ALTER TABLE ONLY map_dynamic_field
    ADD CONSTRAINT map_dynamic_field_pkey PRIMARY KEY (mdfi_id);

ALTER TABLE ONLY map_entity
    ADD CONSTRAINT map_entity_men_name_key UNIQUE (men_name);

ALTER TABLE ONLY map_entity
    ADD CONSTRAINT map_entity_pkey PRIMARY KEY (men_id);

ALTER TABLE ONLY map_excluding_values
    ADD CONSTRAINT map_excluding_values_pkey PRIMARY KEY (mev_id);

ALTER TABLE ONLY map_fd_sv
    ADD CONSTRAINT map_fd_sv_pkey PRIMARY KEY (mfs_id);

ALTER TABLE ONLY map_field_description
    ADD CONSTRAINT map_field_description_mfid_text_key UNIQUE (mfid_text);

ALTER TABLE ONLY map_field_description
    ADD CONSTRAINT map_field_description_pkey PRIMARY KEY (mfid_id);

ALTER TABLE ONLY map_field
    ADD CONSTRAINT map_field_mfi_men_id_mfi_mfid_id_key UNIQUE (mfi_men_id, mfi_mfid_id);

ALTER TABLE ONLY map_field
    ADD CONSTRAINT map_field_mfi_men_id_mfi_name_key UNIQUE (mfi_men_id, mfi_name);

ALTER TABLE ONLY map_field
    ADD CONSTRAINT map_field_pkey PRIMARY KEY (mfi_id);

ALTER TABLE ONLY map_file_description
    ADD CONSTRAINT map_file_description_mfd_name_key UNIQUE (mfd_name);

ALTER TABLE ONLY map_file_description
    ADD CONSTRAINT map_file_description_pkey PRIMARY KEY (mfd_id);

ALTER TABLE ONLY map_file_rule
    ADD CONSTRAINT map_file_rule_pkey PRIMARY KEY (mfr_id);

ALTER TABLE ONLY map_file_section
    ADD CONSTRAINT map_file_section_mfs_name_key UNIQUE (mfs_name);

ALTER TABLE ONLY map_file_section
    ADD CONSTRAINT map_file_section_pkey PRIMARY KEY (mfs_id);

ALTER TABLE ONLY map_file_type
    ADD CONSTRAINT map_file_type_mft_name_mft_delimiter_key UNIQUE (mft_name, mft_delimiter);

ALTER TABLE ONLY map_file_type
    ADD CONSTRAINT map_file_type_pkey PRIMARY KEY (mft_id);

ALTER TABLE ONLY map_lab_group_rule
    ADD CONSTRAINT map_lab_group_rule_pkey PRIMARY KEY (mlgr_id);

ALTER TABLE ONLY map_lab_group_value_rule
    ADD CONSTRAINT map_lab_group_value_rule_pkey PRIMARY KEY (mlgv_id);

ALTER TABLE ONLY map_mapping_rule_field
    ADD CONSTRAINT map_mapping_rule_field_mrf_mmr_id_mrf_mfi_id_key UNIQUE (mrf_mmr_id, mrf_mfi_id);

ALTER TABLE ONLY map_mapping_rule_field
    ADD CONSTRAINT map_mapping_rule_field_pkey PRIMARY KEY (mrf_id);

ALTER TABLE ONLY map_mapping_rule
    ADD CONSTRAINT map_mapping_rule_pkey PRIMARY KEY (mmr_id);

ALTER TABLE ONLY map_project_rule
    ADD CONSTRAINT map_project_rule_mpr_drug_key UNIQUE (mpr_drug);

ALTER TABLE ONLY map_project_rule
    ADD CONSTRAINT map_project_rule_pkey PRIMARY KEY (mpr_id);

ALTER TABLE ONLY map_rct_inst_dshbrd_col_opt
    ADD CONSTRAINT map_rct_inst_dshbrd_col_opt_pkey PRIMARY KEY (mrdc_mdco_id, mrdc_instance_id);

ALTER TABLE ONLY map_rct_inst_dshbrd_row_opt
    ADD CONSTRAINT map_rct_inst_dshbrd_row_opt_pkey PRIMARY KEY (mrdr_mdco_id, mrdr_instance_id);

ALTER TABLE ONLY map_rct_instance_ae_group
    ADD CONSTRAINT map_rct_instance_ae_group_pkey PRIMARY KEY (miag_id);

ALTER TABLE ONLY map_rct_instance_lab_group
    ADD CONSTRAINT map_rct_instance_lab_group_pkey PRIMARY KEY (milg_id);

ALTER TABLE ONLY map_rct_instance_sbj_group
    ADD CONSTRAINT map_rct_instance_sbj_group_pkey PRIMARY KEY (misg_id);

ALTER TABLE ONLY map_rct_instance_sbj_grouping
    ADD CONSTRAINT map_rct_instance_sbj_grouping_pkey PRIMARY KEY (misgr_id);

ALTER TABLE ONLY map_rct_instance_std_rule
    ADD CONSTRAINT map_rct_instance_std_rule_pkey PRIMARY KEY (misd_id);

ALTER TABLE ONLY map_rct_instance_time_intrvl
    ADD CONSTRAINT map_rct_instance_time_intrvl_pkey PRIMARY KEY (miti_id);

ALTER TABLE ONLY map_acuity_instance_bak
    ADD CONSTRAINT map_acuity_instance_bak_mri_name_key UNIQUE (mri_name);

ALTER TABLE ONLY map_acuity_instance_bak
    ADD CONSTRAINT map_acuity_instance_bak_pkey PRIMARY KEY (mri_id);

ALTER TABLE ONLY map_acuity_instance
    ADD CONSTRAINT map_acuity_instance_mri_name_key UNIQUE (mri_name);

ALTER TABLE ONLY map_acuity_instance
    ADD CONSTRAINT map_acuity_instance_pkey PRIMARY KEY (mri_id);

ALTER TABLE ONLY map_sp_module_rct_instance
    ADD CONSTRAINT map_sp_module_rct_instance_pkey PRIMARY KEY (mmd_id);

ALTER TABLE ONLY map_sp_module_subject_grouping
    ADD CONSTRAINT map_sp_module_subject_grouping_pkey PRIMARY KEY (mmsg_id);

ALTER TABLE ONLY map_spotfire_module
    ADD CONSTRAINT map_spotfire_module_pkey PRIMARY KEY (msm_id);

ALTER TABLE ONLY map_spotfire_module_type
    ADD CONSTRAINT map_spotfire_module_type_pkey PRIMARY KEY (msmt_id);

ALTER TABLE ONLY map_spotfire_table
    ADD CONSTRAINT map_spotfire_table_pkey PRIMARY KEY (mst_id);

ALTER TABLE ONLY map_spotfire_view
    ADD CONSTRAINT map_spotfire_view_msv_view_msv_msm_id_key UNIQUE (msv_view, msv_msm_id);

ALTER TABLE ONLY map_spotfire_view
    ADD CONSTRAINT map_spotfire_view_pkey PRIMARY KEY (msv_id);

ALTER TABLE ONLY map_study_ae_group
    ADD CONSTRAINT map_study_ae_group_pkey PRIMARY KEY (msag_id);

ALTER TABLE ONLY map_study_baseline_drug
    ADD CONSTRAINT map_study_baseline_drug_msbd_msr_id_msbd_drug_name_key UNIQUE (msbd_msr_id, msbd_drug_name);

ALTER TABLE ONLY map_study_baseline_drug
    ADD CONSTRAINT map_study_baseline_drug_pkey PRIMARY KEY (msbd_id);

ALTER TABLE ONLY map_study_lab_group
    ADD CONSTRAINT map_study_lab_group_pkey PRIMARY KEY (mslg_id);

ALTER TABLE ONLY map_study_rule
    ADD CONSTRAINT map_study_rule_msr_study_code_msr_prj_id_key UNIQUE (msr_study_code, msr_prj_id);

ALTER TABLE ONLY map_study_rule
    ADD CONSTRAINT map_study_rule_pkey PRIMARY KEY (msr_id);

ALTER TABLE ONLY map_subject_group_annotation
    ADD CONSTRAINT map_subject_group_annotation_msga_grouping_id_msga_group_na_key UNIQUE (msga_grouping_id, msga_group_name);

ALTER TABLE ONLY map_subject_group_annotation
    ADD CONSTRAINT map_subject_group_annotation_pkey PRIMARY KEY (msga_id);

ALTER TABLE ONLY map_subject_group_dosing
    ADD CONSTRAINT map_subject_group_dosing_msgd_msga_id_msgd_drug_key UNIQUE (msgd_msga_id, msgd_drug);

ALTER TABLE ONLY map_subject_group_dosing
    ADD CONSTRAINT map_subject_group_dosing_pkey PRIMARY KEY (msgd_id);

ALTER TABLE ONLY map_subject_group_dosing_sched
    ADD CONSTRAINT map_subject_group_dosing_sched_pkey PRIMARY KEY (msgds_id);

ALTER TABLE ONLY map_subject_group_rule
    ADD CONSTRAINT map_subject_group_rule_pkey PRIMARY KEY (msgr_id);

ALTER TABLE ONLY map_subject_group_value_rule
    ADD CONSTRAINT map_subject_group_value_rule_msgv_group_id_msgv_subject_id_key UNIQUE (msgv_group_id, msgv_subject_id);

ALTER TABLE ONLY map_subject_group_value_rule
    ADD CONSTRAINT map_subject_group_value_rule_pkey PRIMARY KEY (msgv_id);

ALTER TABLE ONLY map_subject_grouping
    ADD CONSTRAINT map_subject_grouping_msg_study_id_msg_grouping_name_key UNIQUE (msg_study_id, msg_grouping_name);

ALTER TABLE ONLY map_subject_grouping
    ADD CONSTRAINT map_subject_grouping_pkey PRIMARY KEY (msg_id);

ALTER TABLE ONLY map_subject_grouping_type
    ADD CONSTRAINT map_subject_grouping_type_msgt_type_key UNIQUE (msgt_type);

ALTER TABLE ONLY map_subject_grouping_type
    ADD CONSTRAINT map_subject_grouping_type_pkey PRIMARY KEY (msgt_id);

ALTER TABLE ONLY map_time_intervals
    ADD CONSTRAINT map_time_intervals_mti_name_key UNIQUE (mti_name);

ALTER TABLE ONLY map_time_intervals
    ADD CONSTRAINT map_time_intervals_pkey PRIMARY KEY (mti_id);

ALTER TABLE ONLY precalc_recist
    ADD CONSTRAINT precalc_recist_pkey PRIMARY KEY (recist_id);

ALTER TABLE ONLY qrtz_blob_triggers
    ADD CONSTRAINT qrtz_blob_triggers_pkey PRIMARY KEY (sched_name, trigger_name, trigger_group);

ALTER TABLE ONLY qrtz_calendars
    ADD CONSTRAINT qrtz_calendars_pkey PRIMARY KEY (sched_name, calendar_name);

ALTER TABLE ONLY qrtz_cron_triggers
    ADD CONSTRAINT qrtz_cron_triggers_pkey PRIMARY KEY (sched_name, trigger_name, trigger_group);

ALTER TABLE ONLY qrtz_fired_triggers
    ADD CONSTRAINT qrtz_fired_triggers_pkey PRIMARY KEY (sched_name, entry_id);

ALTER TABLE ONLY qrtz_job_details
    ADD CONSTRAINT qrtz_job_details_pkey PRIMARY KEY (sched_name, job_name, job_group);

ALTER TABLE ONLY qrtz_locks
    ADD CONSTRAINT qrtz_locks_pkey PRIMARY KEY (sched_name, lock_name);

ALTER TABLE ONLY qrtz_paused_trigger_grps
    ADD CONSTRAINT qrtz_paused_trigger_grps_pkey PRIMARY KEY (sched_name, trigger_group);

ALTER TABLE ONLY qrtz_scheduler_state
    ADD CONSTRAINT qrtz_scheduler_state_pkey PRIMARY KEY (sched_name, instance_name);

ALTER TABLE ONLY qrtz_simple_triggers
    ADD CONSTRAINT qrtz_simple_triggers_pkey PRIMARY KEY (sched_name, trigger_name, trigger_group);

ALTER TABLE ONLY qrtz_simprop_triggers
    ADD CONSTRAINT qrtz_simprop_triggers_pkey PRIMARY KEY (sched_name, trigger_name, trigger_group);

ALTER TABLE ONLY qrtz_triggers
    ADD CONSTRAINT qrtz_triggers_pkey PRIMARY KEY (sched_name, trigger_name, trigger_group);

ALTER TABLE ONLY report_data_field
    ADD CONSTRAINT report_data_field_pkey PRIMARY KEY (rdf_id);

ALTER TABLE ONLY report_data_summary
    ADD CONSTRAINT report_data_summary_pkey PRIMARY KEY (rds_id);

ALTER TABLE ONLY report_data_table
    ADD CONSTRAINT report_data_table_pkey PRIMARY KEY (rdt_id);

ALTER TABLE ONLY report_data_value
    ADD CONSTRAINT report_data_value_pkey PRIMARY KEY (rdv_id);

ALTER TABLE ONLY report_exceptions
    ADD CONSTRAINT report_exceptions_pkey PRIMARY KEY (rex_id);

ALTER TABLE ONLY report_fk_violation
    ADD CONSTRAINT report_fk_violation_pkey PRIMARY KEY (rfv_id);

ALTER TABLE ONLY report_missed_cntlin
    ADD CONSTRAINT report_missed_cntlin_pkey PRIMARY KEY (rmc_id);

ALTER TABLE ONLY report_unique_violation
    ADD CONSTRAINT report_unique_violation_pkey PRIMARY KEY (rcv_id);

ALTER TABLE ONLY report_unique_violation
    ADD CONSTRAINT report_unique_violation_rcv_je_id_rcv_std_code_rcv_entity_n_key UNIQUE (rcv_je_id, rcv_std_code, rcv_entity_name, rcv_entity_hash);

ALTER TABLE ONLY report_unparsed_data
    ADD CONSTRAINT report_unparsed_data_pkey PRIMARY KEY (rud_id);

ALTER TABLE ONLY result_ae_action_taken
    ADD CONSTRAINT result_ae_action_taken_aeat_aes_id_aeat_drug_id_key UNIQUE (aeat_aes_id, aeat_drug_id);

ALTER TABLE ONLY result_ae_action_taken
    ADD CONSTRAINT result_ae_action_taken_pkey PRIMARY KEY (aeat_id);

ALTER TABLE ONLY result_ae_causality
    ADD CONSTRAINT result_ae_causality_aec_ae_id_aec_drug_id_key UNIQUE (aec_ae_id, aec_drug_id);

ALTER TABLE ONLY result_ae_causality
    ADD CONSTRAINT result_ae_causality_pkey PRIMARY KEY (aec_id);

ALTER TABLE ONLY result_ae_num_act_taken
    ADD CONSTRAINT result_ae_num_act_taken_pkey PRIMARY KEY (aenat_id);

ALTER TABLE ONLY result_ae_num_del
    ADD CONSTRAINT result_ae_num_del_pkey PRIMARY KEY (aend_id);

ALTER TABLE ONLY result_ae
    ADD CONSTRAINT result_ae_pkey PRIMARY KEY (ae_id);

ALTER TABLE ONLY result_ae_severity
    ADD CONSTRAINT result_ae_severity_aes_ae_id_aes_start_date_key UNIQUE (aes_ae_id, aes_start_date);

ALTER TABLE ONLY result_ae_severity
    ADD CONSTRAINT result_ae_severity_pkey PRIMARY KEY (aes_id);

ALTER TABLE ONLY result_alcohol_sub_use
    ADD CONSTRAINT result_alcohol_sub_use_pkey PRIMARY KEY (asu_id);

ALTER TABLE ONLY result_algorithm_outcomes
    ADD CONSTRAINT result_algorithm_outcomes_pkey PRIMARY KEY (ao_id);

ALTER TABLE ONLY result_biomarkers
    ADD CONSTRAINT result_biomarkers_pkey PRIMARY KEY (bmr_id);

ALTER TABLE ONLY result_cerebrovascular
    ADD CONSTRAINT result_cerebrovascular_pkey PRIMARY KEY (cer_id);

ALTER TABLE ONLY result_ci_event
    ADD CONSTRAINT result_ci_event_pkey PRIMARY KEY (ci_id);

ALTER TABLE ONLY result_conmed_procedure
    ADD CONSTRAINT result_conmed_procedure_pkey PRIMARY KEY (cp_id);

ALTER TABLE ONLY result_conmed_schedule
    ADD CONSTRAINT result_conmed_schedule_cms_start_date_cms_med_id_cms_pat_id_key UNIQUE (cms_start_date, cms_med_id, cms_pat_id, cms_ae_num);

ALTER TABLE ONLY result_conmed_schedule
    ADD CONSTRAINT result_conmed_schedule_pkey PRIMARY KEY (cms_id);

ALTER TABLE ONLY result_consent
    ADD CONSTRAINT result_consent_pkey PRIMARY KEY (ic_id);

ALTER TABLE ONLY result_country
    ADD CONSTRAINT result_country_ctr_pat_id_key UNIQUE (ctr_pat_id);

ALTER TABLE ONLY result_country
    ADD CONSTRAINT result_country_pkey PRIMARY KEY (ctr_id);

ALTER TABLE ONLY result_ctdna
    ADD CONSTRAINT result_ctdna_pkey PRIMARY KEY (ctd_id);

ALTER TABLE ONLY result_cvot
    ADD CONSTRAINT result_cvot_pkey PRIMARY KEY (cvot_id);

ALTER TABLE ONLY result_death
    ADD CONSTRAINT result_death_dth_pat_id_dth_designation_of_cause_key UNIQUE (dth_pat_id, dth_designation_of_cause);

ALTER TABLE ONLY result_death
    ADD CONSTRAINT result_death_pkey PRIMARY KEY (dth_id);

ALTER TABLE ONLY result_decg
    ADD CONSTRAINT result_decg_decg_tst_id_decg_measurment_label_key UNIQUE (decg_tst_id, decg_measurment_label);

ALTER TABLE ONLY result_decg
    ADD CONSTRAINT result_decg_pkey PRIMARY KEY (decg_id);

ALTER TABLE ONLY result_disease_extent
    ADD CONSTRAINT result_disease_extent_pkey PRIMARY KEY (de_id);

ALTER TABLE ONLY result_drug
    ADD CONSTRAINT result_drug_drug_std_id_drug_name_key UNIQUE (drug_std_id, drug_name);

ALTER TABLE ONLY result_drug
    ADD CONSTRAINT result_drug_pkey PRIMARY KEY (drug_id);

ALTER TABLE ONLY result_ecg
    ADD CONSTRAINT result_ecg_ecg_tst_id_key UNIQUE (ecg_tst_id);

ALTER TABLE ONLY result_ecg
    ADD CONSTRAINT result_ecg_pkey PRIMARY KEY (ecg_id);

ALTER TABLE ONLY result_ediary_medication_usage
    ADD CONSTRAINT result_ediary_medication_usage_pkey PRIMARY KEY (eme_id);

ALTER TABLE ONLY result_ediary_observations
    ADD CONSTRAINT result_ediary_observations_pkey PRIMARY KEY (eo_id);

ALTER TABLE ONLY result_ediary
    ADD CONSTRAINT result_ediary_pkey PRIMARY KEY (ediary_id);

ALTER TABLE ONLY result_eg
    ADD CONSTRAINT result_eg_eg_tst_id_eg_test_name_eg_result_unit_key UNIQUE (eg_tst_id, eg_test_name, eg_result_unit);

ALTER TABLE ONLY result_eg
    ADD CONSTRAINT result_eg_pkey PRIMARY KEY (eg_id);

ALTER TABLE ONLY result_event_type
    ADD CONSTRAINT result_event_type_evt_std_id_evt_soc_evt_hlt_evt_pt_key UNIQUE (evt_std_id, evt_soc, evt_hlt, evt_pt);

ALTER TABLE ONLY result_event_type
    ADD CONSTRAINT result_event_type_pkey PRIMARY KEY (evt_id);

ALTER TABLE ONLY result_exa_severity_map
    ADD CONSTRAINT result_exa_severity_map_esm_std_id_esm_depot_gcs_esm_syscor_key UNIQUE (esm_std_id, esm_depot_gcs, esm_syscort_trt, esm_ics_trt, esm_antibiotics_trt, esm_hospit, esm_emer_trt);

ALTER TABLE ONLY result_exa_severity_map
    ADD CONSTRAINT result_exa_severity_map_pkey PRIMARY KEY (esm_id);

ALTER TABLE ONLY result_exacerbation
    ADD CONSTRAINT result_exacerbation_exa_pat_id_exa_exac_start_date_key UNIQUE (exa_pat_id, exa_exac_start_date);

ALTER TABLE ONLY result_exacerbation
    ADD CONSTRAINT result_exacerbation_pkey PRIMARY KEY (exa_id);

ALTER TABLE ONLY result_fm_gene
    ADD CONSTRAINT result_fm_gene_pkey PRIMARY KEY (fm_id);

ALTER TABLE ONLY result_lab_group
    ADD CONSTRAINT result_lab_group_lgr_lab_code_lgr_std_id_key UNIQUE (lgr_lab_code, lgr_std_id);

ALTER TABLE ONLY result_lab_group
    ADD CONSTRAINT result_lab_group_pkey PRIMARY KEY (lgr_id);

ALTER TABLE ONLY result_laboratory
    ADD CONSTRAINT result_laboratory_lab_code_lab_tst_id_lab_value_lab_src_typ_key UNIQUE (lab_code, lab_tst_id, lab_value, lab_src_type);

ALTER TABLE ONLY result_laboratory
    ADD CONSTRAINT result_laboratory_pkey PRIMARY KEY (lab_id);

ALTER TABLE ONLY result_liver
    ADD CONSTRAINT result_liver_pkey PRIMARY KEY (li_id);

ALTER TABLE ONLY result_liver_risk_factors
    ADD CONSTRAINT result_liver_risk_factors_pkey PRIMARY KEY (lrf_id);

ALTER TABLE ONLY result_liverss
    ADD CONSTRAINT result_liverss_pkey PRIMARY KEY (lss_id);

ALTER TABLE ONLY result_lungfunc
    ADD CONSTRAINT result_lungfunc_lng_pat_id_lng_assess_date_lng_prot_schedul_key UNIQUE (lng_pat_id, lng_assess_date, lng_prot_schedule, lng_measurement);

ALTER TABLE ONLY result_lungfunc
    ADD CONSTRAINT result_lungfunc_pkey PRIMARY KEY (lng_id);

ALTER TABLE ONLY result_lvef
    ADD CONSTRAINT result_lvef_lvf_tst_id_key UNIQUE (lvf_tst_id);

ALTER TABLE ONLY result_lvef
    ADD CONSTRAINT result_lvef_pkey PRIMARY KEY (lvf_id);

ALTER TABLE ONLY result_medical_history
    ADD CONSTRAINT result_medical_history_pkey PRIMARY KEY (mh_id);

ALTER TABLE ONLY result_medicine
    ADD CONSTRAINT result_medicine_pkey PRIMARY KEY (med_id);

ALTER TABLE ONLY result_nicotine_sub_use
    ADD CONSTRAINT result_nicotine_sub_use_pkey PRIMARY KEY (nsu_id);

ALTER TABLE ONLY result_overdose_report
    ADD CONSTRAINT result_overdose_report_pkey PRIMARY KEY (or_id);

ALTER TABLE ONLY result_pathology
    ADD CONSTRAINT result_pathology_pkey PRIMARY KEY (pth_id);

ALTER TABLE ONLY result_patient_group
    ADD CONSTRAINT result_patient_group_pgr_std_id_pgr_pat_part_pgr_pat_subjec_key UNIQUE (pgr_std_id, pgr_pat_part, pgr_pat_subject, pgr_grouping_name);

ALTER TABLE ONLY result_patient_group
    ADD CONSTRAINT result_patient_group_pkey PRIMARY KEY (pgr_id);

ALTER TABLE ONLY result_patient
    ADD CONSTRAINT result_patient_pat_std_id_pat_subject_pat_part_key UNIQUE (pat_std_id, pat_subject, pat_part);

ALTER TABLE ONLY result_patient
    ADD CONSTRAINT result_patient_pkey PRIMARY KEY (pat_id);

ALTER TABLE ONLY result_patient_reported_data
    ADD CONSTRAINT result_patient_reported_data_pkey PRIMARY KEY (rd_id);

ALTER TABLE ONLY result_performance_status
    ADD CONSTRAINT result_performance_status_pkey PRIMARY KEY (ps_id);

ALTER TABLE ONLY result_pk_concentration
    ADD CONSTRAINT result_pk_concentration_pkc_pat_id_pkc_analyte_pkc_treatmen_key UNIQUE (pkc_pat_id, pkc_analyte, pkc_treatment_cycle, pkc_treatment, pkc_treatment_schedule, pkc_specimen_id);

ALTER TABLE ONLY result_pk_concentration
    ADD CONSTRAINT result_pk_concentration_pkey PRIMARY KEY (pkc_id);

ALTER TABLE ONLY result_pregnancy_test
    ADD CONSTRAINT result_pregnancy_test_pkey PRIMARY KEY (pt_id);

ALTER TABLE ONLY result_primary_tumour_location
    ADD CONSTRAINT result_primary_tumour_location_pkey PRIMARY KEY (ptl_id);

ALTER TABLE ONLY result_primary_tumour_location
    ADD CONSTRAINT result_primary_tumour_location_ptl_pat_id_key UNIQUE (ptl_pat_id);

ALTER TABLE ONLY result_project
    ADD CONSTRAINT result_project_pkey PRIMARY KEY (prj_id);

ALTER TABLE ONLY result_project
    ADD CONSTRAINT result_project_prj_name_key UNIQUE (prj_name);

ALTER TABLE ONLY result_randomisation
    ADD CONSTRAINT result_randomisation_pkey PRIMARY KEY (rnd_id);

ALTER TABLE ONLY result_randomisation
    ADD CONSTRAINT result_randomisation_rnd_pat_id_key UNIQUE (rnd_pat_id);

ALTER TABLE ONLY result_recist_assessment
    ADD CONSTRAINT result_recist_assessment_pkey PRIMARY KEY (rca_id);

ALTER TABLE ONLY result_recist_assessment
    ADD CONSTRAINT result_recist_assessment_rca_pat_id_rca_visit_date_rca_visi_key UNIQUE (rca_pat_id, rca_visit_date, rca_visit);

ALTER TABLE ONLY result_recist_nontarget_lesion
    ADD CONSTRAINT result_recist_nontarget_lesio_rntl_pat_id_rntl_lesion_date__key UNIQUE (rntl_pat_id, rntl_lesion_date, rntl_lesion_site, rntl_response);

ALTER TABLE ONLY result_recist_nontarget_lesion
    ADD CONSTRAINT result_recist_nontarget_lesion_pkey PRIMARY KEY (rntl_id);

ALTER TABLE ONLY result_recist_target_lesion
    ADD CONSTRAINT result_recist_target_lesion_pkey PRIMARY KEY (rtl_id);

ALTER TABLE ONLY result_recist_target_lesion
    ADD CONSTRAINT result_recist_target_lesion_rtl_pat_id_rtl_visit_date_rtl_v_key UNIQUE (rtl_pat_id, rtl_visit_date, rtl_visit_number, rtl_lesion_number);

ALTER TABLE ONLY result_sc
    ADD CONSTRAINT result_sc_pkey PRIMARY KEY (sc_id);

ALTER TABLE ONLY result_sc
    ADD CONSTRAINT result_sc_sc_pat_id_sc_ethpop_sc_s_ethpop_key UNIQUE (sc_pat_id, sc_ethpop, sc_s_ethpop);

ALTER TABLE ONLY result_serious_adverse_event
    ADD CONSTRAINT result_serious_adverse_event_pkey PRIMARY KEY (sae_id);

ALTER TABLE ONLY result_serious_adverse_event
    ADD CONSTRAINT result_serious_adverse_event_sae_pat_id_sae_adverse_event_s_key UNIQUE (sae_pat_id, sae_adverse_event, sae_crit_met_date);

ALTER TABLE ONLY result_source
    ADD CONSTRAINT result_source_pkey PRIMARY KEY (src_id);

ALTER TABLE ONLY result_specimen_collection
    ADD CONSTRAINT result_specimen_collection_pkey PRIMARY KEY (spc_id);

ALTER TABLE ONLY result_specimen_collection
    ADD CONSTRAINT result_specimen_collection_spc_pat_id_spc_specimen_id_key UNIQUE (spc_pat_id, spc_specimen_id);

ALTER TABLE ONLY result_stacked_pk_results
    ADD CONSTRAINT result_stacked_pk_results_pkey PRIMARY KEY (stp_id);

ALTER TABLE ONLY result_stacked_pk_results
    ADD CONSTRAINT result_stacked_pk_results_stp_pat_id_stp_analyte_stp_parame_key UNIQUE (stp_pat_id, stp_analyte, stp_parameter, stp_treatment, stp_treatment_schedule, stp_treatment_cycle, stp_protocol_schedule, stp_visit_date, stp_visit_number, stp_protocol_schd_start_day, stp_protocol_schd_start_hour, stp_protocol_schd_start_minute, stp_protocol_schedule_end, stp_protocol_schd_end_day, stp_protocol_schd_end_hour, stp_protocol_schd_end_minute, stp_visit, stp_actual_dose);

ALTER TABLE ONLY result_study
    ADD CONSTRAINT result_study_pkey PRIMARY KEY (std_id);

ALTER TABLE ONLY result_study
    ADD CONSTRAINT result_study_std_prj_id_std_name_key UNIQUE (std_prj_id, std_name);

ALTER TABLE ONLY result_surgical_history
    ADD CONSTRAINT result_surgical_history_pkey PRIMARY KEY (sh_id);

ALTER TABLE ONLY result_target_med_dos_disc
    ADD CONSTRAINT result_target_med_dos_disc_dsc_drug_name_dsc_ipdc_date_dsc__key UNIQUE (dsc_drug_name, dsc_ipdc_date, dsc_pat_id);

ALTER TABLE ONLY result_target_med_dos_disc
    ADD CONSTRAINT result_target_med_dos_disc_pkey PRIMARY KEY (dsc_id);

ALTER TABLE ONLY result_test
    ADD CONSTRAINT result_test_pkey PRIMARY KEY (tst_id);

ALTER TABLE ONLY result_test
    ADD CONSTRAINT result_test_tst_date_tst_pat_id_key UNIQUE (tst_date, tst_pat_id);

ALTER TABLE ONLY result_trg_med_dos_schedule
    ADD CONSTRAINT result_trg_med_dos_schedule_mds_start_date_mds_drug_mds_pat_key UNIQUE (mds_start_date, mds_drug, mds_pat_id);

ALTER TABLE ONLY result_trg_med_dos_schedule
    ADD CONSTRAINT result_trg_med_dos_schedule_pkey PRIMARY KEY (mds_id);

ALTER TABLE ONLY result_visit
    ADD CONSTRAINT result_visit_pkey PRIMARY KEY (vis_id);

ALTER TABLE ONLY result_visit
    ADD CONSTRAINT result_visit_vis_date_vis_pat_id_key UNIQUE (vis_date, vis_pat_id);

ALTER TABLE ONLY result_vitals
    ADD CONSTRAINT result_vitals_pkey PRIMARY KEY (vit_id);

ALTER TABLE ONLY result_vitals
    ADD CONSTRAINT result_vitals_vit_tst_id_vit_test_name_vit_unit_vit_anatomi_key UNIQUE (vit_tst_id, vit_test_name, vit_unit, vit_anatomical_location, vit_anatomical_side_interest, vit_physical_position);

ALTER TABLE ONLY result_withdrawal_completion
    ADD CONSTRAINT result_withdrawal_completion_pkey PRIMARY KEY (wc_id);

ALTER TABLE ONLY result_withdrawal_completion
    ADD CONSTRAINT result_withdrawal_completion_wc_pat_id_key UNIQUE (wc_pat_id);

ALTER TABLE ONLY saved_filter_instance
    ADD CONSTRAINT saved_filter_instance_pkey PRIMARY KEY (id);

ALTER TABLE ONLY saved_filter
    ADD CONSTRAINT saved_filter_name_owner_dataset_id_dataset_class_key UNIQUE (name, owner, dataset_id, dataset_class);

ALTER TABLE ONLY saved_filter_permission
    ADD CONSTRAINT saved_filter_permission_pkey PRIMARY KEY (id);

ALTER TABLE ONLY saved_filter
    ADD CONSTRAINT saved_filter_pkey PRIMARY KEY (id);

ALTER TABLE ONLY schema_version
    ADD CONSTRAINT schema_version_pkey PRIMARY KEY (installed_rank);

ALTER TABLE result_biomarkers
    ADD CONSTRAINT sys_c009366 CHECK ((bmr_somatic_status IS NOT NULL)) NOT VALID;

ALTER TABLE result_patient_reported_data
    ADD CONSTRAINT sys_c009410 CHECK ((rd_src_type IS NOT NULL)) NOT VALID;

ALTER TABLE ONLY user_activity
    ADD CONSTRAINT user_activity_pkey PRIMARY KEY (ua_id);

ALTER TABLE ONLY users
    ADD CONSTRAINT users_linked_username_key UNIQUE (linked_username);

ALTER TABLE ONLY users
    ADD CONSTRAINT users_pkey PRIMARY KEY (username);

ALTER TABLE ONLY util_country_code_lookup
    ADD CONSTRAINT util_country_code_lookup_ccl_code_key UNIQUE (ccl_code);

ALTER TABLE ONLY util_country_code_lookup
    ADD CONSTRAINT util_country_code_lookup_pkey PRIMARY KEY (ccl_id);

ALTER TABLE ONLY util_deployment_env_props
    ADD CONSTRAINT util_deployment_env_props_pkey PRIMARY KEY (dep_id);

ALTER TABLE ONLY util_ecg_evaluation_lookup
    ADD CONSTRAINT util_ecg_evaluation_lookup_pkey PRIMARY KEY (sel_id);

ALTER TABLE ONLY util_ecg_evaluation_lookup
    ADD CONSTRAINT util_ecg_evaluation_lookup_sel_label_key UNIQUE (sel_label);

ALTER TABLE ONLY util_ecg_significant_lookup
    ADD CONSTRAINT util_ecg_significant_lookup_pkey PRIMARY KEY (scl_id);

ALTER TABLE ONLY util_ecg_significant_lookup
    ADD CONSTRAINT util_ecg_significant_lookup_scl_label_key UNIQUE (scl_label);

ALTER TABLE ONLY util_email_details
    ADD CONSTRAINT util_email_details_pkey PRIMARY KEY (emd_id);

ALTER TABLE ONLY util_labcode_dictionary
    ADD CONSTRAINT util_labcode_dictionary_pkey PRIMARY KEY (lcd_id);

ALTER TABLE ONLY util_labcode_lookup
    ADD CONSTRAINT util_labcode_lookup_lcl_labcode_key UNIQUE (lcl_labcode);

ALTER TABLE ONLY util_labcode_lookup
    ADD CONSTRAINT util_labcode_lookup_pkey PRIMARY KEY (lcl_id);

ALTER TABLE ONLY util_labcode_synonym
    ADD CONSTRAINT util_labcode_synonym_lcs_synonym_key UNIQUE (lcs_synonym);

ALTER TABLE ONLY util_labcode_synonym
    ADD CONSTRAINT util_labcode_synonym_pkey PRIMARY KEY (lcs_id);

ALTER TABLE ONLY util_labctcg_lookup
    ADD CONSTRAINT util_labctcg_lookup_pkey PRIMARY KEY (labcgl_code);

ALTER TABLE ONLY util_mutation_type_dictionary
    ADD CONSTRAINT util_mutation_type_dictionary_pkey PRIMARY KEY (mtd_id);

ALTER TABLE ONLY util_mutation_type_synonym
    ADD CONSTRAINT util_mutation_type_synonym_mts_synonym_key UNIQUE (mts_synonym);

ALTER TABLE ONLY util_mutation_type_synonym
    ADD CONSTRAINT util_mutation_type_synonym_pkey PRIMARY KEY (mts_id);

ALTER TABLE ONLY util_recist_response_lookup
    ADD CONSTRAINT util_recist_response_lookup_pkey PRIMARY KEY (rrl_code);
