CREATE TABLE acl_class (
    id numeric(38,0) NOT NULL,
    class character varying(100) NOT NULL,
    acl_remote_id numeric(38,0)
);

CREATE SEQUENCE acl_class_sequence
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE;

CREATE TABLE acl_entry (
    id numeric(38,0) NOT NULL,
    acl_object_identity numeric(38,0) NOT NULL,
    ace_order numeric(38,0) NOT NULL,
    sid numeric(38,0) NOT NULL,
    mask numeric(38,0) NOT NULL,
    granting boolean NOT NULL,
    audit_success boolean NOT NULL,
    audit_failure boolean NOT NULL
);

CREATE SEQUENCE acl_entry_sequence
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE;

CREATE TABLE acl_object (
    id numeric(38,0) NOT NULL,
    name character varying(255) NOT NULL,
    acl_object_identity_id numeric(38,0),
    module_type character varying(100),
    default_vis character varying(100),
    parent_drug_programme character varying(500),
    lockdown boolean DEFAULT false NOT NULL,
    inherited boolean DEFAULT true NOT NULL,
    code character varying(256),
    parent_clinical_study_code character varying(256),
    acl_type character varying(256),
    parent_clinical_study character varying(500)
);

CREATE TABLE acl_object_identity (
    id numeric(38,0) NOT NULL,
    object_id_class numeric(38,0) NOT NULL,
    object_id_identity numeric(38,0) NOT NULL,
    parent_object numeric(38,0),
    owner_sid numeric(38,0),
    entries_inheriting boolean NOT NULL
);

CREATE SEQUENCE acl_object_identity_sequence
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE;

CREATE SEQUENCE acl_object_sequence
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE;

CREATE TABLE acl_remote (
    id numeric(38,0) NOT NULL,
    name character varying(500) NOT NULL,
    base_url character varying(500) NOT NULL,
    enabled boolean NOT NULL
);

CREATE SEQUENCE acl_remote_identity_sequence
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE;

CREATE TABLE acl_sid (
    id numeric(38,0) NOT NULL,
    principal boolean NOT NULL,
    sid character varying(100) NOT NULL
);

CREATE SEQUENCE acl_sid_sequence
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE;

CREATE TABLE authorities (
    username character varying(256) NOT NULL,
    authority character varying(256) NOT NULL
);

CREATE TABLE batch_job_execution (
    job_execution_id bigint NOT NULL,
    version bigint,
    job_instance_id bigint NOT NULL,
    create_time timestamp without time zone NOT NULL,
    start_time timestamp without time zone,
    end_time timestamp without time zone,
    status character varying(10),
    exit_code character varying(2500),
    exit_message character varying(2500),
    last_updated timestamp without time zone,
    job_configuration_location character varying(2500),
    clean_status character varying(10)
);

CREATE TABLE batch_job_execution_context (
    job_execution_id bigint NOT NULL,
    short_context character varying(2500) NOT NULL,
    serialized_context text
);

CREATE TABLE batch_job_execution_params (
    job_execution_id bigint NOT NULL,
    type_cd character varying(6) NOT NULL,
    key_name character varying(100) NOT NULL,
    string_val character varying(250),
    date_val timestamp without time zone,
    long_val bigint,
    double_val numeric,
    identifying character(1) NOT NULL
);

CREATE SEQUENCE batch_job_execution_seq
    START WITH 1
    INCREMENT BY 1
    MINVALUE 0
    NO MAXVALUE;

CREATE TABLE batch_job_instance (
    job_instance_id bigint NOT NULL,
    version bigint,
    job_name character varying(100) NOT NULL,
    job_key character varying(32) NOT NULL
);

CREATE TABLE batch_job_params (
    job_instance_id bigint NOT NULL,
    type_cd character varying(6) NOT NULL,
    key_name character varying(100) NOT NULL,
    string_val character varying(250),
    date_val timestamp without time zone,
    long_val bigint,
    double_val numeric
);

CREATE SEQUENCE batch_job_seq
    START WITH 1
    INCREMENT BY 1
    MINVALUE 0
    NO MAXVALUE;

CREATE TABLE batch_step_execution (
    step_execution_id bigint NOT NULL,
    version bigint NOT NULL,
    step_name character varying(100) NOT NULL,
    job_execution_id bigint NOT NULL,
    start_time timestamp without time zone NOT NULL,
    end_time timestamp without time zone,
    status character varying(10),
    commit_count bigint,
    read_count bigint,
    filter_count bigint,
    write_count bigint,
    read_skip_count bigint,
    write_skip_count bigint,
    process_skip_count bigint,
    rollback_count bigint,
    exit_code character varying(2500),
    exit_message character varying(3000),
    last_updated timestamp without time zone
);

CREATE TABLE batch_step_execution_context (
    step_execution_id bigint NOT NULL,
    short_context character varying(2500) NOT NULL,
    serialized_context text
);

CREATE SEQUENCE batch_step_execution_seq
    START WITH 1
    INCREMENT BY 1
    MINVALUE 0
    NO MAXVALUE;

CREATE SEQUENCE cll_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE;

CREATE TABLE dual (
    code character(5) NOT NULL
);

CREATE UNLOGGED TABLE filter_aes (
    filter_id numeric(38,0) NOT NULL,
    map_acuity_instance_id numeric(38,0) NOT NULL,
    hashcode character varying(300) NOT NULL,
    data bytea,
    last_access_time timestamp without time zone NOT NULL,
    hit_count numeric(38,0) NOT NULL,
    matched_items_count bigint,
    inverse smallint DEFAULT 0 NOT NULL,
    va_security_ids character varying(300) DEFAULT '-1'::character varying NOT NULL,
    use_filters smallint DEFAULT 1 NOT NULL,
    inserted_items_count numeric(38,0) DEFAULT '-1'::integer NOT NULL
);

CREATE TABLE filter_aes_pks (
    foreign_pk character varying(32) NOT NULL,
    filter_id numeric(38,0) NOT NULL
);

CREATE SEQUENCE filter_aes_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE;

CREATE TABLE filter_alcohol (
    filter_id numeric(38,0) NOT NULL,
    map_acuity_instance_id numeric(38,0) NOT NULL,
    hashcode character varying(300) NOT NULL,
    data bytea,
    last_access_time timestamp without time zone NOT NULL,
    hit_count numeric(38,0) NOT NULL,
    matched_items_count bigint,
    inverse smallint DEFAULT 0 NOT NULL,
    va_security_ids character varying(300) DEFAULT '-1'::character varying NOT NULL,
    use_filters smallint DEFAULT 1 NOT NULL,
    inserted_items_count numeric(38,0) DEFAULT '-1'::numeric NOT NULL
);

CREATE TABLE filter_alcohol_pks (
    foreign_pk character varying(32) NOT NULL,
    filter_id numeric(38,0) NOT NULL
);

CREATE SEQUENCE filter_alcohol_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE;

CREATE UNLOGGED TABLE filter_conmeds (
    filter_id numeric(38,0) NOT NULL,
    map_acuity_instance_id numeric(38,0) NOT NULL,
    hashcode character varying(300) NOT NULL,
    data bytea,
    last_access_time timestamp without time zone NOT NULL,
    hit_count numeric(38,0) NOT NULL,
    matched_items_count bigint,
    inverse smallint DEFAULT 0 NOT NULL,
    va_security_ids character varying(300) DEFAULT '-1'::character varying NOT NULL,
    use_filters smallint DEFAULT 1 NOT NULL,
    inserted_items_count numeric(38,0) DEFAULT '-1'::integer NOT NULL
);

CREATE TABLE filter_conmeds_pks (
    foreign_pk character varying(32) NOT NULL,
    filter_id numeric(38,0) NOT NULL
);

CREATE SEQUENCE filter_conmeds_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE;

CREATE TABLE filter_death (
    filter_id numeric(38,0) NOT NULL,
    map_acuity_instance_id numeric(38,0) NOT NULL,
    hashcode character varying(300) NOT NULL,
    data bytea,
    last_access_time timestamp without time zone NOT NULL,
    hit_count numeric(38,0) NOT NULL,
    matched_items_count numeric(38,0),
    inverse smallint DEFAULT 0 NOT NULL,
    va_security_ids character varying(300) DEFAULT '-1'::character varying NOT NULL,
    use_filters smallint DEFAULT 1 NOT NULL,
    inserted_items_count numeric(38,0) DEFAULT '-1'::numeric NOT NULL
);

CREATE TABLE filter_death_pks (
    foreign_pk character varying(32) NOT NULL,
    filter_id numeric(38,0) NOT NULL
);

CREATE SEQUENCE filter_death_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE;

CREATE TABLE filter_dose (
    filter_id numeric(38,0) NOT NULL,
    map_acuity_instance_id numeric(38,0) NOT NULL,
    hashcode character varying(300) NOT NULL,
    last_access_time timestamp without time zone NOT NULL,
    hit_count numeric(38,0) NOT NULL,
    matched_items_count numeric(38,0),
    inverse smallint DEFAULT 0 NOT NULL,
    va_security_ids character varying(300) DEFAULT '-1'::character varying NOT NULL,
    use_filters smallint DEFAULT 1 NOT NULL,
    inserted_items_count numeric(38,0) DEFAULT '-1'::integer NOT NULL
);

CREATE TABLE filter_dose_pks (
    foreign_pk character varying(32) NOT NULL,
    filter_id numeric(38,0) NOT NULL
);

CREATE SEQUENCE filter_dose_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE;

CREATE TABLE filter_dosedisc (
    filter_id numeric(38,0) NOT NULL,
    map_acuity_instance_id numeric(38,0) NOT NULL,
    hashcode character varying(300) NOT NULL,
    data bytea,
    last_access_time timestamp without time zone NOT NULL,
    hit_count numeric(38,0) NOT NULL,
    matched_items_count bigint,
    inverse smallint DEFAULT 0 NOT NULL,
    va_security_ids character varying(300) DEFAULT '-1'::character varying NOT NULL,
    use_filters smallint DEFAULT 1 NOT NULL,
    inserted_items_count numeric(38,0) DEFAULT '-1'::numeric NOT NULL
);

CREATE TABLE filter_dosedisc_pks (
    foreign_pk character varying(32) NOT NULL,
    filter_id numeric(38,0) NOT NULL
);

CREATE SEQUENCE filter_dosedisc_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE;

CREATE SEQUENCE filter_exab_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE;

CREATE TABLE filter_exacerbation (
    filter_id numeric(38,0) NOT NULL,
    map_acuity_instance_id numeric(38,0) NOT NULL,
    hashcode character varying(300) NOT NULL,
    last_access_time timestamp without time zone NOT NULL,
    hit_count numeric(38,0) NOT NULL,
    matched_items_count numeric(38,0),
    inverse smallint DEFAULT 0 NOT NULL,
    va_security_ids character varying(300) DEFAULT '-1'::character varying NOT NULL,
    use_filters smallint DEFAULT 1 NOT NULL,
    inserted_items_count numeric(38,0) DEFAULT '-1'::integer NOT NULL
);

CREATE TABLE filter_exacerbation_pks (
    foreign_pk character varying(32) NOT NULL,
    filter_id numeric(38,0) NOT NULL
);

CREATE TABLE filter_hce (
    filter_id numeric(38,0) NOT NULL,
    map_acuity_instance_id numeric(38,0) NOT NULL,
    hashcode character varying(300) NOT NULL,
    last_access_time timestamp without time zone NOT NULL,
    hit_count numeric(38,0) NOT NULL,
    matched_items_count numeric(38,0),
    inverse smallint DEFAULT 0 NOT NULL,
    va_security_ids character varying(300) DEFAULT '-1'::character varying NOT NULL,
    use_filters smallint DEFAULT 1 NOT NULL,
    inserted_items_count numeric(38,0) DEFAULT '-1'::integer NOT NULL
);

CREATE TABLE filter_hce_pks (
    foreign_pk character varying(32) NOT NULL,
    filter_id numeric(38,0) NOT NULL
);

CREATE SEQUENCE filter_hce_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE;

CREATE UNLOGGED TABLE filter_labs (
    filter_id numeric(38,0) NOT NULL,
    map_acuity_instance_id numeric(38,0) NOT NULL,
    hashcode character varying(300) NOT NULL,
    last_access_time timestamp without time zone NOT NULL,
    hit_count numeric(38,0) NOT NULL,
    matched_items_count bigint,
    inverse smallint DEFAULT 0 NOT NULL,
    va_security_ids character varying(300) DEFAULT '-1'::character varying NOT NULL,
    use_filters smallint DEFAULT 1 NOT NULL,
    inserted_items_count numeric(38,0) DEFAULT '-1'::integer NOT NULL
);

CREATE TABLE filter_labs_pks (
    foreign_pk character varying(32) NOT NULL,
    filter_id numeric(38,0) NOT NULL
);

CREATE SEQUENCE filter_labs_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE;

CREATE UNLOGGED TABLE filter_liver (
    filter_id numeric(38,0) NOT NULL,
    map_acuity_instance_id numeric(38,0) NOT NULL,
    hashcode character varying(300) NOT NULL,
    data bytea,
    last_access_time timestamp without time zone NOT NULL,
    hit_count numeric(38,0) NOT NULL,
    matched_items_count bigint,
    inverse smallint DEFAULT 0 NOT NULL,
    va_security_ids character varying(300) DEFAULT '-1'::character varying NOT NULL,
    use_filters smallint DEFAULT 1 NOT NULL,
    inserted_items_count numeric(38,0) DEFAULT '-1'::integer NOT NULL
);

CREATE TABLE filter_liver_diag (
    filter_id numeric(38,0) NOT NULL,
    map_acuity_instance_id numeric(38,0) NOT NULL,
    hashcode character varying(300) NOT NULL,
    data bytea,
    last_access_time timestamp without time zone NOT NULL,
    hit_count numeric(38,0) NOT NULL,
    matched_items_count bigint,
    inverse smallint DEFAULT 0 NOT NULL,
    va_security_ids character varying(300) DEFAULT '-1'::character varying NOT NULL,
    use_filters smallint DEFAULT 1 NOT NULL,
    inserted_items_count numeric(38,0) DEFAULT '-1'::integer NOT NULL
);

CREATE TABLE filter_liver_diag_pks (
    foreign_pk character varying(32) NOT NULL,
    filter_id numeric(38,0) NOT NULL
);

CREATE SEQUENCE filter_liver_diag_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE;

CREATE TABLE filter_liver_pks (
    foreign_pk character varying(32) NOT NULL,
    filter_id numeric(38,0) NOT NULL
);

CREATE TABLE filter_liver_risk (
    filter_id numeric(38,0) NOT NULL,
    map_acuity_instance_id numeric(38,0) NOT NULL,
    hashcode character varying(300) NOT NULL,
    data bytea,
    last_access_time timestamp without time zone NOT NULL,
    hit_count numeric(38,0) NOT NULL,
    matched_items_count bigint,
    inverse smallint DEFAULT 0 NOT NULL,
    va_security_ids character varying(300) DEFAULT '-1'::character varying NOT NULL,
    use_filters smallint DEFAULT 1 NOT NULL,
    inserted_items_count numeric(38,0) DEFAULT '-1'::integer NOT NULL
);

CREATE TABLE filter_liver_risk_pks (
    foreign_pk character varying(32) NOT NULL,
    filter_id numeric(38,0) NOT NULL
);

CREATE SEQUENCE filter_liver_risk_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE;

CREATE SEQUENCE filter_liver_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE;

CREATE SEQUENCE filter_lung_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE;

CREATE TABLE filter_lungfunction (
    filter_id numeric(38,0) NOT NULL,
    map_acuity_instance_id numeric(38,0) NOT NULL,
    hashcode character varying(300) NOT NULL,
    last_access_time timestamp without time zone NOT NULL,
    hit_count numeric(38,0) NOT NULL,
    matched_items_count numeric(38,0),
    inverse smallint DEFAULT 0 NOT NULL,
    va_security_ids character varying(300) DEFAULT '-1'::character varying NOT NULL,
    use_filters smallint DEFAULT 1 NOT NULL,
    inserted_items_count numeric(38,0) DEFAULT '-1'::integer NOT NULL
);

CREATE TABLE filter_lungfunction_pks (
    foreign_pk character varying(32) NOT NULL,
    filter_id numeric(38,0) NOT NULL
);

CREATE UNLOGGED TABLE filter_lvef (
    filter_id numeric(38,0) NOT NULL,
    map_acuity_instance_id numeric(38,0) NOT NULL,
    hashcode character varying(300) NOT NULL,
    data bytea,
    last_access_time timestamp without time zone NOT NULL,
    hit_count numeric(38,0) NOT NULL,
    matched_items_count bigint,
    inverse smallint DEFAULT 0 NOT NULL
);

CREATE TABLE filter_lvef_pks (
    foreign_pk character varying(32) NOT NULL,
    filter_id numeric(38,0) NOT NULL
);

CREATE SEQUENCE filter_lvef_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE;

CREATE TABLE filter_mh (
    filter_id numeric(38,0) NOT NULL,
    map_acuity_instance_id numeric(38,0) NOT NULL,
    hashcode character varying(300) NOT NULL,
    data bytea,
    last_access_time timestamp without time zone NOT NULL,
    hit_count numeric(38,0) NOT NULL,
    matched_items_count bigint,
    inverse smallint DEFAULT 0 NOT NULL,
    va_security_ids character varying(300) DEFAULT '-1'::character varying NOT NULL,
    use_filters smallint DEFAULT 1 NOT NULL,
    inserted_items_count numeric(38,0) DEFAULT '-1'::numeric NOT NULL
);

CREATE TABLE filter_mh_pks (
    foreign_pk character varying(32) NOT NULL,
    filter_id numeric(38,0) NOT NULL
);

CREATE SEQUENCE filter_mh_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE;

CREATE TABLE filter_nicotine (
    filter_id numeric(38,0) NOT NULL,
    map_acuity_instance_id numeric(38,0) NOT NULL,
    hashcode character varying(300) NOT NULL,
    data bytea,
    last_access_time timestamp without time zone NOT NULL,
    hit_count numeric(38,0) NOT NULL,
    matched_items_count bigint,
    inverse smallint DEFAULT 0 NOT NULL,
    va_security_ids character varying(300) DEFAULT '-1'::character varying NOT NULL,
    use_filters smallint DEFAULT 1 NOT NULL,
    inserted_items_count numeric(38,0) DEFAULT '-1'::numeric NOT NULL
);

CREATE TABLE filter_nicotine_pks (
    foreign_pk character varying(32) NOT NULL,
    filter_id numeric(38,0) NOT NULL
);

CREATE SEQUENCE filter_nicotine_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE;

CREATE SEQUENCE filter_pop_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE;

CREATE TABLE filter_population (
    filter_id numeric(38,0) NOT NULL,
    map_acuity_instance_id numeric(38,0) NOT NULL,
    hashcode character varying(300) NOT NULL,
    data bytea,
    last_access_time timestamp without time zone NOT NULL,
    hit_count numeric(38,0) NOT NULL,
    matched_items_count bigint,
    inverse smallint DEFAULT 0 NOT NULL,
    va_security_ids character varying(300) DEFAULT '-1'::character varying NOT NULL,
    use_filters smallint DEFAULT 1 NOT NULL,
    inserted_items_count numeric(38,0) DEFAULT '-1'::integer NOT NULL
);

CREATE TABLE filter_population_pks (
    foreign_pk character varying(32) NOT NULL,
    filter_id numeric(38,0) NOT NULL
);

CREATE UNLOGGED TABLE filter_qtcf (
    filter_id numeric(38,0) NOT NULL,
    map_acuity_instance_id numeric(38,0) NOT NULL,
    hashcode character varying(300) NOT NULL,
    data bytea,
    last_access_time timestamp without time zone NOT NULL,
    hit_count numeric(38,0) NOT NULL,
    matched_items_count bigint,
    inverse smallint DEFAULT 0 NOT NULL,
    va_security_ids character varying(300) DEFAULT '-1'::character varying NOT NULL,
    use_filters smallint DEFAULT 1 NOT NULL,
    inserted_items_count numeric(38,0) DEFAULT '-1'::integer NOT NULL
);

CREATE TABLE filter_qtcf_pks (
    foreign_pk character varying(32) NOT NULL,
    filter_id numeric(38,0) NOT NULL
);

CREATE SEQUENCE filter_qtcf_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE;

CREATE TABLE filter_recist (
    filter_id numeric(38,0) NOT NULL,
    map_acuity_instance_id numeric(38,0) NOT NULL,
    hashcode character varying(300) NOT NULL,
    data bytea,
    last_access_time timestamp without time zone NOT NULL,
    hit_count numeric(38,0) NOT NULL,
    matched_items_count bigint,
    inverse smallint DEFAULT 0 NOT NULL,
    va_security_ids character varying(300) DEFAULT '-1'::character varying NOT NULL,
    use_filters smallint DEFAULT 1 NOT NULL,
    inserted_items_count numeric(38,0) DEFAULT '-1'::integer NOT NULL
);

CREATE TABLE filter_recist_pks (
    foreign_pk character varying(32) NOT NULL,
    filter_id numeric(38,0) NOT NULL
);

CREATE SEQUENCE filter_recist_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE;

CREATE UNLOGGED TABLE filter_renal (
    filter_id numeric(38,0) NOT NULL,
    map_acuity_instance_id numeric(38,0) NOT NULL,
    hashcode character varying(300) NOT NULL,
    data bytea,
    last_access_time timestamp without time zone NOT NULL,
    hit_count numeric(38,0) NOT NULL,
    matched_items_count bigint,
    inverse smallint DEFAULT 0 NOT NULL,
    va_security_ids character varying(300) DEFAULT '-1'::character varying NOT NULL,
    use_filters smallint DEFAULT 1 NOT NULL,
    inserted_items_count numeric(38,0) DEFAULT '-1'::integer NOT NULL
);

CREATE TABLE filter_renal_pks (
    foreign_pk character varying(32) NOT NULL,
    filter_id numeric(38,0) NOT NULL,
    clearance_direct_ckd numeric,
    clearance_cg numeric,
    clearance_cg_ckd numeric,
    clearance_egfr numeric,
    clearance_egfr_ckd numeric
);

CREATE SEQUENCE filter_renal_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE;

CREATE TABLE filter_sae (
    filter_id numeric(38,0) NOT NULL,
    map_acuity_instance_id numeric(38,0) NOT NULL,
    hashcode character varying(300) NOT NULL,
    data bytea,
    last_access_time timestamp without time zone NOT NULL,
    hit_count numeric(38,0) NOT NULL,
    matched_items_count bigint,
    inverse smallint DEFAULT 0 NOT NULL,
    va_security_ids character varying(300) DEFAULT '-1'::character varying NOT NULL,
    use_filters smallint DEFAULT 1 NOT NULL,
    inserted_items_count numeric(38,0) DEFAULT '-1'::integer NOT NULL
);

CREATE TABLE filter_sae_pks (
    foreign_pk character varying(32) NOT NULL,
    filter_id numeric(38,0) NOT NULL
);

CREATE SEQUENCE filter_sae_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE;

CREATE SEQUENCE filter_safety_pop_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE;

CREATE UNLOGGED TABLE filter_safety_population (
    filter_id numeric(38,0) NOT NULL,
    map_acuity_instance_id numeric(38,0) NOT NULL,
    hashcode character varying(300) NOT NULL,
    data bytea,
    last_access_time timestamp without time zone NOT NULL,
    hit_count numeric(38,0) NOT NULL,
    matched_items_count bigint,
    inverse smallint DEFAULT 0 NOT NULL
);

CREATE TABLE filter_safety_population_pks (
    foreign_pk character varying(32) NOT NULL,
    filter_id numeric(38,0) NOT NULL
);

CREATE TABLE filter_sh (
    filter_id numeric(38,0) NOT NULL,
    map_acuity_instance_id numeric(38,0) NOT NULL,
    hashcode character varying(300) NOT NULL,
    data bytea,
    last_access_time timestamp without time zone NOT NULL,
    hit_count numeric(38,0) NOT NULL,
    matched_items_count bigint,
    inverse smallint DEFAULT 0 NOT NULL,
    va_security_ids character varying(300) DEFAULT '-1'::character varying NOT NULL,
    use_filters smallint DEFAULT 1 NOT NULL,
    inserted_items_count numeric(38,0) DEFAULT '-1'::numeric NOT NULL
);

CREATE TABLE filter_sh_pks (
    foreign_pk character varying(32) NOT NULL,
    filter_id numeric(38,0) NOT NULL
);

CREATE SEQUENCE filter_sh_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE;

CREATE UNLOGGED TABLE filter_vitals (
    filter_id numeric(38,0) NOT NULL,
    map_acuity_instance_id numeric(38,0) NOT NULL,
    hashcode character varying(300) NOT NULL,
    data bytea,
    last_access_time timestamp without time zone NOT NULL,
    hit_count numeric(38,0) NOT NULL,
    matched_items_count bigint,
    inverse smallint DEFAULT 0 NOT NULL,
    va_security_ids character varying(300) DEFAULT '-1'::character varying NOT NULL,
    use_filters smallint DEFAULT 1 NOT NULL,
    inserted_items_count numeric(38,0) DEFAULT '-1'::integer NOT NULL
);

CREATE TABLE filter_vitals_pks (
    foreign_pk character varying(32) NOT NULL,
    filter_id numeric(38,0) NOT NULL
);

CREATE SEQUENCE filter_vitals_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE;

CREATE TABLE group_authorities (
    group_id numeric(38,0) NOT NULL,
    authority character varying(50) NOT NULL
);

CREATE TABLE group_members (
    id numeric(38,0) NOT NULL,
    username character varying(50) NOT NULL,
    group_id numeric(38,0) NOT NULL
);

CREATE SEQUENCE group_members_sequence
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE;

CREATE TABLE groups (
    id numeric(38,0) NOT NULL,
    group_name character varying(256) NOT NULL
);

CREATE SEQUENCE groups_sequence
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE;

CREATE SEQUENCE hibernate_sequence
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE;

