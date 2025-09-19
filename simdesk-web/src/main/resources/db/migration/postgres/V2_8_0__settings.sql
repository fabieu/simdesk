CREATE TABLE IF NOT EXISTS settings
(
    id              SERIAL PRIMARY KEY,
    key             TEXT      NOT NULL UNIQUE,
    value           TEXT      NOT NULL,
    active          BOOLEAN   NOT NULL DEFAULT true,
    update_datetime TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);