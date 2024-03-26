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
    file_directory     TEXT        NOT NULL,
    insert_datetime    TIMESTAMP   NOT NULL DEFAULT CURRENT_TIMESTAMP
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