/* laps */
UPDATE laps
SET car_group = 'GT2'
WHERE car_group IN ('CHL', 'ST');
UPDATE laps
SET car_group = 'GTC'
WHERE car_group = 'CUP';

/* leaderboard_lines */
UPDATE leaderboard_lines
SET car_group = 'GT2'
WHERE car_group IN ('CHL', 'ST');
UPDATE leaderboard_lines
SET car_group = 'GTC'
WHERE car_group = 'CUP';