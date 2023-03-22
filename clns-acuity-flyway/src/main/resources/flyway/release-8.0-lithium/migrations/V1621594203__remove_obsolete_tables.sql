-- remove DUAL table (emulated Oracle DUAL)
DROP TABLE DUAL CASCADE;

-- remove filters tables (no more DB-based filter caching)
DROP TABLE filter_aes CASCADE;
DROP TABLE filter_aes_pks CASCADE;
DROP TABLE filter_alcohol CASCADE;
DROP TABLE filter_alcohol_pks CASCADE;
DROP TABLE filter_conmeds CASCADE;
DROP TABLE filter_conmeds_pks CASCADE;
DROP TABLE filter_death CASCADE;
DROP TABLE filter_death_pks CASCADE;
DROP TABLE filter_dose CASCADE;
DROP TABLE filter_dose_pks CASCADE;
DROP TABLE filter_dosedisc CASCADE;
DROP TABLE filter_dosedisc_pks CASCADE;
DROP TABLE filter_exacerbation CASCADE;
DROP TABLE filter_exacerbation_pks CASCADE;
DROP TABLE filter_hce CASCADE;
DROP TABLE filter_hce_pks CASCADE;
DROP TABLE filter_labs CASCADE;
DROP TABLE filter_labs_pks CASCADE;
DROP TABLE filter_liver CASCADE;
DROP TABLE filter_liver_diag CASCADE;
DROP TABLE filter_liver_diag_pks CASCADE;
DROP TABLE filter_liver_pks CASCADE;
DROP TABLE filter_liver_risk CASCADE;
DROP TABLE filter_liver_risk_pks CASCADE;
DROP TABLE filter_lungfunction CASCADE;
DROP TABLE filter_lungfunction_pks CASCADE;
DROP TABLE filter_lvef CASCADE;
DROP TABLE filter_lvef_pks CASCADE;
DROP TABLE filter_mh CASCADE;
DROP TABLE filter_mh_pks CASCADE;
DROP TABLE filter_nicotine CASCADE;
DROP TABLE filter_nicotine_pks CASCADE;
DROP TABLE filter_population CASCADE;
DROP TABLE filter_population_pks CASCADE;
DROP TABLE filter_qtcf CASCADE;
DROP TABLE filter_qtcf_pks CASCADE;
DROP TABLE filter_recist CASCADE;
DROP TABLE filter_recist_pks CASCADE;
DROP TABLE filter_renal CASCADE;
DROP TABLE filter_renal_pks CASCADE;
DROP TABLE filter_sae CASCADE;
DROP TABLE filter_sae_pks CASCADE;
DROP TABLE filter_safety_population CASCADE;
DROP TABLE filter_safety_population_pks CASCADE;
DROP TABLE filter_sh CASCADE;
DROP TABLE filter_sh_pks CASCADE;
DROP TABLE filter_vitals CASCADE;
DROP TABLE filter_vitals_pks CASCADE;

DROP SEQUENCE filter_aes_seq CASCADE;
DROP SEQUENCE filter_alcohol_seq CASCADE;
DROP SEQUENCE filter_conmeds_seq CASCADE;
DROP SEQUENCE filter_death_seq CASCADE;
DROP SEQUENCE filter_dose_seq CASCADE;
DROP SEQUENCE filter_dosedisc_seq CASCADE;
DROP SEQUENCE filter_exab_seq CASCADE;
DROP SEQUENCE filter_hce_seq CASCADE;
DROP SEQUENCE filter_labs_seq CASCADE;
DROP SEQUENCE filter_liver_seq CASCADE;
DROP SEQUENCE filter_liver_diag_seq CASCADE;
DROP SEQUENCE filter_liver_risk_seq CASCADE;
DROP SEQUENCE filter_lung_seq CASCADE;
DROP SEQUENCE filter_lvef_seq CASCADE;
DROP SEQUENCE filter_mh_seq CASCADE;
DROP SEQUENCE filter_nicotine_seq CASCADE;
DROP SEQUENCE filter_pop_seq CASCADE;
DROP SEQUENCE filter_qtcf_seq CASCADE;
DROP SEQUENCE filter_recist_seq CASCADE;
DROP SEQUENCE filter_renal_seq CASCADE;
DROP SEQUENCE filter_sae_seq CASCADE;
DROP SEQUENCE filter_safety_pop_seq CASCADE;
DROP SEQUENCE filter_sh_seq CASCADE;
DROP SEQUENCE filter_vitals_seq CASCADE;

-- remove a table kept by mistake
DROP TABLE map_acuity_instance_bak CASCADE;

-- remove instance-related tables (instances functionality is no longer used)
DROP TABLE map_acuity_instance CASCADE;
DROP TABLE map_rct_inst_dshbrd_col_opt CASCADE;
DROP TABLE map_rct_inst_dshbrd_row_opt CASCADE;
DROP TABLE map_rct_instance_ae_group CASCADE;
DROP TABLE map_rct_instance_lab_group CASCADE;
DROP TABLE map_rct_instance_sbj_group CASCADE;
DROP TABLE map_rct_instance_sbj_grouping CASCADE;
DROP TABLE map_rct_instance_std_rule CASCADE;
DROP TABLE map_rct_instance_time_intrvl CASCADE;
DROP TABLE map_time_intervals CASCADE;

DROP SEQUENCE seq_rct_instance_time_intrvl;

-- remove precalculation tables (no more precalculation finally - all preparation is done by VAHub and cached) Java code
DROP TABLE precalc_aes CASCADE;
DROP TABLE precalc_aes_inc CASCADE;
DROP TABLE precalc_demo CASCADE;
DROP TABLE precalc_dose CASCADE;
DROP TABLE precalc_ecg CASCADE;
DROP TABLE precalc_labs CASCADE;
DROP TABLE precalc_lungfunc CASCADE;
DROP TABLE precalc_qtcf CASCADE;
DROP TABLE precalc_recist CASCADE;
DROP TABLE precalc_renal CASCADE;
DROP TABLE precalc_vitals CASCADE;

-- remove report tables (report themselves were removed in 2014, so tables can be removed too)

DROP FUNCTION acuity_utils.clean_reports CASCADE;
DROP FUNCTION acuity_utils.clean_all_reports CASCADE;

DROP TABLE report_fk_violation CASCADE;
DROP TABLE report_missed_cntlin CASCADE;
DROP TABLE report_unique_violation CASCADE;
DROP TABLE report_unparsed_data CASCADE;

-- remove old snapshot tables not used in the ACUITY work process
DROP TABLE mv_aes CASCADE;
DROP TABLE mv_aes_inc CASCADE;
DROP TABLE mv_labs CASCADE;
DROP TABLE mv_labs_baselines CASCADE;
DROP TABLE mv_lungfunc CASCADE;
DROP TABLE mv_qtcf CASCADE;

-- remove unused user activity table
DROP TABLE user_activity CASCADE;
DROP SEQUENCE ua_seq CASCADE;
DROP FUNCTION trigger_fct_ua_tr CASCADE;
