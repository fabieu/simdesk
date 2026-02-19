-- Restructure stewarding: Race Weekend -> Series + Round + Session

-- 1. Create stewarding_series table with a temporary column for migration mapping
CREATE TABLE IF NOT EXISTS stewarding_series
(
    id                       INTEGER PRIMARY KEY AUTOINCREMENT,
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
    FOREIGN KEY (penalty_catalog_id) REFERENCES stewarding_penalty_catalog (id)
);

-- 2. Migrate existing race weekends into series (one series per race weekend)
--    Store the source race_weekend_id for deterministic mapping
INSERT INTO stewarding_series (title, description, discord_webhook_url, video_url_enabled, penalty_catalog_id, start_date, end_date, created_at, updated_at, source_race_weekend_id)
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
FROM stewarding_race_weekend;

-- 3. Add series_id column to stewarding_race_weekend (acts as round table)
ALTER TABLE stewarding_race_weekend ADD COLUMN series_id INTEGER REFERENCES stewarding_series (id);

-- 4. Update each race weekend to point to its corresponding series using deterministic mapping
UPDATE stewarding_race_weekend
SET series_id = (
    SELECT s.id
    FROM stewarding_series s
    WHERE s.source_race_weekend_id = stewarding_race_weekend.id
);

CREATE INDEX ix_stewarding_race_weekend_series_id ON stewarding_race_weekend (series_id);

-- 5. Add round_id column to stewarding_entrylist and populate from session's race_weekend_id
ALTER TABLE stewarding_entrylist ADD COLUMN round_id INTEGER REFERENCES stewarding_race_weekend (id);

-- Populate round_id from session_id -> stewarding_session.race_weekend_id
UPDATE stewarding_entrylist
SET round_id = (
    SELECT ss.race_weekend_id
    FROM stewarding_session ss
    WHERE ss.id = stewarding_entrylist.session_id
)
WHERE session_id IS NOT NULL;

-- Fall back to race_weekend_id for entries that have no session_id
UPDATE stewarding_entrylist
SET round_id = race_weekend_id
WHERE round_id IS NULL AND race_weekend_id IS NOT NULL;

CREATE INDEX ix_stewarding_entrylist_round_id ON stewarding_entrylist (round_id);

-- Note: SQLite does not support DROP COLUMN easily.
-- Old columns (description, discord_webhook_url, video_url_enabled, penalty_catalog_id)
-- remain on stewarding_race_weekend but are no longer used by the application.
-- Old columns (race_weekend_id, session_id) remain on stewarding_entrylist but are no longer used.
-- The source_race_weekend_id column on stewarding_series was used for migration mapping and is no longer used.
