CREATE TABLE IF NOT EXISTS stewarding_event
(
    id               INTEGER PRIMARY KEY,
    simulation_id    TEXT        NOT NULL,
    name             TEXT        NOT NULL,
    track_id         VARCHAR(32) NOT NULL,
    start_datetime   TIMESTAMP   NOT NULL,
    end_datetime     TIMESTAMP   NOT NULL,
    archive_datetime TIMESTAMP,
    insert_datetime  TIMESTAMP   NOT NULL DEFAULT CURRENT_TIMESTAMP,
    update_datetime  TIMESTAMP   NOT NULL DEFAULT CURRENT_TIMESTAMP
);