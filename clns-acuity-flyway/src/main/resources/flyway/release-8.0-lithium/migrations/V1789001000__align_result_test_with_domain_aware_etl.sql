ALTER TABLE result_test
    ADD COLUMN IF NOT EXISTS tst_domain character varying(255);

COMMENT ON COLUMN result_test.tst_domain IS 'SDTM domain used to disambiguate test rows';

DROP INDEX IF EXISTS uq_t_date_pat_id;

ALTER TABLE ONLY result_test
    DROP CONSTRAINT IF EXISTS result_test_tst_date_tst_pat_id_key;

ALTER TABLE ONLY result_test
    ADD CONSTRAINT result_test_tst_date_tst_pat_id_key UNIQUE (tst_date, tst_pat_id, tst_domain);

CREATE UNIQUE INDEX IF NOT EXISTS uq_t_date_pat_id ON result_test USING btree (tst_date, tst_pat_id, tst_domain);
