CREATE UNIQUE INDEX i_filter_aes_pks_idpks ON filter_aes_pks USING btree (filter_id, foreign_pk);

CREATE UNIQUE INDEX i_filter_conmeds_pks_idpks ON filter_conmeds_pks USING btree (filter_id, foreign_pk);

CREATE UNIQUE INDEX i_filter_labs_pks_idpks ON filter_labs_pks USING btree (filter_id, foreign_pk);

CREATE UNIQUE INDEX i_filter_liver_pks_idpks ON filter_liver_pks USING btree (filter_id, foreign_pk);

CREATE UNIQUE INDEX i_filter_lvef_pks_idpks ON filter_lvef_pks USING btree (filter_id, foreign_pk);

CREATE UNIQUE INDEX i_filter_pop_pks_idpks ON filter_population_pks USING btree (filter_id, foreign_pk);

CREATE UNIQUE INDEX i_filter_population_pks_idpks ON filter_safety_population_pks USING btree (filter_id, foreign_pk);

CREATE UNIQUE INDEX i_filter_qtcf_pks_idpks ON filter_qtcf_pks USING btree (filter_id, foreign_pk);

CREATE UNIQUE INDEX i_filter_renal_pks_idpks ON filter_renal_pks USING btree (filter_id, foreign_pk);

CREATE UNIQUE INDEX i_filter_vitals_pks_idpks ON filter_vitals_pks USING btree (filter_id, foreign_pk);

CREATE INDEX "idx$$_83570001" ON result_vitals USING btree (vit_test_name, vit_tst_id, vit_value);

CREATE INDEX "idx$$_83570002" ON map_acuity_instance USING btree (mri_module_type);

CREATE INDEX idx_aenat_mds_id ON result_ae_num_act_taken USING btree (aenat_mds_id);

CREATE INDEX idx_aend_mds_id ON result_ae_num_del USING btree (aend_mds_id);

CREATE INDEX idx_asu_pat ON result_alcohol_sub_use USING btree (asu_pat_id);

CREATE INDEX idx_bmr_pat ON result_biomarkers USING btree (bmr_pat_id);

CREATE INDEX idx_cer_pat ON result_cerebrovascular USING btree (cer_pat_id);

CREATE INDEX idx_chemo_pat ON result_chemotherapy USING btree (chemo_pat_id);

CREATE INDEX idx_ci_pat ON result_ci_event USING btree (ci_pat_id);

CREATE INDEX idx_cs_cms_med_id ON result_conmed_schedule USING btree (cms_med_id);

CREATE INDEX idx_cs_cms_pat_id ON result_conmed_schedule USING btree (cms_pat_id);

CREATE INDEX idx_ctd_pat ON result_ctdna USING btree (ctd_pat_id);

CREATE INDEX idx_cvot_pat ON result_cvot USING btree (cvot_pat_id);

CREATE INDEX idx_de_pat ON result_disease_extent USING btree (de_pat_id);

CREATE INDEX idx_decg_tst ON result_decg USING btree (decg_tst_id);

CREATE INDEX idx_dth_pat ON result_death USING btree (dth_pat_id);

CREATE INDEX idx_eg_result_unit ON result_eg USING btree (eg_result_unit);

CREATE INDEX idx_eg_test_name ON result_eg USING btree (eg_test_name);

CREATE INDEX idx_evt_std ON result_event_type USING btree (evt_std_id);

CREATE INDEX idx_exa_pat ON result_exacerbation USING btree (exa_pat_id);

CREATE INDEX idx_fm_pat ON result_fm_gene USING btree (fm_pat_id);

CREATE INDEX idx_ic_pat ON result_consent USING btree (ic_pat_id);

CREATE INDEX idx_l_lab_lgr_id ON result_laboratory USING btree (lab_lgr_id);

CREATE INDEX idx_l_lab_tst_id ON result_laboratory USING btree (lab_tst_id);

CREATE INDEX idx_lgr_std ON result_lab_group USING btree (lgr_std_id);

