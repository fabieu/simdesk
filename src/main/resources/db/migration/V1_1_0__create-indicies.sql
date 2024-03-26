/* sessions */
CREATE UNIQUE INDEX ux_sessions_file_checksum ON sessions(file_checksum);
CREATE INDEX ix_sessions_track_id ON sessions (track_id);

/* leaderboard_lines */
CREATE INDEX ix_leaderboard_lines_session_id ON leaderboard_lines (session_id);

/* leaderboard_drivers */
CREATE INDEX ix_leaderboard_drivers_car_id_session_id ON leaderboard_drivers (car_id, session_id);

/* laps */
CREATE INDEX ix_laps_car_group_valid ON laps (car_group, valid);

/* penalties */
CREATE INDEX ix_penalties_car_id_session_id ON penalties (car_id, session_id);
