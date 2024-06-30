CREATE TABLE IF NOT EXISTS properties
(
    id              INTEGER PRIMARY KEY,
    key             TEXT      NOT NULL UNIQUE,
    value           TEXT      NOT NULL,
    description     TEXT,
    active          BOOLEAN   NOT NULL DEFAULT true,
    update_datetime TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);
