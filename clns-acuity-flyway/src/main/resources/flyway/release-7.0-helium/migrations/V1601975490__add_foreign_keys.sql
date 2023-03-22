ALTER TABLE ONLY acl_entry
    ADD CONSTRAINT fk_acl_entry_acl FOREIGN KEY (sid) REFERENCES acl_sid(id);

ALTER TABLE ONLY acl_entry
    ADD CONSTRAINT fk_acl_entry_object FOREIGN KEY (acl_object_identity) REFERENCES acl_object_identity(id);

ALTER TABLE ONLY acl_object
    ADD CONSTRAINT fk_acl_object_id FOREIGN KEY (acl_object_identity_id) REFERENCES acl_object_identity(id);

ALTER TABLE ONLY acl_object_identity
    ADD CONSTRAINT fk_acl_object_identity_class FOREIGN KEY (object_id_class) REFERENCES acl_class(id);

ALTER TABLE ONLY acl_object_identity
    ADD CONSTRAINT fk_acl_object_identity_owner FOREIGN KEY (owner_sid) REFERENCES acl_sid(id);

ALTER TABLE ONLY acl_object_identity
    ADD CONSTRAINT fk_acl_object_identity_parent FOREIGN KEY (parent_object) REFERENCES acl_object_identity(id);

ALTER TABLE ONLY acl_class
    ADD CONSTRAINT fk_acl_remote_id FOREIGN KEY (acl_remote_id) REFERENCES acl_remote(id);

ALTER TABLE ONLY authorities
    ADD CONSTRAINT fk_authorities_users FOREIGN KEY (username) REFERENCES users(username);

ALTER TABLE ONLY batch_step_execution
    ADD CONSTRAINT fk_bje_job_exec_step FOREIGN KEY (job_execution_id) REFERENCES batch_job_execution(job_execution_id);

ALTER TABLE ONLY batch_job_execution
    ADD CONSTRAINT fk_bje_job_inst_exec FOREIGN KEY (job_instance_id) REFERENCES batch_job_instance(job_instance_id);

ALTER TABLE ONLY batch_job_execution_context
    ADD CONSTRAINT fk_bjec_job_exec_ctx FOREIGN KEY (job_execution_id) REFERENCES batch_job_execution(job_execution_id);

ALTER TABLE ONLY batch_job_params
    ADD CONSTRAINT fk_bjp_job_inst_params FOREIGN KEY (job_instance_id) REFERENCES batch_job_instance(job_instance_id);

ALTER TABLE ONLY batch_step_execution_context
    ADD CONSTRAINT fk_bsec_step_exec_ctx FOREIGN KEY (step_execution_id) REFERENCES batch_step_execution(step_execution_id);

ALTER TABLE ONLY result_chemotherapy
    ADD CONSTRAINT fk_c_chemo_pat_id FOREIGN KEY (chemo_pat_id) REFERENCES result_patient(pat_id) ON DELETE CASCADE;

ALTER TABLE ONLY result_primary_tumour_location
    ADD CONSTRAINT fk_c_ptl_pat_id FOREIGN KEY (ptl_pat_id) REFERENCES result_patient(pat_id) ON DELETE CASCADE;

ALTER TABLE ONLY result_conmed_schedule
    ADD CONSTRAINT fk_cs_cms_med_id FOREIGN KEY (cms_med_id) REFERENCES result_medicine(med_id) ON DELETE CASCADE;

ALTER TABLE ONLY result_conmed_schedule
    ADD CONSTRAINT fk_cs_cms_pat_id FOREIGN KEY (cms_pat_id) REFERENCES result_patient(pat_id) ON DELETE CASCADE;

ALTER TABLE ONLY result_country
    ADD CONSTRAINT fk_ctr_pat_id FOREIGN KEY (ctr_pat_id) REFERENCES result_patient(pat_id) ON DELETE CASCADE;

ALTER TABLE ONLY result_decg
    ADD CONSTRAINT fk_d_decg_tst_id FOREIGN KEY (decg_tst_id) REFERENCES result_test(tst_id) ON DELETE CASCADE;

ALTER TABLE ONLY result_death
    ADD CONSTRAINT fk_d_dth_pat_id FOREIGN KEY (dth_pat_id) REFERENCES result_patient(pat_id) ON DELETE CASCADE;

ALTER TABLE ONLY filter_dose_pks
    ADD CONSTRAINT fk_dose_filter_id FOREIGN KEY (filter_id) REFERENCES filter_dose(filter_id);

ALTER TABLE ONLY result_ecg
    ADD CONSTRAINT fk_e_ecg_tst_id FOREIGN KEY (ecg_tst_id) REFERENCES result_test(tst_id) ON DELETE CASCADE;

ALTER TABLE ONLY result_ediary
    ADD CONSTRAINT fk_e_ediary_pat_id FOREIGN KEY (ediary_pat_id) REFERENCES result_patient(pat_id) ON DELETE CASCADE;

ALTER TABLE ONLY result_ediary_medication_usage
    ADD CONSTRAINT fk_e_eme_pat_id FOREIGN KEY (eme_pat_id) REFERENCES result_patient(pat_id) ON DELETE CASCADE;