CREATE INDEX idx_li_pat ON result_liver USING btree (li_pat_id);

CREATE INDEX idx_lng_pat ON result_lungfunc USING btree (lng_pat_id);

CREATE INDEX idx_lrf_pat ON result_liver_risk_factors USING btree (lrf_pat_id);

CREATE INDEX idx_lss_pat ON result_liverss USING btree (lss_pat_id);

CREATE INDEX idx_med_std ON result_medicine USING btree (med_std_id);

CREATE INDEX idx_mh_pat ON result_medical_history USING btree (mh_pat_id);

CREATE INDEX idx_misd_inst_study ON map_rct_instance_std_rule USING btree (misd_instance_id, misd_study_id);

CREATE INDEX idx_mri_inst_name ON map_acuity_instance USING btree (mri_id, mri_name);

CREATE INDEX idx_msr_id_code ON map_study_rule USING btree (msr_id, msr_study_code);

CREATE INDEX idx_nsu_pat ON result_nicotine_sub_use USING btree (nsu_pat_id);

CREATE INDEX idx_or_pat ON result_overdose_report USING btree (or_pat_id);

CREATE INDEX idx_p_pat_std_id ON result_patient USING btree (pat_std_id);

CREATE INDEX idx_pat_pgr ON result_patient USING btree (pat_pgr_id);

CREATE INDEX idx_pgr_std ON result_patient_group USING btree (pgr_std_id);

CREATE INDEX idx_pkc_pat ON result_pk_concentration USING btree (pkc_pat_id);

CREATE INDEX idx_prec_labcode ON precalc_labs USING btree (plab_labcode);

CREATE INDEX idx_prec_lower_labcode ON precalc_labs USING btree (lower((plab_labcode)::text));

CREATE INDEX idx_precist_data ON precalc_recist USING btree (dataset);

CREATE INDEX idx_precist_dataid ON precalc_recist USING btree (dataset_id);

CREATE INDEX idx_precist_pat_id ON precalc_recist USING btree (pat_id);

CREATE INDEX idx_ps_pat ON result_performance_status USING btree (ps_pat_id);

CREATE INDEX idx_pt_pat ON result_pregnancy_test USING btree (pt_pat_id);

CREATE INDEX idx_pth_pat ON result_pathology USING btree (pth_pat_id);

CREATE INDEX idx_qrtz_ft_inst_job_req_rcvry ON qrtz_fired_triggers USING btree (sched_name, instance_name, requests_recovery);

CREATE INDEX idx_qrtz_ft_j_g ON qrtz_fired_triggers USING btree (sched_name, job_name, job_group);

CREATE INDEX idx_qrtz_ft_jg ON qrtz_fired_triggers USING btree (sched_name, job_group);

CREATE INDEX idx_qrtz_ft_t_g ON qrtz_fired_triggers USING btree (sched_name, trigger_name, trigger_group);

CREATE INDEX idx_qrtz_ft_tg ON qrtz_fired_triggers USING btree (sched_name, trigger_group);

CREATE INDEX idx_qrtz_ft_trig_inst_name ON qrtz_fired_triggers USING btree (sched_name, instance_name);

CREATE INDEX idx_qrtz_j_grp ON qrtz_job_details USING btree (sched_name, job_group);

CREATE INDEX idx_qrtz_j_req_recovery ON qrtz_job_details USING btree (sched_name, requests_recovery);

CREATE INDEX idx_qrtz_t_c ON qrtz_triggers USING btree (sched_name, calendar_name);

CREATE INDEX idx_qrtz_t_g ON qrtz_triggers USING btree (sched_name, trigger_group);

CREATE INDEX idx_qrtz_t_j ON qrtz_triggers USING btree (sched_name, job_name, job_group);

CREATE INDEX idx_qrtz_t_jg ON qrtz_triggers USING btree (sched_name, job_group);

CREATE INDEX idx_qrtz_t_n_g_state ON qrtz_triggers USING btree (sched_name, trigger_group, trigger_state);

