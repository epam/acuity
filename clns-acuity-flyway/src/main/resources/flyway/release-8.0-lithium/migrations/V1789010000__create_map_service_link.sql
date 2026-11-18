-- Service blocks shown on the home page (title and URL), managed in the admin application
CREATE SEQUENCE IF NOT EXISTS msl_seq;

CREATE TABLE IF NOT EXISTS map_service_link (
    msl_id         BIGINT PRIMARY KEY DEFAULT nextval('msl_seq'),
    msl_title      VARCHAR(255) NOT NULL,
    msl_url        VARCHAR(1000),
    msl_sort_order INTEGER NOT NULL DEFAULT 0
);