ALTER TABLE ONLY result_ediary_observations
    ADD CONSTRAINT fk_e_eo_pat_id FOREIGN KEY (eo_pat_id) REFERENCES result_patient(pat_id) ON DELETE CASCADE;

ALTER TABLE ONLY result_eg
    ADD CONSTRAINT fk_eg_tst_id FOREIGN KEY (eg_tst_id) REFERENCES result_test(tst_id) ON DELETE CASCADE;

ALTER TABLE ONLY result_exa_severity_map
    ADD CONSTRAINT fk_esm_std_id FOREIGN KEY (esm_std_id) REFERENCES result_study(std_id) ON DELETE CASCADE;

ALTER TABLE ONLY result_event_type
    ADD CONSTRAINT fk_et_evt_std_id FOREIGN KEY (evt_std_id) REFERENCES result_study(std_id) ON DELETE CASCADE;

ALTER TABLE ONLY result_exacerbation
    ADD CONSTRAINT fk_exa_pat_id FOREIGN KEY (exa_pat_id) REFERENCES result_patient(pat_id) ON DELETE CASCADE;

ALTER TABLE ONLY filter_exacerbation_pks
    ADD CONSTRAINT fk_exab_filter_id FOREIGN KEY (filter_id) REFERENCES filter_exacerbation(filter_id);

ALTER TABLE ONLY result_fm_gene
    ADD CONSTRAINT fk_fm_pat_id FOREIGN KEY (fm_pat_id) REFERENCES result_patient(pat_id) ON DELETE CASCADE;

ALTER TABLE ONLY filter_safety_population
    ADD CONSTRAINT fk_fsp_map_global_rct_inst_id FOREIGN KEY (map_acuity_instance_id) REFERENCES map_acuity_instance(mri_id) ON DELETE CASCADE;

ALTER TABLE ONLY group_authorities
    ADD CONSTRAINT fk_group_authorities_group FOREIGN KEY (group_id) REFERENCES groups(id);

ALTER TABLE ONLY group_members
    ADD CONSTRAINT fk_group_members_group FOREIGN KEY (group_id) REFERENCES groups(id);

ALTER TABLE ONLY filter_hce_pks
    ADD CONSTRAINT fk_hce_filter_id FOREIGN KEY (filter_id) REFERENCES filter_hce(filter_id);

ALTER TABLE ONLY map_rct_instance_sbj_grouping
    ADD CONSTRAINT fk_instance_sbj_group FOREIGN KEY (misgr_misd_id) REFERENCES map_rct_instance_std_rule(misd_id) ON DELETE CASCADE;

ALTER TABLE ONLY result_laboratory
    ADD CONSTRAINT fk_l_lab_lgr_id FOREIGN KEY (lab_lgr_id) REFERENCES result_lab_group(lgr_id) ON DELETE SET NULL;

ALTER TABLE ONLY result_laboratory
    ADD CONSTRAINT fk_l_lab_src_id FOREIGN KEY (lab_src_id) REFERENCES result_source(src_id) ON DELETE SET NULL;

ALTER TABLE ONLY result_laboratory
    ADD CONSTRAINT fk_l_lab_tst_id FOREIGN KEY (lab_tst_id) REFERENCES result_test(tst_id) ON DELETE CASCADE;

ALTER TABLE ONLY result_lab_group
    ADD CONSTRAINT fk_lg_lgr_std_id FOREIGN KEY (lgr_std_id) REFERENCES result_study(std_id) ON DELETE CASCADE;

ALTER TABLE ONLY result_lungfunc
    ADD CONSTRAINT fk_lng_pat_id FOREIGN KEY (lng_pat_id) REFERENCES result_patient(pat_id) ON DELETE CASCADE;

ALTER TABLE ONLY log_arg
    ADD CONSTRAINT fk_log_arg_log_id FOREIGN KEY (log_operation_id) REFERENCES log_operation(log_operation_id);

ALTER TABLE ONLY filter_lungfunction_pks
    ADD CONSTRAINT fk_lung_filter_id FOREIGN KEY (filter_id) REFERENCES filter_lungfunction(filter_id);

ALTER TABLE ONLY result_lvef
    ADD CONSTRAINT fk_lvef_lvf_tst_id FOREIGN KEY (lvf_tst_id) REFERENCES result_test(tst_id) ON DELETE CASCADE;

ALTER TABLE ONLY filter_lvef
    ADD CONSTRAINT fk_lvef_map_rct_inst_id FOREIGN KEY (map_acuity_instance_id) REFERENCES map_acuity_instance(mri_id) ON DELETE CASCADE;

ALTER TABLE ONLY result_medicine
    ADD CONSTRAINT fk_m_med_std_id FOREIGN KEY (med_std_id) REFERENCES result_study(std_id) ON DELETE CASCADE;

ALTER TABLE ONLY map_ae_group_rule
    ADD CONSTRAINT fk_magr_prg_id FOREIGN KEY (magr_project_id) REFERENCES map_project_rule(mpr_id) ON DELETE CASCADE;

ALTER TABLE ONLY map_ae_group_value_rule
    ADD CONSTRAINT fk_magv_grp_id FOREIGN KEY (magv_group_id) REFERENCES map_ae_group_rule(magr_id) ON DELETE CASCADE;