CREATE INDEX idx_qrtz_t_n_state ON qrtz_triggers USING btree (sched_name, trigger_name, trigger_group, trigger_state);

CREATE INDEX idx_qrtz_t_next_fire_time ON qrtz_triggers USING btree (sched_name, next_fire_time);

CREATE INDEX idx_qrtz_t_nft_misfire ON qrtz_triggers USING btree (sched_name, misfire_instr, next_fire_time);

CREATE INDEX idx_qrtz_t_nft_st ON qrtz_triggers USING btree (sched_name, trigger_state, next_fire_time);

CREATE INDEX idx_qrtz_t_nft_st_misfire ON qrtz_triggers USING btree (sched_name, misfire_instr, next_fire_time, trigger_state);

CREATE INDEX idx_qrtz_t_nft_st_misfire_grp ON qrtz_triggers USING btree (sched_name, misfire_instr, next_fire_time, trigger_group, trigger_state);

CREATE INDEX idx_qrtz_t_state ON qrtz_triggers USING btree (sched_name, trigger_state);

CREATE INDEX idx_ra_rca_pat_id ON result_recist_assessment USING btree (rca_pat_id);

CREATE INDEX idx_rad_pat ON result_radiotherapy USING btree (rad_pat_id);

CREATE INDEX idx_rd_pat ON result_patient_reported_data USING btree (rd_pat_id);

CREATE INDEX idx_rdv_je_id ON report_data_value USING btree (rdv_je_id);

CREATE INDEX idx_rntl_pat ON result_recist_nontarget_lesion USING btree (rntl_pat_id);

CREATE INDEX idx_rtl_pat ON result_recist_target_lesion USING btree (rtl_pat_id);

CREATE INDEX idx_s_std_prj_id ON result_study USING btree (std_prj_id);

CREATE INDEX idx_sae_pat ON result_serious_adverse_event USING btree (sae_pat_id);

CREATE INDEX idx_sc_pat ON result_sc USING btree (sc_pat_id);

CREATE INDEX idx_sh_pat ON result_surgical_history USING btree (sh_pat_id);

CREATE INDEX idx_spc_pat ON result_specimen_collection USING btree (spc_pat_id);

CREATE INDEX idx_std_id_name ON result_study USING btree (std_id, std_name);

CREATE INDEX idx_stp_pat ON result_stacked_pk_results USING btree (stp_pat_id);

CREATE INDEX idx_t_tst_pat_id ON result_test USING btree (tst_pat_id);

CREATE INDEX idx_tmdd_dsc_pat_id ON result_target_med_dos_disc USING btree (dsc_pat_id);

CREATE INDEX idx_tmds_mds_pat_id ON result_trg_med_dos_schedule USING btree (mds_pat_id);

CREATE INDEX idx_v_vis_pat_id ON result_visit USING btree (vis_pat_id);

CREATE INDEX idx_vit_tst ON result_vitals USING btree (vit_tst_id);

CREATE INDEX ind_e_ediary_pat_id ON result_ediary USING btree (ediary_pat_id);

CREATE INDEX ind_e_eme_pat_id ON result_ediary_medication_usage USING btree (eme_pat_id);

CREATE INDEX ind_e_eo_pat_id ON result_ediary_observations USING btree (eo_pat_id);

CREATE UNIQUE INDEX ix_auth_username ON authorities USING btree (username, authority);

CREATE INDEX ix_filter_aes ON filter_aes USING btree (hashcode, map_acuity_instance_id);

CREATE INDEX ix_filter_aes_pks ON filter_aes_pks USING btree (filter_id);

CREATE INDEX ix_filter_alcohol ON filter_alcohol USING btree (hashcode, map_acuity_instance_id);

CREATE INDEX ix_filter_alcohol_pks ON filter_alcohol_pks USING btree (filter_id);

CREATE INDEX ix_filter_conmeds ON filter_conmeds USING btree (hashcode, map_acuity_instance_id);

CREATE INDEX ix_filter_conmeds_pks ON filter_conmeds_pks USING btree (filter_id);

