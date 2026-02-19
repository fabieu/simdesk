-- Restructure stewarding: Race Weekend -> Series + Round + Session (schema only)

-- 1. Create stewarding_series table
CREATE TABLE IF NOT EXISTS simdesk.stewarding_series
(
    id                  SERIAL PRIMARY KEY,
    title               VARCHAR(255) NOT NULL,
    description         TEXT,
    discord_webhook_url VARCHAR(500),
    video_url_enabled   BOOLEAN   NOT NULL DEFAULT FALSE,
    penalty_catalog_id  INTEGER,
    start_date          DATE,
    end_date            DATE,
    created_at          TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at          TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (penalty_catalog_id) REFERENCES simdesk.stewarding_penalty_catalog (id)
);

-- 2. Add series_id column to stewarding_race_weekend (acts as round table)
ALTER TABLE simdesk.stewarding_race_weekend ADD COLUMN series_id INTEGER REFERENCES simdesk.stewarding_series (id);

CREATE INDEX ix_stewarding_race_weekend_series_id ON simdesk.stewarding_race_weekend (series_id);

-- 3. Add round_id column to stewarding_entrylist
ALTER TABLE simdesk.stewarding_entrylist ADD COLUMN round_id INTEGER REFERENCES simdesk.stewarding_race_weekend (id);

CREATE INDEX ix_stewarding_entrylist_round_id ON simdesk.stewarding_entrylist (round_id);