ALTER TABLE ONLY map_column_rule
    ADD CONSTRAINT fk_mcr_mmr_id FOREIGN KEY (mcr_mmr_id) REFERENCES map_mapping_rule(mmr_id) ON DELETE CASCADE;

ALTER TABLE ONLY map_clinical_study
    ADD CONSTRAINT fk_mcs_mpr_id FOREIGN KEY (mcs_mpr_id) REFERENCES map_project_rule(mpr_id) ON DELETE CASCADE;

ALTER TABLE ONLY map_description_entity
    ADD CONSTRAINT fk_mde_men_id FOREIGN KEY (mde_men_id) REFERENCES map_entity(men_id) ON DELETE CASCADE;

ALTER TABLE ONLY map_description_entity
    ADD CONSTRAINT fk_mde_mfd_id FOREIGN KEY (mde_mfd_id) REFERENCES map_file_description(mfd_id) ON DELETE CASCADE;

ALTER TABLE ONLY map_description_file
    ADD CONSTRAINT fk_mdf_mfd_id FOREIGN KEY (mdf_mfd_id) REFERENCES map_file_description(mfd_id) ON DELETE CASCADE;

ALTER TABLE ONLY map_description_file
    ADD CONSTRAINT fk_mdf_mfr_id FOREIGN KEY (mdf_mfr_id) REFERENCES map_file_rule(mfr_id) ON DELETE CASCADE;

ALTER TABLE ONLY map_file_description
    ADD CONSTRAINT fk_mfd_mfs_id FOREIGN KEY (mfd_section_id) REFERENCES map_file_section(mfs_id) ON DELETE CASCADE;

ALTER TABLE ONLY map_field
    ADD CONSTRAINT fk_mfi_men_id FOREIGN KEY (mfi_men_id) REFERENCES map_entity(men_id) ON DELETE CASCADE;

ALTER TABLE ONLY map_field
    ADD CONSTRAINT fk_mfi_mfid_id FOREIGN KEY (mfi_mfid_id) REFERENCES map_field_description(mfid_id) ON DELETE CASCADE;

ALTER TABLE ONLY map_file_rule
    ADD CONSTRAINT fk_mfr_mft_id FOREIGN KEY (mfr_mft_id) REFERENCES map_file_type(mft_id) ON DELETE CASCADE;

ALTER TABLE ONLY map_file_rule
    ADD CONSTRAINT fk_mfr_msr_id FOREIGN KEY (mfr_msr_id) REFERENCES map_study_rule(msr_id) ON DELETE CASCADE;

ALTER TABLE ONLY map_fd_sv
    ADD CONSTRAINT fk_mfs_mfd_id FOREIGN KEY (mfs_mfd_id) REFERENCES map_file_description(mfd_id) ON DELETE CASCADE;

ALTER TABLE ONLY map_fd_sv
    ADD CONSTRAINT fk_mfs_msv_id FOREIGN KEY (mfs_msv_id) REFERENCES map_spotfire_view(msv_id) ON DELETE CASCADE;

ALTER TABLE ONLY map_rct_instance_ae_group
    ADD CONSTRAINT fk_miag_grp_id FOREIGN KEY (miag_ae_group_id) REFERENCES map_ae_group_rule(magr_id) ON DELETE CASCADE;

ALTER TABLE ONLY map_rct_instance_ae_group
    ADD CONSTRAINT fk_miag_inst_id FOREIGN KEY (miag_instance_id) REFERENCES map_acuity_instance(mri_id) ON DELETE CASCADE;

ALTER TABLE ONLY map_rct_instance_lab_group
    ADD CONSTRAINT fk_milg_grp_id FOREIGN KEY (milg_ae_group_id) REFERENCES map_lab_group_rule(mlgr_id) ON DELETE CASCADE;

ALTER TABLE ONLY map_rct_instance_lab_group
    ADD CONSTRAINT fk_milg_inst_id FOREIGN KEY (milg_instance_id) REFERENCES map_acuity_instance(mri_id) ON DELETE CASCADE;

ALTER TABLE ONLY map_rct_instance_sbj_group
    ADD CONSTRAINT fk_misg_grp_id FOREIGN KEY (misg_ae_group_id) REFERENCES map_subject_group_rule(msgr_id) ON DELETE CASCADE;

ALTER TABLE ONLY map_rct_instance_sbj_group
    ADD CONSTRAINT fk_misg_inst_id FOREIGN KEY (misg_instance_id) REFERENCES map_acuity_instance(mri_id) ON DELETE CASCADE;

ALTER TABLE ONLY map_rct_instance_std_rule
    ADD CONSTRAINT fk_mist_inst_id FOREIGN KEY (misd_instance_id) REFERENCES map_acuity_instance(mri_id) ON DELETE CASCADE;

ALTER TABLE ONLY map_rct_instance_std_rule
    ADD CONSTRAINT fk_mist_std_id FOREIGN KEY (misd_study_id) REFERENCES map_study_rule(msr_id) ON DELETE CASCADE;

ALTER TABLE ONLY map_rct_instance_time_intrvl
    ADD CONSTRAINT fk_miti_instance_id FOREIGN KEY (miti_instance_id) REFERENCES map_acuity_instance(mri_id) ON DELETE CASCADE;

