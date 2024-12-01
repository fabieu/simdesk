ALTER TABLE driver
    DROP COLUMN visibility;
ALTER TABLE driver
    ADD COLUMN visibility TEXT DEFAULT 'PUBLIC';