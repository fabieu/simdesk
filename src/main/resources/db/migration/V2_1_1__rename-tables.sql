ALTER TABLE simdesk.drivers
    RENAME TO driver;

ALTER TABLE simdesk.laps
    RENAME TO lap;

ALTER TABLE simdesk.leaderboard_drivers
    RENAME TO leaderboard_driver;

ALTER TABLE simdesk.leaderboard_lines
    RENAME TO leaderboard_line;

ALTER TABLE simdesk.penalties
    RENAME TO penalty;

ALTER TABLE simdesk.sessions
    RENAME TO session;