CREATE TABLE log_arg (
    log_arg_id bigint NOT NULL,
    log_arg_name character varying(255) NOT NULL,
    log_arg_string_value character varying(255),
    log_arg_date_value timestamp without time zone,
    log_arg_long_value bigint,
    log_arg_float_value numeric(19,4),
    is_log_returned_error smallint DEFAULT 0 NOT NULL,
    log_operation_id bigint NOT NULL
);

CREATE SEQUENCE log_arg_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE;

CREATE TABLE log_operation (
    log_operation_id bigint NOT NULL,
    datecreated timestamp without time zone,
    log_name character varying(255),
    owner character varying(255) NOT NULL,
    package_and_method_name character varying(255) NOT NULL,
    session_id character varying(50) DEFAULT 'NONE'::character varying NOT NULL,
    object_identity_classname character varying(255),
    object_identity_id numeric(22,0)
);

CREATE SEQUENCE log_operation_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE;

CREATE SEQUENCE magr_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE;

CREATE SEQUENCE magv_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE;

CREATE TABLE map_ae_group_rule (
    magr_id numeric(20,0) NOT NULL,
    magr_time character varying(100),
    magr_default_value character varying(100),
    magr_name character varying(100),
    magr_data_source character varying(255),
    magr_enabled smallint,
    magr_project_id numeric(20,0) NOT NULL
);

CREATE TABLE map_ae_group_value_rule (
    magv_id numeric(20,0) NOT NULL,
    magv_group_id numeric(20,0),
    magv_name character varying(100),
    magv_pt character varying(100)
);

CREATE TABLE map_aggr_fun (
    maf_id numeric(20,0) NOT NULL,
    maf_name character varying(255) NOT NULL,
    maf_description character varying(1024) NOT NULL,
    maf_helper character varying(255),
    maf_result_type character varying(255) NOT NULL
);

CREATE TABLE map_audit (
    mau_id bigint NOT NULL,
    mau_user character varying(255) NOT NULL,
    mau_timestamp timestamp without time zone DEFAULT LOCALTIMESTAMP NOT NULL,
    mau_action character varying(20) NOT NULL,
    mau_entity character varying(100) NOT NULL,
    mau_object_id bigint,
    mau_comment character varying(1024),
    mau_object_name character varying(255)
);

CREATE TABLE map_clinical_study (
    mcs_study_id character varying(255) NOT NULL,
    mcs_study_name character varying(255),
    mcs_mpr_id numeric(20,0) NOT NULL
);

CREATE TABLE map_column_rule (
    mcr_id numeric(20,0) NOT NULL,
    mcr_name character varying(255),
    mcr_mmr_id numeric(20,0) NOT NULL
);

CREATE TABLE map_custom_labcode_lookup (
    cll_id bigint NOT NULL,
    cll_labcode character varying(255) NOT NULL,
    cll_test_name character varying(255),
    cll_sample_name character varying(255),
    cll_msr_id bigint
);

COMMENT ON COLUMN map_custom_labcode_lookup.cll_id IS 'ID';

COMMENT ON COLUMN map_custom_labcode_lookup.cll_labcode IS 'Labcode';

COMMENT ON COLUMN map_custom_labcode_lookup.cll_test_name IS 'Test name';

COMMENT ON COLUMN map_custom_labcode_lookup.cll_sample_name IS 'Sample name';

CREATE TABLE map_dashboard_col_options (
    mdco_id numeric(20,0) NOT NULL,
    mdco_code character varying(100),
    mdco_name character varying(255),
    mdco_mandatory character(1),
    CONSTRAINT cons_mdco_mandatory CHECK ((mdco_mandatory = ANY (ARRAY['0'::bpchar, '1'::bpchar])))
);

CREATE TABLE map_dashboard_row_options (
    mdro_id numeric(20,0) NOT NULL,
    mdro_code character varying(100),
    mdro_name character varying(255),
    mdro_mandatory character(1),
    CONSTRAINT cons_mdro_mandatory CHECK ((mdro_mandatory = ANY (ARRAY['0'::bpchar, '1'::bpchar])))
);

CREATE TABLE map_description_entity (
    mde_id numeric(20,0) NOT NULL,
    mde_men_id numeric(20,0) NOT NULL,
    mde_mfd_id numeric(20,0) NOT NULL,
    mde_process_order smallint
);

CREATE TABLE map_description_file (
    mdf_id numeric(20,0) NOT NULL,
    mdf_mfr_id numeric(20,0) NOT NULL,
    mdf_mfd_id numeric(20,0) NOT NULL
);

CREATE TABLE map_dynamic_field (
    mdfi_id numeric(20,0) NOT NULL,
    mdfi_name character varying(255) NOT NULL,
    mdfi_mmr_id numeric(20,0) NOT NULL
);

CREATE SEQUENCE map_dynamic_field_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE;

CREATE TABLE map_entity (
    men_id numeric(20,0) NOT NULL,
    men_name character varying(255) NOT NULL,
    men_store_in_cache boolean,
    men_global_function character varying(255)
);

CREATE TABLE map_excluding_values (
    mev_id bigint NOT NULL,
    mev_msr_id bigint,
    mev_mfi_id bigint,
    mev_value character varying(255)
);

CREATE TABLE map_fd_sv (
    mfs_id numeric(20,0) NOT NULL,
    mfs_mfd_id numeric(20,0) NOT NULL,
    mfs_msv_id numeric(20,0) NOT NULL,
    mfs_is_obligatory boolean
);

CREATE TABLE map_field (
    mfi_id numeric(20,0) NOT NULL,
    mfi_name character varying(255) NOT NULL,
    mfi_type character varying(255) NOT NULL,
    mfi_mandatory smallint DEFAULT 0 NOT NULL,
    mfi_men_id numeric(20,0) NOT NULL,
    mfi_mfid_id numeric(20,0) NOT NULL,
    mfi_order numeric(10,2)
);

CREATE TABLE map_field_description (
    mfid_id numeric(20,0) NOT NULL,
    mfid_text character varying(2048) NOT NULL
);

CREATE TABLE map_file_description (
    mfd_id numeric(20,0) NOT NULL,
    mfd_name character varying(2048) NOT NULL,
    mfd_display_name character varying(2048) NOT NULL,
    mfd_section_id numeric(20,0) NOT NULL,
    mfd_process_order smallint
);

CREATE TABLE map_file_rule (
    mfr_id numeric(20,0) NOT NULL,
    mfr_name character varying(255),
    mfr_enabled smallint,
    mfr_is_updated smallint DEFAULT 1,
    mfr_msr_id numeric(20,0) NOT NULL,
    mfr_mft_id numeric(20,0) NOT NULL,
    mfr_file_standard character varying(100)
);

CREATE TABLE map_file_section (
    mfs_id numeric(20,0) NOT NULL,
    mfs_name character varying(255) NOT NULL,
    mfs_order numeric(10,5)
);

CREATE TABLE map_file_type (
    mft_id numeric(20,0) NOT NULL,
    mft_name character varying(255) NOT NULL,
    mft_delimiter character varying(255)
);

CREATE TABLE map_lab_group_rule (
    mlgr_id numeric(20,0) NOT NULL,
    mlgr_time character varying(100),
    mlgr_default_value character varying(100),
    mlgr_name character varying(100),
    mlgr_data_source character varying(255),
    mlgr_enabled smallint,
    mlgr_project_id numeric(20,0) NOT NULL
);

CREATE TABLE map_lab_group_value_rule (
    mlgv_id numeric(20,0) NOT NULL,
    mlgv_group_id numeric(20,0),
    mlgv_name character varying(100),
    mlgv_lab_code character varying(100),
    mlgv_description character varying(100)
);

CREATE TABLE map_mapping_rule (
    mmr_id numeric(20,0) NOT NULL,
    mmr_fmt_name character varying(255),
    mmr_fmt_default character varying(255),
    mmr_value character varying(255),
    mmr_maf_id numeric(20,0),
    mmr_mfr_id numeric(20,0) NOT NULL
);

CREATE TABLE map_mapping_rule_field (
    mrf_id numeric(20,0) NOT NULL,
    mrf_mmr_id numeric(20,0) NOT NULL,
    mrf_mfi_id numeric(20,0) NOT NULL
);

CREATE TABLE map_project_rule (
    mpr_id numeric(20,0) NOT NULL,
    mpr_drug character varying(50),
    mpr_admin character varying(100),
    mpr_completed smallint,
    mpr_created_by character varying(100),
    mpr_creation_date timestamp without time zone,
    mpr_create_dashboard smallint,
    mpr_ae_severity_type character varying(12) DEFAULT 'CTC_GRADES'::character varying NOT NULL,
    mpr_drug_display_name character varying(255),
    CONSTRAINT sys_c009681 CHECK (((mpr_ae_severity_type)::text = ANY (ARRAY[('CTC_GRADES'::character varying)::text, ('AE_INTENSITY'::character varying)::text])))
);

CREATE TABLE map_rct_inst_dshbrd_col_opt (
    mrdc_mdco_id numeric(20,0) NOT NULL,
    mrdc_instance_id numeric(20,0) NOT NULL
);

CREATE TABLE map_rct_inst_dshbrd_row_opt (
    mrdr_mdco_id numeric(20,0) NOT NULL,
    mrdr_instance_id numeric(20,0) NOT NULL
);

CREATE SEQUENCE map_rct_inst_sbj_grouping_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE;

CREATE TABLE map_rct_instance_ae_group (
    miag_id numeric(20,0) NOT NULL,
    miag_ae_group_id numeric(20,0),
    miag_instance_id numeric(20,0)
);

CREATE TABLE map_rct_instance_lab_group (
    milg_id numeric(20,0) NOT NULL,
    milg_ae_group_id numeric(20,0),
    milg_instance_id numeric(20,0)
);

CREATE TABLE map_rct_instance_sbj_group (
    misg_id numeric(20,0) NOT NULL,
    misg_ae_group_id numeric(20,0),
    misg_instance_id numeric(20,0)
);

CREATE TABLE map_rct_instance_sbj_grouping (
    misgr_id numeric(20,0) NOT NULL,
    misgr_misd_id numeric(20,0),
    misgr_msg_id bigint
);

CREATE TABLE map_rct_instance_std_rule (
    misd_id numeric(20,0) NOT NULL,
    misd_study_id numeric(20,0),
    misd_instance_id numeric(20,0),
    misd_cohort_dose_grouping character varying(255),
    misd_cohort_other_grouping character varying(255)
);

CREATE TABLE map_rct_instance_time_intrvl (
    miti_id numeric(20,0) NOT NULL,
    miti_mti_id numeric(20,0),
    miti_instance_id numeric(20,0)
);

CREATE TABLE map_acuity_instance (
    mri_id numeric(20,0) NOT NULL,
    mri_name character varying(1024),
    mri_module_type character varying(20),
    mri_project_id numeric(20,0),
    mri_status smallint,
    mri_completed smallint,
    mri_created_by character varying(100),
    mri_creation_date timestamp without time zone,
    mri_library_path character varying(1024),
    mri_default_vis_method character varying(20)
);

CREATE TABLE map_acuity_instance_bak (
    mri_id numeric(20,0) NOT NULL,
    mri_name character varying(1024),
    mri_module_type character varying(20),
    mri_project_id numeric(20,0),
    mri_status smallint,
    mri_completed smallint,
    mri_created_by character varying(100),
    mri_creation_date timestamp without time zone,
    mri_library_path character varying(1024),
    mri_default_vis_method character varying(20)
);

CREATE SEQUENCE map_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE;

CREATE TABLE map_sp_module_rct_instance (
    mmd_id numeric(20,0) NOT NULL,
    mmd_module_id numeric(20,0),
    mmd_instance_id numeric(20,0)
);

CREATE TABLE map_sp_module_subject_grouping (
    mmsg_id numeric(20,0) NOT NULL,
    msmt_id numeric(20,0),
    msgt_id numeric(20,0)
);

CREATE TABLE map_spotfire_module (
    msm_id numeric(20,0) NOT NULL,
    msm_module character varying(1024) NOT NULL,
    msm_msmt_id numeric(20,0) NOT NULL
);

CREATE TABLE map_spotfire_module_type (
    msmt_id numeric(20,0) NOT NULL,
    msmt_mod_type character varying(1024) NOT NULL
);

CREATE TABLE map_spotfire_table (
    mst_id numeric(20,0) NOT NULL,
    mst_name character varying(1024) NOT NULL,
    mst_il_path character varying(2048) NOT NULL,
    mst_il_parameter_column character varying(1024) NOT NULL,
    mst_il_parameter_expression character varying(1024) NOT NULL,
    mst_msmt_id numeric(20,0) NOT NULL
);

CREATE TABLE map_spotfire_view (
    msv_id numeric(20,0) NOT NULL,
    msv_view character varying(1024) NOT NULL,
    msv_msm_id numeric(20,0) NOT NULL
);

CREATE TABLE map_study_ae_group (
    msag_id numeric(20,0) NOT NULL,
    msag_ae_group_id numeric(20,0),
    msag_msr_id numeric(20,0)
);

CREATE TABLE map_study_baseline_drug (
    msbd_id bigint NOT NULL,
    msbd_msr_id bigint NOT NULL,
    msbd_drug_name character varying(255) NOT NULL,
    msbd_include boolean DEFAULT false NOT NULL,
    CONSTRAINT msbd_ck_include CHECK ((msbd_include = ANY (ARRAY[false, true])))
);

CREATE TABLE map_study_lab_group (
    mslg_id numeric(20,0) NOT NULL,
    mslg_lab_group_id numeric(20,0),
    mslg_msr_id numeric(20,0)
);

CREATE TABLE map_study_rule (
    msr_id numeric(20,0) NOT NULL,
    msr_study_name character varying(255),
    msr_study_code character varying(255),
    msr_prj_id numeric(20,0),
    msr_phase character varying(100),
    msr_blinded boolean,
    msr_randomised boolean,
    msr_regulatory boolean,
    msr_type character varying(255),
    msr_delivery_model character varying(255),
    msr_status character varying(100),
    msr_fsi_pln timestamp without time zone,
    msr_dbl_pln timestamp without time zone,
    msr_completed smallint,
    msr_valid smallint,
    msr_enabled smallint,
    msr_created_by character varying(100),
    msr_creation_date timestamp without time zone,
    msr_phase_type character varying(100),
    msr_config_type character varying(32),
    msr_scheduled boolean,
    msr_cron_expression character varying(32),
    msr_use_alt_lab_codes smallint,
    msr_primary_source character varying(1024),
    msr_main_drug character varying(128),
    msr_use_custom_baseline_drugs boolean DEFAULT false NOT NULL,
    msr_mcs_study_id character varying(255),
    msr_limit_x_axis_to_visit boolean,
    msr_auto_assigned_country boolean,
    msr_clean_scheduled boolean DEFAULT false,
    msr_mapping_modified_date timestamp without time zone,
    msr_cbio_profiles_mask bigint DEFAULT 0 NOT NULL,
    msr_cbio_profile_study_code character varying(255),
    msr_aml_enabled smallint DEFAULT 0 NOT NULL,
    CONSTRAINT cons_msr_clean_scheduled CHECK ((msr_clean_scheduled = ANY (ARRAY[false, true]))),
    CONSTRAINT cons_msr_scheduled CHECK ((msr_scheduled = ANY (ARRAY[false, true]))),
    CONSTRAINT msr_ck_custom_baseline_drugs CHECK ((msr_use_custom_baseline_drugs = ANY (ARRAY[false, true])))
);

CREATE TABLE map_subject_group_annotation (
    msga_id bigint NOT NULL,
    msga_group_name character varying(255),
    msga_group_prefered_name character varying(255),
    msga_grouping_id bigint,
    msga_index numeric(38,0)
);

CREATE TABLE map_subject_group_dosing (
    msgd_id bigint NOT NULL,
    msgd_msga_id bigint,
    msgd_drug character varying(100),
    msgd_formulation character varying(100),
    msgd_administration_route character varying(100),
    msgd_total_duration_cycles numeric,
    msgd_dose_schedule character varying(1000),
    msgd_dosing_continuity character varying(100),
    msgd_total_duration_type character varying(100)
);

CREATE TABLE map_subject_group_dosing_sched (
    msgds_id bigint NOT NULL,
    msgds_msgd_id bigint,
    msgds_dosing boolean DEFAULT false NOT NULL,
    msgds_duration numeric,
    msgds_duration_unit character varying(100),
    msgds_dose numeric,
    msgds_dose_unit character varying(100),
    msgds_frequency numeric,
    msgds_frequency_unit character varying(100),
    msgds_repeat boolean DEFAULT false NOT NULL,
    msgds_frequency_term character varying(100)
);

CREATE TABLE map_subject_group_rule (
    msgr_id numeric(20,0) NOT NULL,
    msgr_time character varying(100),
    msgr_default_value character varying(100),
    msgr_name character varying(100),
    msgr_data_source character varying(255),
    msgr_enabled smallint,
    msgr_study_id numeric(20,0) NOT NULL
);

CREATE TABLE map_subject_group_value_rule (
    msgv_id numeric(20,0) NOT NULL,
    msgv_group_id numeric(20,0),
    msgv_name character varying(100),
    msgv_subject_id character varying(100)
);

CREATE TABLE map_subject_grouping (
    msg_id bigint NOT NULL,
    msg_study_id bigint,
    msg_grouping_name character varying(255),
    msg_msgt_id numeric(20,0) DEFAULT 0 NOT NULL,
    msg_grouping_selected boolean DEFAULT false NOT NULL
);

CREATE TABLE map_subject_grouping_type (
    msgt_id numeric(20,0) NOT NULL,
    msgt_type character varying(1024) NOT NULL
);

CREATE TABLE map_time_intervals (
    mti_id numeric(20,0) NOT NULL,
    mti_name character varying(255) NOT NULL
);

CREATE SEQUENCE mau_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE;

CREATE SEQUENCE mcr_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE;

CREATE SEQUENCE mdf_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE;

CREATE SEQUENCE mev_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE;

CREATE SEQUENCE mfr_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE;

CREATE SEQUENCE mgr_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE;

CREATE SEQUENCE miag_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE;

CREATE SEQUENCE milg_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE;

CREATE SEQUENCE mipg_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE;

CREATE SEQUENCE misd_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE;

CREATE SEQUENCE misg_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE;

CREATE SEQUENCE mlgr_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE;

CREATE SEQUENCE mlgv_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE;

CREATE SEQUENCE mmd_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE;

CREATE SEQUENCE mmr_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE;

CREATE SEQUENCE mpr_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE;

CREATE SEQUENCE mrf_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE;

CREATE SEQUENCE mri_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE;

CREATE SEQUENCE msag_seq
    START WITH 9
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE;

CREATE SEQUENCE msg_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE;

CREATE SEQUENCE msga_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE;

CREATE SEQUENCE msgd_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE;

CREATE SEQUENCE msgds_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE;

CREATE SEQUENCE msgr_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE;

CREATE SEQUENCE msgv_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE;

CREATE SEQUENCE msi_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE;

CREATE SEQUENCE mslg_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE;

CREATE SEQUENCE msr_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE;

CREATE UNLOGGED TABLE mv_aes (
    vaes_id character varying(32),
    vaes_instance numeric(20,0) NOT NULL,
    vaes_pat_id character varying(32),
    vaes_pat_part character varying(255),
    vaes_pat_subject character varying(255),
    vaes_pat_centre bigint,
    vaes_pat_dth_date timestamp without time zone,
    vaes_study character varying(255),
    vaes_evt_id character varying(32),
    vaes_evt_pt character varying(255),
    vaes_evt_hlt character varying(255),
    vaes_evt_llt character varying(255),
    vaes_evt_soc character varying(255),
    vaes_text character varying(255),
    vaes_comment character varying(255),
    vaes_action_taken_primary character varying(4000),
    vaes_custom character varying(100),
    vaes_serious character varying(255),
    vaes_causality character varying(4000),
    vaes_max_ctc character varying(255),
    vaes_webapp_max_ctc character varying(255),
    vaes_num_max_ctc bigint,
    vaes_start_date timestamp without time zone,
    vaes_end_date timestamp without time zone,
    vaes_days_since_first_dose numeric,
    vaes_week numeric,
    vaes_week2 numeric,
    vaes_week3 numeric,
    vaes_week4 numeric,
    vaes_start_day numeric,
    vaes_end_day numeric,
    vaes_days_since_randomisation numeric,
    vaes_week_since_randomisation numeric,
    vaes_end_day_from_rand numeric,
    vaes_start_day_from_rand numeric,
    vaes_duration numeric,
    vaes_dose_at_start numeric,
    vaes_ongoing character varying(1),
    vaes_start_prior_to_rand character varying(3),
    vaes_end_prior_to_rand character varying(3),
    vaes_outcome character varying(255),
    vaes_dose_limiting_toxicity character varying(31),
    vaes_time_point character varying(255),
    vaes_immune_mediated character varying(255),
    vaes_infusion_acuityion character varying(255),
    vaes_required_treatment character varying(31),
    vaes_caused_subject_withdrawal character varying(255)
);

COMMENT ON TABLE mv_aes IS 'snapshot table for snapshot ACUITY_DEMO.MV_AES';

CREATE UNLOGGED TABLE mv_aes_inc (
    vaes_id character varying(32),
    vaes_instance numeric(20,0) NOT NULL,
    vaes_pat_id character varying(32),
    vaes_pat_part character varying(255),
    vaes_pat_subject character varying(255),
    vaes_pat_centre bigint,
    vaes_pat_dth_date timestamp without time zone,
    vaes_study character varying(255),
    vaes_evt_id character varying(32),
    vaes_evt_pt character varying(255),
    vaes_evt_hlt character varying(255),
    vaes_evt_llt character varying(255),
    vaes_evt_soc character varying(255),
    vaes_text character varying(255),
    vaes_comment character varying(255),
    vaes_action_taken_primary character varying(4000),
    vaes_custom character varying(100),
    vaes_serious character varying(255),
    vaes_causality character varying(4000),
    vaes_max_ctc character varying(255),
    vaes_webapp_max_ctc character varying(255),
    vaes_num_max_ctc numeric,
    vaes_start_date timestamp without time zone,
    vaes_end_date timestamp without time zone,
    vaes_days_since_first_dose numeric,
    vaes_week numeric,
    vaes_week2 numeric,
    vaes_week3 numeric,
    vaes_week4 numeric,
    vaes_start_day numeric,
    vaes_end_day numeric,
    vaes_days_since_randomisation numeric,
    vaes_week_since_randomisation numeric,
    vaes_end_day_from_rand numeric,
    vaes_start_day_from_rand numeric,
    vaes_duration numeric,
    vaes_dose_at_start numeric,
    vaes_ongoing character varying(1),
    vaes_start_prior_to_rand character varying(3),
    vaes_end_prior_to_rand character varying(3),
    vaes_outcome character varying(255),
    vaes_dose_limiting_toxicity character varying(31),
    vaes_time_point character varying(255),
    vaes_immune_mediated character varying(255),
    vaes_infusion_acuityion character varying(255),
    vaes_required_treatment character varying(31),
    vaes_caused_subject_withdrawal character varying(255)
);

COMMENT ON TABLE mv_aes_inc IS 'snapshot table for snapshot ACUITY_DEMO.MV_AES_INC';

CREATE UNLOGGED TABLE mv_labs (
    vlab_id character varying(32),
    vlab_instance numeric(20,0),
    vlab_pat_id character varying(32),
    vlab_pat_race character varying(255),
    vlab_pat_sex character varying(255),
    vlab_pat_birthdate timestamp without time zone,
    vlab_study character varying(255),
    vlab_part character varying(255),
    vlab_subject character varying(255),
    vlab_age numeric,
    vlab_centre bigint,
    vlab_sample_date timestamp without time zone,
    vlab_labcode character varying(255),
    vlab_group character varying(100),
    vlab_value numeric(19,2),
    vlab_uln_normalisation numeric,
    vlab_ref_range_normalisation numeric,
    vlab_dynamic_normalisation numeric,
    vlab_unit character varying(255),
    vlab_ref_high numeric(19,2),
    vlab_ref_low numeric(19,2),
    vlab_comment character varying(255),
    vlab_visit_number numeric(10,2),
    vlab_date_of_first_dose timestamp without time zone,
    vlab_pat_rand_date timestamp without time zone,
    vlab_week_first_dose numeric,
    vlab_week2_first_dose numeric,
    vlab_week3_first_dose numeric,
    vlab_week4_first_dose numeric,
    vlab_week_rand numeric,
    vlab_week2_rand numeric,
    vlab_week3_rand numeric,
    vlab_week4_rand numeric,
    vlab_baseline numeric,
    vlab_baseline_flag character(1),
    vlab_nearest_weight numeric
);

COMMENT ON TABLE mv_labs IS 'snapshot table for snapshot ACUITYDEV.MV_LABS';

CREATE UNLOGGED TABLE mv_labs_baselines (
    vlab_instance numeric(20,0) NOT NULL,
    vlab_pat_id character varying(32),
    vlab_labcode character varying(255),
    vlab_baseline numeric
);

COMMENT ON TABLE mv_labs_baselines IS 'snapshot table for snapshot ACUITY_DEMO.MV_LABS_BASELINES';

CREATE UNLOGGED TABLE mv_lungfunc (
    vlng_id character varying(32),
    vlng_instance numeric(20,0) NOT NULL,
    vlng_pat_id character varying(32),
    vlng_pat_race character varying(255),
    vlng_pat_sex character varying(255),
    vlng_pat_birthdate timestamp without time zone,
    vlng_pat_study_status character varying(100),
    vlng_pat_withdrawal_date timestamp without time zone,
    vlng_dth_date timestamp without time zone,
    vlng_study character varying(255),
    vlng_part character varying(255),
    vlng_subject character varying(255),
    vlng_age numeric,
    vlng_centre bigint,
    vlng_visit_date timestamp without time zone,
    vlng_measurement character varying(255),
    vlng_assess_date timestamp without time zone,
    vlng_prot_schedule character varying(255),
    vlng_value numeric(19,2),
    vlng_visit numeric(10,2),
    vlng_date_of_first_dose timestamp without time zone,
    vlng_pat_rand_date timestamp without time zone,
    vlng_week_first_dose numeric,
    vlng_week2_first_dose numeric,
    vlng_week3_first_dose numeric,
    vlng_week4_first_dose numeric,
    vlng_baseline numeric,
    vlng_baseline_flag character(1)
);

