ALTER TABLE map_study_rule
    ADD COLUMN IF NOT EXISTS msr_study_comment character varying(4000);
