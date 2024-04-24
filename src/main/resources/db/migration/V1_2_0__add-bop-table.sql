CREATE TABLE IF NOT EXISTS bop
(
    track_id        TEXT      NOT NULL,
    car_id          INTEGER   NOT NULL,
    ballast_kg      INTEGER   NOT NULL,
    restrictor      INTEGER   NOT NULL,
    active          BOOLEAN   NOT NULL DEFAULT TRUE,
    username        TEXT,
    update_datetime TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (track_id, car_id)
);