ALTER TABLE ONLY map_rct_instance_time_intrvl
    ADD CONSTRAINT fk_miti_mti_id FOREIGN KEY (miti_mti_id) REFERENCES map_time_intervals(mti_id) ON DELETE CASCADE;

ALTER TABLE ONLY map_lab_group_rule
    ADD CONSTRAINT fk_mlgr_prg_id FOREIGN KEY (mlgr_project_id) REFERENCES map_project_rule(mpr_id) ON DELETE CASCADE;

ALTER TABLE ONLY map_lab_group_value_rule
    ADD CONSTRAINT fk_mlgv_grp_id FOREIGN KEY (mlgv_group_id) REFERENCES map_lab_group_rule(mlgr_id) ON DELETE CASCADE;

ALTER TABLE ONLY map_sp_module_rct_instance
    ADD CONSTRAINT fk_mmd_inst_id FOREIGN KEY (mmd_instance_id) REFERENCES map_acuity_instance(mri_id) ON DELETE CASCADE;

ALTER TABLE ONLY map_sp_module_rct_instance
    ADD CONSTRAINT fk_mmd_mdl_id FOREIGN KEY (mmd_module_id) REFERENCES map_spotfire_module(msm_id) ON DELETE CASCADE;

ALTER TABLE ONLY map_dynamic_field
    ADD CONSTRAINT fk_mmr_id FOREIGN KEY (mdfi_mmr_id) REFERENCES map_mapping_rule(mmr_id) ON DELETE CASCADE;

ALTER TABLE ONLY map_mapping_rule
    ADD CONSTRAINT fk_mmr_maf_id FOREIGN KEY (mmr_maf_id) REFERENCES map_aggr_fun(maf_id) ON DELETE CASCADE;

ALTER TABLE ONLY map_mapping_rule
    ADD CONSTRAINT fk_mmr_mfr_id FOREIGN KEY (mmr_mfr_id) REFERENCES map_file_rule(mfr_id) ON DELETE CASCADE;

ALTER TABLE ONLY map_sp_module_subject_grouping
    ADD CONSTRAINT fk_module_type FOREIGN KEY (msmt_id) REFERENCES map_spotfire_module_type(msmt_id);

ALTER TABLE ONLY map_study_rule
    ADD CONSTRAINT fk_mpr_id FOREIGN KEY (msr_prj_id) REFERENCES map_project_rule(mpr_id) ON DELETE CASCADE;

ALTER TABLE ONLY map_rct_inst_dshbrd_col_opt
    ADD CONSTRAINT fk_mrdc_instance_id FOREIGN KEY (mrdc_instance_id) REFERENCES map_acuity_instance(mri_id) ON DELETE CASCADE;

ALTER TABLE ONLY map_rct_inst_dshbrd_col_opt
    ADD CONSTRAINT fk_mrdc_mdco_id FOREIGN KEY (mrdc_mdco_id) REFERENCES map_dashboard_col_options(mdco_id) ON DELETE CASCADE;

ALTER TABLE ONLY map_rct_inst_dshbrd_row_opt
    ADD CONSTRAINT fk_mrdr_instance_id FOREIGN KEY (mrdr_instance_id) REFERENCES map_acuity_instance(mri_id) ON DELETE CASCADE;

ALTER TABLE ONLY map_rct_inst_dshbrd_row_opt
    ADD CONSTRAINT fk_mrdr_mdco_id FOREIGN KEY (mrdr_mdco_id) REFERENCES map_dashboard_row_options(mdro_id) ON DELETE CASCADE;

ALTER TABLE ONLY map_mapping_rule_field
    ADD CONSTRAINT fk_mrf_mfi_id FOREIGN KEY (mrf_mfi_id) REFERENCES map_field(mfi_id) ON DELETE CASCADE;

ALTER TABLE ONLY map_mapping_rule_field
    ADD CONSTRAINT fk_mrf_mmr_id FOREIGN KEY (mrf_mmr_id) REFERENCES map_mapping_rule(mmr_id) ON DELETE CASCADE;

ALTER TABLE ONLY map_acuity_instance_bak
    ADD CONSTRAINT fk_mri_prj_id FOREIGN KEY (mri_project_id) REFERENCES map_project_rule(mpr_id) ON DELETE CASCADE;

ALTER TABLE ONLY map_study_ae_group
    ADD CONSTRAINT fk_msag_grp_id FOREIGN KEY (msag_ae_group_id) REFERENCES map_ae_group_rule(magr_id) ON DELETE CASCADE;

ALTER TABLE ONLY map_study_ae_group
    ADD CONSTRAINT fk_msag_msr_id FOREIGN KEY (msag_msr_id) REFERENCES map_study_rule(msr_id) ON DELETE CASCADE;

ALTER TABLE ONLY map_subject_grouping
    ADD CONSTRAINT fk_msg_msr_id FOREIGN KEY (msg_study_id) REFERENCES map_study_rule(msr_id) ON DELETE CASCADE;

ALTER TABLE ONLY map_subject_group_annotation
    ADD CONSTRAINT fk_msga_msg_id FOREIGN KEY (msga_grouping_id) REFERENCES map_subject_grouping(msg_id) ON DELETE CASCADE;

