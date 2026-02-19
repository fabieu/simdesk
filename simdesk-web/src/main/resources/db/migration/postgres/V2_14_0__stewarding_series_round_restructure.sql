-- Restructure stewarding: Race Weekend -> Series + Round + Session

-- 1. Create stewarding_series table with a temporary column for migration mapping
CREATE TABLE IF NOT EXISTS simdesk.stewarding_series
(
    id                       SERIAL PRIMARY KEY,
    title                    VARCHAR(255) NOT NULL,
    description              TEXT,
    discord_webhook_url      VARCHAR(500),
    video_url_enabled        BOOLEAN   NOT NULL DEFAULT FALSE,
    penalty_catalog_id       INTEGER,
    start_date               DATE,
    end_date                 DATE,
    created_at               TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at               TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    source_race_weekend_id   INTEGER UNIQUE,
    FOREIGN KEY (penalty_catalog_id) REFERENCES simdesk.stewarding_penalty_catalog (id)
);

-- 2. Migrate existing race weekends into series (one series per race weekend)
--    Store the source race_weekend_id for deterministic mapping
INSERT INTO simdesk.stewarding_series (title, description, discord_webhook_url, video_url_enabled, penalty_catalog_id, start_date, end_date, created_at, updated_at, source_race_weekend_id)
SELECT title || ' Series',
       description,
       discord_webhook_url,
       video_url_enabled,
       penalty_catalog_id,
       start_date,
       end_date,
       created_at,
       updated_at,
       id
FROM simdesk.stewarding_race_weekend;

-- 3. Add series_id column to stewarding_race_weekend (acts as round table)
ALTER TABLE simdesk.stewarding_race_weekend ADD COLUMN series_id INTEGER REFERENCES simdesk.stewarding_series (id);

-- 4. Update each race weekend to point to its corresponding series using deterministic mapping
UPDATE simdesk.stewarding_race_weekend rw
SET series_id = s.id
FROM simdesk.stewarding_series s
WHERE s.source_race_weekend_id = rw.id;

CREATE INDEX ix_stewarding_race_weekend_series_id ON simdesk.stewarding_race_weekend (series_id);

-- 5. Add round_id column to stewarding_entrylist and populate from session's race_weekend_id
ALTER TABLE simdesk.stewarding_entrylist ADD COLUMN round_id INTEGER REFERENCES simdesk.stewarding_race_weekend (id);

-- Populate round_id from session_id -> stewarding_session.race_weekend_id
UPDATE simdesk.stewarding_entrylist el
SET round_id = ss.race_weekend_id
FROM simdesk.stewarding_session ss
WHERE ss.id = el.session_id
  AND el.session_id IS NOT NULL;

-- Fall back to race_weekend_id for entries that have no session_id
UPDATE simdesk.stewarding_entrylist
SET round_id = race_weekend_id
WHERE round_id IS NULL AND race_weekend_id IS NOT NULL;

CREATE INDEX ix_stewarding_entrylist_round_id ON simdesk.stewarding_entrylist (round_id);

-- 6. Drop old columns from stewarding_race_weekend that moved to series
ALTER TABLE simdesk.stewarding_race_weekend DROP COLUMN IF EXISTS description;
ALTER TABLE simdesk.stewarding_race_weekend DROP COLUMN IF EXISTS discord_webhook_url;
ALTER TABLE simdesk.stewarding_race_weekend DROP COLUMN IF EXISTS video_url_enabled;
ALTER TABLE simdesk.stewarding_race_weekend DROP COLUMN IF EXISTS penalty_catalog_id;
DROP INDEX IF EXISTS simdesk.ix_stewarding_race_weekend_penalty_catalog_id;

-- 7. Drop old columns from stewarding_entrylist
ALTER TABLE simdesk.stewarding_entrylist DROP COLUMN IF EXISTS race_weekend_id;
ALTER TABLE simdesk.stewarding_entrylist DROP COLUMN IF EXISTS session_id;
DROP INDEX IF EXISTS simdesk.ix_stewarding_entrylist_race_weekend_id;
DROP INDEX IF EXISTS simdesk.ix_stewarding_entrylist_session_id;

-- 8. Drop temporary migration mapping column from stewarding_series
ALTER TABLE simdesk.stewarding_series DROP COLUMN IF EXISTS source_race_weekend_id;
