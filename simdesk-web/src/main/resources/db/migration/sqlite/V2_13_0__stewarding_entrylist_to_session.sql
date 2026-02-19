-- Move entrylist from race weekend to session
ALTER TABLE stewarding_entrylist ADD COLUMN session_id INTEGER REFERENCES stewarding_session (id);
UPDATE stewarding_entrylist SET session_id = (SELECT id FROM stewarding_session WHERE race_weekend_id = stewarding_entrylist.race_weekend_id LIMIT 1);

DROP INDEX IF EXISTS ix_stewarding_entrylist_race_weekend_id;
CREATE INDEX ix_stewarding_entrylist_session_id ON stewarding_entrylist (session_id);

-- Add video_url_enabled setting to race weekend
ALTER TABLE stewarding_race_weekend ADD COLUMN video_url_enabled BOOLEAN NOT NULL DEFAULT FALSE;
