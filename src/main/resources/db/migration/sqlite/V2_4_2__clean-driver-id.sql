UPDATE driver
SET driver_id = substr(driver_id, 2);

UPDATE lap
SET driver_id = substr(driver_id, 2);

UPDATE leaderboard_driver
SET driver_id = substr(driver_id, 2);