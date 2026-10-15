ALTER TABLE map_subject_grouping ALTER COLUMN msg_msgt_id DROP DEFAULT;
ALTER TABLE map_subject_grouping ALTER COLUMN msg_msgt_id DROP NOT NULL;
COMMENT ON COLUMN map_subject_grouping.msg_msgt_id IS
    'Deprecated legacy annotate/select subject grouping type. Runtime grouping labels are read from RESULT_PATIENT_GROUP and MAP_SUBJECT_GROUP_RULE.';

COMMENT ON COLUMN map_subject_grouping.msg_grouping_selected IS
    'Deprecated legacy Select subject groupings flag. Runtime grouping visibility no longer depends on this flag.';

COMMENT ON TABLE map_subject_grouping_type IS
    'Deprecated legacy subject grouping type lookup retained only for old configuration data.';