ALTER TABLE ONLY map_subject_group_rule
    ADD CONSTRAINT fk_msgr_prg_id FOREIGN KEY (msgr_study_id) REFERENCES map_study_rule(msr_id) ON DELETE CASCADE;

ALTER TABLE ONLY map_subject_group_value_rule
    ADD CONSTRAINT fk_msgv_grp_id FOREIGN KEY (msgv_group_id) REFERENCES map_subject_group_rule(msgr_id) ON DELETE CASCADE;

ALTER TABLE ONLY map_study_lab_group
    ADD CONSTRAINT fk_mslg_grp_id FOREIGN KEY (mslg_lab_group_id) REFERENCES map_lab_group_rule(mlgr_id) ON DELETE CASCADE;

ALTER TABLE ONLY map_study_lab_group
    ADD CONSTRAINT fk_mslg_msr_id FOREIGN KEY (mslg_msr_id) REFERENCES map_study_rule(msr_id) ON DELETE CASCADE;

ALTER TABLE ONLY map_spotfire_module
    ADD CONSTRAINT fk_msm_msmt_id FOREIGN KEY (msm_msmt_id) REFERENCES map_spotfire_module_type(msmt_id) ON DELETE CASCADE;

ALTER TABLE ONLY map_study_rule
    ADD CONSTRAINT fk_msr_mcs_study_id FOREIGN KEY (msr_mcs_study_id, msr_prj_id) REFERENCES map_clinical_study(mcs_study_id, mcs_mpr_id) ON DELETE CASCADE;

ALTER TABLE ONLY map_spotfire_table
    ADD CONSTRAINT fk_mst_msmt_id FOREIGN KEY (mst_msmt_id) REFERENCES map_spotfire_module_type(msmt_id) ON DELETE CASCADE;

ALTER TABLE ONLY map_spotfire_view
    ADD CONSTRAINT fk_msv_msm_id FOREIGN KEY (msv_msm_id) REFERENCES map_spotfire_module(msm_id) ON DELETE CASCADE;

ALTER TABLE ONLY result_patient
    ADD CONSTRAINT fk_p_pat_pgr_id FOREIGN KEY (pat_pgr_id) REFERENCES result_patient_group(pgr_id) ON DELETE CASCADE;

ALTER TABLE ONLY result_patient
    ADD CONSTRAINT fk_p_pat_std_id FOREIGN KEY (pat_std_id) REFERENCES result_study(std_id) ON DELETE CASCADE;

ALTER TABLE ONLY result_patient_group
    ADD CONSTRAINT fk_pg_pgr_std_id FOREIGN KEY (pgr_std_id) REFERENCES result_study(std_id) ON DELETE CASCADE;

ALTER TABLE ONLY result_pk_concentration
    ADD CONSTRAINT fk_pkc_pat_id_fk FOREIGN KEY (pkc_pat_id) REFERENCES result_patient(pat_id) ON DELETE CASCADE;

ALTER TABLE ONLY precalc_labs
    ADD CONSTRAINT fk_plab_src_id FOREIGN KEY (plab_src_id) REFERENCES result_source(src_id) ON DELETE SET NULL;

ALTER TABLE ONLY result_radiotherapy
    ADD CONSTRAINT fk_r_rad_pat_id FOREIGN KEY (rad_pat_id) REFERENCES result_patient(pat_id) ON DELETE CASCADE;

ALTER TABLE ONLY result_recist_assessment
    ADD CONSTRAINT fk_ra_rca_pat_id FOREIGN KEY (rca_pat_id) REFERENCES result_patient(pat_id) ON DELETE CASCADE;

ALTER TABLE ONLY result_patient_reported_data
    ADD CONSTRAINT fk_rd_pat_id FOREIGN KEY (rd_pat_id) REFERENCES result_patient(pat_id) ON DELETE CASCADE;

ALTER TABLE ONLY result_patient_reported_data
    ADD CONSTRAINT fk_rd_src_id FOREIGN KEY (rd_src_id) REFERENCES result_source(src_id) ON DELETE SET NULL;

ALTER TABLE ONLY filter_recist_pks
    ADD CONSTRAINT fk_recist_filter_id FOREIGN KEY (filter_id) REFERENCES filter_recist(filter_id);

ALTER TABLE ONLY report_fk_violation
    ADD CONSTRAINT fk_rfv_je_id FOREIGN KEY (rfv_je_id) REFERENCES batch_job_execution(job_execution_id);

ALTER TABLE ONLY report_missed_cntlin
    ADD CONSTRAINT fk_rmc_je_id FOREIGN KEY (rmc_je_id) REFERENCES batch_job_execution(job_execution_id);

ALTER TABLE ONLY result_randomisation
    ADD CONSTRAINT fk_rnd_pat_id FOREIGN KEY (rnd_pat_id) REFERENCES result_patient(pat_id) ON DELETE CASCADE;

ALTER TABLE ONLY report_unparsed_data
    ADD CONSTRAINT fk_rud_rud_je_id FOREIGN KEY (rud_je_id) REFERENCES batch_job_execution(job_execution_id);

ALTER TABLE ONLY report_unique_violation
    ADD CONSTRAINT fk_ruv_rcv_je_id FOREIGN KEY (rcv_je_id) REFERENCES batch_job_execution(job_execution_id);

