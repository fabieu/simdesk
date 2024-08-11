/* Tables */
CREATE TABLE IF NOT EXISTS sessions
(
    id                 INTEGER PRIMARY KEY,
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

CREATE TABLE IF NOT EXISTS drivers
(
    player_id       VARCHAR(18) NOT NULL,
    first_name      VARCHAR(64),
    last_name       VARCHAR(64),
    short_name      VARCHAR(3),
    locked          BOOLEAN     NOT NULL DEFAULT FALSE,
    last_activity   TIMESTAMP,
    insert_datetime TIMESTAMP   NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (player_id)
);

CREATE TABLE IF NOT EXISTS laps
(
    id              INTEGER PRIMARY KEY,
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
    FOREIGN KEY (session_id) REFERENCES sessions (id),
    FOREIGN KEY (driver_id) REFERENCES drivers (player_id)
);

CREATE TABLE IF NOT EXISTS penalties
(
    id              INTEGER PRIMARY KEY,
    session_id      INTEGER     NOT NULL,
    car_id          INTEGER     NOT NULL,
    reason          VARCHAR(64) NOT NULL,
    penalty         VARCHAR(64) NOT NULL,
    penalty_value   INTEGER     NOT NULL,
    violation_lap   INTEGER     NOT NULL,
    cleared_lap     INTEGER     NOT NULL,
    post_race       BOOLEAN     NOT NULL,
    insert_datetime TIMESTAMP   NOT NULL DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (session_id) REFERENCES sessions (id)
);

CREATE TABLE IF NOT EXISTS leaderboard_lines
(
    id                   INTEGER PRIMARY KEY,
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
    FOREIGN KEY (session_id) REFERENCES sessions (id)
);


CREATE TABLE IF NOT EXISTS leaderboard_drivers
(
    id                INTEGER PRIMARY KEY,
    session_id        INTEGER     NOT NULL,
    player_id         VARCHAR(18) NOT NULL,
    car_id            INTEGER     NOT NULL,
    drive_time_millis BIGINT,
    FOREIGN KEY (session_id) REFERENCES sessions (id),
    FOREIGN KEY (player_id) REFERENCES drivers (player_id)
);

CREATE TABLE IF NOT EXISTS users
(
    user_id         INTEGER PRIMARY KEY AUTOINCREMENT,
    username        TEXT                                not null,
    password        TEXT                                not null,
    insert_datetime TIMESTAMP default CURRENT_TIMESTAMP not null,
    UNIQUE (username)
);

CREATE TABLE IF NOT EXISTS users_roles
(
    role_id         INTEGER PRIMARY KEY AUTOINCREMENT,
    user_id         INTEGER                             not null,
    role_name       TEXT                                not null,
    insert_datetime TIMESTAMP default CURRENT_TIMESTAMP not null,
    UNIQUE (user_id, role_name)
);

CREATE TABLE IF NOT EXISTS users_discord
(
    user_id         INTEGER   NOT NULL,
    username        TEXT,
    global_name     TEXT,
    update_datetime TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    permits         TEXT,
    PRIMARY KEY (user_id)
);

CREATE TABLE IF NOT EXISTS bop
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

CREATE TABLE IF NOT EXISTS properties
(
    id              INTEGER PRIMARY KEY,
    key             TEXT      NOT NULL UNIQUE,
    value           TEXT      NOT NULL,
    description     TEXT,
    active          BOOLEAN   NOT NULL DEFAULT true,
    update_datetime TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

/* Indexes */
CREATE INDEX ix_sessions_track_id ON sessions (track_id);
CREATE INDEX ix_leaderboard_lines_session_id ON leaderboard_lines (session_id);
CREATE INDEX ix_leaderboard_drivers_car_id_session_id ON leaderboard_drivers (car_id, session_id);
CREATE INDEX ix_laps_driver_id ON laps (driver_id);
CREATE INDEX ix_laps_session_id_driver_id ON laps (session_id, driver_id);
CREATE INDEX ix_laps_fastest_laps ON laps (car_model_id, driver_id, lap_time_millis);
CREATE INDEX ix_penalties_car_id_session_id ON penalties (car_id, session_id);

/* Default data */
INSERT INTO users_roles (user_id, role_name)
VALUES (10000, 'ROLE_ADMIN')
ON CONFLICT DO NOTHING;



