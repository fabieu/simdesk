ALTER TABLE simdesk.leaderboard_driver
    RENAME COLUMN player_id TO driver_id;
ALTER TABLE simdesk.driver
    RENAME COLUMN player_id TO driver_id;

CREATE INDEX ix_sessions_session_datetime ON simdesk.session (session_datetime);
CREATE INDEX ix_leaderboard_driver_driver_id ON simdesk.leaderboard_driver (driver_id);