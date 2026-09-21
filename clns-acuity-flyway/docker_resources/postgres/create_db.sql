DO $$
BEGIN
    IF NOT EXISTS (
        SELECT 1
        FROM pg_roles
        WHERE rolname = 'dbadmin'
    ) THEN
        CREATE ROLE dbadmin;
    END IF;
END
$$;
