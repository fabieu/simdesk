ALTER TABLE simdesk.lap
    DROP CONSTRAINT IF EXISTS lap_driver_id_fkey;

ALTER TABLE simdesk.lap
    DROP CONSTRAINT IF EXISTS laps_driver_id_fkey;

ALTER TABLE simdesk.lap
    ADD CONSTRAINT lap_driver_id_fkey
        FOREIGN KEY (driver_id) REFERENCES simdesk.driver ON UPDATE CASCADE;

ALTER TABLE simdesk.leaderboard_driver
    DROP CONSTRAINT IF EXISTS leaderboard_driver_player_id_fkey;

ALTER TABLE simdesk.leaderboard_driver
    DROP CONSTRAINT IF EXISTS leaderboard_drivers_player_id_fkey;

ALTER TABLE simdesk.leaderboard_driver
    ADD CONSTRAINT leaderboard_driver_driver_id_fkey
        FOREIGN KEY (driver_id) REFERENCES simdesk.driver ON UPDATE CASCADE;

UPDATE simdesk.driver
SET driver_id = substr(driver_id, 2);