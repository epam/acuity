-- Upgrade Spring Batch tables from the legacy (<=4.x) schema to the Spring Batch 5.x schema
-- (bundled with the Spring Boot 3.x upgrade done as part of the Java 8 -> 21 migration).
-- This is a PoC environment with no batch job history worth preserving, so old batch data
-- (job execution params) is discarded rather than migrated.

-- batch_job_execution_params: the old generic-column schema (key_name/type_cd/string_val/
-- date_val/long_val/double_val) is replaced by Spring Batch 5.x's schema
-- (parameter_name/parameter_type/parameter_value), matching what JdbcJobExecutionDao actually
-- reads/writes at runtime.
DROP TABLE batch_job_execution_params CASCADE;

CREATE TABLE batch_job_execution_params (
    job_execution_id bigint NOT NULL,
    parameter_name character varying(100) NOT NULL,
    parameter_type character varying(100) NOT NULL,
    parameter_value character varying(2500),
    identifying character(1) NOT NULL
);

ALTER TABLE ONLY batch_job_execution_params
    ADD CONSTRAINT job_exec_params_fk FOREIGN KEY (job_execution_id) REFERENCES batch_job_execution(job_execution_id);

-- batch_job_params (job-instance level params) does not exist in Spring Batch 5.x at all.
-- Its only consumers, the acuity_utils.clean_reports / clean_all_reports functions, were
-- already dropped in V1621594203__remove_obsolete_tables.sql, and no application code
-- references this table.
DROP TABLE batch_job_params CASCADE;

-- batch_step_execution is missing CREATE_TIME, which Spring Batch 5.x's JdbcStepExecutionDao
-- requires on every INSERT.
ALTER TABLE batch_step_execution
    ADD COLUMN create_time timestamp without time zone NOT NULL DEFAULT now();

ALTER TABLE batch_step_execution
    ALTER COLUMN create_time DROP DEFAULT;

-- batch_step_execution.start_time was NOT NULL under the legacy schema, but Spring Batch 5.x
-- inserts a step execution row up front (in STARTING status) with only CREATE_TIME populated;
-- START_TIME is set later once the step actually starts running. The official Spring Batch
-- schema-postgresql.sql declares START_TIME as nullable (TIMESTAMP DEFAULT NULL).
ALTER TABLE batch_step_execution
    ALTER COLUMN start_time DROP NOT NULL;