COMMENT ON TABLE mv_lungfunc IS 'snapshot table for snapshot ACUITY.MV_LUNGFUNC';

CREATE UNLOGGED TABLE mv_qtcf (
    tst_id character varying(32),
    vqtcf_id character varying(32),
    vqtcf_instance numeric(20,0) NOT NULL,
    vqtcf_study character varying(255),
    vqtcf_pat_id character varying(32),
    vqtcf_part character varying(255),
    vqtcf_subject character varying(255),
    vqtcf_centre bigint,
    vqtcf_date_of_first_dose timestamp without time zone,
    vqtcf_sample_date timestamp without time zone,
    vqtcf_visit_number numeric(10,2),
    vqtcf_meas_time numeric,
    vqtcf_days_since_first_dose numeric,
    vqtcf_days_since_randomisation numeric,
    vqtcf_value numeric,
    vqtcf_baseline numeric,
    vqtcf_change_from_baseline numeric
);

COMMENT ON TABLE mv_qtcf IS 'snapshot table for snapshot ACUITY.MV_QTCF';

CREATE TABLE precalc_aes (
    paes_id character varying(32),
    paes_ae_id character varying(32),
    paes_dataset_id numeric(20,0) NOT NULL,
    paes_dataset character varying(100) NOT NULL,
    paes_pat_id character varying(32),
    paes_pat_part character varying(255),
    paes_pat_subject character varying(255),
    paes_pat_centre bigint,
    paes_pat_dth_date timestamp without time zone,
    paes_study character varying(255),
    paes_evt_id character varying(32),
    paes_evt_pt character varying(255),
    paes_evt_hlt character varying(255),
    paes_evt_llt character varying(255),
    paes_evt_soc character varying(255),
    paes_text character varying(255),
    paes_comment character varying(255),
    paes_action_taken_primary character varying(4000),
    paes_custom character varying(100),
    paes_serious character varying(255),
    paes_causality character varying(4000),
    paes_max_ctc character varying(255),
    paes_webapp_max_ctc character varying(255),
    paes_num_max_ctc bigint,
    paes_start_date timestamp without time zone,
    paes_end_date timestamp without time zone,
    paes_days_since_first_dose numeric,
    paes_week numeric,
    paes_week2 numeric,
    paes_week3 numeric,
    paes_week4 numeric,
    paes_start_day numeric,
    paes_end_day numeric,
    paes_days_since_randomisation numeric,
    paes_week_since_randomisation numeric,
    paes_end_day_from_rand numeric,
    paes_start_day_from_rand numeric,
    paes_duration numeric,
    paes_dose_at_start numeric,
    paes_ongoing character(1),
    paes_start_prior_to_rand character varying(3),
    paes_end_prior_to_rand character varying(3),
    paes_outcome character varying(255),
    paes_dose_limiting_toxicity character varying(31),
    paes_time_point character varying(255),
    paes_immune_mediated character varying(255),
    paes_infusion_reaction character varying(255),
    paes_required_treatment character varying(31),
    paes_caused_subject_withdrawal character varying(255),
    paes_suspected_endpoint character varying(255),
    paes_suspected_endpoint_cat character varying(255),
    paes_ae_number numeric,
    paes_ae_of_special_interest character varying(255)
);

CREATE TABLE precalc_aes_inc (
    paes_id character varying(32),
    paes_ae_id character varying(32),
    paes_dataset_id numeric(20,0) NOT NULL,
    paes_dataset character varying(100) NOT NULL,
    paes_pat_id character varying(32),
    paes_pat_part character varying(255),
    paes_pat_subject character varying(255),
    paes_pat_centre bigint,
    paes_pat_dth_date timestamp without time zone,
    paes_study character varying(255),
    paes_evt_id character varying(32),
    paes_evt_pt character varying(255),
    paes_evt_hlt character varying(255),
    paes_evt_llt character varying(255),
    paes_evt_soc character varying(255),
    paes_text character varying(255),
    paes_comment character varying(255),
    paes_action_taken_primary character varying(4000),
    paes_custom character varying(100),
    paes_serious character varying(255),
    paes_causality character varying(4000),
    paes_max_ctc character varying(255),
    paes_webapp_max_ctc character varying(255),
    paes_num_max_ctc bigint,
    paes_start_date timestamp without time zone,
    paes_end_date timestamp without time zone,
    paes_days_since_first_dose numeric,
    paes_week numeric,
    paes_week2 numeric,
    paes_week3 numeric,
    paes_week4 numeric,
    paes_start_day numeric,
    paes_end_day numeric,
    paes_days_since_randomisation numeric,
    paes_week_since_randomisation numeric,
    paes_end_day_from_rand numeric,
    paes_start_day_from_rand numeric,
    paes_duration numeric,
    paes_dose_at_start numeric,
    paes_ongoing character(1),
    paes_start_prior_to_rand character varying(3),
    paes_end_prior_to_rand character varying(3),
    paes_outcome character varying(255),
    paes_dose_limiting_toxicity character varying(31),
    paes_time_point character varying(255),
    paes_immune_mediated character varying(255),
    paes_infusion_reaction character varying(255),
    paes_required_treatment character varying(31),
    paes_caused_subject_withdrawal character varying(255),
    paes_suspected_endpoint character varying(255),
    paes_suspected_endpoint_cat character varying(255),
    paes_ae_number numeric,
    paes_ae_of_special_interest character varying(255)
);

CREATE TABLE precalc_demo (
    pdm_dataset character varying(255),
    pdm_dataset_id numeric(20,0) NOT NULL,
    pdm_pat_id character varying(32),
    pdm_pat_birthdat timestamp without time zone,
    pdm_pat_visdat timestamp without time zone,
    pdm_pat_centre bigint,
    pdm_pat_race character varying(255),
    pdm_pat_sex character varying(255),
    pdm_pat_subject character varying(255),
    pdm_pat_part character varying(255),
    pdm_pat_received_drug character varying(3),
    pdm_pat_ip_dose_first_date timestamp without time zone,
    pdm_pat_last_treatment_date timestamp without time zone,
    pdm_pat_withdrawal_reason character varying(255),
    pdm_pat_withdrawal_date timestamp without time zone,
    pdm_pat_study_status character varying(100),
    pdm_pat_rand_date timestamp without time zone,
    pdm_pat_baseline_date timestamp without time zone,
    pdm_pat_country character varying(255),
    pdm_pat_age numeric,
    pdm_dth_date timestamp without time zone,
    pdm_pat_ethnic_group character varying(255),
    pdm_pat_specified_ethnic_group character varying(255),
    pdm_std_part character varying(512),
    pdm_pat_days_on_study numeric,
    pdm_pat_has_withdrawal character varying(3),
    pdm_pat_randomised character varying(3),
    pdm_pat_death character varying(3),
    pdm_pat_cohort_dose_group character varying(255),
    pdm_pat_cohort_other_group character varying(255),
    pdm_pat_weight numeric(10,3),
    pdm_pat_height numeric(10,3)
);

CREATE TABLE precalc_dose (
    pdos_dataset_id numeric(20,0) NOT NULL,
    pdos_dataset character varying(255),
    pdos_pat_id character varying(32),
    pdos_drug character varying(255),
    pdos_start_date timestamp without time zone,
    pdos_end_date timestamp without time zone,
    pdos_period_type character varying(12),
    pdos_subsequent_period_type character varying(12),
    pdos_ongoing numeric,
    pdos_dose numeric,
    pdos_dose_unit character varying(255),
    pdos_freq character varying(255),
    pdos_freq_rank numeric
);

CREATE TABLE precalc_ecg (
    pecg_dataset_id numeric(20,0) NOT NULL,
    pecg_dataset character varying(255),
    pecg_ecg_id character varying(32),
    pecg_study_part character varying(255),
    pecg_pat_baseline_date timestamp without time zone,
    pecg_tst_id character varying(32),
    pecg_tst_date timestamp without time zone,
    pecg_tst_visit numeric(10,2),
    pecg_pat_id character varying(32),
    pecg_test character varying(255),
    pecg_unit character varying(255),
    pecg_value numeric,
    pecg_evaluation character varying(255),
    pecg_significant character varying(255),
    pecg_week numeric,
    pecg_days_on_study numeric,
    pecg_pat_subject character varying(255),
    pecg_pat_ip_dose_first_date timestamp without time zone,
    pecg_pat_rand_date timestamp without time zone,
    pecg_measurement_category character varying(17),
    pecg_baseline_tst_id character varying(32),
    pecg_bsl_date timestamp without time zone,
    pecg_bsl_value numeric,
    pecg_change_from_baseline numeric,
    pecg_percent_change_bsl numeric,
    pecg_baseline_flag character varying(3),
    pecg_sch_timepoint character varying(255),
    pecg_date_last_dose timestamp without time zone,
    pecg_last_dose_amount character varying(255),
    pecg_method character varying(255),
    pecg_atrial_fibr character varying(255),
    pecg_sinus_rhythm character varying(255),
    pecg_reas_no_sinus_rhythm character varying(255),
    pecg_heart_rhythm character varying(255),
    pecg_heart_rhythm_oth character varying(255),
    pecg_extra_systoles character varying(255),
    pecg_specify_extra_systoles character varying(255),
    pecg_type_cond character varying(255),
    pecg_cond character varying(255),
    pecg_reas_abnormal_cond character varying(255),
    pecg_stt_changes character varying(255),
    pecg_st_segment character varying(255),
    pecg_wave character varying(255),
    pecg_beat_group_num bigint,
    pecg_beat_num_in_beat_group bigint,
    pecg_num_beats_avr_beat bigint,
    pecg_beat_group_length_sec numeric(10,2),
    pecg_comment character varying(255)
);

CREATE TABLE precalc_labs (
    plab_id character varying(32),
    plab_dataset_id numeric(20,0) NOT NULL,
    plab_dataset character varying(100) NOT NULL,
    plab_pat_id character varying(32),
    plab_pat_race character varying(255),
    plab_pat_sex character varying(255),
    plab_pat_birthdate timestamp without time zone,
    plab_pat_baseline_date timestamp without time zone,
    plab_study character varying(255),
    plab_part character varying(255),
    plab_subject character varying(255),
    plab_age numeric,
    plab_centre bigint,
    plab_sample_date timestamp without time zone,
    plab_labcode character varying(255),
    plab_labcode_raw character varying(255),
    plab_group character varying(100),
    plab_category character varying(255),
    plab_value numeric(19,2),
    plab_uln_normalisation numeric,
    plab_ref_range_normalisation numeric,
    plab_dynamic_normalisation numeric,
    plab_unit character varying(255),
    plab_ref_high numeric(19,2),
    plab_ref_low numeric(19,2),
    plab_comment character varying(255),
    plab_visit_number numeric(10,2),
    plab_date_of_first_dose timestamp without time zone,
    plab_pat_rand_date timestamp without time zone,
    plab_week_first_dose numeric,
    plab_week2_first_dose numeric,
    plab_week3_first_dose numeric,
    plab_week4_first_dose numeric,
    plab_week_rand numeric,
    plab_week2_rand numeric,
    plab_week3_rand numeric,
    plab_week4_rand numeric,
    plab_baseline numeric,
    plab_baseline_flag character(1),
    plab_nearest_weight numeric,
    plab_value_dipstick character varying(255),
    plab_sch_timepoint character varying(255),
    plab_src_type character varying(255) DEFAULT 'Sponsor'::character varying NOT NULL,
    plab_src_id character varying(32)
);

COMMENT ON COLUMN precalc_labs.plab_src_type IS 'Source (sponsor/patient etc.)';

COMMENT ON COLUMN precalc_labs.plab_src_id IS 'FK to result_lab_source table';

CREATE TABLE precalc_lungfunc (
    plng_id character varying(32),
    plng_dataset_id numeric(20,0) NOT NULL,
    plng_dataset character varying(100) NOT NULL,
    plng_pat_id character varying(32),
    plng_pat_race character varying(255),
    plng_pat_sex character varying(255),
    plng_pat_birthdate timestamp without time zone,
    plng_pat_study_status character varying(100),
    plng_pat_withdrawal_date timestamp without time zone,
    plng_dth_date timestamp without time zone,
    plng_study character varying(255),
    plng_part character varying(255),
    plng_subject character varying(255),
    plng_age numeric,
    plng_centre numeric,
    plng_visit_date timestamp without time zone,
    plng_measurement character varying(255),
    plng_assess_date timestamp without time zone,
    plng_prot_schedule character varying(255),
    plng_value numeric(19,2),
    plng_visit numeric(10,2),
    plng_date_of_first_dose timestamp without time zone,
    plng_pat_rand_date timestamp without time zone,
    plng_week_first_dose numeric,
    plng_week2_first_dose numeric,
    plng_week3_first_dose numeric,
    plng_week4_first_dose numeric,
    plng_baseline numeric,
    plng_baseline_flag character(1)
);

CREATE TABLE precalc_qtcf (
    pqtcf_tst_id character varying(32),
    pqtcf_dataset_id numeric(20,0) NOT NULL,
    pqtcf_dataset character varying(100) NOT NULL,
    pqtcf_id character varying(32),
    pqtcf_study character varying(255),
    pqtcf_pat_id character varying(32),
    pqtcf_part character varying(255),
    pqtcf_subject character varying(255),
    pqtcf_centre numeric,
    pqtcf_date_of_first_dose timestamp without time zone,
    pqtcf_sample_date timestamp without time zone,
    pqtcf_visit_number numeric(10,2),
    pqtcf_meas_time numeric,
    pqtcf_days_since_first_dose numeric,
    pqtcf_days_since_randomisation numeric,
    pqtcf_value numeric,
    pqtcf_baseline numeric,
    pqtcf_change_from_baseline numeric
);

CREATE TABLE precalc_recist (
    dataset character varying(255),
    dataset_id numeric(20,0) NOT NULL,
    pat_id character varying(32),
    pat_subject character varying(255),
    assessment_date timestamp without time zone,
    parameter character varying(200),
    parameter_code character varying(200),
    result_value numeric(38,8),
    baseline numeric(38,8),
    change_from_baseline numeric(38,8),
    target_lesions_present character varying(1),
    valid_baseline_target character varying(1),
    is_baseline character varying(1),
    best_assessment character varying(1),
    visit_num numeric(10,2),
    imputed_assessment character varying(200),
    recist_assessment character varying(200),
    investigator_assessment character varying(200),
    recist_agreement character varying(1),
    recist_id character varying(200) NOT NULL,
    CONSTRAINT cbest_assessment CHECK (((best_assessment)::text = ANY (ARRAY[('Y'::character varying)::text, ('N'::character varying)::text]))),
    CONSTRAINT cis_baseline CHECK (((is_baseline)::text = ANY (ARRAY[('Y'::character varying)::text, ('N'::character varying)::text]))),
    CONSTRAINT crecist_agreement CHECK (((recist_agreement)::text = ANY (ARRAY[('Y'::character varying)::text, ('N'::character varying)::text]))),
    CONSTRAINT ctarget_lesions_present CHECK (((target_lesions_present)::text = ANY (ARRAY[('Y'::character varying)::text, ('N'::character varying)::text]))),
    CONSTRAINT cvalid_baseline_target CHECK (((valid_baseline_target)::text = ANY (ARRAY[('Y'::character varying)::text, ('N'::character varying)::text])))
);

CREATE TABLE precalc_renal (
    pren_dataset_id numeric(20,0) NOT NULL,
    pren_dataset character varying(255),
    pren_lab_id character varying(130),
    pren_pat_id character varying(32),
    pren_subject character varying(255),
    pren_part character varying(255),
    pren_visit_num numeric(10,2),
    pren_study_week numeric,
    pren_lab_date timestamp without time zone,
    pren_days_since_first_dose numeric,
    pren_days_since_randomisation numeric,
    pren_first_treatment_date timestamp without time zone,
    pren_randomisation_date timestamp without time zone,
    pren_lab_name character varying(255),
    pren_lab_value numeric,
    pren_lab_unit character varying(255),
    pren_baseline numeric,
    pren_change_from_baseline numeric,
    pren_ref_high numeric(19,2),
    pren_ref_low numeric,
    pren_ckd_stage numeric
);

CREATE TABLE precalc_vitals (
    pvit_dataset_id numeric(20,0) NOT NULL,
    pvit_dataset character varying(100) NOT NULL,
    pvit_id character varying(32),
    pvit_test_name character varying(255),
    pvit_unit character varying(255),
    pvit_value numeric(10,3),
    pvit_anatomical_location character varying(255),
    pvit_physical_position character varying(255),
    pvit_pat_id character varying(32),
    pvit_pat_part character varying(255),
    pvit_pat_subject character varying(255),
    pvit_pat_baseline_date timestamp without time zone,
    pvit_pat_ip_dose_first_date timestamp without time zone,
    pvit_pat_rand_date timestamp without time zone,
    pvit_tst_date timestamp without time zone,
    pvit_tst_visit numeric(10,2),
    pvit_baseline_value numeric(10,3),
    pvit_baseline_date timestamp without time zone,
    pvit_change_from_baseline numeric(10,3),
    pvit_prc_change_from_bsl numeric(10,3),
    pvit_week numeric,
    pvit_days_since_first_dose numeric,
    pvit_baseline_flag character varying(3),
    pvit_clinically_significant character varying(255),
    pvit_sch_timepoint character varying(255),
    pvit_last_ip_date timestamp without time zone,
    pvit_last_ip_dose character varying(255),
    pvit_anatomical_side_interest character varying(255)
);

CREATE TABLE qrtz_blob_triggers (
    sched_name character varying(120) NOT NULL,
    trigger_name character varying(200) NOT NULL,
    trigger_group character varying(200) NOT NULL,
    blob_data bytea
);

CREATE TABLE qrtz_calendars (
    sched_name character varying(120) NOT NULL,
    calendar_name character varying(200) NOT NULL,
    calendar bytea NOT NULL
);

CREATE TABLE qrtz_cron_triggers (
    sched_name character varying(120) NOT NULL,
    trigger_name character varying(200) NOT NULL,
    trigger_group character varying(200) NOT NULL,
    cron_expression character varying(120) NOT NULL,
    time_zone_id character varying(80)
);

CREATE TABLE qrtz_fired_triggers (
    sched_name character varying(120) NOT NULL,
    entry_id character varying(95) NOT NULL,
    trigger_name character varying(200) NOT NULL,
    trigger_group character varying(200) NOT NULL,
    instance_name character varying(200) NOT NULL,
    fired_time bigint NOT NULL,
    sched_time bigint NOT NULL,
    priority bigint NOT NULL,
    state character varying(16) NOT NULL,
    job_name character varying(200),
    job_group character varying(200),
    is_nonconcurrent character varying(1),
    requests_recovery character varying(1)
);

CREATE TABLE qrtz_job_details (
    sched_name character varying(120) NOT NULL,
    job_name character varying(200) NOT NULL,
    job_group character varying(200) NOT NULL,
    description character varying(250),
    job_class_name character varying(250) NOT NULL,
    is_durable character varying(1) NOT NULL,
    is_nonconcurrent character varying(1) NOT NULL,
    is_update_data character varying(1) NOT NULL,
    requests_recovery character varying(1) NOT NULL,
    job_data bytea
);

CREATE TABLE qrtz_locks (
    sched_name character varying(120) NOT NULL,
    lock_name character varying(40) NOT NULL
);

CREATE TABLE qrtz_paused_trigger_grps (
    sched_name character varying(120) NOT NULL,
    trigger_group character varying(200) NOT NULL
);

CREATE TABLE qrtz_scheduler_state (
    sched_name character varying(120) NOT NULL,
    instance_name character varying(200) NOT NULL,
    last_checkin_time bigint NOT NULL,
    checkin_interval bigint NOT NULL
);

CREATE TABLE qrtz_simple_triggers (
    sched_name character varying(120) NOT NULL,
    trigger_name character varying(200) NOT NULL,
    trigger_group character varying(200) NOT NULL,
    repeat_count integer NOT NULL,
    repeat_interval bigint NOT NULL,
    times_triggered bigint NOT NULL
);

CREATE TABLE qrtz_simprop_triggers (
    sched_name character varying(120) NOT NULL,
    trigger_name character varying(200) NOT NULL,
    trigger_group character varying(200) NOT NULL,
    str_prop_1 character varying(512),
    str_prop_2 character varying(512),
    str_prop_3 character varying(512),
    int_prop_1 bigint,
    int_prop_2 bigint,
    long_prop_1 bigint,
    long_prop_2 bigint,
    dec_prop_1 numeric(13,4),
    dec_prop_2 numeric(13,4),
    bool_prop_1 character varying(1),
    bool_prop_2 character varying(1)
);

CREATE TABLE qrtz_triggers (
    sched_name character varying(120) NOT NULL,
    trigger_name character varying(200) NOT NULL,
    trigger_group character varying(200) NOT NULL,
    job_name character varying(200) NOT NULL,
    job_group character varying(200) NOT NULL,
    description character varying(250),
    next_fire_time bigint,
    prev_fire_time bigint,
    priority bigint,
    trigger_state character varying(16) NOT NULL,
    trigger_type character varying(8) NOT NULL,
    start_time bigint NOT NULL,
    end_time bigint,
    calendar_name character varying(200),
    misfire_instr smallint,
    job_data bytea
);

CREATE SEQUENCE rcv_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE;

CREATE SEQUENCE rda_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE;

CREATE SEQUENCE rdf_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE;

CREATE SEQUENCE rds_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE;

CREATE SEQUENCE rdt_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE;

CREATE SEQUENCE rdv_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE;

CREATE TABLE report_data_alert (
    rda_id bigint NOT NULL,
    rda_data_entity character varying(100),
    rda_count_source_file bigint,
    rda_count_database bigint,
    rda_std_code character varying(100) NOT NULL,
    rda_je_id bigint NOT NULL
);

CREATE TABLE report_data_field (
    rdf_id bigint NOT NULL,
    rdf_je_id bigint NOT NULL,
    rdf_data_field character varying(255),
    rdf_raw_data_column character varying(255),
    rdf_is_mapped character varying(20),
    rdf_error_type character varying(255),
    rdf_error_description character varying(255),
    rdf_data_source character varying(1024),
    rdf_rag_status character varying(8)
);

CREATE TABLE report_data_summary (
    rds_id bigint NOT NULL,
    rds_je_id bigint,
    rds_study_code character varying(250),
    rds_rag_status character varying(5),
    rds_files_size numeric,
    rds_files_count bigint
);

CREATE TABLE report_data_table (
    rdt_id bigint NOT NULL,
    rdt_je_id bigint,
    rdt_data_source character varying(1024),
    rdt_acuity_entities character varying(255),
    rdt_num_subject_source numeric,
    rdt_num_subjects_acuity numeric,
    rdt_num_events_uploaded numeric,
    rdt_rag_status character varying(5),
    rdt_file_size numeric
);

CREATE TABLE report_data_value (
    rdv_id bigint NOT NULL,
    rdv_je_id bigint,
    rdv_data_field character varying(255),
    rdv_raw_data_column character varying(255),
    rdv_raw_data_value character varying(255),
    rdv_error_type character varying(255),
    rdv_error_description character varying(1024),
    rdv_data_source character varying(1024),
    rdv_rag_status character varying(5),
    rdv_error_count bigint
);

CREATE TABLE report_exceptions (
    rex_id bigint NOT NULL,
    rex_je_id bigint,
    rex_etl_step character varying(255),
    rex_exception_type character varying(255),
    rex_stack_trace text,
    rex_rag_status character varying(5),
    rex_message character varying(2048)
);

CREATE TABLE report_fk_violation (
    rfv_id numeric(20,0) NOT NULL,
    rfv_std_code character varying(255),
    rfv_parent_entity character varying(255),
    rfv_parent_files character varying(255),
    rfv_child_entity character varying(255),
    rfv_hash character varying(255),
    rfv_fk_fields character varying(1023),
    rfv_row_numbers text,
    rfv_je_id bigint NOT NULL
);

CREATE TABLE report_missed_cntlin (
    rmc_id numeric(20,0) NOT NULL,
    rmc_std_code character varying(255),
    rmc_fmt_name character varying(255),
    rmc_fmt_value character varying(255),
    rmc_referred_by_column character varying(255),
    rmc_referred_by_location text,
    rmc_je_id bigint NOT NULL
);

CREATE TABLE report_unique_violation (
    rcv_id numeric(20,0) NOT NULL,
    rcv_std_code character varying(255),
    rcv_entity_name character varying(255),
    rcv_entity character varying(1023),
    rcv_internal_entity character varying(1023),
    rcv_entity_hash character varying(255),
    rcv_row_numbers text,
    rcv_duplicate_count bigint,
    rcv_decision character varying(1023),
    rcv_internal_decision character varying(1023),
    rcv_decision_file character varying(255),
    rcv_decision_row bigint,
    rcv_decision_table character varying(255),
    rcv_decision_id character varying(32),
    rcv_reason character varying(255),
    rcv_je_id bigint NOT NULL
);

CREATE TABLE report_unparsed_data (
    rud_id numeric(20,0) NOT NULL,
    rud_std_code character varying(255),
    rud_filename character varying(255),
    rud_column_name character varying(255),
    rud_value character varying(255),
    rud_type character varying(255),
    rud_row_numbers text,
    rud_je_id bigint NOT NULL
);

