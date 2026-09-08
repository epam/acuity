-- Repeatable migration: keeps the `acuity` role's password in sync with
-- whatever is currently supplied as ${user.acuity.password} or FLYWAY_ACUITY_PASSWORD.

ALTER ROLE acuity WITH PASSWORD '${user.acuity.password}';
