CREATE TRIGGER a_d_study AFTER DELETE ON result_study FOR EACH STATEMENT EXECUTE PROCEDURE trigger_fct_a_d_study();

CREATE TRIGGER acl_class_id_trigger BEFORE INSERT ON acl_class FOR EACH ROW EXECUTE PROCEDURE trigger_fct_acl_class_id_trigger();

CREATE TRIGGER acl_entry_id_trigger BEFORE INSERT ON acl_entry FOR EACH ROW EXECUTE PROCEDURE trigger_fct_acl_entry_id_trigger();

CREATE TRIGGER acl_object_id_trigger BEFORE INSERT ON acl_object FOR EACH ROW EXECUTE PROCEDURE trigger_fct_acl_object_id_trigger();

CREATE TRIGGER acl_object_identity_id_trigger BEFORE INSERT ON acl_object_identity FOR EACH ROW EXECUTE PROCEDURE trigger_fct_acl_object_identity_id_trigger();

CREATE TRIGGER acl_remote_identity_id_trigger BEFORE INSERT ON acl_remote FOR EACH ROW EXECUTE PROCEDURE trigger_fct_acl_remote_identity_id_trigger();

CREATE TRIGGER acl_sid_id_trigger BEFORE INSERT ON acl_sid FOR EACH ROW EXECUTE PROCEDURE trigger_fct_acl_sid_id_trigger();

CREATE TRIGGER group_members_id_trigger BEFORE INSERT ON group_members FOR EACH ROW EXECUTE PROCEDURE trigger_fct_group_members_id_trigger();

CREATE TRIGGER groups_id_trigger BEFORE INSERT ON groups FOR EACH ROW EXECUTE PROCEDURE trigger_fct_groups_id_trigger();

CREATE TRIGGER map_dynamic_field_seq_nextval BEFORE INSERT ON map_dynamic_field FOR EACH ROW EXECUTE PROCEDURE trigger_fct_map_dynamic_field_seq_nextval();

CREATE TRIGGER map_project_display BEFORE INSERT ON map_project_rule FOR EACH ROW EXECUTE PROCEDURE trigger_fct_map_project_display();

CREATE TRIGGER rename_custom_subject_grouping AFTER UPDATE OF msgr_name ON map_subject_group_rule FOR EACH ROW EXECUTE PROCEDURE trigger_fct_rename_custom_subject_grouping();

CREATE TRIGGER ua_tr BEFORE INSERT ON user_activity FOR EACH ROW EXECUTE PROCEDURE trigger_fct_ua_tr();