CREATE INDEX ix_filter_death ON filter_death USING btree (hashcode, map_acuity_instance_id);

CREATE INDEX ix_filter_death_pks ON filter_death_pks USING btree (filter_id);

CREATE INDEX ix_filter_dose ON filter_dose USING btree (hashcode, map_acuity_instance_id);

CREATE INDEX ix_filter_dose_pks ON filter_dose_pks USING btree (filter_id);

CREATE INDEX ix_filter_dosedisc ON filter_dosedisc USING btree (hashcode, map_acuity_instance_id);

CREATE INDEX ix_filter_dosedisc_pks ON filter_dosedisc_pks USING btree (filter_id);

CREATE INDEX ix_filter_exab ON filter_exacerbation USING btree (hashcode, map_acuity_instance_id);

CREATE INDEX ix_filter_exab_pks ON filter_exacerbation_pks USING btree (filter_id);

CREATE INDEX ix_filter_hce ON filter_hce USING btree (hashcode, map_acuity_instance_id);

CREATE INDEX ix_filter_hce_pks ON filter_hce_pks USING btree (filter_id);

CREATE INDEX ix_filter_labs ON filter_labs USING btree (hashcode, map_acuity_instance_id);

CREATE INDEX ix_filter_labs_pks ON filter_labs_pks USING btree (filter_id);

CREATE INDEX ix_filter_liver ON filter_liver USING btree (hashcode, map_acuity_instance_id);

CREATE INDEX ix_filter_liver_diag ON filter_liver_diag USING btree (hashcode, map_acuity_instance_id);

CREATE INDEX ix_filter_liver_diag_pks ON filter_liver_diag_pks USING btree (filter_id);

CREATE INDEX ix_filter_liver_pks ON filter_liver_pks USING btree (filter_id);

CREATE INDEX ix_filter_liver_risk ON filter_liver_risk USING btree (hashcode, map_acuity_instance_id);

CREATE INDEX ix_filter_liver_risk_pks ON filter_liver_risk_pks USING btree (filter_id);

CREATE INDEX ix_filter_lung ON filter_lungfunction USING btree (hashcode, map_acuity_instance_id);

CREATE INDEX ix_filter_lung_pks ON filter_lungfunction_pks USING btree (filter_id);

CREATE INDEX ix_filter_lvef ON filter_lvef USING btree (hashcode, map_acuity_instance_id);

CREATE INDEX ix_filter_lvef_pks ON filter_lvef_pks USING btree (filter_id);

CREATE INDEX ix_filter_mh ON filter_mh USING btree (hashcode, map_acuity_instance_id);

CREATE INDEX ix_filter_mh_pks ON filter_mh_pks USING btree (filter_id);

CREATE INDEX ix_filter_nicotine ON filter_nicotine USING btree (hashcode, map_acuity_instance_id);

CREATE INDEX ix_filter_nicotine_pks ON filter_nicotine_pks USING btree (filter_id);

CREATE INDEX ix_filter_pop ON filter_population USING btree (hashcode, map_acuity_instance_id);

CREATE INDEX ix_filter_pop_pks ON filter_population_pks USING btree (filter_id);

CREATE INDEX ix_filter_qtcf ON filter_qtcf USING btree (hashcode, map_acuity_instance_id);

CREATE INDEX ix_filter_qtcf_pks ON filter_qtcf_pks USING btree (filter_id);

CREATE INDEX ix_filter_recist ON filter_recist USING btree (hashcode, map_acuity_instance_id);

CREATE INDEX ix_filter_recist_pks ON filter_recist_pks USING btree (filter_id);

CREATE INDEX ix_filter_renal ON filter_renal USING btree (hashcode, map_acuity_instance_id);

CREATE INDEX ix_filter_renal_pks ON filter_renal_pks USING btree (filter_id);

CREATE INDEX ix_filter_sae ON filter_sae USING btree (hashcode, map_acuity_instance_id);

