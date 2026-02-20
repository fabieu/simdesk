-- Drop all stewarding tables and recreate with string IDs and stewarding_round table name

DROP TABLE IF EXISTS stewarding_appeal;
DROP TABLE IF EXISTS stewarding_decision;
DROP TABLE IF EXISTS stewarding_incident_involved_entry;
DROP TABLE IF EXISTS stewarding_incident;
DROP TABLE IF EXISTS stewarding_entrylist_driver;
DROP TABLE IF EXISTS stewarding_entrylist_entry;
DROP TABLE IF EXISTS stewarding_entrylist;
DROP TABLE IF EXISTS stewarding_session;
DROP TABLE IF EXISTS stewarding_race_weekend;
DROP TABLE IF EXISTS stewarding_series;
DROP TABLE IF EXISTS stewarding_penalty_definition;
DROP TABLE IF EXISTS stewarding_penalty_catalog;
DROP TABLE IF EXISTS stewarding_reasoning_template;
DROP TABLE IF EXISTS stewarding_track;

CREATE TABLE stewarding_track
(
    id            VARCHAR(12) PRIMARY KEY NOT NULL,
    name          VARCHAR(255)            NOT NULL,
    country       VARCHAR(100),
    map_image_url VARCHAR(500),
    map_metadata  TEXT,
    created_at    TIMESTAMP               NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at    TIMESTAMP               NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE stewarding_penalty_catalog
(
    id          VARCHAR(12) PRIMARY KEY NOT NULL,
    name        VARCHAR(255)            NOT NULL,
    description TEXT,
    created_at  TIMESTAMP               NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at  TIMESTAMP               NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE stewarding_penalty_definition
(
    id              VARCHAR(12) PRIMARY KEY NOT NULL,
    catalog_id      VARCHAR(12)             NOT NULL,
    code            VARCHAR(50),
    name            VARCHAR(255)            NOT NULL,
    description     TEXT,
    category        VARCHAR(100),
    session_type    VARCHAR(20)             NOT NULL DEFAULT 'ALL',
    default_penalty VARCHAR(255),
    severity        INTEGER,
    sort_order      INTEGER                          DEFAULT 0,
    FOREIGN KEY (catalog_id) REFERENCES stewarding_penalty_catalog (id)
);

CREATE INDEX ix_stewarding_penalty_definition_catalog_id ON stewarding_penalty_definition (catalog_id);

CREATE TABLE stewarding_reasoning_template
(
    id            VARCHAR(12) PRIMARY KEY NOT NULL,
    name          VARCHAR(255)            NOT NULL,
    category      VARCHAR(100),
    template_text TEXT                    NOT NULL,
    sort_order    INTEGER                          DEFAULT 0
);

CREATE TABLE stewarding_series
(
    id                  VARCHAR(12) PRIMARY KEY NOT NULL,
    title               VARCHAR(255)            NOT NULL,
    description         TEXT,
    discord_webhook_url VARCHAR(500),
    video_url_enabled   BOOLEAN                 NOT NULL DEFAULT FALSE,
    penalty_catalog_id  VARCHAR(12),
    start_date          DATE,
    end_date            DATE,
    created_at          TIMESTAMP               NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at          TIMESTAMP               NOT NULL DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (penalty_catalog_id) REFERENCES stewarding_penalty_catalog (id)
);

CREATE TABLE stewarding_round
(
    id         VARCHAR(12) PRIMARY KEY NOT NULL,
    series_id  VARCHAR(12),
    track_id   VARCHAR(12),
    title      VARCHAR(255)            NOT NULL,
    start_date DATE,
    end_date   DATE,
    created_at TIMESTAMP               NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP               NOT NULL DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (series_id) REFERENCES stewarding_series (id),
    FOREIGN KEY (track_id) REFERENCES stewarding_track (id)
);

CREATE INDEX ix_stewarding_round_series_id ON stewarding_round (series_id);
CREATE INDEX ix_stewarding_round_track_id ON stewarding_round (track_id);

CREATE TABLE stewarding_session
(
    id           VARCHAR(12) PRIMARY KEY NOT NULL,
    round_id     VARCHAR(12)             NOT NULL,
    session_type VARCHAR(20)             NOT NULL,
    title        VARCHAR(255),
    start_time   TIMESTAMP,
    end_time     TIMESTAMP,
    sort_order   INTEGER                          DEFAULT 0,
    created_at   TIMESTAMP               NOT NULL DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (round_id) REFERENCES stewarding_round (id)
);

CREATE INDEX ix_stewarding_session_round_id ON stewarding_session (round_id);

CREATE TABLE stewarding_entrylist
(
    id          VARCHAR(12) PRIMARY KEY NOT NULL,
    round_id    VARCHAR(12),
    uploaded_at TIMESTAMP               NOT NULL DEFAULT CURRENT_TIMESTAMP,
    raw_json    TEXT,
    FOREIGN KEY (round_id) REFERENCES stewarding_round (id)
);

CREATE INDEX ix_stewarding_entrylist_round_id ON stewarding_entrylist (round_id);

CREATE TABLE stewarding_entrylist_entry
(
    id           VARCHAR(12) PRIMARY KEY NOT NULL,
    entrylist_id VARCHAR(12)             NOT NULL,
    race_number  INTEGER                 NOT NULL,
    car_model_id INTEGER,
    team_name    VARCHAR(255),
    display_name VARCHAR(255),
    FOREIGN KEY (entrylist_id) REFERENCES stewarding_entrylist (id) ON DELETE CASCADE
);

CREATE INDEX ix_stewarding_entrylist_entry_entrylist_id ON stewarding_entrylist_entry (entrylist_id);

CREATE TABLE stewarding_entrylist_driver
(
    id         VARCHAR(12) PRIMARY KEY NOT NULL,
    entry_id   VARCHAR(12)             NOT NULL,
    first_name VARCHAR(100),
    last_name  VARCHAR(100),
    short_name VARCHAR(10),
    steam_id   VARCHAR(50),
    category   INTEGER,
    FOREIGN KEY (entry_id) REFERENCES stewarding_entrylist_entry (id) ON DELETE CASCADE
);

CREATE INDEX ix_stewarding_entrylist_driver_entry_id ON stewarding_entrylist_driver (entry_id);

CREATE TABLE stewarding_incident
(
    id                   VARCHAR(12) PRIMARY KEY NOT NULL,
    session_id           VARCHAR(12)             NOT NULL,
    title                VARCHAR(255)            NOT NULL,
    description          TEXT,
    lap                  INTEGER,
    timestamp_in_session VARCHAR(100),
    map_marker_x         REAL,
    map_marker_y         REAL,
    video_url            VARCHAR(500),
    involved_cars_text   VARCHAR(500),
    status               VARCHAR(30)             NOT NULL DEFAULT 'REPORTED',
    reported_by_user_id  INTEGER,
    created_at           TIMESTAMP               NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at           TIMESTAMP               NOT NULL DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (session_id) REFERENCES stewarding_session (id)
);

CREATE INDEX ix_stewarding_incident_session_id ON stewarding_incident (session_id);
CREATE INDEX ix_stewarding_incident_status ON stewarding_incident (status);

CREATE TABLE stewarding_incident_involved_entry
(
    incident_id VARCHAR(12) NOT NULL,
    entry_id    VARCHAR(12) NOT NULL,
    PRIMARY KEY (incident_id, entry_id),
    FOREIGN KEY (incident_id) REFERENCES stewarding_incident (id) ON DELETE CASCADE,
    FOREIGN KEY (entry_id) REFERENCES stewarding_entrylist_entry (id) ON DELETE CASCADE
);

CREATE TABLE stewarding_decision
(
    id                    VARCHAR(12) PRIMARY KEY NOT NULL,
    incident_id           VARCHAR(12),
    session_id            VARCHAR(12)             NOT NULL,
    decided_by_user_id    INTEGER,
    penalty_definition_id VARCHAR(12),
    custom_penalty        VARCHAR(255),
    reasoning             TEXT,
    reasoning_template_id VARCHAR(12),
    is_no_action          BOOLEAN                 NOT NULL DEFAULT FALSE,
    penalized_entry_id    VARCHAR(12),
    penalized_car_text    VARCHAR(255),
    decided_at            TIMESTAMP               NOT NULL DEFAULT CURRENT_TIMESTAMP,
    superseded_by_id      VARCHAR(12),
    is_active             BOOLEAN                 NOT NULL DEFAULT TRUE,
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

CREATE TABLE stewarding_appeal
(
    id                   VARCHAR(12) PRIMARY KEY NOT NULL,
    decision_id          VARCHAR(12)             NOT NULL,
    filed_by_user_id     INTEGER,
    filed_by_entry_id    VARCHAR(12),
    reason               TEXT                    NOT NULL,
    status               VARCHAR(20)             NOT NULL DEFAULT 'PENDING',
    response             TEXT,
    responded_by_user_id INTEGER,
    filed_at             TIMESTAMP               NOT NULL DEFAULT CURRENT_TIMESTAMP,
    responded_at         TIMESTAMP,
    FOREIGN KEY (decision_id) REFERENCES stewarding_decision (id),
    FOREIGN KEY (filed_by_entry_id) REFERENCES stewarding_entrylist_entry (id)
);

CREATE INDEX ix_stewarding_appeal_decision_id ON stewarding_appeal (decision_id);
CREATE INDEX ix_stewarding_appeal_status ON stewarding_appeal (status);