CREATE TABLE result_ae (
    ae_id character varying(32) NOT NULL,
    ae_date_created timestamp without time zone,
    ae_date_updated timestamp without time zone,
    ae_unq_sha1 character varying(40),
    ae_sec_hash bigint,
    ae_text character varying(255),
    ae_serious character varying(255),
    ae_number numeric,
    ae_evt_id character varying(32),
    ae_pat_id character varying(32),
    ae_comment character varying(255),
    ae_outcome character varying(255),
    ae_dose_limiting_toxicity character varying(31),
    ae_time_point character varying(255),
    ae_immune_mediated character varying(255),
    ae_infusion_reaction character varying(255),
    ae_required_treatment character varying(31),
    ae_caused_subject_withdrawal character varying(255),
    ae_suspected_endpoint character varying(255),
    ae_suspected_endpoint_category character varying(255),
    ae_of_special_interest character varying(255)
);

COMMENT ON COLUMN result_ae.ae_of_special_interest IS 'Flag to specify that this AE is of particular interest';

CREATE TABLE result_ae_action_taken (
    aeat_id character varying(32) NOT NULL,
    aeat_date_created timestamp without time zone,
    aeat_date_updated timestamp without time zone,
    aeat_unq_sha1 character varying(40),
    aeat_sec_hash bigint,
    aeat_aes_id character varying(32),
    aeat_drug_id character varying(100),
    aeat_action_taken character varying(255)
);

CREATE TABLE result_ae_causality (
    aec_id character varying(32) NOT NULL,
    aec_date_created timestamp without time zone,
    aec_date_updated timestamp without time zone,
    aec_unq_sha1 character varying(40),
    aec_sec_hash bigint,
    aec_ae_id character varying(32),
    aec_drug_id character varying(100),
    aec_causality character varying(255)
);

CREATE TABLE result_ae_num_act_taken (
    aenat_id character varying(32) NOT NULL,
    aenat_pat_id character varying(32),
    aenat_mds_id character varying(32),
    aenat_num_act_taken bigint,
    aenat_unq_sha1 character varying(255),
    aenat_sec_hash bigint,
    aenat_ref_sha1 character varying(255)
);

CREATE TABLE result_ae_num_del (
    aend_id character varying(32) NOT NULL,
    aend_pat_id character varying(32),
    aend_mds_id character varying(32),
    aend_num_cycle_del bigint,
    aend_unq_sha1 character varying(255),
    aend_sec_hash bigint,
    aend_ref_sha1 character varying(255)
);

CREATE TABLE result_ae_severity (
    aes_id character varying(32) NOT NULL,
    aes_date_created timestamp without time zone,
    aes_date_updated timestamp without time zone,
    aes_unq_sha1 character varying(40),
    aes_sec_hash bigint,
    aes_ae_id character varying(32),
    aes_severity character varying(255),
    aes_start_date timestamp without time zone,
    aes_end_date timestamp without time zone,
    aes_ongoing character varying(1),
    aes_end_date_raw timestamp without time zone,
    aes_end_type character varying(32) DEFAULT 'NONE'::character varying,
    CONSTRAINT cons_aes_ongoing CHECK (((aes_ongoing)::text = ANY (ARRAY[('0'::character varying)::text, ('1'::character varying)::text])))
);

CREATE TABLE result_alcohol_sub_use (
    asu_id character varying(32) NOT NULL,
    asu_date_created timestamp without time zone,
    asu_date_updated timestamp without time zone,
    asu_unq_sha1 character varying(40),
    asu_sec_hash bigint,
    asu_pat_id character varying(32),
    asu_category character varying(255),
    asu_use_occur character varying(255),
    asu_type character varying(255),
    asu_oth_type_spec character varying(255),
    asu_consumption numeric(10,2),
    asu_freq character varying(255),
    asu_start_date timestamp without time zone,
    asu_end_date timestamp without time zone,
    asu_type_use_occur character varying(255)
);

COMMENT ON COLUMN result_alcohol_sub_use.asu_category IS 'A value that describes the substance use category';

COMMENT ON COLUMN result_alcohol_sub_use.asu_use_occur IS 'A value that describes the usage occurrence (never, current, former)';

COMMENT ON COLUMN result_alcohol_sub_use.asu_type IS 'A value that describes the type of the substance';

COMMENT ON COLUMN result_alcohol_sub_use.asu_oth_type_spec IS 'A value that describes alternative type of the substance';

COMMENT ON COLUMN result_alcohol_sub_use.asu_consumption IS 'A value that describes the substance consumption (Units)';

COMMENT ON COLUMN result_alcohol_sub_use.asu_freq IS 'A value that describes frequency interval of the substance usage';

COMMENT ON COLUMN result_alcohol_sub_use.asu_start_date IS 'A date on which the subject started substance usage';

COMMENT ON COLUMN result_alcohol_sub_use.asu_end_date IS 'A date on which the subject ended substance usage';

COMMENT ON COLUMN result_alcohol_sub_use.asu_type_use_occur IS 'A value that describes the substance type usage occurrence (yes/no)';

CREATE TABLE result_algorithm_outcomes (
    ao_id character varying(32) NOT NULL,
    ao_event_id character varying(32) NOT NULL,
    ao_event_type character varying(32) NOT NULL,
    ao_src_id character varying(32),
    ao_result character varying(255) NOT NULL,
    ao_date_created timestamp without time zone,
    ao_date_updated timestamp without time zone
);

COMMENT ON COLUMN result_algorithm_outcomes.ao_id IS 'ID';

COMMENT ON COLUMN result_algorithm_outcomes.ao_event_id IS 'FK to some event table e.g. RESULT_EG';

COMMENT ON COLUMN result_algorithm_outcomes.ao_event_type IS 'Type of event';

COMMENT ON COLUMN result_algorithm_outcomes.ao_src_id IS 'FK to RESULT_SOURCE table';

COMMENT ON COLUMN result_algorithm_outcomes.ao_result IS 'Algorithm calculation result';

COMMENT ON COLUMN result_algorithm_outcomes.ao_date_created IS 'Record create date';

COMMENT ON COLUMN result_algorithm_outcomes.ao_date_updated IS 'Record update date';

CREATE TABLE result_biomarkers (
    bmr_id character varying(32) NOT NULL,
    bmr_date_created timestamp without time zone,
    bmr_date_updated timestamp without time zone,
    bmr_unq_sha1 character varying(40),
    bmr_sec_hash bigint,
    bmr_pat_id character varying(32),
    bmr_gene character varying(32) NOT NULL,
    bmr_specimen_id character varying(32),
    bmr_sample_id character varying(32),
    bmr_cdna_change character varying(255),
    bmr_external_variant_id character varying(255),
    bmr_total_reads numeric(20,0),
    bmr_germline_frequency numeric(20,0),
    bmr_mutant_allele_freq numeric(20,0),
    bmr_copy_number numeric(20,0),
    bmr_cin_rank numeric(20,0),
    bmr_mutation_type character varying(255),
    bmr_variant_count numeric,
    bmr_chromosome character varying(255),
    bmr_somatic_status character varying(255),
    bmr_amino_acid_change character varying(255),
    bmr_chromosome_location_start numeric(20,0),
    bmr_chromosome_location_end numeric(20,0),
    bmr_variant_type character varying(32) NOT NULL,
    bmr_cna_type character varying(32),
    bmr_rearr_gene_1 character varying(255),
    bmr_rearr_description character varying(255),
    bmr_tumour_mutational_burden numeric(20,0)
);

CREATE TABLE result_cerebrovascular (
    cer_id character varying(32) NOT NULL,
    cer_date_created timestamp without time zone,
    cer_date_updated timestamp without time zone,
    cer_unq_sha1 character varying(40),
    cer_sec_hash bigint,
    cer_pat_id character varying(32),
    cer_ae_num bigint,
    cer_start_date timestamp without time zone,
    cer_term character varying(255),
    cer_event_type character varying(255),
    cer_prim_ischemic_stroke character varying(255),
    cer_traumatic character varying(255),
    cer_loc_intra_hemorrhage character varying(255),
    cer_loc_intra_hemorrhage_oth character varying(255),
    cer_symptoms_duration character varying(255),
    cer_mrs_prior_to_stroke character varying(255),
    cer_mrs_stroke_hospital character varying(255),
    cer_mrs_cur_visit_90d_after character varying(255),
    cer_comment character varying(255)
);

COMMENT ON COLUMN result_cerebrovascular.cer_ae_num IS 'The number assigned to the associated AE record';

COMMENT ON COLUMN result_cerebrovascular.cer_start_date IS 'The start date of the event';

COMMENT ON COLUMN result_cerebrovascular.cer_term IS 'The term used to describe the event';

COMMENT ON COLUMN result_cerebrovascular.cer_event_type IS 'The category of cerebrovascular event';

COMMENT ON COLUMN result_cerebrovascular.cer_prim_ischemic_stroke IS 'Whether it was a primary ischemic stroke';

COMMENT ON COLUMN result_cerebrovascular.cer_traumatic IS 'Whether the event was traumatic';

COMMENT ON COLUMN result_cerebrovascular.cer_loc_intra_hemorrhage IS 'Categorical location of the primary intracranial hemorrhage';

COMMENT ON COLUMN result_cerebrovascular.cer_loc_intra_hemorrhage_oth IS 'Specification of uncategorised location of the primary intracranial hemorrhage';

COMMENT ON COLUMN result_cerebrovascular.cer_symptoms_duration IS 'The duration of the symptoms';

COMMENT ON COLUMN result_cerebrovascular.cer_mrs_prior_to_stroke IS 'Whether subject had MRS prior to stroke';

COMMENT ON COLUMN result_cerebrovascular.cer_mrs_stroke_hospital IS 'Whether subject had MRS during the stroke hospitalisation';

COMMENT ON COLUMN result_cerebrovascular.cer_mrs_cur_visit_90d_after IS 'MRS at Current Visit or 90D After Stroke';

COMMENT ON COLUMN result_cerebrovascular.cer_comment IS 'Any comment about the event';

CREATE TABLE result_chemotherapy (
    chemo_id character varying(32),
    chemo_date_created timestamp without time zone,
    chemo_date_updated timestamp without time zone,
    chemo_visit bigint,
    chemo_visit_dat timestamp without time zone,
    chemo_preferred_name_of_med character varying(255),
    chemo_start_date timestamp without time zone,
    chemo_end_date timestamp without time zone,
    chemo_num_of_cycles bigint,
    chemo_class character varying(255),
    chemo_treatment_status character varying(255),
    chemo_best_response character varying(255),
    chemo_reason_for_failure character varying(255),
    chemo_unq_sha1 character varying(255),
    chemo_ref_sha1 character varying(255),
    chemo_pat_id character varying(32),
    chemo_sec_hash bigint,
    chemo_time_status character varying(255),
    chemo_concomitant_therapy character varying(255),
    chemo_num_of_regiments bigint,
    chemo_cancer_therapy_agent character varying(255),
    chemo_therapy_reason character varying(255),
    chemo_route character varying(255),
    chemo_treatment_continues character varying(255)
);

COMMENT ON COLUMN result_chemotherapy.chemo_id IS 'ID';

COMMENT ON COLUMN result_chemotherapy.chemo_date_created IS 'Record create date';

COMMENT ON COLUMN result_chemotherapy.chemo_date_updated IS 'Record update date';

COMMENT ON COLUMN result_chemotherapy.chemo_visit IS 'Visit number';

COMMENT ON COLUMN result_chemotherapy.chemo_visit_dat IS 'Visit Date';

COMMENT ON COLUMN result_chemotherapy.chemo_preferred_name_of_med IS 'Preferred name of cancer therapy agent';

COMMENT ON COLUMN result_chemotherapy.chemo_start_date IS 'Cancer therapy regimen start date';

COMMENT ON COLUMN result_chemotherapy.chemo_end_date IS 'Cancer therapy regimen end date';

COMMENT ON COLUMN result_chemotherapy.chemo_num_of_cycles IS 'Number of treatment cycles';

COMMENT ON COLUMN result_chemotherapy.chemo_class IS 'Therapy class';

COMMENT ON COLUMN result_chemotherapy.chemo_treatment_status IS 'Treatment status (first line, second line etc.)';

COMMENT ON COLUMN result_chemotherapy.chemo_best_response IS 'Best response to treatment';

COMMENT ON COLUMN result_chemotherapy.chemo_reason_for_failure IS 'Reason for therapy failure';

COMMENT ON COLUMN result_chemotherapy.chemo_unq_sha1 IS 'SHA1 for unique fields';

COMMENT ON COLUMN result_chemotherapy.chemo_ref_sha1 IS 'SHA1 for FK fields';

COMMENT ON COLUMN result_chemotherapy.chemo_pat_id IS 'FK to Patient';

COMMENT ON COLUMN result_chemotherapy.chemo_time_status IS 'CHEMOTHERAPY TIME STATUS';

COMMENT ON COLUMN result_chemotherapy.chemo_concomitant_therapy IS 'FLAG INDICATING THAT THIS IS CONCOMITANT CHEMORADIOTHERAPY';

COMMENT ON COLUMN result_chemotherapy.chemo_num_of_regiments IS 'THE NUMBER OF PRIOR CHEMO REGIMENS';

COMMENT ON COLUMN result_chemotherapy.chemo_cancer_therapy_agent IS 'THERAPY ANTI-CANCER DRUG';

COMMENT ON COLUMN result_chemotherapy.chemo_therapy_reason IS 'THERAPY REASON';

COMMENT ON COLUMN result_chemotherapy.chemo_route IS 'ROUTE OF ADMINISTRATION';

COMMENT ON COLUMN result_chemotherapy.chemo_treatment_continues IS 'FLAG INDICATING THAT TREATMENT IS ONGOING';

CREATE TABLE result_ci_event (
    ci_id character varying(32) NOT NULL,
    ci_date_created timestamp without time zone,
    ci_date_updated timestamp without time zone,
    ci_unq_sha1 character varying(40),
    ci_sec_hash bigint,
    ci_pat_id character varying(32),
    ci_start_date timestamp without time zone,
    ci_event_term character varying(255),
    ci_ae_num bigint,
    ci_ischemic_symptoms character varying(255),
    ci_symptoms_duration character varying(255),
    ci_symptoms_prompt_uns_hosp character varying(255),
    ci_event_due_to_stent_thromb character varying(255),
    ci_prev_ecg_available character varying(255),
    ci_prev_ecg_date timestamp without time zone,
    ci_ecg_at_event_time character varying(255),
    ci_no_ecg_at_event_time character varying(255),
    ci_local_card_biom_drawn character varying(255),
    ci_coron_angiography_perf character varying(255),
    ci_date_of_angiogr timestamp without time zone,
    ci_fin_diagnosis character varying(255),
    ci_oth_diagnosis character varying(255),
    ci_description1 character varying(255),
    ci_description2 character varying(255),
    ci_description3 character varying(255),
    ci_description4 character varying(255),
    ci_description5 character varying(255)
);

COMMENT ON COLUMN result_ci_event.ci_start_date IS 'The start date of the event';

COMMENT ON COLUMN result_ci_event.ci_event_term IS 'The term used to describe the event';

COMMENT ON COLUMN result_ci_event.ci_ae_num IS 'The number assigned to the associated AE record';

COMMENT ON COLUMN result_ci_event.ci_ischemic_symptoms IS 'The category of cardiac ischemic symptoms';

COMMENT ON COLUMN result_ci_event.ci_symptoms_duration IS 'The duration of cardiac ischemic symptoms';

COMMENT ON COLUMN result_ci_event.ci_symptoms_prompt_uns_hosp IS 'Whether the symptoms caused an unscheduled hospitalisation';

COMMENT ON COLUMN result_ci_event.ci_event_due_to_stent_thromb IS 'Whether the symptoms were suspected to be caused by stent thrombosis';

COMMENT ON COLUMN result_ci_event.ci_prev_ecg_available IS 'Whether an ECG prior to the event is available';

COMMENT ON COLUMN result_ci_event.ci_prev_ecg_date IS 'The date of the prior ECG, if any';

COMMENT ON COLUMN result_ci_event.ci_ecg_at_event_time IS 'Whether an ECG is available at the time of the event';

COMMENT ON COLUMN result_ci_event.ci_no_ecg_at_event_time IS 'Specify regarding no ECG at the time of the event';

COMMENT ON COLUMN result_ci_event.ci_local_card_biom_drawn IS 'Whether local cardiac biomarkers were drawn';

COMMENT ON COLUMN result_ci_event.ci_coron_angiography_perf IS 'Whether a coronary angiography was performed';

COMMENT ON COLUMN result_ci_event.ci_date_of_angiogr IS 'The date of coronary angiography';

COMMENT ON COLUMN result_ci_event.ci_fin_diagnosis IS 'A category describing the final diagnosis for the CI event';

COMMENT ON COLUMN result_ci_event.ci_oth_diagnosis IS 'Specification for uncategorised CI event diagnosis';

COMMENT ON COLUMN result_ci_event.ci_description1 IS 'Text describing the event';

COMMENT ON COLUMN result_ci_event.ci_description2 IS 'Text describing the event';

COMMENT ON COLUMN result_ci_event.ci_description3 IS 'Text describing the event';

COMMENT ON COLUMN result_ci_event.ci_description4 IS 'Text describing the event';

COMMENT ON COLUMN result_ci_event.ci_description5 IS 'Text describing the event';

CREATE TABLE result_conmed_procedure (
    cp_id character varying(32) NOT NULL,
    cp_date_created timestamp without time zone,
    cp_date_updated timestamp without time zone,
    cp_unq_sha1 character varying(40),
    cp_sec_hash bigint,
    cp_pat_id character varying(32),
    cp_value character varying(255),
    cp_pt character varying(255),
    cp_llt character varying(255),
    cp_hlt character varying(255),
    cp_soc character varying(255),
    cp_start_date timestamp without time zone,
    cp_end_date timestamp without time zone,
    cp_primary_reason character varying(255),
    cp_primary_reason_oth character varying(255),
    cp_continues_study_disc_compl character varying(255),
    cp_reason character varying(255),
    cp_num_lesions_resected bigint,
    cp_date_wound_healed timestamp without time zone,
    cp_hospital_discharge_date timestamp without time zone
);

COMMENT ON COLUMN result_conmed_procedure.cp_value IS 'Procedure Text';

COMMENT ON COLUMN result_conmed_procedure.cp_pt IS 'MedDRA Preferred Term Name string assigned to the procedure';

COMMENT ON COLUMN result_conmed_procedure.cp_llt IS 'MedDRA Lowest Level Term Name string assigned to the procedure';

COMMENT ON COLUMN result_conmed_procedure.cp_hlt IS 'MedDRA High Level Term Name string assigned to the procedure';

COMMENT ON COLUMN result_conmed_procedure.cp_soc IS 'MedDRA System Organ Class Name string assigned to the procedure';

COMMENT ON COLUMN result_conmed_procedure.cp_start_date IS 'The date and time on which procedure was started (Time)';

COMMENT ON COLUMN result_conmed_procedure.cp_end_date IS 'The date and time on which procedure was ended (Time)';

COMMENT ON COLUMN result_conmed_procedure.cp_primary_reason IS 'A value describes primary reason for which procedure was performed';

COMMENT ON COLUMN result_conmed_procedure.cp_primary_reason_oth IS 'A value describes alternative primary reason for which procedure was performed';

COMMENT ON COLUMN result_conmed_procedure.cp_continues_study_disc_compl IS 'Flag indicating that procedure continues at study disc/compl (yes)';

COMMENT ON COLUMN result_conmed_procedure.cp_reason IS 'A value describes reason for which procedure was performed';

COMMENT ON COLUMN result_conmed_procedure.cp_num_lesions_resected IS 'A value denoting the number of lesions that was resected';

COMMENT ON COLUMN result_conmed_procedure.cp_date_wound_healed IS 'The date on which wound healed';

COMMENT ON COLUMN result_conmed_procedure.cp_hospital_discharge_date IS 'The date on which the subject was discharged from hospital';

CREATE TABLE result_conmed_schedule (
    cms_id character varying(32) NOT NULL,
    cms_date_created timestamp without time zone,
    cms_date_updated timestamp without time zone,
    cms_dose numeric(10,2),
    cms_start_date timestamp without time zone,
    cms_end_date timestamp without time zone,
    cms_atc_code character varying(255),
    cms_frequency character varying(255),
    cms_dose_unit character varying(255),
    cms_reason character varying(255),
    cms_unq_sha1 character varying(255),
    cms_med_id character varying(32),
    cms_pat_id character varying(32),
    cms_ref_sha1 character varying(255),
    cms_sec_hash bigint,
    cms_atc_code_text character varying(255),
    cms_dose_total numeric(10,2),
    cms_dose_unit_other character varying(255),
    cms_frequency_other character varying(255),
    cms_route character varying(255),
    cms_reason_other character varying(255),
    cms_proph_spec_other character varying(255),
    cms_ae_num bigint,
    cms_reason_stop character varying(255),
    cms_reason_stop_other character varying(255),
    cms_inf_body_sys character varying(255),
    cms_inf_body_sys_other character varying(255),
    cms_active_ingr_1 character varying(255),
    cms_active_ingr_2 character varying(255),
    cms_therapy_reason character varying(255)
);

COMMENT ON COLUMN result_conmed_schedule.cms_id IS 'ID';

COMMENT ON COLUMN result_conmed_schedule.cms_date_created IS 'Record create date';

COMMENT ON COLUMN result_conmed_schedule.cms_date_updated IS 'Record update date';

COMMENT ON COLUMN result_conmed_schedule.cms_dose IS 'Dose per Administration';

COMMENT ON COLUMN result_conmed_schedule.cms_start_date IS 'Start date of concomitant medication';

COMMENT ON COLUMN result_conmed_schedule.cms_end_date IS 'End date of concomitant medication';

COMMENT ON COLUMN result_conmed_schedule.cms_atc_code IS 'ATC code';

COMMENT ON COLUMN result_conmed_schedule.cms_frequency IS 'Dose Frequency';

COMMENT ON COLUMN result_conmed_schedule.cms_dose_unit IS 'Dose Unit';

COMMENT ON COLUMN result_conmed_schedule.cms_reason IS 'Reason for treatment';

COMMENT ON COLUMN result_conmed_schedule.cms_unq_sha1 IS 'SHA1 for unique fields';

COMMENT ON COLUMN result_conmed_schedule.cms_med_id IS 'FK to Medicine';

COMMENT ON COLUMN result_conmed_schedule.cms_pat_id IS 'FK to Patient';

COMMENT ON COLUMN result_conmed_schedule.cms_ref_sha1 IS 'SHA1 for FK fields';

COMMENT ON COLUMN result_conmed_schedule.cms_dose_total IS 'A value giving the dose of medication given to the subject per day';

COMMENT ON COLUMN result_conmed_schedule.cms_dose_unit_other IS 'A value denoting alternative unit in which the dose is measured';

COMMENT ON COLUMN result_conmed_schedule.cms_frequency_other IS 'A value denoting alternative frequency with which medication is administrated to the subject';

COMMENT ON COLUMN result_conmed_schedule.cms_route IS 'A value describes route of administration';

COMMENT ON COLUMN result_conmed_schedule.cms_reason_other IS 'A value describes alternative reason why medication was administrated to the subject';

COMMENT ON COLUMN result_conmed_schedule.cms_proph_spec_other IS 'A value describes alternative reason (prophylaxis) why medication was administrated to the subject';

COMMENT ON COLUMN result_conmed_schedule.cms_ae_num IS 'A value giving AE number for which medication taken';

COMMENT ON COLUMN result_conmed_schedule.cms_reason_stop IS 'Value describes the reason the treatment to stop';

COMMENT ON COLUMN result_conmed_schedule.cms_reason_stop_other IS 'Another value describes the reason the treatment to stop';

COMMENT ON COLUMN result_conmed_schedule.cms_inf_body_sys IS 'Value describes infected body system';

COMMENT ON COLUMN result_conmed_schedule.cms_inf_body_sys_other IS 'Value describes another infected body system';

COMMENT ON COLUMN result_conmed_schedule.cms_active_ingr_1 IS 'Value describes the active ingredient 1';

COMMENT ON COLUMN result_conmed_schedule.cms_active_ingr_2 IS 'Value describes the active ingredient 2';

COMMENT ON COLUMN result_conmed_schedule.cms_therapy_reason IS 'A value describes reason why medication was administrated to the subject';

CREATE TABLE result_consent (
    ic_id character varying(32) NOT NULL,
    ic_date_created timestamp without time zone,
    ic_date_updated timestamp without time zone,
    ic_unq_sha1 character varying(40),
    ic_sec_hash bigint,
    ic_pat_id character varying(32),
    ic_consent_date timestamp without time zone
);

COMMENT ON COLUMN result_consent.ic_consent_date IS 'The date and time on which informed consent was signed (Time)';

CREATE TABLE result_country (
    ctr_id character varying(32) NOT NULL,
    ctr_date_created timestamp without time zone,
    ctr_date_updated timestamp without time zone,
    ctr_country character varying(255),
    ctr_unq_sha1 character varying(255),
    ctr_sec_hash bigint,
    ctr_pat_id character varying(32),
    ctr_ref_sha1 character varying(255)
);

CREATE TABLE result_ctdna (
    ctd_id character varying(32) NOT NULL,
    ctd_date_created timestamp without time zone,
    ctd_date_updated timestamp without time zone,
    ctd_unq_sha1 character varying(40),
    ctd_sec_hash bigint,
    ctd_ref_sha1 character varying(255),
    ctd_pat_id character varying(32),
    ctd_gene character varying(255),
    ctd_mutation character varying(255) NOT NULL,
    ctd_reported_var_allele_freq numeric(11,4),
    ctd_sample_date timestamp without time zone NOT NULL,
    ctd_tracked_mutation character varying(255),
    ctd_visit_name character varying(255),
    ctd_visit_number numeric(11,2)
);

COMMENT ON COLUMN result_ctdna.ctd_id IS 'ID';

COMMENT ON COLUMN result_ctdna.ctd_date_created IS 'Record create date';

COMMENT ON COLUMN result_ctdna.ctd_date_updated IS 'Record update date';

