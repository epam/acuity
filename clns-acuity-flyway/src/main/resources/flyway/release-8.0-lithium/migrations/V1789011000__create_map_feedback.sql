-- Feedback block of the admin page: one row with the contact email
CREATE SEQUENCE IF NOT EXISTS mfb_seq;

CREATE TABLE IF NOT EXISTS map_feedback (
    mfb_id    BIGINT PRIMARY KEY DEFAULT nextval('mfb_seq'),
    mfb_email VARCHAR(255)
);