CREATE INDEX ix_filter_sae_pks ON filter_sae_pks USING btree (filter_id);

CREATE INDEX ix_filter_safety_pop ON filter_safety_population USING btree (hashcode, map_acuity_instance_id);

CREATE INDEX ix_filter_safety_pop_pks ON filter_safety_population_pks USING btree (filter_id);

CREATE INDEX ix_filter_sh ON filter_sh USING btree (hashcode, map_acuity_instance_id);

CREATE INDEX ix_filter_sh_pks ON filter_sh_pks USING btree (filter_id);

CREATE INDEX ix_filter_vitals ON filter_vitals USING btree (hashcode, map_acuity_instance_id);

CREATE INDEX ix_filter_vitals_pks ON filter_vitals_pks USING btree (filter_id);

CREATE UNIQUE INDEX msbd_uk_msr_drug ON map_study_baseline_drug USING btree (msbd_msr_id, msbd_drug_name);

CREATE INDEX mv_aes_inc_id ON mv_aes_inc USING btree (vaes_id);

CREATE INDEX mv_aes_inc_instance ON mv_aes_inc USING btree (vaes_instance);

CREATE INDEX mv_aes_inc_instance_aesid ON mv_aes_inc USING btree (vaes_instance, vaes_id);

CREATE INDEX mv_aes_inc_patid ON mv_aes_inc USING btree (vaes_pat_id);

CREATE INDEX mv_aes_instance ON mv_aes USING btree (vaes_instance);

CREATE INDEX mv_aes_instance_aesid ON mv_aes USING btree (vaes_instance, vaes_id);

CREATE INDEX mv_aes_labid ON mv_aes USING btree (vaes_id);

CREATE INDEX mv_aes_patid ON mv_aes USING btree (vaes_pat_id);

CREATE INDEX mv_labs_baselines_unq ON mv_labs_baselines USING btree (vlab_instance, vlab_pat_id, lower((vlab_labcode)::text));

CREATE INDEX mv_lngf_instance ON mv_lungfunc USING btree (vlng_instance);

CREATE INDEX mv_lngf_instance_lngid ON mv_lungfunc USING btree (vlng_instance, vlng_id);

CREATE INDEX mv_lngf_lngid ON mv_lungfunc USING btree (vlng_id);

CREATE INDEX mv_lngf_pat_id ON mv_lungfunc USING btree (vlng_pat_id);

CREATE INDEX mv_qtcf_instance ON mv_qtcf USING btree (vqtcf_instance);

CREATE INDEX mv_qtcf_instance_qtcfid ON mv_qtcf USING btree (vqtcf_instance, vqtcf_id);

CREATE INDEX mv_qtcf_pat_id ON mv_qtcf USING btree (vqtcf_pat_id);

CREATE INDEX mv_qtcf_qtcfid ON mv_qtcf USING btree (vqtcf_id);

CREATE INDEX plab_pat_id_plab_labcode ON precalc_labs USING btree (plab_pat_id, lower((plab_labcode)::text));

CREATE INDEX precalc_aes_dataset ON precalc_aes USING btree (paes_dataset);

CREATE INDEX precalc_aes_dataset_aesid ON precalc_aes USING btree (paes_dataset, paes_id);

CREATE INDEX precalc_aes_dataset_id ON precalc_aes USING btree (paes_dataset_id);

CREATE UNIQUE INDEX precalc_aes_id ON precalc_aes USING btree (paes_id);

CREATE INDEX precalc_aes_inc_dataset ON precalc_aes_inc USING btree (paes_dataset);

CREATE INDEX precalc_aes_inc_dataset_aesid ON precalc_aes_inc USING btree (paes_dataset, paes_id);

CREATE INDEX precalc_aes_inc_dataset_id ON precalc_aes_inc USING btree (paes_dataset_id);

CREATE UNIQUE INDEX precalc_aes_inc_id ON precalc_aes_inc USING btree (paes_id);

CREATE INDEX precalc_aes_inc_patid ON precalc_aes_inc USING btree (paes_pat_id);