COMMENT ON COLUMN result_ctdna.ctd_unq_sha1 IS 'SHA1 for unique fields';

COMMENT ON COLUMN result_ctdna.ctd_sec_hash IS 'SHA1 for secondary fields';

COMMENT ON COLUMN result_ctdna.ctd_ref_sha1 IS 'SHA1 for FK fields';

COMMENT ON COLUMN result_ctdna.ctd_pat_id IS 'FK to Patient';

COMMENT ON COLUMN result_ctdna.ctd_gene IS 'Gene';

COMMENT ON COLUMN result_ctdna.ctd_mutation IS 'Mutation';

COMMENT ON COLUMN result_ctdna.ctd_reported_var_allele_freq IS 'Reported variant allele frequency';

COMMENT ON COLUMN result_ctdna.ctd_sample_date IS 'Sample date';

COMMENT ON COLUMN result_ctdna.ctd_tracked_mutation IS 'Is mutation tracked. Options: YES, NO';

COMMENT ON COLUMN result_ctdna.ctd_visit_name IS 'Visit name';

CREATE TABLE result_cvot (
    cvot_id character varying(32) NOT NULL,
    cvot_date_created timestamp without time zone,
    cvot_date_updated timestamp without time zone,
    cvot_unq_sha1 character varying(40),
    cvot_sec_hash bigint,
    cvot_pat_id character varying(32),
    cvot_ae_num bigint,
    cvot_start_date timestamp without time zone,
    cvot_term character varying(255),
    cvot_category1 character varying(255),
    cvot_category2 character varying(255),
    cvot_category3 character varying(255),
    cvot_description1 character varying(255),
    cvot_description2 character varying(255),
    cvot_description3 character varying(255)
);

COMMENT ON COLUMN result_cvot.cvot_ae_num IS 'The number assigned to the associated AE record';

COMMENT ON COLUMN result_cvot.cvot_start_date IS 'The start date of the event';

COMMENT ON COLUMN result_cvot.cvot_term IS 'The term used to describe the event';

COMMENT ON COLUMN result_cvot.cvot_category1 IS 'The category of cerebrovascular event';

COMMENT ON COLUMN result_cvot.cvot_category2 IS 'The category of cerebrovascular event';

COMMENT ON COLUMN result_cvot.cvot_category3 IS 'The category of cerebrovascular event';

COMMENT ON COLUMN result_cvot.cvot_description1 IS 'Some event Description';

COMMENT ON COLUMN result_cvot.cvot_description2 IS 'Some event Description';

COMMENT ON COLUMN result_cvot.cvot_description3 IS 'Some event Description';

CREATE TABLE result_death (
    dth_id character varying(32) NOT NULL,
    dth_date_created timestamp without time zone,
    dth_date_updated timestamp without time zone,
    dth_date timestamp without time zone,
    dth_unq_sha1 character varying(255),
    dth_ref_sha1 character varying(255),
    dth_pat_id character varying(32),
    dth_sec_hash bigint,
    dth_cause character varying(255),
    dth_designation_of_cause character varying(255),
    dth_autopsy_performed character varying(255),
    dth_related_inv_disease character varying(255),
    dth_narrative_cause character varying(255),
    dth_preferred_term character varying(255),
    dth_llt character varying(255),
    dth_hlt character varying(255),
    dth_soc character varying(255)
);

COMMENT ON COLUMN result_death.dth_id IS 'ID';

COMMENT ON COLUMN result_death.dth_date_created IS 'Record create date';

COMMENT ON COLUMN result_death.dth_date_updated IS 'Record update date';

COMMENT ON COLUMN result_death.dth_date IS 'Date of death';

COMMENT ON COLUMN result_death.dth_unq_sha1 IS 'SHA1 for unique fields';

COMMENT ON COLUMN result_death.dth_pat_id IS 'FK to Patient';

COMMENT ON COLUMN result_death.dth_cause IS 'Disease that caused death';

COMMENT ON COLUMN result_death.dth_designation_of_cause IS 'Flag indicating that the cause is a primary or secondary';

COMMENT ON COLUMN result_death.dth_autopsy_performed IS 'Flag indicating that autopsy was performed or was not';

COMMENT ON COLUMN result_death.dth_related_inv_disease IS 'Flag indicating that death related to disease under investigation or doesnt';

COMMENT ON COLUMN result_death.dth_narrative_cause IS 'Free text describes cause of death';

COMMENT ON COLUMN result_death.dth_preferred_term IS 'MedDRA Preferred Term Name string assigned to the cause of death';

COMMENT ON COLUMN result_death.dth_llt IS 'MedDRA Lowest Level Term Name string assigned to the cause of death';

COMMENT ON COLUMN result_death.dth_hlt IS 'MedDRA High Level Term Name string assigned to the cause of death';

COMMENT ON COLUMN result_death.dth_soc IS 'MedDRA System Organ Class Name string assigned to the cause of death';

CREATE TABLE result_decg (
    decg_id character varying(32) NOT NULL,
    decg_date_created timestamp without time zone,
    decg_date_updated timestamp without time zone,
    decg_abnormality character varying(255),
    decg_measurment_label character varying(255),
    decg_measurment_value character varying(255),
    decg_unq_sha1 character varying(255),
    decg_tst_id character varying(32),
    decg_ref_sha1 character varying(255),
    decg_sec_hash bigint,
    decg_evaluation character varying(255),
    decg_significant character varying(255),
    decg_method character varying(255),
    decg_beat_group_num bigint,
    decg_beat_num_in_beat_group bigint,
    decg_num_beats_avr_beat bigint,
    decg_comment character varying(255),
    decg_sch_timepoint character varying(255),
    decg_beat_group_length_sec numeric(10,2)
);

COMMENT ON COLUMN result_decg.decg_id IS 'ID';

COMMENT ON COLUMN result_decg.decg_date_created IS 'Record create date';

COMMENT ON COLUMN result_decg.decg_date_updated IS 'Record update date';

COMMENT ON COLUMN result_decg.decg_abnormality IS 'Reason, Abnormal Overall ECG Evaluation';

COMMENT ON COLUMN result_decg.decg_measurment_label IS 'ECG Test or Examination Short Name';

COMMENT ON COLUMN result_decg.decg_measurment_value IS 'Numeric Result/Finding in Standard Units';

COMMENT ON COLUMN result_decg.decg_unq_sha1 IS 'SHA1 for unique fields';

COMMENT ON COLUMN result_decg.decg_tst_id IS 'FK to Test';

COMMENT ON COLUMN result_decg.decg_ref_sha1 IS 'SHA1 for FK fields';

COMMENT ON COLUMN result_decg.decg_method IS 'A value that describes used method of ECG';

COMMENT ON COLUMN result_decg.decg_beat_group_num IS 'The sequential number of the continuous DECG extraction counted across study period';

COMMENT ON COLUMN result_decg.decg_beat_num_in_beat_group IS 'The sequential number of the individual beat within the continuous DECG extraction';

COMMENT ON COLUMN result_decg.decg_num_beats_avr_beat IS 'The number of beats used to construct the averaged representative wave form';

COMMENT ON COLUMN result_decg.decg_comment IS 'A value that provides the cardiologist comment';

CREATE TABLE result_disease_extent (
    de_id character varying(32) NOT NULL,
    de_date_created timestamp without time zone,
    de_date_updated timestamp without time zone,
    de_unq_sha1 character varying(40),
    de_sec_hash bigint,
    de_pat_id character varying(32),
    de_meta_loc_adv character varying(255),
    de_site_local_meta_dis character varying(255),
    de_oth_loc_adv_sites character varying(255),
    de_oth_meta_sites character varying(255),
    de_visit_date timestamp without time zone,
    de_rec_prgs_date timestamp without time zone,
    de_recu_earl_cancer character varying(255)
);

COMMENT ON COLUMN result_disease_extent.de_meta_loc_adv IS 'A value specifies if cancer locally advanced or metastatic';

COMMENT ON COLUMN result_disease_extent.de_site_local_meta_dis IS 'A value specifies location of local or metastatic disease';

COMMENT ON COLUMN result_disease_extent.de_oth_loc_adv_sites IS 'A value specifies alternative location of local disease';

COMMENT ON COLUMN result_disease_extent.de_oth_meta_sites IS 'A value specifies alternative location of metastatic disease';

COMMENT ON COLUMN result_disease_extent.de_visit_date IS 'The date of the subject visit';

COMMENT ON COLUMN result_disease_extent.de_rec_prgs_date IS 'The date of recent progression';

COMMENT ON COLUMN result_disease_extent.de_recu_earl_cancer IS 'Flag indicating that the disease is recurrence of earlier cancer (yes/no)';

CREATE TABLE result_drug (
    drug_id character varying(32) NOT NULL,
    drug_name character varying(100),
    drug_date_created timestamp without time zone,
    drug_date_updated timestamp without time zone,
    drug_unq_sha1 character varying(40),
    drug_sec_hash bigint,
    drug_std_id character varying(32)
);

COMMENT ON COLUMN result_drug.drug_id IS 'DRUG_ID is Drug Name';

CREATE TABLE result_ecg (
    ecg_id character varying(32) NOT NULL,
    ecg_date_created timestamp without time zone,
    ecg_date_updated timestamp without time zone,
    ecg_abnormality character varying(255),
    ecg_evaluation character varying(255),
    ecg_pr bigint,
    ecg_qrs bigint,
    ecg_qt bigint,
    ecg_rr bigint,
    ecg_unq_sha1 character varying(255),
    ecg_tst_id character varying(32),
    ecg_ref_sha1 character varying(255),
    ecg_sec_hash bigint,
    ecg_qtcf bigint,
    ecg_significant character varying(255)
);

COMMENT ON COLUMN result_ecg.ecg_id IS 'ID';

COMMENT ON COLUMN result_ecg.ecg_date_created IS 'Record create date';

COMMENT ON COLUMN result_ecg.ecg_date_updated IS 'Record update date';

COMMENT ON COLUMN result_ecg.ecg_abnormality IS 'Reason, Abnormal Overall ECG Evaluation';

COMMENT ON COLUMN result_ecg.ecg_evaluation IS 'Overall ECG evaluation';

COMMENT ON COLUMN result_ecg.ecg_pr IS 'PR interval duration';

COMMENT ON COLUMN result_ecg.ecg_qrs IS 'QRS Duration';

COMMENT ON COLUMN result_ecg.ecg_qt IS 'QT interval duration';

COMMENT ON COLUMN result_ecg.ecg_rr IS 'RR interval duration';

COMMENT ON COLUMN result_ecg.ecg_unq_sha1 IS 'SHA1 for unique fields';

COMMENT ON COLUMN result_ecg.ecg_tst_id IS 'FK to Test';

COMMENT ON COLUMN result_ecg.ecg_ref_sha1 IS 'SHA1 for FK fields';

CREATE TABLE result_ediary (
    ediary_id character varying(32) NOT NULL,
    ediary_date_created timestamp without time zone,
    ediary_date_updated timestamp without time zone,
    ediary_assessment_date timestamp without time zone NOT NULL,
    ediary_drug_intake_time character varying(10),
    ediary_device_type character varying(255),
    ediary_assessment_time_morning character varying(10),
    ediary_pef_morning numeric(5,1),
    ediary_assessment_time_evening character varying(10),
    ediary_pef_evening numeric(5,1),
    ediary_asthma_score_night smallint,
    ediary_asthma_score_day smallint,
    ediary_woke_due_to_asthma character varying(10),
    ediary_unq_sha1 character varying(255),
    ediary_sec_hash bigint,
    ediary_pat_id character varying(32) NOT NULL,
    ediary_ref_sha1 character varying(255)
);

COMMENT ON COLUMN result_ediary.ediary_id IS 'ID';

COMMENT ON COLUMN result_ediary.ediary_date_created IS 'Record create date';

COMMENT ON COLUMN result_ediary.ediary_date_updated IS 'Record update date';

COMMENT ON COLUMN result_ediary.ediary_assessment_date IS 'Assessment date';

COMMENT ON COLUMN result_ediary.ediary_drug_intake_time IS 'Time for study drug intake';

COMMENT ON COLUMN result_ediary.ediary_device_type IS 'Device or study drug name';

COMMENT ON COLUMN result_ediary.ediary_assessment_time_morning IS 'Assessment time (morning)';

COMMENT ON COLUMN result_ediary.ediary_pef_morning IS 'PEF morning (L/min)';

COMMENT ON COLUMN result_ediary.ediary_assessment_time_evening IS 'Assessment time (evening)';

COMMENT ON COLUMN result_ediary.ediary_pef_evening IS 'PEF evening (L/min)';

COMMENT ON COLUMN result_ediary.ediary_asthma_score_night IS 'Asthma symptom score (night-time)';

COMMENT ON COLUMN result_ediary.ediary_asthma_score_day IS 'Asthma symptom score (day-time)';

COMMENT ON COLUMN result_ediary.ediary_woke_due_to_asthma IS 'Woke due to asthma';

COMMENT ON COLUMN result_ediary.ediary_unq_sha1 IS 'SHA1 for unique fields';

COMMENT ON COLUMN result_ediary.ediary_pat_id IS 'FK to Patient';

COMMENT ON COLUMN result_ediary.ediary_ref_sha1 IS 'SHA1 for FK fields';

CREATE TABLE result_ediary_medication_usage (
    eme_id character varying(32) NOT NULL,
    eme_date_created timestamp without time zone,
    eme_date_updated timestamp without time zone,
    eme_assessment_date timestamp without time zone NOT NULL,
    eme_drug_intake_time character varying(10),
    eme_device_type character varying(255),
    eme_unq_sha1 character varying(255),
    eme_sec_hash bigint,
    eme_pat_id character varying(32) NOT NULL,
    eme_ref_sha1 character varying(255)
);

COMMENT ON COLUMN result_ediary_medication_usage.eme_id IS 'ID';

COMMENT ON COLUMN result_ediary_medication_usage.eme_date_created IS 'Record create date';

COMMENT ON COLUMN result_ediary_medication_usage.eme_date_updated IS 'Record update date';

COMMENT ON COLUMN result_ediary_medication_usage.eme_assessment_date IS 'Assessment date';

COMMENT ON COLUMN result_ediary_medication_usage.eme_drug_intake_time IS 'Time for study drug intake';

COMMENT ON COLUMN result_ediary_medication_usage.eme_device_type IS 'Device or study drug name';

COMMENT ON COLUMN result_ediary_medication_usage.eme_unq_sha1 IS 'SHA1 for unique fields';

COMMENT ON COLUMN result_ediary_medication_usage.eme_pat_id IS 'FK to Patient';

COMMENT ON COLUMN result_ediary_medication_usage.eme_ref_sha1 IS 'SHA1 for FK fields';

CREATE TABLE result_ediary_observations (
    eo_id character varying(32) NOT NULL,
    eo_date_created timestamp without time zone,
    eo_date_updated timestamp without time zone,
    eo_assessment_date timestamp without time zone NOT NULL,
    eo_assessment_time_morning character varying(10),
    eo_pef_morning numeric(5,1),
    eo_assessment_time_evening character varying(10),
    eo_pef_evening numeric(5,1),
    eo_asthma_score_night smallint,
    eo_asthma_score_day smallint,
    eo_woke_due_to_asthma character varying(10),
    eo_unq_sha1 character varying(255),
    eo_sec_hash bigint,
    eo_pat_id character varying(32) NOT NULL,
    eo_ref_sha1 character varying(255)
);

COMMENT ON COLUMN result_ediary_observations.eo_id IS 'ID';

COMMENT ON COLUMN result_ediary_observations.eo_date_created IS 'Record create date';

COMMENT ON COLUMN result_ediary_observations.eo_date_updated IS 'Record update date';

COMMENT ON COLUMN result_ediary_observations.eo_assessment_date IS 'Assessment date';

COMMENT ON COLUMN result_ediary_observations.eo_assessment_time_morning IS 'Assessment time (morning)';

COMMENT ON COLUMN result_ediary_observations.eo_pef_morning IS 'PEF morning (L/min)';

COMMENT ON COLUMN result_ediary_observations.eo_assessment_time_evening IS 'Assessment time (evening)';

COMMENT ON COLUMN result_ediary_observations.eo_pef_evening IS 'PEF evening (L/min)';

COMMENT ON COLUMN result_ediary_observations.eo_asthma_score_night IS 'Asthma symptom score (night-time)';

COMMENT ON COLUMN result_ediary_observations.eo_asthma_score_day IS 'Asthma symptom score (day-time)';

COMMENT ON COLUMN result_ediary_observations.eo_woke_due_to_asthma IS 'Woke due to asthma';

COMMENT ON COLUMN result_ediary_observations.eo_unq_sha1 IS 'SHA1 for unique fields';

COMMENT ON COLUMN result_ediary_observations.eo_pat_id IS 'FK to Patient';

COMMENT ON COLUMN result_ediary_observations.eo_ref_sha1 IS 'SHA1 for FK fields';

CREATE TABLE result_eg (
    eg_id character varying(32) NOT NULL,
    eg_test_name character varying(255),
    eg_test_result numeric,
    eg_result_unit character varying(255),
    eg_abnormality character varying(255),
    eg_evaluation character varying(255),
    eg_tst_id character varying(32),
    eg_date_created timestamp without time zone,
    eg_date_updated timestamp without time zone,
    eg_unq_sha1 character varying(255),
    eg_ref_sha1 character varying(255),
    eg_sec_hash bigint,
    eg_significant character varying(255),
    eg_date_last_dose timestamp without time zone,
    eg_last_dose_amount character varying(255),
    eg_method character varying(255),
    eg_atrial_fibr character varying(255),
    eg_sinus_rhythm character varying(255),
    eg_reas_no_sinus_rhythm character varying(255),
    eg_heart_rhythm character varying(255),
    eg_heart_rhythm_oth character varying(255),
    eg_extra_systoles character varying(255),
    eg_specify_extra_systoles character varying(255),
    eg_type_cond character varying(255),
    eg_cond character varying(255),
    eg_reas_abnormal_cond character varying(255),
    eg_stt_changes character varying(255),
    eg_st_segment character varying(255),
    eg_wave character varying(255),
    eg_sch_timepoint character varying(255)
);

COMMENT ON COLUMN result_eg.eg_date_last_dose IS 'The date on which the drug was taken the last time (Time)';

COMMENT ON COLUMN result_eg.eg_last_dose_amount IS 'A value that provides the last drug dose';

COMMENT ON COLUMN result_eg.eg_method IS 'A value that describes used method of ECG';

COMMENT ON COLUMN result_eg.eg_atrial_fibr IS 'A flag indicating if atrial fibrillation is present (yes/no)';

COMMENT ON COLUMN result_eg.eg_sinus_rhythm IS 'A flag indicating if sinus rhythm is revealed (yes/no)';

COMMENT ON COLUMN result_eg.eg_reas_no_sinus_rhythm IS 'A value that describes the reason of sinus rhythm absence';

COMMENT ON COLUMN result_eg.eg_heart_rhythm IS 'A value that describes the heart rhythm';

COMMENT ON COLUMN result_eg.eg_heart_rhythm_oth IS 'A value that describes the other heart rhythm';

COMMENT ON COLUMN result_eg.eg_extra_systoles IS 'A flag indicating if extra systoles are revealed (yes/no)';

COMMENT ON COLUMN result_eg.eg_specify_extra_systoles IS 'A value that provides the specification of extra systoles';

COMMENT ON COLUMN result_eg.eg_type_cond IS 'A value that describes the type of conduction';

COMMENT ON COLUMN result_eg.eg_cond IS 'A flag indicating if extra systoles are normal or abnormal';

COMMENT ON COLUMN result_eg.eg_reas_abnormal_cond IS 'A value that describes the reason of abnormal conduction';

COMMENT ON COLUMN result_eg.eg_stt_changes IS 'A flag indicating if ST-T changes are revealed (yes/no)';

COMMENT ON COLUMN result_eg.eg_st_segment IS 'A value that describes the ST segment';

COMMENT ON COLUMN result_eg.eg_wave IS 'A value that describes the T-wave';

CREATE TABLE result_event_type (
    evt_id character varying(32) NOT NULL,
    evt_date_created timestamp without time zone,
    evt_date_updated timestamp without time zone,
    evt_hlt character varying(255),
    evt_llt character varying(255),
    evt_pt character varying(255),
    evt_soc character varying(255),
    evt_unq_sha1 character varying(255),
    evt_std_id character varying(32),
    evt_ref_sha1 character varying(255),
    evt_meddra_version numeric(12,2),
    evt_sec_hash bigint
);

COMMENT ON COLUMN result_event_type.evt_id IS 'ID';

COMMENT ON COLUMN result_event_type.evt_date_created IS 'Record create date';

COMMENT ON COLUMN result_event_type.evt_date_updated IS 'Record update date';

COMMENT ON COLUMN result_event_type.evt_hlt IS 'MEDDRA Higher-Level Term';

COMMENT ON COLUMN result_event_type.evt_llt IS 'MEDDRA Low-Level Term';

COMMENT ON COLUMN result_event_type.evt_pt IS 'MEDDRA Preferred term.';

COMMENT ON COLUMN result_event_type.evt_soc IS 'MEDDRA System Organ Class';

COMMENT ON COLUMN result_event_type.evt_unq_sha1 IS 'SHA1 for unique fields';

COMMENT ON COLUMN result_event_type.evt_std_id IS 'FK to Study';

COMMENT ON COLUMN result_event_type.evt_ref_sha1 IS 'SHA1 for FK fields';

COMMENT ON COLUMN result_event_type.evt_meddra_version IS 'MEDDRA Version';

CREATE TABLE result_exa_severity_map (
    esm_id character varying(32) NOT NULL,
    esm_std_id character varying(32) NOT NULL,
    esm_severity character varying(255),
    esm_depot_gcs character varying(255),
    esm_syscort_trt character varying(255),
    esm_ics_trt character varying(255),
    esm_antibiotics_trt character varying(255),
    esm_hospit character varying(255),
    esm_emer_trt character varying(255),
    esm_date_created timestamp without time zone,
    esm_date_updated timestamp without time zone,
    esm_unq_sha1 character varying(255),
    esm_sec_hash bigint,
    esm_ref_sha1 character varying(255)
);

CREATE TABLE result_exacerbation (
    exa_id character varying(32) NOT NULL,
    exa_pat_id character varying(32) NOT NULL,
    exa_exac_start_date timestamp without time zone,
    exa_exac_end_date timestamp without time zone,
    exa_ltpca character varying(255),
    exa_ltpca_start_date timestamp without time zone,
    exa_ltpca_end_date timestamp without time zone,
    exa_ediary_alert_date timestamp without time zone,
    exa_depot_gcs character varying(255),
    exa_depot_gcs_start_date timestamp without time zone,
    exa_syscort_trt character varying(255),
    exa_syscort_trt_start_date timestamp without time zone,
    exa_syscort_trt_end_date timestamp without time zone,
    exa_antibiotics_trt character varying(255),
    exa_antibiotics_trt_start_date timestamp without time zone,
    exa_antibiotics_trt_end_date timestamp without time zone,
    exa_emer_trt character varying(255),
    exa_emer_trt_date timestamp without time zone,
    exa_hospit character varying(255),
    exa_hospit_start_date timestamp without time zone,
    exa_hospit_end_date timestamp without time zone,
    exa_ics_trt character varying(255),
    exa_ics_start_date timestamp without time zone,
    exa_ics_end_date timestamp without time zone,
    exa_date_created timestamp without time zone,
    exa_date_updated timestamp without time zone,
    exa_unq_sha1 character varying(255),
    exa_sec_hash bigint,
    exa_ref_sha1 character varying(255),
    exa_severity character varying(100)
);

COMMENT ON COLUMN result_exacerbation.exa_exac_start_date IS 'The start date of the exacerbation';

COMMENT ON COLUMN result_exacerbation.exa_exac_end_date IS 'The end date of the exacerbation';

COMMENT ON COLUMN result_exacerbation.exa_ltpca IS 'Long term poorly controlled asthma';

COMMENT ON COLUMN result_exacerbation.exa_ltpca_start_date IS 'Long term poorly controlled asthma start date';

COMMENT ON COLUMN result_exacerbation.exa_ltpca_end_date IS 'Long term poorly controlled asthma end date';

COMMENT ON COLUMN result_exacerbation.exa_ediary_alert_date IS 'e-diary alert date';

COMMENT ON COLUMN result_exacerbation.exa_depot_gcs IS 'Whether a depot glucocorticoidsteroid treatment was required';

COMMENT ON COLUMN result_exacerbation.exa_depot_gcs_start_date IS 'Depot glucocorticosteroid start date';

COMMENT ON COLUMN result_exacerbation.exa_syscort_trt IS 'Whether systemic corticosteroid treatment has been administered.';

COMMENT ON COLUMN result_exacerbation.exa_syscort_trt_start_date IS 'The start of systemic corticosteroid treatment';

COMMENT ON COLUMN result_exacerbation.exa_syscort_trt_end_date IS 'The end of systemic corticosteroid treatment';

COMMENT ON COLUMN result_exacerbation.exa_antibiotics_trt IS 'Whether antibiotic treatment has been administered.';

COMMENT ON COLUMN result_exacerbation.exa_antibiotics_trt_start_date IS 'The start of antibiotics treatment';

COMMENT ON COLUMN result_exacerbation.exa_antibiotics_trt_end_date IS 'The end of antibiotics treatment';

COMMENT ON COLUMN result_exacerbation.exa_emer_trt IS 'Whether the exacerbation caused emergency treatment';

COMMENT ON COLUMN result_exacerbation.exa_emer_trt_date IS 'The date of emergency treatment';

COMMENT ON COLUMN result_exacerbation.exa_hospit IS 'Whether the exacerbation required hospitalisation';

COMMENT ON COLUMN result_exacerbation.exa_hospit_start_date IS 'The hospitalisation start date';

COMMENT ON COLUMN result_exacerbation.exa_hospit_end_date IS 'The hospitalisation end date';

