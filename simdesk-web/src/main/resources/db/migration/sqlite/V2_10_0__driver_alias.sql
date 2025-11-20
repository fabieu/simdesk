CREATE TABLE IF NOT EXISTS driver_alias
(
    id              INTEGER PRIMARY KEY AUTOINCREMENT,
    driver_id       VARCHAR(18) NOT NULL,
    first_name      VARCHAR(64),
    last_name       VARCHAR(64),
    created_at      TIMESTAMP   NOT NULL DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (driver_id) REFERENCES driver (driver_id) ON DELETE CASCADE
);

CREATE INDEX ix_driver_alias_driver_id ON driver_alias (driver_id);
CREATE INDEX ix_driver_alias_created_at ON driver_alias (created_at DESC);
