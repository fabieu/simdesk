ALTER TABLE leaderboard_driver
    RENAME COLUMN player_id TO driver_id;
ALTER TABLE driver
    RENAME COLUMN player_id TO driver_id;

CREATE INDEX ix_sessions_session_datetime ON session (session_datetime);
CREATE INDEX ix_leaderboard_driver_driver_id ON leaderboard_driver (driver_id);