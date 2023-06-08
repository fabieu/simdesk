CREATE SCHEMA IF NOT EXISTS acc_leaderboard;

CREATE TABLE IF NOT EXISTS acc_leaderboard.sessions (
    id INT AUTO_INCREMENT,
    session_type VARCHAR(10) NOT NULL,
    race_weekend_index INT NOT NULL,
    server_name TEXT NOT NULL,
    track_name VARCHAR(32) NOT NULL,
    wet_session BOOLEAN NOT NULL,
    driver_count INT NOT NULL,
    file_checksum VARCHAR(64) NOT NULL,
    file_name VARCHAR(64) NOT NULL,
    file_directory TEXT NOT NULL,
    PRIMARY KEY (id)
);

CREATE TABLE IF NOT EXISTS acc_leaderboard.drivers (
    player_id VARCHAR (32) NOT NULL,
    first_name VARCHAR(64),
    last_name VARCHAR (64),
    short_name VARCHAR(3),
    PRIMARY KEY (player_id)
);

CREATE TABLE IF NOT EXISTS acc_leaderboard.laps (
    id INT AUTO_INCREMENT,
    session_id INT NOT NULL,
    driver_id VARCHAR(32) NOT NULL,
    car_group VARCHAR(8) NOT NULL,
    car_model INT NOT NULL,
    lap_time_millis BIGINT NOT NULL,
    split1_millis BIGINT NOT NULL,
    split2_millis BIGINT NOT NULL,
    split3_millis BIGINT NOT NULL,
    valid BOOLEAN NOT NULL,
    PRIMARY KEY (id),
    FOREIGN KEY (session_id) REFERENCES acc_leaderboard.sessions (id),
    FOREIGN KEY (driver_id) REFERENCES acc_leaderboard.drivers (player_id)
);

CREATE UNIQUE INDEX ux_sessions_file_checksum ON acc_leaderboard.sessions (file_checksum);