ALTER TABLE ONLY result_study
    ADD CONSTRAINT fk_s_std_prj_id FOREIGN KEY (std_prj_id) REFERENCES result_project(prj_id);

ALTER TABLE ONLY result_serious_adverse_event
    ADD CONSTRAINT fk_sae_pat_id FOREIGN KEY (sae_pat_id) REFERENCES result_patient(pat_id) ON DELETE CASCADE;

ALTER TABLE ONLY saved_filter_dataset
    ADD CONSTRAINT fk_saved_filter_id FOREIGN KEY (saved_filter_id) REFERENCES saved_filter(id);

ALTER TABLE ONLY result_sc
    ADD CONSTRAINT fk_sc_pat_id FOREIGN KEY (sc_pat_id) REFERENCES result_patient(pat_id) ON DELETE CASCADE;

ALTER TABLE ONLY result_specimen_collection
    ADD CONSTRAINT fk_spc_pat_id FOREIGN KEY (spc_pat_id) REFERENCES result_patient(pat_id) ON DELETE CASCADE;

ALTER TABLE ONLY result_stacked_pk_results
    ADD CONSTRAINT fk_stp_pat_id FOREIGN KEY (stp_pat_id) REFERENCES result_patient(pat_id) ON DELETE CASCADE;

ALTER TABLE ONLY map_rct_instance_sbj_grouping
    ADD CONSTRAINT fk_subject_grouping FOREIGN KEY (misgr_msg_id) REFERENCES map_subject_grouping(msg_id) ON DELETE CASCADE;

ALTER TABLE ONLY map_sp_module_subject_grouping
    ADD CONSTRAINT fk_subject_grouping_type FOREIGN KEY (msgt_id) REFERENCES map_subject_grouping_type(msgt_id);

ALTER TABLE ONLY result_test
    ADD CONSTRAINT fk_t_tst_pat_id FOREIGN KEY (tst_pat_id) REFERENCES result_patient(pat_id) ON DELETE CASCADE;

ALTER TABLE ONLY result_target_med_dos_disc
    ADD CONSTRAINT fk_tmdd_dsc_pat_id FOREIGN KEY (dsc_pat_id) REFERENCES result_patient(pat_id) ON DELETE CASCADE;

ALTER TABLE ONLY result_trg_med_dos_schedule
    ADD CONSTRAINT fk_tmds_mds_pat_id FOREIGN KEY (mds_pat_id) REFERENCES result_patient(pat_id) ON DELETE CASCADE;

ALTER TABLE ONLY result_visit
    ADD CONSTRAINT fk_v_vis_pat_id FOREIGN KEY (vis_pat_id) REFERENCES result_patient(pat_id) ON DELETE CASCADE;

ALTER TABLE ONLY result_vitals
    ADD CONSTRAINT fk_vitals_tst FOREIGN KEY (vit_tst_id) REFERENCES result_test(tst_id) ON DELETE CASCADE;

ALTER TABLE ONLY result_withdrawal_completion
    ADD CONSTRAINT fk_wc_pat_id FOREIGN KEY (wc_pat_id) REFERENCES result_patient(pat_id) ON DELETE CASCADE;

ALTER TABLE ONLY saved_filter_instance
    ADD CONSTRAINT fkd4mq5aaohqqhlk17pyndh3kke FOREIGN KEY (saved_filter_id) REFERENCES saved_filter(id);

ALTER TABLE ONLY saved_filter_permission
    ADD CONSTRAINT fktimjbet2glm43tbbrc49r1fe8 FOREIGN KEY (saved_filter_id) REFERENCES saved_filter(id);

ALTER TABLE ONLY batch_job_execution_params
    ADD CONSTRAINT job_exec_params_fk FOREIGN KEY (job_execution_id) REFERENCES batch_job_execution(job_execution_id);

ALTER TABLE ONLY map_custom_labcode_lookup
    ADD CONSTRAINT map_custom_labcode_lkp_fk_std FOREIGN KEY (cll_msr_id) REFERENCES map_study_rule(msr_id) ON DELETE CASCADE;

ALTER TABLE ONLY map_excluding_values
    ADD CONSTRAINT map_excluding_values_mfi FOREIGN KEY (mev_mfi_id) REFERENCES map_field(mfi_id) ON DELETE CASCADE;

ALTER TABLE ONLY map_study_baseline_drug
    ADD CONSTRAINT msbd_fk_msr FOREIGN KEY (msbd_msr_id) REFERENCES map_study_rule(msr_id) ON DELETE CASCADE;

ALTER TABLE ONLY qrtz_blob_triggers
    ADD CONSTRAINT qrtz_blob_trig_to_trig_fk FOREIGN KEY (sched_name, trigger_name, trigger_group) REFERENCES qrtz_triggers(sched_name, trigger_name, trigger_group);

ALTER TABLE ONLY qrtz_cron_triggers
    ADD CONSTRAINT qrtz_cron_trig_to_trig_fk FOREIGN KEY (sched_name, trigger_name, trigger_group) REFERENCES qrtz_triggers(sched_name, trigger_name, trigger_group);