COMMENT ON COLUMN result_exacerbation.exa_ics_trt IS 'Whether inhaled corticosteroid treatment dosage has been increased.';

COMMENT ON COLUMN result_exacerbation.exa_ics_start_date IS 'The start of increase in inhaled corticosteroid treatment';

COMMENT ON COLUMN result_exacerbation.exa_ics_end_date IS 'The end to increased in inhaled corticosteroid treatment';

CREATE TABLE result_fm_gene (
    fm_id character varying(32) NOT NULL,
    fm_date_created timestamp without time zone,
    fm_date_updated timestamp without time zone,
    fm_sample_id character varying(255),
    fm_disease character varying(255),
    fm_median_exon_coverage bigint,
    fm_known_variants character varying(1024),
    fm_likely_variants character varying(1024),
    fm_high_lvl_amplifications character varying(1024),
    fm_low_lvl_amplifications character varying(1024),
    fm_deletions character varying(1024),
    fm_rearrangements character varying(1024),
    fm_comments character varying(1024),
    fm_unq_sha1 character varying(255),
    fm_sec_hash bigint,
    fm_pat_id character varying(32) NOT NULL,
    fm_ref_sha1 character varying(255)
);

COMMENT ON COLUMN result_fm_gene.fm_id IS 'ID';

COMMENT ON COLUMN result_fm_gene.fm_date_created IS 'Record create date';

COMMENT ON COLUMN result_fm_gene.fm_date_updated IS 'Record update date';

COMMENT ON COLUMN result_fm_gene.fm_sample_id IS 'Sample ID';

COMMENT ON COLUMN result_fm_gene.fm_disease IS 'Disease';

COMMENT ON COLUMN result_fm_gene.fm_median_exon_coverage IS 'Median exon coverage';

COMMENT ON COLUMN result_fm_gene.fm_known_variants IS 'Known somatic short variants (percent-reads, coverage)';

COMMENT ON COLUMN result_fm_gene.fm_likely_variants IS 'Likely somatic short variants (percent-reads, coverage)';

COMMENT ON COLUMN result_fm_gene.fm_high_lvl_amplifications IS 'High-level (CN>8) and focal (CN>5) amplifications of genes amplifications of genes known to be recurrently amplified in cancer (copy number, exons)';

COMMENT ON COLUMN result_fm_gene.fm_low_lvl_amplifications IS 'Non-focal lower-level (CN<=8) amplifications of genes known to be recurrently amplified in cancer (copy-number, exons)';

COMMENT ON COLUMN result_fm_gene.fm_deletions IS 'Homozygous deletions of genes known to be recurrently deleted in cancer (0,exons)	';

COMMENT ON COLUMN result_fm_gene.fm_rearrangements IS 'Likely functional rearrangements (e.g., gene fusions) (gene1_gene2_genomic event description_supporting-reads)	';

COMMENT ON COLUMN result_fm_gene.fm_comments IS 'Comments';

CREATE TABLE result_lab_group (
    lgr_id character varying(32) NOT NULL,
    lgr_date_created timestamp without time zone,
    lgr_date_updated timestamp without time zone,
    lgr_name character varying(255),
    lgr_lab_code character varying(255),
    lgr_lab_descr character varying(255),
    lgr_unq_sha1 character varying(255),
    lgr_std_id character varying(32),
    lgr_ref_sha1 character varying(255),
    lgr_sec_hash bigint
);

COMMENT ON COLUMN result_lab_group.lgr_id IS 'ID';

COMMENT ON COLUMN result_lab_group.lgr_date_created IS 'Record create date';

COMMENT ON COLUMN result_lab_group.lgr_date_updated IS 'Record update date';

COMMENT ON COLUMN result_lab_group.lgr_lab_code IS 'Laboratory Test Identifier';

COMMENT ON COLUMN result_lab_group.lgr_lab_descr IS 'Group Name';

COMMENT ON COLUMN result_lab_group.lgr_unq_sha1 IS 'SHA1 for unique fields';

COMMENT ON COLUMN result_lab_group.lgr_std_id IS 'FK to Study';

COMMENT ON COLUMN result_lab_group.lgr_ref_sha1 IS 'SHA1 for FK fields';

CREATE TABLE result_laboratory (
    lab_id character varying(32) NOT NULL,
    lab_date_created timestamp without time zone,
    lab_date_updated timestamp without time zone,
    lab_code character varying(255),
    lab_unit character varying(255),
    lab_value numeric(19,2),
    lab_ref_high numeric(19,2),
    lab_ref_low numeric(19,2),
    lab_unq_sha1 character varying(255),
    lab_lgr_id character varying(32),
    lab_tst_id character varying(32),
    lab_ref_sha1 character varying(255),
    lab_sec_hash bigint,
    lab_comment character varying(255),
    lab_value_dipstick character varying(255),
    lab_sch_timepoint character varying(255),
    lab_src_type character varying(255) DEFAULT 'Sponsor'::character varying NOT NULL,
    lab_src_id character varying(32)
);

COMMENT ON COLUMN result_laboratory.lab_id IS 'ID';

COMMENT ON COLUMN result_laboratory.lab_date_created IS 'Record create date';

COMMENT ON COLUMN result_laboratory.lab_date_updated IS 'Record update date';

COMMENT ON COLUMN result_laboratory.lab_code IS 'Laboratory Test Identifier';

COMMENT ON COLUMN result_laboratory.lab_unit IS 'Laboratory Test Unit';

COMMENT ON COLUMN result_laboratory.lab_value IS 'Laboratory Value';

COMMENT ON COLUMN result_laboratory.lab_ref_high IS 'Upper Reference Value';

COMMENT ON COLUMN result_laboratory.lab_ref_low IS 'Lower reference Value';

COMMENT ON COLUMN result_laboratory.lab_unq_sha1 IS 'SHA1 for unique fields';

COMMENT ON COLUMN result_laboratory.lab_lgr_id IS 'FK to Lab Group';

COMMENT ON COLUMN result_laboratory.lab_tst_id IS 'FK to Study';

COMMENT ON COLUMN result_laboratory.lab_ref_sha1 IS 'SHA1 for FK fields';

COMMENT ON COLUMN result_laboratory.lab_value_dipstick IS 'A value describes laboratory value dipstick';

COMMENT ON COLUMN result_laboratory.lab_sch_timepoint IS 'A value that describes the protocol schedule timepoint of the measurement';

COMMENT ON COLUMN result_laboratory.lab_src_type IS 'Source (sponsor/patient etc.)';

COMMENT ON COLUMN result_laboratory.lab_src_id IS 'FK to RESULT_LAB_SOURCE table';

CREATE TABLE result_liver (
    li_id character varying(32) NOT NULL,
    li_date_created timestamp without time zone,
    li_date_updated timestamp without time zone,
    li_unq_sha1 character varying(40),
    li_sec_hash bigint,
    li_pat_id character varying(32),
    li_value character varying(255),
    li_date timestamp without time zone,
    li_res character varying(255),
    li_pot_hys_law_case_num bigint,
    li_spec character varying(255)
);

COMMENT ON COLUMN result_liver.li_value IS 'A value that describes the method used to perform the liver diagnostic investigation';

COMMENT ON COLUMN result_liver.li_date IS 'The date on which the liver diagnostic investigation was performed';

COMMENT ON COLUMN result_liver.li_res IS 'A value that describes the results of the liver diagnostic investigation';

COMMENT ON COLUMN result_liver.li_pot_hys_law_case_num IS 'A value denoting potential Hy`s law case number';

COMMENT ON COLUMN result_liver.li_spec IS 'A value that describes the alternative method used to perform the liver diagnostic investigation';

CREATE TABLE result_liver_risk_factors (
    lrf_id character varying(32) NOT NULL,
    lrf_date_created timestamp without time zone,
    lrf_date_updated timestamp without time zone,
    lrf_unq_sha1 character varying(40),
    lrf_sec_hash bigint,
    lrf_pat_id character varying(32),
    lrf_pot_hys_law_case_num bigint,
    lrf_value character varying(255),
    lrf_occur character varying(255),
    lrf_ref_period character varying(255),
    lrf_details character varying(255),
    lrf_start_date timestamp without time zone,
    lrf_stop_date timestamp without time zone,
    lrf_comment character varying(255)
);

COMMENT ON COLUMN result_liver_risk_factors.lrf_pot_hys_law_case_num IS 'A value denoting potential Hys law case number';

COMMENT ON COLUMN result_liver_risk_factors.lrf_value IS 'A value that describes the liver risk factor';

COMMENT ON COLUMN result_liver_risk_factors.lrf_occur IS 'A flag indicating the liver risk factor occurrence (yes/no)';

COMMENT ON COLUMN result_liver_risk_factors.lrf_ref_period IS 'A value denoting the liver risk factor reference period (past or current)';

COMMENT ON COLUMN result_liver_risk_factors.lrf_details IS 'A value that describes the details of the liver risk factor';

COMMENT ON COLUMN result_liver_risk_factors.lrf_start_date IS 'The date on which the liver risk factor was started';

COMMENT ON COLUMN result_liver_risk_factors.lrf_stop_date IS 'The date on which the liver risk factor was stopped';

COMMENT ON COLUMN result_liver_risk_factors.lrf_comment IS 'A value that provides a liver risk factor comment';

CREATE TABLE result_liverss (
    lss_id character varying(32) NOT NULL,
    lss_date_created timestamp without time zone,
    lss_date_updated timestamp without time zone,
    lss_unq_sha1 character varying(40),
    lss_sec_hash bigint,
    lss_pat_id character varying(32),
    lss_pot_hys_law_case_num bigint,
    lss_value character varying(255),
    lss_occurrence character varying(255),
    lss_start_date timestamp without time zone,
    lss_stop_date timestamp without time zone,
    lss_intermittent character varying(255),
    lss_spec character varying(255),
    lss_value_text character varying(255),
    lss_pt character varying(255),
    lss_llt character varying(255),
    lss_hlt character varying(255),
    lss_soc character varying(255),
    ctd_gene character varying(255) NOT NULL,
    ctd_mutation character varying(255) NOT NULL,
    ctd_sample_date timestamp without time zone NOT NULL,
    ctd_tracked_mutation character varying(255),
    ctd_visit_name character varying(255),
    ctd_visit_number character varying(255),
    ctd_reported_var_allele_freq numeric(11,4) NOT NULL
);

COMMENT ON COLUMN result_liverss.lss_pot_hys_law_case_num IS 'A value denoting potential Hys law case number';

COMMENT ON COLUMN result_liverss.lss_value IS 'A value that describes the liver sign/symptom';

COMMENT ON COLUMN result_liverss.lss_occurrence IS 'A value that describes the liver sign/symptom occurrence (yes/no)';

COMMENT ON COLUMN result_liverss.lss_start_date IS 'The date on which the liver sign/symptom was started';

COMMENT ON COLUMN result_liverss.lss_stop_date IS 'The date on which the liver sign/symptom was stopped';

COMMENT ON COLUMN result_liverss.lss_intermittent IS 'A flag indicating that the liver sign/symptom is intermittent (yes/no)';

COMMENT ON COLUMN result_liverss.lss_spec IS 'A value that describes the liver sign/symptom specification';

COMMENT ON COLUMN result_liverss.lss_value_text IS 'A value denoting the liver sign/symptom text';

COMMENT ON COLUMN result_liverss.lss_pt IS 'MedDRA Preferred Term string assigned to the liver sign/symptom';

COMMENT ON COLUMN result_liverss.lss_llt IS 'MedDRA Lowest Level Term Name string assigned to the liver sign/symptom';

COMMENT ON COLUMN result_liverss.lss_hlt IS 'MedDRA High Level Term Name string assigned to the liver sign/symptom';

COMMENT ON COLUMN result_liverss.lss_soc IS '	MedDRA System Organ Class string assigned to the liver sign/symptom';

CREATE TABLE result_lungfunc (
    lng_id character varying(32) NOT NULL,
    lng_pat_id character varying(32) NOT NULL,
    lng_visit numeric(10,2),
    lng_visit_date timestamp without time zone,
    lng_assess_date timestamp without time zone,
    lng_prot_schedule character varying(255),
    lng_measurement character varying(255),
    lng_result numeric(19,2),
    lng_date_created timestamp without time zone,
    lng_date_updated timestamp without time zone,
    lng_unq_sha1 character varying(255),
    lng_sec_hash bigint,
    lng_ref_sha1 character varying(255),
    lng_dlco numeric(10,2),
    lng_tot_lung_cap numeric(10,2),
    lng_insp_ox_frac numeric(10,2)
);

COMMENT ON COLUMN result_lungfunc.lng_dlco IS 'A value denoting diffusing capacity of the lung for carbon monoxide';

COMMENT ON COLUMN result_lungfunc.lng_tot_lung_cap IS 'A value denoting total lung capacity (TLC (L))';

COMMENT ON COLUMN result_lungfunc.lng_insp_ox_frac IS 'A value denoting fraction of inspired oxygen	';

CREATE TABLE result_lvef (
    lvf_id character varying(32) NOT NULL,
    lvf_date_created timestamp without time zone,
    lvf_date_updated timestamp without time zone,
    lvf_lvef bigint,
    lvf_method character varying(255),
    lvf_method_other character varying(255),
    lvf_unq_sha1 character varying(255),
    lvf_tst_id character varying(32),
    lvf_ref_sha1 character varying(255),
    lvf_sec_hash bigint
);

COMMENT ON COLUMN result_lvef.lvf_id IS 'ID';

COMMENT ON COLUMN result_lvef.lvf_date_created IS 'Record create date';

COMMENT ON COLUMN result_lvef.lvf_date_updated IS 'Record update date';

COMMENT ON COLUMN result_lvef.lvf_lvef IS 'Left ventricular ejection fraction';

COMMENT ON COLUMN result_lvef.lvf_method IS 'LVEF Method';

COMMENT ON COLUMN result_lvef.lvf_method_other IS 'LVEF Method, Other';

COMMENT ON COLUMN result_lvef.lvf_unq_sha1 IS 'SHA1 for unique fields';

COMMENT ON COLUMN result_lvef.lvf_tst_id IS 'FK to Test';

COMMENT ON COLUMN result_lvef.lvf_ref_sha1 IS 'SHA1 for FK fields';

CREATE TABLE result_medical_history (
    mh_id character varying(32) NOT NULL,
    mh_date_created timestamp without time zone,
    mh_date_updated timestamp without time zone,
    mh_unq_sha1 character varying(40),
    mh_sec_hash bigint,
    mh_condition_status character varying(255),
    mh_start_date timestamp without time zone,
    mh_end_date timestamp without time zone,
    mh_llt_name character varying(255),
    mh_pt_name character varying(255),
    mh_hlt_name character varying(255),
    mh_soc_name character varying(255),
    mh_current_medication character varying(255),
    mh_pat_id character varying(32),
    mh_category character varying(255),
    mh_term character varying(255),
    mh_medical_condition character varying(255)
);

CREATE TABLE result_medicine (
    med_id character varying(32) NOT NULL,
    med_date_created timestamp without time zone,
    med_date_updated timestamp without time zone,
    med_unq_sha1 character varying(255),
    med_drug_name character varying(255),
    med_drug_parent character varying(255),
    med_std_id character varying(32),
    med_ref_sha1 character varying(255),
    med_sec_hash bigint
);

COMMENT ON COLUMN result_medicine.med_id IS 'ID';

COMMENT ON COLUMN result_medicine.med_date_created IS 'Record create date';

COMMENT ON COLUMN result_medicine.med_date_updated IS 'Record update date';

COMMENT ON COLUMN result_medicine.med_unq_sha1 IS 'SHA1 for unique fields';

COMMENT ON COLUMN result_medicine.med_drug_name IS 'Medication Name';

COMMENT ON COLUMN result_medicine.med_drug_parent IS 'Medication Group';

COMMENT ON COLUMN result_medicine.med_std_id IS 'FK to Study';

COMMENT ON COLUMN result_medicine.med_ref_sha1 IS 'SHA1 for FK fields';

CREATE TABLE result_nicotine_sub_use (
    nsu_id character varying(32) NOT NULL,
    nsu_date_created timestamp without time zone,
    nsu_date_updated timestamp without time zone,
    nsu_unq_sha1 character varying(40),
    nsu_sec_hash bigint,
    nsu_pat_id character varying(32),
    nsu_category character varying(255),
    nsu_type character varying(255),
    nsu_oth_type_spec character varying(255),
    nsu_use_occur character varying(255),
    nsu_current_use_spec character varying(255),
    nsu_use_start_date character varying(255),
    nsu_use_end_date character varying(255),
    nsu_consumption numeric(10,2),
    nsu_use_freq_interval character varying(255),
    nsu_sub_type_use_occur character varying(255),
    nsu_num_pack_years numeric
);

COMMENT ON COLUMN result_nicotine_sub_use.nsu_category IS 'A value describes substance use category';

COMMENT ON COLUMN result_nicotine_sub_use.nsu_type IS 'A value describes type of substance';

COMMENT ON COLUMN result_nicotine_sub_use.nsu_oth_type_spec IS 'A value describes alternative type of substance';

COMMENT ON COLUMN result_nicotine_sub_use.nsu_use_occur IS 'A value describes usage occurrence (never, current, former)';

COMMENT ON COLUMN result_nicotine_sub_use.nsu_current_use_spec IS 'A value describes current substance use specification (habitual or occasional)';

COMMENT ON COLUMN result_nicotine_sub_use.nsu_use_start_date IS 'The date on which the subject started substance usage';

COMMENT ON COLUMN result_nicotine_sub_use.nsu_use_end_date IS 'The date on which the subject ended substance usage';

COMMENT ON COLUMN result_nicotine_sub_use.nsu_consumption IS 'A value describes substance consumption (Units)';

COMMENT ON COLUMN result_nicotine_sub_use.nsu_use_freq_interval IS 'A value describes frequency interval of substance usage';

COMMENT ON COLUMN result_nicotine_sub_use.nsu_sub_type_use_occur IS 'A value that describes the substance type usage occurrence (never, current, former)';

CREATE TABLE result_overdose_report (
    or_id character varying(32) NOT NULL,
    or_date_created timestamp without time zone,
    or_date_updated timestamp without time zone,
    or_unq_sha1 character varying(40),
    or_sec_hash bigint,
    or_pat_id character varying(32),
    or_drug character varying(255),
    or_route character varying(255),
    or_start_date timestamp without time zone,
    or_stop_date timestamp without time zone,
    or_intent_flag character varying(255),
    or_overdose_ae_flag character varying(255),
    or_num_for_ae bigint,
    or_ae_overdose_ass character varying(255),
    or_total_dose double precision,
    or_total_dose_unit character varying(255),
    or_further_info character varying(255)
);

COMMENT ON COLUMN result_overdose_report.or_drug IS 'A value describes trade name/generic name of the drug';

COMMENT ON COLUMN result_overdose_report.or_route IS 'A value describes route of administration';

COMMENT ON COLUMN result_overdose_report.or_start_date IS 'The date and time on which overdose was started (Time)';

COMMENT ON COLUMN result_overdose_report.or_stop_date IS 'The date and time on which overdose was stopped (Time)';

COMMENT ON COLUMN result_overdose_report.or_intent_flag IS 'Flag indicating that overdose was intentional (yes/no)';

COMMENT ON COLUMN result_overdose_report.or_overdose_ae_flag IS 'Flag indicating that overdose associated with AE (yes/no)';

COMMENT ON COLUMN result_overdose_report.or_num_for_ae IS 'A value denoting number of AE associated with overdose';

COMMENT ON COLUMN result_overdose_report.or_ae_overdose_ass IS 'A value describes CRF text for AE associated with overdose';

COMMENT ON COLUMN result_overdose_report.or_total_dose IS 'A value denoting total dose';

COMMENT ON COLUMN result_overdose_report.or_total_dose_unit IS 'A value denoting unit in which total dose is measured';

COMMENT ON COLUMN result_overdose_report.or_further_info IS 'A value describes further information (for non SAE)';

CREATE TABLE result_pathology (
    pth_id character varying(32) NOT NULL,
    pth_date_created timestamp without time zone,
    pth_date_updated timestamp without time zone,
    pth_unq_sha1 character varying(40),
    pth_sec_hash bigint,
    pth_date timestamp without time zone,
    pth_his_type character varying(255),
    pth_his_type_details character varying(255),
    pth_tumour_grade character varying(255),
    pth_stage character varying(255),
    pth_tumor_location character varying(255),
    pth_prim_tum_status character varying(255),
    pth_nodes_status character varying(255),
    pth_metastases_status character varying(255),
    pth_determ_method character varying(255),
    pth_other_methods character varying(255),
    pth_pat_id character varying(32)
);

COMMENT ON COLUMN result_pathology.pth_date IS 'The date on which the subject was originally diagnosed with disease';

COMMENT ON COLUMN result_pathology.pth_his_type IS 'A value describes histology type of tumour';

COMMENT ON COLUMN result_pathology.pth_his_type_details IS 'A value giving details about tumour histology type';

COMMENT ON COLUMN result_pathology.pth_tumour_grade IS 'A value specifies tumour grade';

COMMENT ON COLUMN result_pathology.pth_stage IS 'A value specifies tumour stage';

COMMENT ON COLUMN result_pathology.pth_tumor_location IS 'A value specifies location';

COMMENT ON COLUMN result_pathology.pth_prim_tum_status IS 'A value specifies status of primary tumour';

COMMENT ON COLUMN result_pathology.pth_nodes_status IS 'A value specifies status regional lymph nodes';

COMMENT ON COLUMN result_pathology.pth_metastases_status IS 'A value specifies status of distant metastases';

COMMENT ON COLUMN result_pathology.pth_determ_method IS 'A value describes the method of determination';

COMMENT ON COLUMN result_pathology.pth_other_methods IS 'A value describes an alternative method of determination';

CREATE TABLE result_patient (
    pat_id character varying(32) NOT NULL,
    pat_date_created timestamp without time zone,
    pat_date_updated timestamp without time zone,
    pat_birthdat timestamp without time zone,
    pat_visdat timestamp without time zone,
    pat_centre bigint,
    pat_race character varying(255),
    pat_sex character varying(255),
    pat_subject character varying(255),
    pat_part character varying(255),
    pat_received_drug character varying(3),
    pat_unq_sha1 character varying(255),
    pat_std_id character varying(32),
    pat_pgr_id character varying(32),
    pat_ref_sha1 character varying(255),
    pat_sec_hash bigint,
    pat_ip_dose_first_date timestamp without time zone,
    pat_withdrawal_reason character varying(255),
    pat_withdrawal_date timestamp without time zone,
    pat_study_status character varying(100),
    pat_best_tumour_response character varying(255),
    pat_country character varying(255),
    pat_rand_date timestamp without time zone,
    pat_baseline_date timestamp without time zone
);

COMMENT ON COLUMN result_patient.pat_id IS 'ID';

COMMENT ON COLUMN result_patient.pat_date_created IS 'Record create date';

COMMENT ON COLUMN result_patient.pat_date_updated IS 'Record update date';

COMMENT ON COLUMN result_patient.pat_birthdat IS 'Date of Birth';

COMMENT ON COLUMN result_patient.pat_visdat IS 'Visit date';

COMMENT ON COLUMN result_patient.pat_centre IS 'Centre number';

COMMENT ON COLUMN result_patient.pat_race IS 'Race';

COMMENT ON COLUMN result_patient.pat_sex IS 'Sex';

COMMENT ON COLUMN result_patient.pat_subject IS 'Subject identifier';

COMMENT ON COLUMN result_patient.pat_part IS 'Study part identifier';

COMMENT ON COLUMN result_patient.pat_received_drug IS 'Received drug';

COMMENT ON COLUMN result_patient.pat_unq_sha1 IS 'SHA1 for unique fields';

COMMENT ON COLUMN result_patient.pat_std_id IS 'FK to Study';

COMMENT ON COLUMN result_patient.pat_pgr_id IS 'FK to Patient Group';

COMMENT ON COLUMN result_patient.pat_ref_sha1 IS 'SHA1 for FK fields';

COMMENT ON COLUMN result_patient.pat_ip_dose_first_date IS 'Date of first IP dose [RCT-3487]';

COMMENT ON COLUMN result_patient.pat_withdrawal_reason IS 'Reason for Withdrawal/Completion [RCT-3501]';

COMMENT ON COLUMN result_patient.pat_withdrawal_date IS 'Date of Withdrawal/Completion [RCT-3584]';

COMMENT ON COLUMN result_patient.pat_study_status IS 'Subject Study Status (Ongoing, Discontinued, or Withdrawn/complete) [RCT-3488]';

CREATE TABLE result_patient_group (
    pgr_id character varying(32) NOT NULL,
    pgr_date_created timestamp without time zone,
    pgr_date_updated timestamp without time zone,
    pgr_group_name character varying(255),
    pgr_grouping_name character varying(255),
    pgr_pat_part character varying(255),
    pgr_pat_subject character varying(255),
    pgr_unq_sha1 character varying(255),
    pgr_std_id character varying(32),
    pgr_ref_sha1 character varying(255),
    pgr_sec_hash bigint
);

COMMENT ON COLUMN result_patient_group.pgr_id IS 'ID';

COMMENT ON COLUMN result_patient_group.pgr_date_created IS 'Record create date';

COMMENT ON COLUMN result_patient_group.pgr_date_updated IS 'Record update date';

COMMENT ON COLUMN result_patient_group.pgr_group_name IS 'Subject group name';

COMMENT ON COLUMN result_patient_group.pgr_grouping_name IS 'Subject grouping name';

COMMENT ON COLUMN result_patient_group.pgr_unq_sha1 IS 'SHA1 for unique fields';

COMMENT ON COLUMN result_patient_group.pgr_std_id IS 'FK to Study';

COMMENT ON COLUMN result_patient_group.pgr_ref_sha1 IS 'SHA1 for FK fields';

