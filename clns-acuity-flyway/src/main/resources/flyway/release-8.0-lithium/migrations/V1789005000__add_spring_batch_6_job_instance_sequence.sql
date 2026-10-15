-- Spring Batch 6's PostgreSQL schema uses BATCH_JOB_INSTANCE_SEQ for job
-- instance ids. The legacy ACUITY schema used BATCH_JOB_SEQ instead, so the
-- Batch 6 JdbcJobRepositoryFactoryBean fails on nextval('BATCH_JOB_INSTANCE_SEQ')
-- unless this compatibility sequence exists.

CREATE SEQUENCE IF NOT EXISTS batch_job_instance_seq
    MAXVALUE 9223372036854775807
    NO CYCLE;

GRANT USAGE, SELECT ON SEQUENCE batch_job_instance_seq TO acuity;

DO $$
DECLARE
    existing_count bigint;
    target_value bigint;
    legacy_value bigint;
    current_value bigint;
    current_called boolean;
BEGIN
    SELECT COUNT(*), COALESCE(MAX(job_instance_id), 0)
    INTO existing_count, target_value
    FROM batch_job_instance;

    IF existing_count > 0 AND to_regclass('batch_job_seq') IS NOT NULL THEN
        EXECUTE 'SELECT last_value FROM batch_job_seq' INTO legacy_value;
        target_value := GREATEST(target_value, legacy_value);
    END IF;

    SELECT last_value, is_called
    INTO current_value, current_called
    FROM batch_job_instance_seq;

    IF current_called THEN
        target_value := GREATEST(target_value, current_value);
    END IF;

    IF target_value > 0 THEN
        PERFORM setval('batch_job_instance_seq', target_value, true);
    ELSE
        PERFORM setval('batch_job_instance_seq', 1, false);
    END IF;
END $$;
