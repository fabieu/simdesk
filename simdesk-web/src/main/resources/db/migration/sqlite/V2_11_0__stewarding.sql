-- Stewarding module tables

CREATE TABLE IF NOT EXISTS stewarding_track
(
    id            INTEGER PRIMARY KEY AUTOINCREMENT,
    name          VARCHAR(255) NOT NULL,
    country       VARCHAR(100),
    map_image_url VARCHAR(500),
    map_metadata  TEXT,
    created_at    TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at    TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS stewarding_penalty_catalog
(
    id          INTEGER PRIMARY KEY AUTOINCREMENT,
    name        VARCHAR(255) NOT NULL,
    description TEXT,
    created_at  TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at  TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS stewarding_penalty_definition
(
    id              INTEGER PRIMARY KEY AUTOINCREMENT,
    catalog_id      INTEGER      NOT NULL,
    code            VARCHAR(50),
    name            VARCHAR(255) NOT NULL,
    description     TEXT,
    category        VARCHAR(100),
    session_type    VARCHAR(20)  NOT NULL DEFAULT 'ALL',
    default_penalty VARCHAR(255),
    severity        INTEGER,
    sort_order      INTEGER               DEFAULT 0,
    FOREIGN KEY (catalog_id) REFERENCES stewarding_penalty_catalog (id)
);

CREATE INDEX ix_stewarding_penalty_definition_catalog_id ON stewarding_penalty_definition (catalog_id);

CREATE TABLE IF NOT EXISTS stewarding_reasoning_template
(
    id            INTEGER PRIMARY KEY AUTOINCREMENT,
    name          VARCHAR(255) NOT NULL,
    category      VARCHAR(100),
    template_text TEXT         NOT NULL,
    sort_order    INTEGER               DEFAULT 0
);

CREATE TABLE IF NOT EXISTS stewarding_race_weekend
(
    id                  INTEGER PRIMARY KEY AUTOINCREMENT,
    title               VARCHAR(255) NOT NULL,
    description         TEXT,
    track_id            INTEGER      NOT NULL,
    penalty_catalog_id  INTEGER      NOT NULL,
    discord_webhook_url VARCHAR(500),
    start_date          DATE,
    end_date            DATE,
    created_at          TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at          TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (track_id) REFERENCES stewarding_track (id),
    FOREIGN KEY (penalty_catalog_id) REFERENCES stewarding_penalty_catalog (id)
);

CREATE INDEX ix_stewarding_race_weekend_track_id ON stewarding_race_weekend (track_id);
CREATE INDEX ix_stewarding_race_weekend_penalty_catalog_id ON stewarding_race_weekend (penalty_catalog_id);

CREATE TABLE IF NOT EXISTS stewarding_session
(
    id              INTEGER PRIMARY KEY AUTOINCREMENT,
    race_weekend_id INTEGER     NOT NULL,
    session_type    VARCHAR(20) NOT NULL,
    title           VARCHAR(255),
    start_time      TIMESTAMP,
    end_time        TIMESTAMP,
    sort_order      INTEGER              DEFAULT 0,
    created_at      TIMESTAMP   NOT NULL DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (race_weekend_id) REFERENCES stewarding_race_weekend (id)
);

CREATE INDEX ix_stewarding_session_race_weekend_id ON stewarding_session (race_weekend_id);

CREATE TABLE IF NOT EXISTS stewarding_entrylist
(
    id              INTEGER PRIMARY KEY AUTOINCREMENT,
    race_weekend_id INTEGER   NOT NULL UNIQUE,
    uploaded_at     TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    raw_json        TEXT,
    FOREIGN KEY (race_weekend_id) REFERENCES stewarding_race_weekend (id)
);

CREATE INDEX ix_stewarding_entrylist_race_weekend_id ON stewarding_entrylist (race_weekend_id);

CREATE TABLE IF NOT EXISTS stewarding_entrylist_entry
(
    id           INTEGER PRIMARY KEY AUTOINCREMENT,
    entrylist_id INTEGER NOT NULL,
    race_number  INTEGER NOT NULL,
    car_model_id INTEGER,
    team_name    VARCHAR(255),
    display_name VARCHAR(255),
    FOREIGN KEY (entrylist_id) REFERENCES stewarding_entrylist (id) ON DELETE CASCADE
);

CREATE INDEX ix_stewarding_entrylist_entry_entrylist_id ON stewarding_entrylist_entry (entrylist_id);

CREATE TABLE IF NOT EXISTS stewarding_entrylist_driver
(
    id         INTEGER PRIMARY KEY AUTOINCREMENT,
    entry_id   INTEGER NOT NULL,
    first_name VARCHAR(100),
    last_name  VARCHAR(100),
    short_name VARCHAR(10),
    steam_id   VARCHAR(50),
    category   INTEGER,
    FOREIGN KEY (entry_id) REFERENCES stewarding_entrylist_entry (id) ON DELETE CASCADE
);

CREATE INDEX ix_stewarding_entrylist_driver_entry_id ON stewarding_entrylist_driver (entry_id);

CREATE TABLE IF NOT EXISTS stewarding_incident
(
    id                   INTEGER PRIMARY KEY AUTOINCREMENT,
    session_id           INTEGER      NOT NULL,
    title                VARCHAR(255) NOT NULL,
    description          TEXT,
    lap                  INTEGER,
    timestamp_in_session VARCHAR(100),
    map_marker_x         REAL,
    map_marker_y         REAL,
    video_url            VARCHAR(500),
    involved_cars_text   VARCHAR(500),
    status               VARCHAR(30)  NOT NULL DEFAULT 'REPORTED',
    reported_by_user_id  INTEGER,
    created_at           TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at           TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (session_id) REFERENCES stewarding_session (id)
);

CREATE INDEX ix_stewarding_incident_session_id ON stewarding_incident (session_id);
CREATE INDEX ix_stewarding_incident_status ON stewarding_incident (status);

CREATE TABLE IF NOT EXISTS stewarding_incident_involved_entry
(
    incident_id INTEGER NOT NULL,
    entry_id    INTEGER NOT NULL,
    PRIMARY KEY (incident_id, entry_id),
    FOREIGN KEY (incident_id) REFERENCES stewarding_incident (id) ON DELETE CASCADE,
    FOREIGN KEY (entry_id) REFERENCES stewarding_entrylist_entry (id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS stewarding_decision
(
    id                      INTEGER PRIMARY KEY AUTOINCREMENT,
    incident_id             INTEGER,
    session_id              INTEGER   NOT NULL,
    decided_by_user_id      INTEGER,
    penalty_definition_id   INTEGER,
    custom_penalty          VARCHAR(255),
    reasoning               TEXT,
    reasoning_template_id   INTEGER,
    is_no_action            BOOLEAN   NOT NULL DEFAULT FALSE,
    penalized_entry_id      INTEGER,
    penalized_car_text      VARCHAR(255),
    decided_at              TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    superseded_by_id        INTEGER,
    is_active               BOOLEAN   NOT NULL DEFAULT TRUE,
    FOREIGN KEY (incident_id) REFERENCES stewarding_incident (id),
    FOREIGN KEY (session_id) REFERENCES stewarding_session (id),
    FOREIGN KEY (penalty_definition_id) REFERENCES stewarding_penalty_definition (id),
    FOREIGN KEY (reasoning_template_id) REFERENCES stewarding_reasoning_template (id),
    FOREIGN KEY (penalized_entry_id) REFERENCES stewarding_entrylist_entry (id),
    FOREIGN KEY (superseded_by_id) REFERENCES stewarding_decision (id)
);

CREATE INDEX ix_stewarding_decision_incident_id ON stewarding_decision (incident_id);
CREATE INDEX ix_stewarding_decision_session_id ON stewarding_decision (session_id);
CREATE INDEX ix_stewarding_decision_is_active ON stewarding_decision (is_active);

CREATE TABLE IF NOT EXISTS stewarding_appeal
(
    id                   INTEGER PRIMARY KEY AUTOINCREMENT,
    decision_id          INTEGER     NOT NULL,
    filed_by_user_id     INTEGER,
    filed_by_entry_id    INTEGER,
    reason               TEXT        NOT NULL,
    status               VARCHAR(20) NOT NULL DEFAULT 'PENDING',
    response             TEXT,
    responded_by_user_id INTEGER,
    filed_at             TIMESTAMP   NOT NULL DEFAULT CURRENT_TIMESTAMP,
    responded_at         TIMESTAMP,
    FOREIGN KEY (decision_id) REFERENCES stewarding_decision (id),
    FOREIGN KEY (filed_by_entry_id) REFERENCES stewarding_entrylist_entry (id)
);

CREATE INDEX ix_stewarding_appeal_decision_id ON stewarding_appeal (decision_id);
CREATE INDEX ix_stewarding_appeal_status ON stewarding_appeal (status);
