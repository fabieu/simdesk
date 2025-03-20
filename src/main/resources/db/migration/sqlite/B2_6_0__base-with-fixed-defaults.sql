/* Tables */
CREATE TABLE IF NOT EXISTS session
(
    id                 INTEGER PRIMARY KEY,
    session_type       VARCHAR(10) NOT NULL,
    race_weekend_index INTEGER     NOT NULL,
    server_name        TEXT        NOT NULL,
    track_id           VARCHAR(32) NOT NULL,
    wet_session        BOOLEAN     NOT NULL,
    car_count          INTEGER     NOT NULL,
    session_datetime   TIMESTAMP   NOT NULL DEFAULT ((UNIXEPOCH('subsec') * 1000)),
    file_checksum      VARCHAR(64) NOT NULL UNIQUE,
    file_name          VARCHAR(64) NOT NULL,
    file_content       TEXT        NOT NULL,
    insert_datetime    TIMESTAMP   NOT NULL DEFAULT ((UNIXEPOCH('subsec') * 1000))
);

CREATE INDEX ix_session_track_id ON session (track_id);
CREATE INDEX ix_session_session_datetime ON session (session_datetime);
CREATE INDEX ix_session_insert_datetime ON session (insert_datetime);

CREATE TABLE IF NOT EXISTS driver
(
    driver_id       VARCHAR(18) PRIMARY KEY,
    first_name      VARCHAR(64),
    last_name       VARCHAR(64),
    short_name      VARCHAR(3),
    visibility      TEXT               DEFAULT 'PUBLIC',
    last_activity   TIMESTAMP,
    insert_datetime TIMESTAMP NOT NULL DEFAULT ((UNIXEPOCH('subsec') * 1000))
);

CREATE INDEX ix_lap_driver_id ON lap (driver_id);

CREATE TABLE IF NOT EXISTS lap
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
    insert_datetime TIMESTAMP   NOT NULL DEFAULT ((UNIXEPOCH('subsec') * 1000)),
    FOREIGN KEY (session_id) REFERENCES session (id),
    FOREIGN KEY (driver_id) REFERENCES driver (driver_id)
);

CREATE INDEX ix_lap_session_id_driver_id ON lap (session_id, driver_id);
CREATE INDEX ix_lap_fastest_laps ON lap (car_model_id, driver_id, lap_time_millis);

CREATE TABLE IF NOT EXISTS penalty
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
    insert_datetime TIMESTAMP   NOT NULL DEFAULT ((UNIXEPOCH('subsec') * 1000)),
    FOREIGN KEY (session_id) REFERENCES session (id)
);

CREATE INDEX ix_penalty_car_id_session_id ON penalty (car_id, session_id);

CREATE TABLE IF NOT EXISTS bop
(
    track_id        TEXT      NOT NULL,
    car_id          INTEGER   NOT NULL,
    ballast_kg      INTEGER   NOT NULL,
    restrictor      INTEGER   NOT NULL,
    active          BOOLEAN   NOT NULL DEFAULT TRUE,
    username        TEXT,
    update_datetime TIMESTAMP NOT NULL DEFAULT ((UNIXEPOCH('subsec') * 1000)),
    PRIMARY KEY (track_id, car_id)
);

CREATE TABLE IF NOT EXISTS leaderboard_line
(
    id                   INTEGER PRIMARY KEY,
    session_id           INTEGER     NOT NULL,
    ranking              INTEGER     NOT NULL,
    cup_category         VARCHAR(32) NOT NULL,
    car_id               INTEGER     NOT NULL,
    car_model_id         INTEGER     NOT NULL,
    ballast_kg           INTEGER,
    race_number          INTEGER     NOT NULL,
    best_lap_time_millis BIGINT      NOT NULL,
    best_split1_millis   BIGINT      NOT NULL,
    best_split2_millis   BIGINT      NOT NULL,
    best_split3_millis   BIGINT      NOT NULL,
    total_time_millis    BIGINT      NOT NULL,
    lap_count            INTEGER     NOT NULL,
    insert_datetime      TIMESTAMP   NOT NULL DEFAULT ((UNIXEPOCH('subsec') * 1000)),
    FOREIGN KEY (session_id) REFERENCES session (id)
);

CREATE INDEX ix_leaderboard_line_session_id ON leaderboard_line (session_id);

CREATE TABLE IF NOT EXISTS leaderboard_driver
(
    id                INTEGER PRIMARY KEY,
    session_id        INTEGER     NOT NULL,
    driver_id         VARCHAR(18) NOT NULL,
    car_id            INTEGER     NOT NULL,
    drive_time_millis BIGINT,
    FOREIGN KEY (session_id) REFERENCES session (id),
    FOREIGN KEY (driver_id) REFERENCES driver (driver_id)
);

CREATE INDEX ix_leaderboard_driver_driver_id ON leaderboard_driver (driver_id);
CREATE INDEX ix_leaderboard_driver_car_id_session_id ON leaderboard_driver (car_id, session_id);

CREATE TABLE "user"
(
    user_id         INTEGER PRIMARY KEY AUTOINCREMENT,
    username        TEXT      NOT NULL UNIQUE,
    password        TEXT,
    type            TEXT      NOT NULL,
    insert_datetime TIMESTAMP NOT NULL DEFAULT ((UNIXEPOCH('subsec') * 1000))
);

CREATE TABLE IF NOT EXISTS user_role
(
    name            TEXT PRIMARY KEY,
    description     TEXT,
    discord_role_id BIGINT
);

CREATE TABLE user_permission
(
    user_id INTEGER,
    role    TEXT,
    PRIMARY KEY (user_id, role),
    FOREIGN KEY (user_id) REFERENCES "user" (user_id),
    FOREIGN KEY (role) REFERENCES user_role (name)
);

CREATE TABLE IF NOT EXISTS user_api_key
(
    api_key_id        INTEGER PRIMARY KEY AUTOINCREMENT,
    user_id           INTEGER   NOT NULL,
    api_key           TEXT      NOT NULL UNIQUE,
    name              TEXT      NOT NULL,
    active            BOOLEAN   NOT NULL DEFAULT TRUE,
    creation_datetime TIMESTAMP NOT NULL DEFAULT ((UNIXEPOCH('subsec') * 1000)),
    FOREIGN KEY (user_id) REFERENCES "user" (user_id)
);

/* Default data */
INSERT INTO user_role (name, description)
VALUES ('ROLE_ADMIN', 'Role with full access to the application');

INSERT INTO user_role (name, description)
VALUES ('ROLE_MANAGER', 'Role with management access to the application');

INSERT INTO "user" (type, user_id, username)
VALUES ('SYSTEM', 1, 'admin');

INSERT INTO user_permission (user_id, role)
VALUES (1, 'ROLE_ADMIN');

UPDATE sqlite_sequence
SET seq=99
WHERE name = 'user';