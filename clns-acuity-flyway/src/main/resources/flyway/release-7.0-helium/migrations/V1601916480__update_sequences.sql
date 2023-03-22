SELECT setval('acl_class_sequence', (SELECT cast(max(id) as bigint) FROM acl_class), true);
SELECT setval('acl_remote_identity_sequence', (SELECT cast(max(id) as bigint) FROM acl_remote), true);
SELECT setval('acl_sid_sequence', (SELECT cast(max(id) as bigint) FROM acl_sid), true);
SELECT setval('groups_sequence', (SELECT cast(max(id) as bigint) FROM groups), true);