CREATE INDEX precalc_aes_patid ON precalc_aes USING btree (paes_pat_id);

CREATE INDEX precalc_demo_dataset ON precalc_demo USING btree (pdm_dataset);

CREATE INDEX precalc_demo_dataset_id ON precalc_demo USING btree (pdm_dataset_id);

CREATE UNIQUE INDEX precalc_demo_pat_id ON precalc_demo USING btree (pdm_pat_id);

CREATE INDEX precalc_dose_dataset ON precalc_dose USING btree (pdos_dataset);

CREATE INDEX precalc_dose_dataset_id ON precalc_dose USING btree (pdos_dataset_id);

CREATE INDEX precalc_dose_drug ON precalc_dose USING btree (pdos_drug);

CREATE INDEX precalc_dose_pat_id ON precalc_dose USING btree (pdos_pat_id);

CREATE INDEX precalc_ecg_dataset ON precalc_ecg USING btree (pecg_dataset);

CREATE INDEX precalc_ecg_dataset_id ON precalc_ecg USING btree (pecg_dataset_id);

CREATE UNIQUE INDEX precalc_ecg_ecg_id ON precalc_ecg USING btree (pecg_ecg_id);

CREATE INDEX precalc_labs_dataset ON precalc_labs USING btree (plab_dataset);

CREATE INDEX precalc_labs_dataset_id ON precalc_labs USING btree (plab_dataset_id);

CREATE INDEX precalc_labs_dataset_labid ON precalc_labs USING btree (plab_dataset, plab_id);

CREATE UNIQUE INDEX precalc_labs_labid ON precalc_labs USING btree (plab_id);

CREATE INDEX precalc_labs_pat_id ON precalc_labs USING btree (plab_pat_id);

CREATE INDEX precalc_lungfunc_dataset ON precalc_lungfunc USING btree (plng_dataset);

CREATE INDEX precalc_lungfunc_dataset_id ON precalc_lungfunc USING btree (plng_dataset_id);

CREATE INDEX precalc_lungfunc_dataset_lngid ON precalc_lungfunc USING btree (plng_dataset, plng_id);

CREATE INDEX precalc_lungfunc_lngid ON precalc_lungfunc USING btree (plng_id);

CREATE INDEX precalc_lungfunc_pat_id ON precalc_lungfunc USING btree (plng_pat_id);

CREATE INDEX precalc_qtcf_dataset ON precalc_qtcf USING btree (pqtcf_dataset);

CREATE INDEX precalc_qtcf_dataset_id ON precalc_qtcf USING btree (pqtcf_dataset_id);

CREATE INDEX precalc_qtcf_dataset_qtcfid ON precalc_qtcf USING btree (pqtcf_dataset, pqtcf_id);

CREATE INDEX precalc_qtcf_pat_id ON precalc_qtcf USING btree (pqtcf_pat_id);

CREATE INDEX precalc_qtcf_qtcfid ON precalc_qtcf USING btree (pqtcf_id);

CREATE INDEX precalc_renal_dataset ON precalc_renal USING btree (pren_dataset);

CREATE INDEX precalc_renal_dataset_id ON precalc_renal USING btree (pren_dataset_id);

CREATE INDEX precalc_renal_dataset_lab_id ON precalc_renal USING btree (pren_dataset, pren_lab_id);

CREATE UNIQUE INDEX precalc_renal_lab_id ON precalc_renal USING btree (pren_lab_id);

CREATE INDEX precalc_renal_patid ON precalc_renal USING btree (pren_pat_id);

CREATE INDEX precalc_vitals_dataset ON precalc_vitals USING btree (pvit_dataset);

CREATE INDEX precalc_vitals_dataset_aesid ON precalc_vitals USING btree (pvit_dataset, pvit_id);

CREATE INDEX precalc_vitals_dataset_id ON precalc_vitals USING btree (pvit_dataset_id);

CREATE UNIQUE INDEX precalc_vitals_id ON precalc_vitals USING btree (pvit_id);

