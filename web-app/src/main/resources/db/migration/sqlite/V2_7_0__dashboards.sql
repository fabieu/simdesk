CREATE TABLE IF NOT EXISTS dashboard
(
    id              VARCHAR(12) PRIMARY KEY,
    visibility      TEXT      NOT NULL,
    name            TEXT      NOT NULL,
    description     TEXT,
    broadcast_url   TEXT,
    start_datetime  TIMESTAMP,
    end_datetime    TIMESTAMP,
    state           TEXT,
    state_datetime TIMESTAMP,
    update_datetime TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    create_datetime TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_dashboard_visibility
    ON dashboard (visibility);