ALTER TABLE ONLY qrtz_simple_triggers
    ADD CONSTRAINT qrtz_simple_trig_to_trig_fk FOREIGN KEY (sched_name, trigger_name, trigger_group) REFERENCES qrtz_triggers(sched_name, trigger_name, trigger_group);

ALTER TABLE ONLY qrtz_simprop_triggers
    ADD CONSTRAINT qrtz_simprop_trig_to_trig_fk FOREIGN KEY (sched_name, trigger_name, trigger_group) REFERENCES qrtz_triggers(sched_name, trigger_name, trigger_group);

ALTER TABLE ONLY qrtz_triggers
    ADD CONSTRAINT qrtz_trigger_to_jobs_fk FOREIGN KEY (sched_name, job_name, job_group) REFERENCES qrtz_job_details(sched_name, job_name, job_group);

ALTER TABLE ONLY report_data_field
    ADD CONSTRAINT report_data_field_batch_j_fk1 FOREIGN KEY (rdf_je_id) REFERENCES batch_job_execution(job_execution_id) ON DELETE CASCADE;

ALTER TABLE ONLY report_data_summary
    ADD CONSTRAINT report_data_sumary_batch_j_fk1 FOREIGN KEY (rds_je_id) REFERENCES batch_job_execution(job_execution_id) ON DELETE CASCADE;

ALTER TABLE ONLY report_data_table
    ADD CONSTRAINT report_data_table_batch_j_fk1 FOREIGN KEY (rdt_je_id) REFERENCES batch_job_execution(job_execution_id) ON DELETE CASCADE;

ALTER TABLE ONLY report_data_value
    ADD CONSTRAINT report_data_value_batch_j_fk1 FOREIGN KEY (rdv_je_id) REFERENCES batch_job_execution(job_execution_id);

ALTER TABLE ONLY report_exceptions
    ADD CONSTRAINT report_exceptions_batch_j_fk1 FOREIGN KEY (rex_je_id) REFERENCES batch_job_execution(job_execution_id) ON DELETE CASCADE;

ALTER TABLE ONLY result_drug
    ADD CONSTRAINT sys_c0010008 FOREIGN KEY (drug_std_id) REFERENCES result_study(std_id) ON DELETE CASCADE;

ALTER TABLE ONLY result_ae_causality
    ADD CONSTRAINT sys_c0010018 FOREIGN KEY (aec_ae_id) REFERENCES result_ae(ae_id) ON DELETE CASCADE;

ALTER TABLE ONLY result_ae_causality
    ADD CONSTRAINT sys_c0010019 FOREIGN KEY (aec_drug_id) REFERENCES result_drug(drug_id) ON DELETE CASCADE;

ALTER TABLE ONLY map_subject_grouping
    ADD CONSTRAINT sys_c0010028 FOREIGN KEY (msg_msgt_id) REFERENCES map_subject_grouping_type(msgt_id);

ALTER TABLE ONLY map_subject_group_dosing
    ADD CONSTRAINT sys_c0010043 FOREIGN KEY (msgd_msga_id) REFERENCES map_subject_group_annotation(msga_id) ON DELETE CASCADE;

ALTER TABLE ONLY report_data_alert
    ADD CONSTRAINT sys_c0010046 FOREIGN KEY (rda_je_id) REFERENCES batch_job_execution(job_execution_id);

ALTER TABLE ONLY result_recist_nontarget_lesion
    ADD CONSTRAINT sys_c0010049 FOREIGN KEY (rntl_pat_id) REFERENCES result_patient(pat_id) ON DELETE CASCADE;

ALTER TABLE ONLY result_ae
    ADD CONSTRAINT sys_c0010055 FOREIGN KEY (ae_evt_id) REFERENCES result_event_type(evt_id) ON DELETE CASCADE;

ALTER TABLE ONLY result_ae
    ADD CONSTRAINT sys_c0010056 FOREIGN KEY (ae_pat_id) REFERENCES result_patient(pat_id) ON DELETE CASCADE;

ALTER TABLE ONLY result_cvot
    ADD CONSTRAINT sys_c0010062 FOREIGN KEY (cvot_pat_id) REFERENCES result_patient(pat_id) ON DELETE CASCADE;

ALTER TABLE ONLY result_ci_event
    ADD CONSTRAINT sys_c0010063 FOREIGN KEY (ci_pat_id) REFERENCES result_patient(pat_id) ON DELETE CASCADE;

ALTER TABLE ONLY result_cerebrovascular
    ADD CONSTRAINT sys_c0010064 FOREIGN KEY (cer_pat_id) REFERENCES result_patient(pat_id) ON DELETE CASCADE;

ALTER TABLE ONLY result_biomarkers
    ADD CONSTRAINT sys_c0010065 FOREIGN KEY (bmr_pat_id) REFERENCES result_patient(pat_id) ON DELETE CASCADE;

ALTER TABLE ONLY result_pathology
    ADD CONSTRAINT sys_c0010066 FOREIGN KEY (pth_pat_id) REFERENCES result_patient(pat_id) ON DELETE CASCADE;

ALTER TABLE ONLY result_surgical_history
    ADD CONSTRAINT sys_c0010067 FOREIGN KEY (sh_pat_id) REFERENCES result_patient(pat_id) ON DELETE CASCADE;

