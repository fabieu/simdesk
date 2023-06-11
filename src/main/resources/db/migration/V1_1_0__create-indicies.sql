CREATE UNIQUE INDEX ux_sessions_file_checksum ON acc_leaderboard.sessions (file_checksum);

CREATE INDEX ix_sessions_track_id ON acc_leaderboard.sessions (track_id);

CREATE INDEX ix_laps_driver_ranking ON acc_leaderboard.laps (car_group, valid);
