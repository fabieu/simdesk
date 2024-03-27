DROP INDEX ix_laps_car_group_valid;

CREATE INDEX ix_laps_driver_id ON laps (driver_id);
CREATE INDEX ix_laps_session_id_driver_id ON laps (session_id, driver_id);
CREATE INDEX ix_laps_fastest_laps ON laps (car_model_id, driver_id, lap_time_millis);