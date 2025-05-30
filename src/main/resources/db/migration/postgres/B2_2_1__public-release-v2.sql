/* Tables */
CREATE TABLE IF NOT EXISTS simdesk.session
(
    id                 SERIAL PRIMARY KEY,
    session_type       VARCHAR(10) NOT NULL,
    race_weekend_index INTEGER     NOT NULL,
    server_name        TEXT        NOT NULL,
    track_id           VARCHAR(32) NOT NULL,
    wet_session        BOOLEAN     NOT NULL,
    car_count          INTEGER     NOT NULL,
    session_datetime   TIMESTAMP   NOT NULL DEFAULT CURRENT_TIMESTAMP,
    file_checksum      VARCHAR(64) NOT NULL,
    file_name          VARCHAR(64) NOT NULL,
    file_content       TEXT        NOT NULL,
    insert_datetime    TIMESTAMP   NOT NULL DEFAULT CURRENT_TIMESTAMP,
    UNIQUE (file_checksum)
);

CREATE TABLE IF NOT EXISTS simdesk.driver
(
    player_id       VARCHAR(18) NOT NULL,
    first_name      VARCHAR(64),
    last_name       VARCHAR(64),
    short_name      VARCHAR(3),
    visibility      TEXT                 DEFAULT 'PUBLIC',
    last_activity   TIMESTAMP,
    insert_datetime TIMESTAMP   NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (player_id)
);

CREATE TABLE IF NOT EXISTS simdesk.lap
(
    id              SERIAL PRIMARY KEY,
    session_id      INTEGER     NOT NULL,
    driver_id       VARCHAR(18) NOT NULL,
    car_group       VARCHAR(3)  NOT NULL,
    car_model_id    INTEGER     NOT NULL,
    lap_time_millis BIGINT      NOT NULL,
    split1_millis   BIGINT      NOT NULL,
    split2_millis   BIGINT      NOT NULL,
    split3_millis   BIGINT      NOT NULL,
    valid           BOOLEAN     NOT NULL,
    insert_datetime TIMESTAMP   NOT NULL DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (session_id) REFERENCES session (id),
    FOREIGN KEY (driver_id) REFERENCES driver (player_id)
);

CREATE TABLE IF NOT EXISTS simdesk.penalty
(
    id              SERIAL PRIMARY KEY,
    session_id      INTEGER     NOT NULL,
    car_id          INTEGER     NOT NULL,
    reason          VARCHAR(64) NOT NULL,
    penalty         VARCHAR(64) NOT NULL,
    penalty_value   INTEGER     NOT NULL,
    violation_lap   INTEGER     NOT NULL,
    cleared_lap     INTEGER     NOT NULL,
    post_race       BOOLEAN     NOT NULL,
    insert_datetime TIMESTAMP   NOT NULL DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (session_id) REFERENCES session (id)
);

CREATE TABLE IF NOT EXISTS simdesk.leaderboard_line
(
    id                   SERIAL PRIMARY KEY,
    session_id           INTEGER     NOT NULL,
    ranking              INTEGER     NOT NULL,
    cup_category         VARCHAR(32) NOT NULL,
    car_id               INTEGER     NOT NULL,
    car_group            VARCHAR(3)  NOT NULL,
    car_model_id         INTEGER     NOT NULL,
    ballast_kg           INTEGER,
    race_number          INTEGER     NOT NULL,
    best_lap_time_millis BIGINT      NOT NULL,
    best_split1_millis   BIGINT      NOT NULL,
    best_split2_millis   BIGINT      NOT NULL,
    best_split3_millis   BIGINT      NOT NULL,
    total_time_millis    BIGINT      NOT NULL,
    lap_count            INTEGER     NOT NULL,
    insert_datetime      TIMESTAMP   NOT NULL DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (session_id) REFERENCES session (id)
);


CREATE TABLE IF NOT EXISTS simdesk.leaderboard_driver
(
    id                SERIAL PRIMARY KEY,
    session_id        INTEGER     NOT NULL,
    player_id         VARCHAR(18) NOT NULL,
    car_id            INTEGER     NOT NULL,
    drive_time_millis BIGINT,
    FOREIGN KEY (session_id) REFERENCES session (id),
    FOREIGN KEY (player_id) REFERENCES driver (player_id)
);

CREATE TABLE IF NOT EXISTS simdesk.user
(
    user_id         SERIAL PRIMARY KEY,
    username        TEXT                                NOT NULL,
    password        TEXT                                NOT NULL,
    insert_datetime TIMESTAMP default CURRENT_TIMESTAMP NOT NULL,
    UNIQUE (username)
);

CREATE TABLE IF NOT EXISTS simdesk.user_role
(
    name            TEXT PRIMARY KEY,
    description     TEXT,
    discord_role_id TEXT
);

CREATE TABLE IF NOT EXISTS simdesk.user_permission
(
    user_id         BIGINT,
    role_name       TEXT,
    insert_datetime TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    PRIMARY KEY (user_id, role_name),
    FOREIGN KEY (user_id) REFERENCES simdesk.user (user_id),
    FOREIGN KEY (role_name) REFERENCES simdesk.user_role (name)
);

CREATE TABLE IF NOT EXISTS simdesk.bop
(
    track_id        TEXT      NOT NULL,
    car_id          INTEGER   NOT NULL,
    ballast_kg      INTEGER   NOT NULL,
    restrictor      INTEGER   NOT NULL,
    active          BOOLEAN   NOT NULL DEFAULT TRUE,
    username        TEXT,
    update_datetime TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (track_id, car_id)
);

/* Indexes */
CREATE INDEX ix_sessions_track_id ON simdesk.session (track_id);
CREATE INDEX ix_leaderboard_lines_session_id ON simdesk.leaderboard_line (session_id);
CREATE INDEX ix_leaderboard_drivers_car_id_session_id ON simdesk.leaderboard_driver (car_id, session_id);
CREATE INDEX ix_laps_driver_id ON simdesk.lap (driver_id);
CREATE INDEX ix_laps_session_id_driver_id ON simdesk.lap (session_id, driver_id);
CREATE INDEX ix_laps_fastest_laps ON simdesk.lap (car_model_id, driver_id, lap_time_millis);
CREATE INDEX ix_penalties_car_id_session_id ON simdesk.penalty (car_id, session_id);

/* Default data */
INSERT INTO simdesk.user (user_id, username, password)
VALUES (10000, 'admin', 'admin');

INSERT INTO simdesk.user_role (name, description)
VALUES ('ROLE_ADMIN', 'Role with full access to the application');

INSERT INTO simdesk.user_role (name, description)
VALUES ('ROLE_MANAGER', 'Role with management access to the application');

INSERT INTO simdesk.user_permission (user_id, role_name)
VALUES (10000, 'ROLE_ADMIN');



