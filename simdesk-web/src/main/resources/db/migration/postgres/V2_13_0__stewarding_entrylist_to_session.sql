-- Move entrylist from race weekend to session
ALTER TABLE simdesk.stewarding_entrylist ADD COLUMN session_id INTEGER REFERENCES simdesk.stewarding_session (id);
UPDATE simdesk.stewarding_entrylist SET session_id = (SELECT id FROM simdesk.stewarding_session WHERE race_weekend_id = simdesk.stewarding_entrylist.race_weekend_id ORDER BY sort_order, created_at LIMIT 1);

DROP INDEX IF EXISTS simdesk.ix_stewarding_entrylist_race_weekend_id;
CREATE INDEX ix_stewarding_entrylist_session_id ON simdesk.stewarding_entrylist (session_id);

-- Add video_url_enabled setting to race weekend
ALTER TABLE simdesk.stewarding_race_weekend ADD COLUMN video_url_enabled BOOLEAN NOT NULL DEFAULT FALSE;