CREATE TABLE result_patient_reported_data (
    rd_id character varying(32) NOT NULL,
    rd_date_created timestamp without time zone,
    rd_date_updated timestamp without time zone,
    rd_pat_id character varying(32),
    rd_measurement_name character varying(255),
    rd_value numeric(19,2),
    rd_unit character varying(255),
    rd_measurement_date timestamp without time zone,
    rd_report_date timestamp without time zone,
    rd_comment character varying(255),
    rd_src_id character varying(32),
    rd_src_type character varying(255),
    rd_sec_hash bigint,
    rd_unq_sha1 character varying(255),
    rd_ref_sha1 character varying(255)
);

COMMENT ON COLUMN result_patient_reported_data.rd_id IS 'ID';

COMMENT ON COLUMN result_patient_reported_data.rd_date_created IS 'Record create date';

COMMENT ON COLUMN result_patient_reported_data.rd_date_updated IS 'Record update date';

COMMENT ON COLUMN result_patient_reported_data.rd_pat_id IS 'FK to RESULT_PATIENT table';

COMMENT ON COLUMN result_patient_reported_data.rd_measurement_name IS 'Measurement name (standardised)';

COMMENT ON COLUMN result_patient_reported_data.rd_value IS 'Value';

COMMENT ON COLUMN result_patient_reported_data.rd_unit IS 'Unit';

COMMENT ON COLUMN result_patient_reported_data.rd_measurement_date IS 'Date of measurement';

COMMENT ON COLUMN result_patient_reported_data.rd_report_date IS 'Date of report';

COMMENT ON COLUMN result_patient_reported_data.rd_comment IS 'Free text/comment field';

COMMENT ON COLUMN result_patient_reported_data.rd_src_id IS 'FK to RESULT_LAB_SOURCE table';

COMMENT ON COLUMN result_patient_reported_data.rd_src_type IS 'Source (sponsor/patient etc.)';

COMMENT ON COLUMN result_patient_reported_data.rd_sec_hash IS 'SHA1 for secondary fields';

COMMENT ON COLUMN result_patient_reported_data.rd_unq_sha1 IS 'SHA1 for unique fields';

COMMENT ON COLUMN result_patient_reported_data.rd_ref_sha1 IS 'SHA1 for FK fields';

CREATE TABLE result_performance_status (
    ps_id character varying(32) NOT NULL,
    ps_date_created timestamp without time zone,
    ps_date_updated timestamp without time zone,
    ps_unq_sha1 character varying(40),
    ps_sec_hash bigint,
    ps_pat_id character varying(32),
    ps_vis_num bigint,
    ps_vis_date timestamp without time zone,
    ps_ass_date timestamp without time zone,
    ps_performance_status character varying(255),
    ps_questionnaire character varying(255)
);

COMMENT ON COLUMN result_performance_status.ps_vis_num IS 'The number of the subject visit';

COMMENT ON COLUMN result_performance_status.ps_vis_date IS 'The date of the subject visit';

COMMENT ON COLUMN result_performance_status.ps_ass_date IS 'The date on which assessment of performance was made';

COMMENT ON COLUMN result_performance_status.ps_performance_status IS 'A value describes performance status of the patient';

COMMENT ON COLUMN result_performance_status.ps_questionnaire IS 'A value denoting the questionnaire name that was used for assessment';

CREATE TABLE result_pk_concentration (
    pkc_id character varying(32) NOT NULL,
    pkc_date_created timestamp without time zone,
    pkc_date_updated timestamp without time zone,
    pkc_specimen_id character varying(255),
    pkc_analyte character varying(50),
    pkc_analyte_concentration_unit character varying(255),
    pkc_lower_limit numeric(19,2),
    pkc_treatment_cycle character varying(255),
    pkc_treatment character varying(255),
    pkc_treatment_schedule character varying(255),
    pkc_comment character varying(255),
    pkc_unq_sha1 character varying(255),
    pkc_sec_hash bigint,
    pkc_pat_id character varying(32),
    pkc_ref_sha1 character varying(255),
    pkc_analyte_concentration numeric(28,11)
);

COMMENT ON COLUMN result_pk_concentration.pkc_id IS 'ID';

COMMENT ON COLUMN result_pk_concentration.pkc_date_created IS 'Record create date';

COMMENT ON COLUMN result_pk_concentration.pkc_date_updated IS 'Record update date';

COMMENT ON COLUMN result_pk_concentration.pkc_specimen_id IS 'Specimen ID';

COMMENT ON COLUMN result_pk_concentration.pkc_analyte IS 'Name of analyte';

COMMENT ON COLUMN result_pk_concentration.pkc_analyte_concentration_unit IS 'Unit of concentration';

COMMENT ON COLUMN result_pk_concentration.pkc_lower_limit IS 'Lower limit of quantification';

COMMENT ON COLUMN result_pk_concentration.pkc_treatment_cycle IS 'Cycle of treatment';

COMMENT ON COLUMN result_pk_concentration.pkc_treatment IS 'Description of treatment';

COMMENT ON COLUMN result_pk_concentration.pkc_treatment_schedule IS 'Description of treatment schedule';

COMMENT ON COLUMN result_pk_concentration.pkc_comment IS 'Comment';

COMMENT ON COLUMN result_pk_concentration.pkc_unq_sha1 IS 'SHA1 for unique fields';

COMMENT ON COLUMN result_pk_concentration.pkc_pat_id IS 'FK to Patient';

COMMENT ON COLUMN result_pk_concentration.pkc_ref_sha1 IS 'SHA1 for FK fields';

COMMENT ON COLUMN result_pk_concentration.pkc_analyte_concentration IS 'Concentration of analyte';

CREATE TABLE result_pregnancy_test (
    pt_id character varying(32) NOT NULL,
    pt_date_created timestamp without time zone,
    pt_date_updated timestamp without time zone,
    pt_unq_sha1 character varying(40),
    pt_sec_hash bigint,
    pt_pat_id character varying(32),
    pt_vis_num bigint,
    pt_vis_date timestamp without time zone,
    pt_sampling_date timestamp without time zone,
    pt_serum character varying(255),
    pt_urine character varying(255),
    pt_plasma character varying(255),
    pt_preg_test character varying(255)
);

COMMENT ON COLUMN result_pregnancy_test.pt_vis_num IS 'The number of the subject visit';

COMMENT ON COLUMN result_pregnancy_test.pt_vis_date IS 'The date of the subject visit';

COMMENT ON COLUMN result_pregnancy_test.pt_sampling_date IS 'The date on which the measurement was made';

COMMENT ON COLUMN result_pregnancy_test.pt_serum IS 'A value denoting the result of pregnancy test - Serum (positive or negative)';

COMMENT ON COLUMN result_pregnancy_test.pt_urine IS 'A value denoting the result of pregnancy test - Urine (positive or negative)';

COMMENT ON COLUMN result_pregnancy_test.pt_plasma IS 'A value denoting the result of pregnancy test - Plasma (positive or negative)';

COMMENT ON COLUMN result_pregnancy_test.pt_preg_test IS 'A value denoting the result of pregnancy test (positive or negative)';

CREATE TABLE result_primary_tumour_location (
    ptl_id character varying(32) NOT NULL,
    ptl_date_created timestamp without time zone,
    ptl_date_updated timestamp without time zone,
    ptl_prim_tum_location character varying(255),
    ptl_original_diagnosis_date timestamp without time zone,
    ptl_prim_tum_location_comment character varying(255),
    ptl_unq_sha1 character varying(255),
    ptl_pat_id character varying(32),
    ptl_ref_sha1 character varying(255),
    ptl_sec_hash bigint
);

COMMENT ON COLUMN result_primary_tumour_location.ptl_id IS 'ID';

COMMENT ON COLUMN result_primary_tumour_location.ptl_date_created IS 'Record create date';

COMMENT ON COLUMN result_primary_tumour_location.ptl_date_updated IS 'Record update date';

COMMENT ON COLUMN result_primary_tumour_location.ptl_prim_tum_location IS 'Primary Tumor Location';

COMMENT ON COLUMN result_primary_tumour_location.ptl_original_diagnosis_date IS 'Original Diagnosis Date';

COMMENT ON COLUMN result_primary_tumour_location.ptl_prim_tum_location_comment IS 'Specify Primary Tumor Location';

COMMENT ON COLUMN result_primary_tumour_location.ptl_unq_sha1 IS 'SHA1 for unique fields';

COMMENT ON COLUMN result_primary_tumour_location.ptl_pat_id IS 'FK to Patient';

COMMENT ON COLUMN result_primary_tumour_location.ptl_ref_sha1 IS 'SHA1 for FK fields';

CREATE TABLE result_project (
    prj_id character varying(32) NOT NULL,
    prj_date_created timestamp without time zone,
    prj_date_updated timestamp without time zone,
    prj_name character varying(255),
    prj_ref_sha1 character varying(255)
);

COMMENT ON COLUMN result_project.prj_id IS 'ID';

COMMENT ON COLUMN result_project.prj_date_created IS 'Record create date';

COMMENT ON COLUMN result_project.prj_date_updated IS 'Record update date';

COMMENT ON COLUMN result_project.prj_name IS 'Project name';

COMMENT ON COLUMN result_project.prj_ref_sha1 IS 'SHA1 for FK fields';

CREATE TABLE result_radiotherapy (
    rad_id character varying(32),
    rad_date_created timestamp without time zone,
    rad_date_updated timestamp without time zone,
    rad_visit bigint,
    rad_visit_dat timestamp without time zone,
    rad_given character varying(255),
    rad_site_or_region character varying(255),
    rad_start_date timestamp without time zone,
    rad_end_date timestamp without time zone,
    rad_treatment_status character varying(255),
    rad_radiation_dose numeric(10,2),
    rad_number_of_doses bigint,
    rad_unq_sha1 character varying(255),
    rad_ref_sha1 character varying(255),
    rad_pat_id character varying(32),
    rad_sec_hash bigint,
    rad_time_status character varying(255)
);

COMMENT ON COLUMN result_radiotherapy.rad_id IS 'ID';

COMMENT ON COLUMN result_radiotherapy.rad_date_created IS 'Record create date';

COMMENT ON COLUMN result_radiotherapy.rad_date_updated IS 'Record update date';

COMMENT ON COLUMN result_radiotherapy.rad_visit IS 'Visit number';

COMMENT ON COLUMN result_radiotherapy.rad_visit_dat IS 'Visit Date';

COMMENT ON COLUMN result_radiotherapy.rad_given IS 'Radiotherapy given?';

COMMENT ON COLUMN result_radiotherapy.rad_site_or_region IS 'Radiotherapy site or region';

COMMENT ON COLUMN result_radiotherapy.rad_start_date IS 'Radiotherapy start date';

COMMENT ON COLUMN result_radiotherapy.rad_end_date IS 'Radiotherapy stop date';

COMMENT ON COLUMN result_radiotherapy.rad_treatment_status IS 'Treatment status';

COMMENT ON COLUMN result_radiotherapy.rad_radiation_dose IS 'Fraction dose (Gy)';

COMMENT ON COLUMN result_radiotherapy.rad_number_of_doses IS 'Number of Fraction Doses';

COMMENT ON COLUMN result_radiotherapy.rad_unq_sha1 IS 'SHA1 for unique fields';

COMMENT ON COLUMN result_radiotherapy.rad_ref_sha1 IS 'SHA1 for FK fields';

COMMENT ON COLUMN result_radiotherapy.rad_pat_id IS 'FK to Patient';

COMMENT ON COLUMN result_radiotherapy.rad_time_status IS 'Radiotherapy time status (Previous/Current/Post)';

CREATE TABLE result_randomisation (
    rnd_id character varying(32) NOT NULL,
    rnd_date_created timestamp without time zone,
    rnd_date_updated timestamp without time zone,
    rnd_date timestamp without time zone,
    rnd_unq_sha1 character varying(255),
    rnd_sec_hash bigint,
    rnd_pat_id character varying(32),
    rnd_ref_sha1 character varying(255)
);

CREATE TABLE result_recist_assessment (
    rca_id character varying(32) NOT NULL,
    rca_date_created timestamp without time zone,
    rca_date_updated timestamp without time zone,
    rca_new_les_since_baseline character varying(100),
    rca_new_les_site character varying(100),
    rca_assessment_date timestamp without time zone,
    rca_recist_response character varying(100),
    rca_inves_agrees_with_recist character varying(100),
    rca_inves_assessment character varying(100),
    rca_reason_differ character varying(512),
    rca_unq_sha1 character varying(255),
    rca_pat_id character varying(32),
    rca_ref_sha1 character varying(255),
    rca_visit_date timestamp without time zone,
    rca_assess_freq numeric(10,2),
    rca_visit numeric(10,2),
    rca_sec_hash bigint
);

COMMENT ON COLUMN result_recist_assessment.rca_id IS 'ID';

COMMENT ON COLUMN result_recist_assessment.rca_date_created IS 'Record create date';

COMMENT ON COLUMN result_recist_assessment.rca_date_updated IS 'Record update date';

COMMENT ON COLUMN result_recist_assessment.rca_new_les_since_baseline IS 'Any New Lesions Since Baseline (yes/no)';

COMMENT ON COLUMN result_recist_assessment.rca_new_les_site IS 'New Lesion Site';

COMMENT ON COLUMN result_recist_assessment.rca_assessment_date IS 'New Lesion Date of Scan/Clinical Examination';

COMMENT ON COLUMN result_recist_assessment.rca_recist_response IS 'RECIST Response';

COMMENT ON COLUMN result_recist_assessment.rca_inves_agrees_with_recist IS 'Investigator Opinion Agrees with RECIST Response (yes/no)';

COMMENT ON COLUMN result_recist_assessment.rca_inves_assessment IS 'Investigator Opinion of Patient Status';

COMMENT ON COLUMN result_recist_assessment.rca_reason_differ IS 'Reason RECIST and Investigator Assessments Differ';

COMMENT ON COLUMN result_recist_assessment.rca_unq_sha1 IS 'SHA1 for unique fields';

COMMENT ON COLUMN result_recist_assessment.rca_pat_id IS 'FK to Patient';

COMMENT ON COLUMN result_recist_assessment.rca_ref_sha1 IS 'SHA1 for FK fields';

COMMENT ON COLUMN result_recist_assessment.rca_visit_date IS 'Visit date';

COMMENT ON COLUMN result_recist_assessment.rca_assess_freq IS 'Assessment frequency (weeks)';

COMMENT ON COLUMN result_recist_assessment.rca_visit IS 'Visit number';

CREATE TABLE result_recist_nontarget_lesion (
    rntl_id character varying(32) NOT NULL,
    rntl_pat_id character varying(32),
    rntl_lesion_present character varying(100),
    rntl_lesion_date timestamp without time zone,
    rntl_lesion_site character varying(140),
    rntl_visit_date timestamp without time zone,
    rntl_visit_number numeric(10,2),
    rntl_date_created timestamp without time zone,
    rntl_date_updated timestamp without time zone,
    rntl_unq_sha1 character varying(255),
    rntl_ref_sha1 character varying(255),
    rntl_sec_hash bigint,
    rntl_response character varying(255)
);

COMMENT ON COLUMN result_recist_nontarget_lesion.rntl_response IS 'Response of Non-Target Lesions';

CREATE TABLE result_recist_target_lesion (
    rtl_id character varying(32) NOT NULL,
    rtl_pat_id character varying(32),
    rtl_lesion_present character varying(100),
    rtl_lesion_number character varying(12),
    rtl_lesion_date timestamp without time zone,
    rtl_lesion_site character varying(100),
    rtl_lesion_diameter numeric(19,2),
    rtl_investigators_response character varying(255),
    rtl_visit_date timestamp without time zone,
    rtl_visit_number numeric(10,2),
    rtl_date_created timestamp without time zone,
    rtl_date_updated timestamp without time zone,
    rtl_unq_sha1 character varying(255),
    rtl_sec_hash bigint,
    rtl_ref_sha1 character varying(255),
    rtl_lesion_no_long_measur character varying(255),
    rtl_method_of_assessment character varying(255)
);

COMMENT ON COLUMN result_recist_target_lesion.rtl_lesion_no_long_measur IS 'Lesion No Longer Measurable';

COMMENT ON COLUMN result_recist_target_lesion.rtl_method_of_assessment IS 'Method of Assessment';

CREATE TABLE result_sc (
    sc_id character varying(32) NOT NULL,
    sc_date_created timestamp without time zone,
    sc_date_updated timestamp without time zone,
    sc_visit numeric(10,2),
    sc_visit_date timestamp without time zone,
    sc_ethpop character varying(255),
    sc_s_ethpop character varying(255),
    sc_unq_sha1 character varying(255),
    sc_sec_hash bigint,
    sc_pat_id character varying(32),
    sc_ref_sha1 character varying(255)
);

COMMENT ON COLUMN result_sc.sc_id IS 'ID';

COMMENT ON COLUMN result_sc.sc_date_created IS 'Record create date';

COMMENT ON COLUMN result_sc.sc_date_updated IS 'Record update date';

COMMENT ON COLUMN result_sc.sc_visit IS 'Visit number';

COMMENT ON COLUMN result_sc.sc_visit_date IS 'Visit date';

COMMENT ON COLUMN result_sc.sc_ethpop IS 'Ethnic Population';

COMMENT ON COLUMN result_sc.sc_s_ethpop IS 'Other Ethnic Population';

COMMENT ON COLUMN result_sc.sc_unq_sha1 IS 'SHA1 for unique fields';

COMMENT ON COLUMN result_sc.sc_pat_id IS 'FK to Patient';

COMMENT ON COLUMN result_sc.sc_ref_sha1 IS 'SHA1 for FK fields';

CREATE TABLE result_serious_adverse_event (
    sae_id character varying(32) NOT NULL,
    sae_date_created timestamp without time zone,
    sae_date_updated timestamp without time zone,
    sae_adverse_event character varying(255),
    sae_crit_met_date timestamp without time zone,
    sae_inv_aware_date timestamp without time zone,
    sae_res_in_death character varying(10),
    sae_life_thr character varying(10),
    sae_reqr_hosp character varying(10),
    sae_hosp_date timestamp without time zone,
    sae_disch_date timestamp without time zone,
    sae_pers_disability character varying(10),
    sae_cong_anom character varying(10),
    sae_other_event character varying(10),
    sae_caused_other_med character varying(10),
    sae_other_med character varying(255),
    sae_caused_std_procd character varying(10),
    sae_std_procd character varying(255),
    sae_ae_desc character varying(4000),
    sae_unq_sha1 character varying(255),
    sae_pat_id character varying(32),
    sae_ref_sha1 character varying(255),
    sae_sec_hash bigint,
    sae_ae_num bigint,
    sae_prim_cause_death character varying(255),
    sae_sec_cause_death character varying(255),
    sae_add_drug character varying(255),
    sae_caused_by_add_drug character varying(255),
    sae_add_drug1 character varying(255),
    sae_caused_by_add_drug1 character varying(255),
    sae_add_drug2 character varying(255),
    sae_caused_by_add_drug2 character varying(255)
);

COMMENT ON COLUMN result_serious_adverse_event.sae_id IS 'ID';

COMMENT ON COLUMN result_serious_adverse_event.sae_date_created IS 'Record create date';

COMMENT ON COLUMN result_serious_adverse_event.sae_date_updated IS 'Record update date';

COMMENT ON COLUMN result_serious_adverse_event.sae_adverse_event IS 'AE Term';

COMMENT ON COLUMN result_serious_adverse_event.sae_crit_met_date IS 'Date AE Met Criteria for Serious AE';

COMMENT ON COLUMN result_serious_adverse_event.sae_inv_aware_date IS 'Date Investigator Aware of Serious AE';

COMMENT ON COLUMN result_serious_adverse_event.sae_res_in_death IS 'Results in Death (yes/no)';

COMMENT ON COLUMN result_serious_adverse_event.sae_life_thr IS 'Life Threatening (yes/no)';

COMMENT ON COLUMN result_serious_adverse_event.sae_reqr_hosp IS 'Requires or Prolongs Hospitalization';

COMMENT ON COLUMN result_serious_adverse_event.sae_hosp_date IS 'Date of Hospitalization';

COMMENT ON COLUMN result_serious_adverse_event.sae_disch_date IS 'Date of Discharge';

COMMENT ON COLUMN result_serious_adverse_event.sae_pers_disability IS 'Persist. or Sign. Disability/Incapacity';

COMMENT ON COLUMN result_serious_adverse_event.sae_cong_anom IS 'Congenital Anomaly or Birth Defect';

COMMENT ON COLUMN result_serious_adverse_event.sae_other_event IS 'Other Serious/Important  Medically Event';

COMMENT ON COLUMN result_serious_adverse_event.sae_caused_other_med IS 'SAE Caused by Other Medication';

COMMENT ON COLUMN result_serious_adverse_event.sae_other_med IS 'Other Medication or Drug that Caused AE';

COMMENT ON COLUMN result_serious_adverse_event.sae_caused_std_procd IS 'SAE Caused by Study Procedure(s)';

COMMENT ON COLUMN result_serious_adverse_event.sae_std_procd IS 'Study Procedure(s) that Caused AE';

COMMENT ON COLUMN result_serious_adverse_event.sae_ae_desc IS 'AE Description';

COMMENT ON COLUMN result_serious_adverse_event.sae_unq_sha1 IS 'SHA1 for unique fields';

COMMENT ON COLUMN result_serious_adverse_event.sae_pat_id IS 'FK to Patient';

COMMENT ON COLUMN result_serious_adverse_event.sae_ref_sha1 IS 'SHA1 for FK fields';

COMMENT ON COLUMN result_serious_adverse_event.sae_ae_num IS 'A value giving AE number for which medication taken';

COMMENT ON COLUMN result_serious_adverse_event.sae_prim_cause_death IS 'Is AE primary cause of death? (yes/no)';

COMMENT ON COLUMN result_serious_adverse_event.sae_sec_cause_death IS 'Is AE secondary cause of death? (yes/no)';

COMMENT ON COLUMN result_serious_adverse_event.sae_add_drug IS 'Additional Drug';

COMMENT ON COLUMN result_serious_adverse_event.sae_caused_by_add_drug IS 'AE Caused by Additional Drug (yes/no)';

COMMENT ON COLUMN result_serious_adverse_event.sae_add_drug1 IS 'Additional Drug 1';

COMMENT ON COLUMN result_serious_adverse_event.sae_caused_by_add_drug1 IS 'AE Caused by Additional Drug 1 (yes/no)';

COMMENT ON COLUMN result_serious_adverse_event.sae_add_drug2 IS 'Additional Drug 2';

COMMENT ON COLUMN result_serious_adverse_event.sae_caused_by_add_drug2 IS 'AE Caused by Additional Drug 2 (yes/no)';

CREATE TABLE result_source (
    src_id character varying(32) NOT NULL,
    src_date_created timestamp without time zone,
    src_date_updated timestamp without time zone,
    src_name character varying(255),
    src_version character varying(255),
    src_type character varying(255),
    src_unq_sha1 character varying(255),
    src_ref_sha1 character varying(255),
    src_sec_hash bigint
);

COMMENT ON COLUMN result_source.src_id IS 'ID';

COMMENT ON COLUMN result_source.src_date_created IS 'Record create date';

COMMENT ON COLUMN result_source.src_date_updated IS 'Record update date';

COMMENT ON COLUMN result_source.src_name IS 'Device name or Algorithm name';

COMMENT ON COLUMN result_source.src_version IS 'Device version or Algorithm version';

COMMENT ON COLUMN result_source.src_type IS 'Hardware or Software';

COMMENT ON COLUMN result_source.src_unq_sha1 IS 'SHA1 for unique fields';

COMMENT ON COLUMN result_source.src_ref_sha1 IS 'SHA1 for FK fields';

CREATE TABLE result_specimen_collection (
    spc_id character varying(32) NOT NULL,
    spc_date_created timestamp without time zone,
    spc_date_updated timestamp without time zone,
    spc_visit bigint,
    spc_specimen_date timestamp without time zone,
    spc_category character varying(255),
    spc_protocol_schedule character varying(255),
    spc_protocol_schedule_day bigint,
    spc_protocol_schedule_hour numeric(12,2),
    spc_protocol_schedule_minute bigint,
    spc_drug_adm_date timestamp without time zone,
    spc_unq_sha1 character varying(255),
    spc_pat_id character varying(32),
    spc_ref_sha1 character varying(255),
    spc_sec_hash bigint,
    spc_specimen_id character varying(255),
    spc_visit_date timestamp without time zone
);

COMMENT ON COLUMN result_specimen_collection.spc_id IS 'ID';

COMMENT ON COLUMN result_specimen_collection.spc_date_created IS 'Record create date';

COMMENT ON COLUMN result_specimen_collection.spc_date_updated IS 'Record update date';

COMMENT ON COLUMN result_specimen_collection.spc_visit IS 'Visit number';

COMMENT ON COLUMN result_specimen_collection.spc_specimen_date IS 'Specimen collection date/time';

COMMENT ON COLUMN result_specimen_collection.spc_category IS 'Specimen collection category';

COMMENT ON COLUMN result_specimen_collection.spc_protocol_schedule IS 'Protocol schedule';

COMMENT ON COLUMN result_specimen_collection.spc_protocol_schedule_day IS 'Protocol schedule day';

COMMENT ON COLUMN result_specimen_collection.spc_protocol_schedule_hour IS 'Protocol schedule hour';

COMMENT ON COLUMN result_specimen_collection.spc_protocol_schedule_minute IS 'Protocol schedule minute';

COMMENT ON COLUMN result_specimen_collection.spc_drug_adm_date IS 'Date/time of investigational drug administration';

COMMENT ON COLUMN result_specimen_collection.spc_unq_sha1 IS 'SHA1 for unique fields';

COMMENT ON COLUMN result_specimen_collection.spc_pat_id IS 'FK to Patient';

COMMENT ON COLUMN result_specimen_collection.spc_ref_sha1 IS 'SHA1 for FK fields';

COMMENT ON COLUMN result_specimen_collection.spc_visit_date IS 'Visit date';

