CREATE UNIQUE INDEX ux_sessions_file_checksum ON acc_leaderboard.sessions (file_checksum);
CREATE INDEX ix_sessions_track_id ON acc_leaderboard.sessions (track_id);

CREATE INDEX ix_leaderboard_lines_session_id ON acc_leaderboard.leaderboard_lines (session_id);

CREATE INDEX ix_leaderboard_drivers_car_id_session_id ON acc_leaderboard.leaderboard_drivers (car_id, session_id);

CREATE INDEX ix_laps_car_group_valid ON acc_leaderboard.laps (car_group, valid);
