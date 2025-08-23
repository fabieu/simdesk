ALTER TABLE leaderboard_line
    RENAME COLUMN best_split1_millis TO best_sector1_millis;
ALTER TABLE leaderboard_line
    RENAME COLUMN best_split2_millis TO best_sector2_millis;
ALTER TABLE leaderboard_line
    RENAME COLUMN best_split3_millis TO best_sector3_millis;

ALTER TABLE lap
    RENAME COLUMN split1_millis TO sector1_millis;
ALTER TABLE lap
    RENAME COLUMN split2_millis TO sector2_millis;
ALTER TABLE lap
    RENAME COLUMN split3_millis TO sector3_millis;