CREATE TABLE result_stacked_pk_results (
    stp_id character varying(32) NOT NULL,
    stp_date_created timestamp without time zone,
    stp_date_updated timestamp without time zone,
    stp_visit_date timestamp without time zone,
    stp_visit_number bigint,
    stp_treatment character varying(255),
    stp_treatment_schedule character varying(255),
    stp_treatment_cycle character varying(255),
    stp_parameter character varying(255),
    stp_analyte character varying(50),
    stp_parameter_value numeric(19,2),
    stp_parameter_value_unit character varying(255),
    stp_protocol_schedule character varying(255),
    stp_protocol_schd_start_hour bigint,
    stp_protocol_schd_start_minute bigint,
    stp_protocol_schd_end_hour bigint,
    stp_protocol_schd_end_minute bigint,
    stp_comment character varying(255),
    stp_unq_sha1 character varying(255),
    stp_sec_hash bigint,
    stp_pat_id character varying(32),
    stp_ref_sha1 character varying(255),
    stp_protocol_schedule_end character varying(255),
    stp_visit character varying(255),
    stp_actual_dose character varying(255),
    stp_protocol_schd_start_day character varying(255),
    stp_protocol_schd_end_day character varying(255)
);

COMMENT ON COLUMN result_stacked_pk_results.stp_id IS 'ID';

COMMENT ON COLUMN result_stacked_pk_results.stp_date_created IS 'Record create date';

COMMENT ON COLUMN result_stacked_pk_results.stp_date_updated IS 'Record update date';

COMMENT ON COLUMN result_stacked_pk_results.stp_visit_date IS 'Visit date';

COMMENT ON COLUMN result_stacked_pk_results.stp_visit_number IS 'Visit number';

COMMENT ON COLUMN result_stacked_pk_results.stp_treatment IS 'Description of treatment';

COMMENT ON COLUMN result_stacked_pk_results.stp_treatment_schedule IS 'Description of treatment schedule';

COMMENT ON COLUMN result_stacked_pk_results.stp_treatment_cycle IS 'Cycle of treatment';

COMMENT ON COLUMN result_stacked_pk_results.stp_parameter IS 'Description of PK parameter';

COMMENT ON COLUMN result_stacked_pk_results.stp_analyte IS 'Name of analyte';

COMMENT ON COLUMN result_stacked_pk_results.stp_parameter_value IS 'Concentration of analyte';

COMMENT ON COLUMN result_stacked_pk_results.stp_parameter_value_unit IS 'Unit of concentration';

COMMENT ON COLUMN result_stacked_pk_results.stp_protocol_schedule IS 'Protocol schedule';

COMMENT ON COLUMN result_stacked_pk_results.stp_protocol_schd_start_hour IS 'Protocol start schedule hour';

COMMENT ON COLUMN result_stacked_pk_results.stp_protocol_schd_start_minute IS 'Protocol start schedule minute';

COMMENT ON COLUMN result_stacked_pk_results.stp_protocol_schd_end_hour IS 'Protocol end schedule hour';

COMMENT ON COLUMN result_stacked_pk_results.stp_protocol_schd_end_minute IS 'Protocol end schedule minute';

COMMENT ON COLUMN result_stacked_pk_results.stp_comment IS 'Comment';

COMMENT ON COLUMN result_stacked_pk_results.stp_unq_sha1 IS 'SHA1 for unique fields';

COMMENT ON COLUMN result_stacked_pk_results.stp_pat_id IS 'FK to Patient';

COMMENT ON COLUMN result_stacked_pk_results.stp_ref_sha1 IS 'SHA1 for FK fields';

COMMENT ON COLUMN result_stacked_pk_results.stp_protocol_schedule_end IS 'Protocol end schedule';

COMMENT ON COLUMN result_stacked_pk_results.stp_visit IS 'Visit';

COMMENT ON COLUMN result_stacked_pk_results.stp_actual_dose IS 'Actual administered dose';

CREATE TABLE result_study (
    std_id character varying(32) NOT NULL,
    std_date_created timestamp without time zone,
    std_date_updated timestamp without time zone,
    std_name character varying(255),
    std_display character varying(255),
    std_prj_id character varying(32),
    std_stg_id character varying(32),
    std_ref_sha1 character varying(255),
    std_last_event_date timestamp without time zone,
    std_date_last_uploaded timestamp without time zone
);

COMMENT ON COLUMN result_study.std_id IS 'ID';

COMMENT ON COLUMN result_study.std_date_created IS 'Record create date';

COMMENT ON COLUMN result_study.std_date_updated IS 'Record update date';

COMMENT ON COLUMN result_study.std_name IS 'Study identifier';

COMMENT ON COLUMN result_study.std_display IS 'Study Display Name';

COMMENT ON COLUMN result_study.std_prj_id IS 'FK to Project';

COMMENT ON COLUMN result_study.std_stg_id IS 'Study Group ID';

COMMENT ON COLUMN result_study.std_ref_sha1 IS 'SHA1 for FK fields';

CREATE TABLE result_surgical_history (
    sh_id character varying(32) NOT NULL,
    sh_date_created timestamp without time zone,
    sh_date_updated timestamp without time zone,
    sh_unq_sha1 character varying(40),
    sh_sec_hash bigint,
    sh_start_date timestamp without time zone,
    sh_procedure character varying(255),
    sh_hlt character varying(255),
    sh_llt character varying(255),
    sh_pt character varying(255),
    sh_soc character varying(255),
    sh_current character varying(255),
    sh_pat_id character varying(32)
);

COMMENT ON COLUMN result_surgical_history.sh_start_date IS 'The date on which the procedure was started';

COMMENT ON COLUMN result_surgical_history.sh_procedure IS 'Medical History Term';

COMMENT ON COLUMN result_surgical_history.sh_hlt IS 'MedDRA Lowest Level Term Name string assigned to the surgical procedure';

COMMENT ON COLUMN result_surgical_history.sh_llt IS 'MedDRA Preferred Term Name string assigned to the surgical procedure';

COMMENT ON COLUMN result_surgical_history.sh_pt IS 'MedDRA High Level Term Name string assigned to the surgical procedure';

COMMENT ON COLUMN result_surgical_history.sh_soc IS 'MedDRA System Organ Class Name string assigned to the surgical procedure';

COMMENT ON COLUMN result_surgical_history.sh_current IS 'Flag indicating that this is current medication(yes/no)';

CREATE TABLE result_target_med_dos_disc (
    dsc_id character varying(32) NOT NULL,
    dsc_date_created timestamp without time zone,
    dsc_date_updated timestamp without time zone,
    dsc_ipdc_date timestamp without time zone,
    dsc_ipdc_reas character varying(255),
    dsc_ipdc_spec character varying(255),
    dsc_drug_name character varying(255),
    dsc_unq_sha1 character varying(255),
    dsc_pat_id character varying(32),
    dsc_ref_sha1 character varying(255),
    dsc_sec_hash bigint,
    dsc_subj_dec_spec character varying(255),
    dsc_subj_dec_spec_other character varying(255)
);

COMMENT ON COLUMN result_target_med_dos_disc.dsc_id IS 'ID';

COMMENT ON COLUMN result_target_med_dos_disc.dsc_date_created IS 'Record create date';

COMMENT ON COLUMN result_target_med_dos_disc.dsc_date_updated IS 'Record update date';

COMMENT ON COLUMN result_target_med_dos_disc.dsc_ipdc_date IS 'Date of Investigational Product Discontinuation';

COMMENT ON COLUMN result_target_med_dos_disc.dsc_ipdc_reas IS 'Main reason for Investigational Product Discontinuation';

COMMENT ON COLUMN result_target_med_dos_disc.dsc_ipdc_spec IS 'Free text where the reason is specified';

COMMENT ON COLUMN result_target_med_dos_disc.dsc_drug_name IS 'Study Drug Name';

COMMENT ON COLUMN result_target_med_dos_disc.dsc_unq_sha1 IS 'SHA1 for unique fields';

COMMENT ON COLUMN result_target_med_dos_disc.dsc_pat_id IS 'FK to Patient';

COMMENT ON COLUMN result_target_med_dos_disc.dsc_ref_sha1 IS 'SHA1 for FK fields';

COMMENT ON COLUMN result_target_med_dos_disc.dsc_subj_dec_spec IS 'A value that provides the subject decision specification';

COMMENT ON COLUMN result_target_med_dos_disc.dsc_subj_dec_spec_other IS 'A value that provides the other subject decision specification';

CREATE TABLE result_test (
    tst_id character varying(32) NOT NULL,
    tst_date_created timestamp without time zone,
    tst_date_updated timestamp without time zone,
    tst_date timestamp without time zone,
    tst_visit numeric(10,2),
    tst_unq_sha1 character varying(255),
    tst_pat_id character varying(32),
    tst_ref_sha1 character varying(255),
    tst_sec_hash bigint
);

COMMENT ON COLUMN result_test.tst_id IS 'ID';

COMMENT ON COLUMN result_test.tst_date_created IS 'Record create date';

COMMENT ON COLUMN result_test.tst_date_updated IS 'Record update date';

COMMENT ON COLUMN result_test.tst_date IS 'Date of the measurement';

COMMENT ON COLUMN result_test.tst_visit IS 'Visit number';

COMMENT ON COLUMN result_test.tst_unq_sha1 IS 'SHA1 for unique fields';

COMMENT ON COLUMN result_test.tst_pat_id IS 'FK to Patient';

COMMENT ON COLUMN result_test.tst_ref_sha1 IS 'SHA1 for FK fields';

CREATE TABLE result_trg_med_dos_schedule (
    mds_id character varying(32) NOT NULL,
    mds_date_created timestamp without time zone DEFAULT now(),
    mds_date_updated timestamp without time zone,
    mds_drug character varying(255),
    mds_dose numeric(10,2),
    mds_dose_unit character varying(255),
    mds_start_date timestamp without time zone,
    mds_end_date timestamp without time zone,
    mds_frequency_unit character varying(255),
    mds_frequency bigint,
    mds_action_taken character varying(255),
    mds_reason_for_action_taken character varying(255),
    mds_comment character varying(255),
    mds_unq_sha1 character varying(255),
    mds_pat_id character varying(32),
    mds_ref_sha1 character varying(255),
    mds_sec_hash bigint,
    mds_dosing_freq_name character varying(255),
    mds_dosing_freq_rank numeric(20,10),
    mds_period_type character varying(25),
    mds_subsequent_period_type character varying(25),
    mds_total_daily_dose numeric(10,2),
    mds_cycle_delayed character varying(255),
    mds_reason_cycle_delayed character varying(255),
    mds_reason_cycle_delayed_oth character varying(255),
    mds_med_code character varying(255),
    mds_med_dictionary_text character varying(255),
    mds_atc_code character varying(255),
    mds_atc_dictionary_text character varying(255),
    mds_drug_pref_name character varying(255),
    mds_med_group_name character varying(255),
    mds_active_ingredient character varying(255),
    mds_study_drug_cat character varying(255),
    mds_planned_dose bigint,
    mds_planned_dose_units character varying(255),
    mds_planned_no_days_trt bigint,
    mds_ae_num_act_taken character varying(40),
    mds_formulation character varying(255),
    mds_route character varying(255),
    mds_reason_for_therapy character varying(255),
    mds_ae_num_trt_cycle_del character varying(40)
);

COMMENT ON COLUMN result_trg_med_dos_schedule.mds_id IS 'ID';

COMMENT ON COLUMN result_trg_med_dos_schedule.mds_date_created IS 'Record create date';

COMMENT ON COLUMN result_trg_med_dos_schedule.mds_date_updated IS 'Record update date';

COMMENT ON COLUMN result_trg_med_dos_schedule.mds_drug IS 'Study Drug Name';

COMMENT ON COLUMN result_trg_med_dos_schedule.mds_dose IS 'Dose per Administration';

COMMENT ON COLUMN result_trg_med_dos_schedule.mds_dose_unit IS 'Dose Unit';

COMMENT ON COLUMN result_trg_med_dos_schedule.mds_start_date IS 'Start date/time of the dose schedule';

COMMENT ON COLUMN result_trg_med_dos_schedule.mds_end_date IS 'End date/time of the dose schedule';

COMMENT ON COLUMN result_trg_med_dos_schedule.mds_frequency_unit IS 'Dosing Frequency Interval Unit';

COMMENT ON COLUMN result_trg_med_dos_schedule.mds_frequency IS 'Dosing Frequency (per Interval)';

COMMENT ON COLUMN result_trg_med_dos_schedule.mds_action_taken IS 'Action Taken, Study Drug';

COMMENT ON COLUMN result_trg_med_dos_schedule.mds_reason_for_action_taken IS 'Main Reason for Action Taken, Study Drug';

COMMENT ON COLUMN result_trg_med_dos_schedule.mds_comment IS 'Reason for Study Drug Dose Change Specification';

COMMENT ON COLUMN result_trg_med_dos_schedule.mds_unq_sha1 IS 'SHA1 for unique fields';

COMMENT ON COLUMN result_trg_med_dos_schedule.mds_pat_id IS 'FK to Patient';

COMMENT ON COLUMN result_trg_med_dos_schedule.mds_ref_sha1 IS 'SHA1 for FK fields';

COMMENT ON COLUMN result_trg_med_dos_schedule.mds_total_daily_dose IS 'A value denoting total daily dose';

COMMENT ON COLUMN result_trg_med_dos_schedule.mds_cycle_delayed IS 'Flag indicating that treatment cycle was delayed (yes/no)';

COMMENT ON COLUMN result_trg_med_dos_schedule.mds_reason_cycle_delayed IS 'A value describes reason why treatment cycle was delayed';

COMMENT ON COLUMN result_trg_med_dos_schedule.mds_reason_cycle_delayed_oth IS 'A value describes alternative reason why treatment cycle was delayed';

CREATE TABLE result_visit (
    vis_id character varying(32) NOT NULL,
    vis_date_created timestamp without time zone,
    vis_date_updated timestamp without time zone,
    vis_date timestamp without time zone,
    vis_number numeric(10,2),
    vis_unq_sha1 character varying(255),
    vis_pat_id character varying(32),
    vis_ref_sha1 character varying(255),
    vis_sec_hash bigint
);

COMMENT ON COLUMN result_visit.vis_id IS 'ID';

COMMENT ON COLUMN result_visit.vis_date_created IS 'Record create date';

COMMENT ON COLUMN result_visit.vis_date_updated IS 'Record update date';

COMMENT ON COLUMN result_visit.vis_date IS 'Visit Date';

COMMENT ON COLUMN result_visit.vis_number IS 'Visit number';

COMMENT ON COLUMN result_visit.vis_unq_sha1 IS 'SHA1 for unique fields';

COMMENT ON COLUMN result_visit.vis_pat_id IS 'FK to Patient';

COMMENT ON COLUMN result_visit.vis_ref_sha1 IS 'SHA1 for FK fields';

CREATE TABLE result_vitals (
    vit_id character varying(32) NOT NULL,
    vit_date_created timestamp without time zone,
    vit_date_updated timestamp without time zone,
    vit_lpdat timestamp without time zone,
    vit_lpdos numeric(10,3),
    vit_value numeric(10,3),
    vit_unq_sha1 character varying(255),
    vit_sec_hash bigint,
    vit_tst_id character varying(32),
    vit_ref_sha1 character varying(255),
    vit_unit character varying(255),
    vit_test_name character varying(255),
    vit_anatomical_location character varying(255),
    vit_anatomical_side_interest character varying(255),
    vit_physical_position character varying(255),
    vit_clinically_significant character varying(255),
    vit_entity_class character varying(255),
    vit_sch_timepoint character varying(255),
    vit_last_ip_date timestamp without time zone,
    vit_last_ip_dose character varying(255)
);

COMMENT ON COLUMN result_vitals.vit_id IS 'ID';

COMMENT ON COLUMN result_vitals.vit_date_created IS 'Record create date';

COMMENT ON COLUMN result_vitals.vit_date_updated IS 'Record update date';

COMMENT ON COLUMN result_vitals.vit_lpdat IS 'Last Investigational Product Date/Time';

COMMENT ON COLUMN result_vitals.vit_lpdos IS 'Last Investigational Product Dose';

COMMENT ON COLUMN result_vitals.vit_value IS 'Value';

COMMENT ON COLUMN result_vitals.vit_unq_sha1 IS 'SHA1 for unique fields';

COMMENT ON COLUMN result_vitals.vit_tst_id IS 'FK to Test';

COMMENT ON COLUMN result_vitals.vit_ref_sha1 IS 'SHA1 for FK fields';

COMMENT ON COLUMN result_vitals.vit_unit IS 'Unit';

CREATE TABLE result_withdrawal_completion (
    wc_id character varying(32) NOT NULL,
    wc_date_created timestamp without time zone,
    wc_date_updated timestamp without time zone,
    wc_withdrawal_completion_date timestamp without time zone,
    wc_prematurely_withdrawn character varying(255),
    wc_main_reason character varying(255),
    wc_specification character varying(255),
    wc_unq_sha1 character varying(255),
    wc_sec_hash bigint,
    wc_pat_id character varying(32),
    wc_ref_sha1 character varying(255)
);

COMMENT ON COLUMN result_withdrawal_completion.wc_id IS 'ID';

COMMENT ON COLUMN result_withdrawal_completion.wc_date_created IS 'Record create date';

COMMENT ON COLUMN result_withdrawal_completion.wc_date_updated IS 'Record update date';

COMMENT ON COLUMN result_withdrawal_completion.wc_withdrawal_completion_date IS 'The date that the subject withdrew';

COMMENT ON COLUMN result_withdrawal_completion.wc_prematurely_withdrawn IS 'Whether the subject prematurely withdrew';

COMMENT ON COLUMN result_withdrawal_completion.wc_main_reason IS 'The main reason why the subject prematurely withdrew';

COMMENT ON COLUMN result_withdrawal_completion.wc_specification IS 'An explanation of the reason for premature withdrawal';

COMMENT ON COLUMN result_withdrawal_completion.wc_unq_sha1 IS 'SHA1 for unique fields';

COMMENT ON COLUMN result_withdrawal_completion.wc_pat_id IS 'FK to Patient';

COMMENT ON COLUMN result_withdrawal_completion.wc_ref_sha1 IS 'SHA1 for FK fields';

CREATE SEQUENCE rex_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE;

CREATE SEQUENCE rfv_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE;

CREATE SEQUENCE rmc_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE;

CREATE SEQUENCE rud_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE;

CREATE TABLE saved_filter (
    id bigint NOT NULL,
    created_date timestamp without time zone,
    name character varying(255) NOT NULL,
    operator character varying(255),
    owner character varying(255) NOT NULL,
    dataset_id character varying(255) NOT NULL,
    dataset_class character varying(255) NOT NULL
);

CREATE TABLE saved_filter_dataset (
    id bigint NOT NULL,
    dataset_class character varying(255),
    dataset_id bigint,
    saved_filter_id bigint
);

CREATE TABLE saved_filter_instance (
    id bigint NOT NULL,
    filterview character varying(255),
    json text,
    type character varying(255),
    saved_filter_id bigint
);

CREATE TABLE saved_filter_permission (
    id bigint NOT NULL,
    prid character varying(255),
    saved_filter_id bigint
);

CREATE TABLE schema_version (
    installed_rank numeric(38,0) NOT NULL,
    version character varying(50),
    description character varying(200) NOT NULL,
    type character varying(20) NOT NULL,
    script character varying(1000) NOT NULL,
    checksum numeric(38,0),
    installed_by character varying(100) NOT NULL,
    installed_on timestamp without time zone DEFAULT CURRENT_TIMESTAMP NOT NULL,
    execution_time numeric(38,0) NOT NULL,
    success smallint NOT NULL
);

CREATE SEQUENCE seq_rct_instance_time_intrvl
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE;

CREATE SEQUENCE ua_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE;

CREATE SEQUENCE ucl_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE;

CREATE TABLE user_activity (
    ua_id character varying(32) NOT NULL,
    ua_user_session_id character varying(32),
    ua_analytics_session_id character varying(32),
    ua_analytics_session_date timestamp without time zone,
    ua_drug_name character varying(100),
    ua_std_name character varying(255),
    ua_dataset_type character varying(100),
    ua_view_name character varying(255),
    ua_visualisation_name character varying(255),
    ua_visualisation_access_date timestamp without time zone
);

CREATE TABLE users (
    username character varying(256) NOT NULL,
    fullname character varying(256) NOT NULL,
    password character varying(256) NOT NULL,
    enabled boolean NOT NULL,
    linked_username character varying(256)
);

CREATE TABLE util_country_code_lookup (
    ccl_id numeric(20,0) NOT NULL,
    ccl_code character varying(10),
    ccl_name character varying(255)
);

CREATE TABLE util_ctcg_lookup (
    cgl_id numeric(38,0),
    cgl_label character varying(4000),
    cgl_lookup character varying(255),
    cgl_numeric bigint,
    cgl_webapp_lookup character varying(255)
);

CREATE TABLE util_deployment_env_props (
    dep_id numeric(20,0) NOT NULL,
    dep_env character varying(255),
    dep_name character varying(255),
    dep_value character varying(255)
);

COMMENT ON COLUMN util_deployment_env_props.dep_id IS 'ID';

COMMENT ON COLUMN util_deployment_env_props.dep_env IS 'Environment name';

COMMENT ON COLUMN util_deployment_env_props.dep_name IS 'Property name';

COMMENT ON COLUMN util_deployment_env_props.dep_value IS 'Property value';

CREATE TABLE util_ecg_evaluation_lookup (
    sel_id numeric(20,0) NOT NULL,
    sel_label character varying(255),
    sel_lookup character varying(255)
);

CREATE TABLE util_ecg_significant_lookup (
    scl_id numeric(20,0) NOT NULL,
    scl_label character varying(255),
    scl_lookup character varying(255)
);

CREATE TABLE util_email_details (
    emd_id bigint NOT NULL,
    emd_email_type character varying(100),
    emd_to_addresses character varying(255),
    emd_cc_addresses character varying(255),
    emd_from_address character varying(255),
    emd_subject character varying(255),
    emd_attachments character varying(255),
    emd_text character varying(4000)
);

COMMENT ON COLUMN util_email_details.emd_id IS 'ID';

COMMENT ON COLUMN util_email_details.emd_email_type IS 'Email type ID';

COMMENT ON COLUMN util_email_details.emd_to_addresses IS 'Email TO addresses';

COMMENT ON COLUMN util_email_details.emd_cc_addresses IS 'Email CC addresses';

COMMENT ON COLUMN util_email_details.emd_from_address IS 'Email FROM addresses';

COMMENT ON COLUMN util_email_details.emd_subject IS 'Email subject';

COMMENT ON COLUMN util_email_details.emd_attachments IS 'Attachments';

COMMENT ON COLUMN util_email_details.emd_text IS 'Email body text';

CREATE TABLE util_labcode_dictionary (
    lcd_id numeric(20,0) NOT NULL,
    lcd_definition character varying(255)
);

COMMENT ON COLUMN util_labcode_dictionary.lcd_id IS 'ID';

COMMENT ON COLUMN util_labcode_dictionary.lcd_definition IS 'Labcode definition';

CREATE TABLE util_labcode_lookup (
    lcl_id bigint NOT NULL,
    lcl_labcode character varying(10) NOT NULL,
    lcl_test_name character varying(255) NOT NULL,
    lcl_sample_name character varying(255)
);

COMMENT ON COLUMN util_labcode_lookup.lcl_id IS 'ID';

COMMENT ON COLUMN util_labcode_lookup.lcl_labcode IS 'Laboratory Code';

COMMENT ON COLUMN util_labcode_lookup.lcl_test_name IS 'Test name';

COMMENT ON COLUMN util_labcode_lookup.lcl_sample_name IS 'Sample name';

CREATE TABLE util_labcode_synonym (
    lcs_id uuid DEFAULT public.uuid_generate_v4() NOT NULL,
    lcs_lcd_id numeric(20,0),
    lcs_synonym character varying(255)
);

COMMENT ON COLUMN util_labcode_synonym.lcs_id IS 'ID';

COMMENT ON COLUMN util_labcode_synonym.lcs_lcd_id IS 'FK to Labcode Dictionary';

COMMENT ON COLUMN util_labcode_synonym.lcs_synonym IS 'Synonym';

CREATE TABLE util_labctcg_lookup (
    labcgl_code character varying(20) NOT NULL,
    labcgl_test_name character varying(255),
    labcgl_category character varying(255)
);

CREATE TABLE util_mutation_type_dictionary (
    mtd_id character varying(32) NOT NULL,
    mtd_definition character varying(255)
);

COMMENT ON COLUMN util_mutation_type_dictionary.mtd_id IS 'Id';

COMMENT ON COLUMN util_mutation_type_dictionary.mtd_definition IS 'Mutation type definition';

CREATE TABLE util_mutation_type_synonym (
    mts_id character varying(32) NOT NULL,
    mts_mtd_id numeric(20,0),
    mts_synonym character varying(255)
);

COMMENT ON COLUMN util_mutation_type_synonym.mts_id IS 'Id';

COMMENT ON COLUMN util_mutation_type_synonym.mts_mtd_id IS 'FK to Mutation Type dictionary';

COMMENT ON COLUMN util_mutation_type_synonym.mts_synonym IS 'Mutation synonym';

CREATE TABLE util_recist_response_lookup (
    rrl_code integer NOT NULL,
    rrl_regex character varying(255),
    rrl_name character varying(255) NOT NULL,
    rrl_shortname character varying(255),
    rrl_rank integer NOT NULL
);

COMMENT ON COLUMN util_recist_response_lookup.rrl_code IS 'Code';

COMMENT ON COLUMN util_recist_response_lookup.rrl_regex IS 'Response regex pattern';

COMMENT ON COLUMN util_recist_response_lookup.rrl_name IS 'Response full name';

COMMENT ON COLUMN util_recist_response_lookup.rrl_shortname IS 'Response short name';

COMMENT ON COLUMN util_recist_response_lookup.rrl_rank IS 'Response numeric value';