CREATE INDEX precalc_vitals_patid ON precalc_vitals USING btree (pvit_pat_id);

CREATE INDEX schema_version_s_idx ON schema_version USING btree (success);

CREATE INDEX sys_c00107669 ON result_ae USING btree (ae_evt_id);

CREATE INDEX sys_c00107670 ON result_ae USING btree (ae_pat_id);

CREATE INDEX sys_c00107680 ON result_ae_causality USING btree (aec_drug_id);

CREATE INDEX sys_c00107684 ON result_ae_action_taken USING btree (aeat_drug_id);

CREATE UNIQUE INDEX sys_c009623 ON map_description_file USING btree (mdf_mfr_id, mdf_mfd_id);

CREATE UNIQUE INDEX sys_c009626 ON map_spotfire_view USING btree (msv_view, msv_msm_id);

CREATE UNIQUE INDEX sys_c009645 ON map_subject_group_dosing USING btree (msgd_msga_id, msgd_drug);

CREATE UNIQUE INDEX sys_c009652 ON map_mapping_rule_field USING btree (mrf_mmr_id, mrf_mfi_id);

CREATE UNIQUE INDEX sys_c009673 ON result_ae_severity USING btree (aes_ae_id, aes_start_date);

CREATE UNIQUE INDEX sys_c009706 ON map_description_entity USING btree (mde_men_id, mde_mfd_id);

CREATE UNIQUE INDEX sys_c009726 ON map_subject_group_annotation USING btree (msga_grouping_id, msga_group_name);

CREATE UNIQUE INDEX sys_c009751 ON map_file_type USING btree (mft_name, mft_delimiter);

CREATE UNIQUE INDEX sys_c009762 ON map_field USING btree (mfi_men_id, mfi_name);

CREATE UNIQUE INDEX sys_c009763 ON map_field USING btree (mfi_men_id, mfi_mfid_id);

CREATE UNIQUE INDEX sys_c009805 ON result_drug USING btree (drug_std_id, drug_name);

CREATE UNIQUE INDEX sys_c009831 ON map_subject_grouping USING btree (msg_study_id, msg_grouping_name);

CREATE UNIQUE INDEX ua_d_decg_tst_id ON result_decg USING btree (decg_tst_id, decg_measurment_label);

CREATE UNIQUE INDEX uk_acl_object_identity ON acl_object_identity USING btree (object_id_class, object_id_identity);

CREATE UNIQUE INDEX uk_eg_tst_name_unit ON result_eg USING btree (eg_tst_id, eg_test_name, eg_result_unit);

CREATE UNIQUE INDEX uk_esm ON result_exa_severity_map USING btree (esm_std_id, esm_depot_gcs, esm_syscort_trt, esm_ics_trt, esm_antibiotics_trt, esm_hospit, esm_emer_trt);

CREATE UNIQUE INDEX uk_exa ON result_exacerbation USING btree (exa_pat_id, exa_exac_start_date);

CREATE UNIQUE INDEX uk_lng ON result_lungfunc USING btree (lng_pat_id, lng_assess_date, lng_prot_schedule, lng_measurement);

CREATE UNIQUE INDEX unique_acl_entry ON acl_entry USING btree (acl_object_identity, ace_order);

CREATE UNIQUE INDEX unique_acl_sid ON acl_sid USING btree (sid, principal);

CREATE UNIQUE INDEX unique_name_owner_ds ON saved_filter USING btree (name, owner, dataset_id, dataset_class);

CREATE UNIQUE INDEX uq_bji_job_inst ON batch_job_instance USING btree (job_name, job_key);

CREATE UNIQUE INDEX uq_cs_start_med_id_pat_id ON result_conmed_schedule USING btree (cms_start_date, cms_med_id, cms_pat_id, cms_ae_num);

CREATE UNIQUE INDEX uq_d_dth_pat_id ON result_death USING btree (dth_pat_id, dth_designation_of_cause);

