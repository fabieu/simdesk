ALTER TABLE simdesk.driver
    DROP COLUMN locked;
ALTER TABLE simdesk.driver
    ADD COLUMN visibility TEXT DEFAULT 'PUBLIC' NOT NULL;