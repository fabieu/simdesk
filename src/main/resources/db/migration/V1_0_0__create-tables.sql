CREATE SCHEMA IF NOT EXISTS acc_leaderboard;

CREATE TABLE IF NOT EXISTS acc_leaderboard.sessions
(
    id                 INT AUTO_INCREMENT,
    session_type       VARCHAR(10) NOT NULL,
    race_weekend_index INT         NOT NULL,
    server_name        TEXT        NOT NULL,
    track_id           VARCHAR(32) NOT NULL,
    wet_session        BOOLEAN     NOT NULL,
    car_count          INT         NOT NULL,
    session_datetime   DATETIME    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    file_checksum      VARCHAR(64) NOT NULL,
    file_name          VARCHAR(64) NOT NULL,
    file_directory     TEXT        NOT NULL,
    insert_datetime    DATETIME    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    update_datetime    DATETIME    NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (id)
);

CREATE TABLE IF NOT EXISTS acc_leaderboard.drivers
(
    player_id       VARCHAR(18) NOT NULL,
    first_name      VARCHAR(64),
    last_name       VARCHAR(64),
    short_name      VARCHAR(3),
    locked          BOOLEAN     NOT NULL DEFAULT FALSE,
    last_activity   DATETIME,
    insert_datetime DATETIME    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    update_datetime DATETIME    NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (player_id)
);

CREATE TABLE IF NOT EXISTS acc_leaderboard.laps
(
    id              INT AUTO_INCREMENT,
    session_id      INT         NOT NULL,
    driver_id       VARCHAR(18) NOT NULL,
    car_group       VARCHAR(3)  NOT NULL,
    car_model_id    INT         NOT NULL,
    lap_time_millis BIGINT      NOT NULL,
    split1_millis   BIGINT      NOT NULL,
    split2_millis   BIGINT      NOT NULL,
    split3_millis   BIGINT      NOT NULL,
    valid           BOOLEAN     NOT NULL,
    insert_datetime DATETIME    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    update_datetime DATETIME    NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    FOREIGN KEY (session_id) REFERENCES acc_leaderboard.sessions (id),
    FOREIGN KEY (driver_id) REFERENCES acc_leaderboard.drivers (player_id)
);

CREATE TABLE IF NOT EXISTS acc_leaderboard.leaderboard_lines
(
    id                   INT AUTO_INCREMENT,
    session_id           INT         NOT NULL,
    ranking              INT         NOT NULL,
    cup_category         VARCHAR(32) NOT NULL,
    car_id               INT         NOT NULL,
    car_group            VARCHAR(3)  NOT NULL,
    car_model_id         INT         NOT NULL,
    race_number          INT         NOT NULL,
    best_lap_time_millis BIGINT      NOT NULL,
    best_split1_millis   BIGINT      NOT NULL,
    best_split2_millis   BIGINT      NOT NULL,
    best_split3_millis   BIGINT      NOT NULL,
    total_time_millis    BIGINT      NOT NULL,
    lap_count            INT         NOT NULL,
    insert_datetime      DATETIME    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    update_datetime      DATETIME    NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    FOREIGN KEY (session_id) REFERENCES acc_leaderboard.sessions (id)
);


CREATE TABLE IF NOT EXISTS acc_leaderboard.leaderboard_drivers
(
    id         INT AUTO_INCREMENT,
    session_id INT         NOT NULL,
    player_id  VARCHAR(18) NOT NULL,
    car_id     INT         NOT NULL,
    PRIMARY KEY (id),
    FOREIGN KEY (session_id) REFERENCES acc_leaderboard.sessions (id),
    FOREIGN KEY (player_id) REFERENCES acc_leaderboard.drivers (player_id)
);