CREATE UNIQUE INDEX uq_l_lab_code_tst_id_lab_value ON result_laboratory USING btree (lab_code, lab_tst_id, lab_value, lab_src_type);

CREATE UNIQUE INDEX uq_lg_lab_code_std_id ON result_lab_group USING btree (lgr_lab_code, lgr_std_id);

CREATE UNIQUE INDEX uq_m_name_parent_std_id ON result_medicine USING btree (med_drug_name, med_drug_parent, med_std_id);

CREATE UNIQUE INDEX uq_msgv_subj_grp ON map_subject_group_value_rule USING btree (msgv_group_id, msgv_subject_id);

CREATE UNIQUE INDEX uq_ntl_ldate_site_resp_pat_id ON result_recist_nontarget_lesion USING btree (rntl_pat_id, rntl_lesion_date, rntl_lesion_site, rntl_response);

CREATE UNIQUE INDEX uq_p_std_id_subj_part ON result_patient USING btree (pat_std_id, pat_subject, pat_part);

CREATE UNIQUE INDEX uq_pg_std_id_part_subj_grp ON result_patient_group USING btree (pgr_std_id, pgr_pat_part, pgr_pat_subject, pgr_grouping_name);

CREATE UNIQUE INDEX uq_pkc_unq ON result_pk_concentration USING btree (pkc_pat_id, pkc_analyte, pkc_treatment_cycle, pkc_treatment, pkc_treatment_schedule, pkc_specimen_id);

CREATE UNIQUE INDEX uq_ra_patid_asssate_nlsite ON result_recist_assessment USING btree (rca_pat_id, rca_visit_date, rca_visit);

CREATE UNIQUE INDEX uq_ruv_id_code_name_hash ON report_unique_violation USING btree (rcv_je_id, rcv_std_code, rcv_entity_name, rcv_entity_hash);

CREATE UNIQUE INDEX uq_s_prj_id_name ON result_study USING btree (std_prj_id, std_name);

CREATE UNIQUE INDEX uq_sae_pat_id_adv_ev_crit ON result_serious_adverse_event USING btree (sae_pat_id, sae_adverse_event, sae_crit_met_date);

CREATE UNIQUE INDEX uq_sc ON result_sc USING btree (sc_pat_id, sc_ethpop, sc_s_ethpop);

CREATE UNIQUE INDEX uq_spc_pat_id_spc_id ON result_specimen_collection USING btree (spc_pat_id, spc_specimen_id);

CREATE UNIQUE INDEX uq_std_soc_hlt_pt ON result_event_type USING btree (evt_std_id, evt_soc, evt_hlt, evt_pt);

CREATE UNIQUE INDEX uq_stp_unq ON result_stacked_pk_results USING btree (stp_pat_id, stp_analyte, stp_parameter, stp_treatment, stp_treatment_schedule, stp_treatment_cycle, stp_protocol_schedule, stp_visit_date, stp_visit_number, stp_protocol_schd_start_day, stp_protocol_schd_start_hour, stp_protocol_schd_start_minute, stp_protocol_schedule_end, stp_protocol_schd_end_day, stp_protocol_schd_end_hour, stp_protocol_schd_end_minute, stp_visit, stp_actual_dose);

CREATE UNIQUE INDEX uq_t_date_pat_id ON result_test USING btree (tst_date, tst_pat_id);

CREATE UNIQUE INDEX uq_tmdd_name_ipc_pat_id ON result_target_med_dos_disc USING btree (dsc_drug_name, dsc_ipdc_date, dsc_pat_id);

CREATE UNIQUE INDEX uq_tmds_start_drug_pat_id ON result_trg_med_dos_schedule USING btree (mds_start_date, mds_drug, mds_pat_id);

CREATE UNIQUE INDEX uq_v_date_pat_id ON result_visit USING btree (vis_date, vis_pat_id);

CREATE UNIQUE INDEX uq_vitald_tst_id_category ON result_vitals USING btree (vit_tst_id, vit_test_name, vit_unit, vit_anatomical_location, vit_anatomical_side_interest, vit_physical_position);