ALTER TABLE ONLY result_disease_extent
    ADD CONSTRAINT sys_c0010068 FOREIGN KEY (de_pat_id) REFERENCES result_patient(pat_id) ON DELETE CASCADE;

ALTER TABLE ONLY result_liver
    ADD CONSTRAINT sys_c0010069 FOREIGN KEY (li_pat_id) REFERENCES result_patient(pat_id) ON DELETE CASCADE;

ALTER TABLE ONLY result_conmed_procedure
    ADD CONSTRAINT sys_c0010070 FOREIGN KEY (cp_pat_id) REFERENCES result_patient(pat_id) ON DELETE CASCADE;

ALTER TABLE ONLY result_consent
    ADD CONSTRAINT sys_c0010071 FOREIGN KEY (ic_pat_id) REFERENCES result_patient(pat_id) ON DELETE CASCADE;

ALTER TABLE ONLY result_pregnancy_test
    ADD CONSTRAINT sys_c0010072 FOREIGN KEY (pt_pat_id) REFERENCES result_patient(pat_id) ON DELETE CASCADE;

ALTER TABLE ONLY result_nicotine_sub_use
    ADD CONSTRAINT sys_c0010073 FOREIGN KEY (nsu_pat_id) REFERENCES result_patient(pat_id) ON DELETE CASCADE;

ALTER TABLE ONLY result_alcohol_sub_use
    ADD CONSTRAINT sys_c0010074 FOREIGN KEY (asu_pat_id) REFERENCES result_patient(pat_id) ON DELETE CASCADE;

ALTER TABLE ONLY result_performance_status
    ADD CONSTRAINT sys_c0010075 FOREIGN KEY (ps_pat_id) REFERENCES result_patient(pat_id) ON DELETE CASCADE;

ALTER TABLE ONLY result_overdose_report
    ADD CONSTRAINT sys_c0010076 FOREIGN KEY (or_pat_id) REFERENCES result_patient(pat_id) ON DELETE CASCADE;

ALTER TABLE ONLY result_liver_risk_factors
    ADD CONSTRAINT sys_c0010077 FOREIGN KEY (lrf_pat_id) REFERENCES result_patient(pat_id) ON DELETE CASCADE;

ALTER TABLE ONLY result_ae_num_act_taken
    ADD CONSTRAINT sys_c0010079 FOREIGN KEY (aenat_mds_id) REFERENCES result_trg_med_dos_schedule(mds_id) ON DELETE CASCADE;

ALTER TABLE ONLY result_ae_num_act_taken
    ADD CONSTRAINT sys_c0010080 FOREIGN KEY (aenat_pat_id) REFERENCES result_patient(pat_id) ON DELETE CASCADE;

ALTER TABLE ONLY result_ae_num_del
    ADD CONSTRAINT sys_c0010081 FOREIGN KEY (aend_mds_id) REFERENCES result_trg_med_dos_schedule(mds_id) ON DELETE CASCADE;

ALTER TABLE ONLY result_ae_num_del
    ADD CONSTRAINT sys_c0010082 FOREIGN KEY (aend_pat_id) REFERENCES result_patient(pat_id) ON DELETE CASCADE;

ALTER TABLE ONLY result_liverss
    ADD CONSTRAINT sys_c0010083 FOREIGN KEY (lss_pat_id) REFERENCES result_patient(pat_id) ON DELETE CASCADE;

ALTER TABLE ONLY result_ctdna
    ADD CONSTRAINT sys_c0010101 FOREIGN KEY (ctd_pat_id) REFERENCES result_patient(pat_id) ON DELETE CASCADE;

ALTER TABLE ONLY map_acuity_instance
    ADD CONSTRAINT sys_c009917 FOREIGN KEY (mri_project_id) REFERENCES map_project_rule(mpr_id) ON DELETE CASCADE;

ALTER TABLE ONLY result_ae_severity
    ADD CONSTRAINT sys_c009923 FOREIGN KEY (aes_ae_id) REFERENCES result_ae(ae_id) ON DELETE CASCADE;

ALTER TABLE ONLY result_medical_history
    ADD CONSTRAINT sys_c009946 FOREIGN KEY (mh_pat_id) REFERENCES result_patient(pat_id) ON DELETE CASCADE;

ALTER TABLE ONLY result_recist_target_lesion
    ADD CONSTRAINT sys_c009950 FOREIGN KEY (rtl_pat_id) REFERENCES result_patient(pat_id) ON DELETE CASCADE;

ALTER TABLE ONLY result_ae_action_taken
    ADD CONSTRAINT sys_c009969 FOREIGN KEY (aeat_aes_id) REFERENCES result_ae_severity(aes_id) ON DELETE CASCADE;

ALTER TABLE ONLY result_ae_action_taken
    ADD CONSTRAINT sys_c009970 FOREIGN KEY (aeat_drug_id) REFERENCES result_drug(drug_id) ON DELETE CASCADE;

ALTER TABLE ONLY map_subject_group_dosing_sched
    ADD CONSTRAINT sys_c009997 FOREIGN KEY (msgds_msgd_id) REFERENCES map_subject_group_dosing(msgd_id) ON DELETE CASCADE;
