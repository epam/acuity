CREATE EXTENSION IF NOT EXISTS orafce WITH SCHEMA acuity;
COMMENT ON EXTENSION orafce IS 'Functions and operators that emulate a subset of functions and packages from the Oracle RDBMS';

CREATE EXTENSION IF NOT EXISTS tablefunc WITH SCHEMA acuity;
COMMENT ON EXTENSION tablefunc IS 'functions that manipulate whole tables, including crosstab';

CREATE EXTENSION IF NOT EXISTS "uuid-ossp" WITH SCHEMA public;
COMMENT ON EXTENSION "uuid-ossp" IS 'generate universally unique identifiers (UUIDs)';
