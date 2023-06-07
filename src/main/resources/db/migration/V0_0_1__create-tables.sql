-- schema 'acc_leaderboard'
CREATE SCHEMA IF NOT EXISTS acc_leaderboard;

CREATE TABLE IF NOT EXISTS acc_leaderboard.sessions (
    id INT AUTO_INCREMENT,
    session_type VARCHAR NOT NULL,
    race_weekend_index INT NOT NULL,
    server_name VARCHAR NOT NULL,
    track_name VARCHAR NOT NULL,
    wet_session BOOLEAN NOT NULL,
    driver_count INT NOT NULL,
    file_checksum VARCHAR NOT NULL,
    file_name VARCHAR NOT NULL,
    file_directory VARCHAR NOT NULL,
    PRIMARY KEY (id)
);

CREATE TABLE IF NOT EXISTS acc_leaderboard.laps (
    id INT AUTO_INCREMENT,
    session_id INT NOT NULL,
    car_group VARCHAR NOT NULL,
    car_model INT NOT NULL,
    driver_first_name VARCHAR,
    driver_last_name VARCHAR,
    driver_short_name VARCHAR,
    driver_player_id VARCHAR NOT NULL,
    lap_time_millis INT NOT NULL,
    split1_time_millis INT NOT NULL,
    split2_time_millis INT NOT NULL,
    split3_time_millis INT NOT NULL,
    valid BOOLEAN NOT NULL,
    PRIMARY KEY (id),
    FOREIGN KEY (session_id) REFERENCES acc_leaderboard.sessions (id)
);