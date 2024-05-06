CREATE TABLE IF NOT EXISTS users_discord
(
    user_id     INTEGER NOT NULL,
    username        TEXT,
    global_name TEXT,
    update_datetime TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (user_id)
);

CREATE TABLE IF NOT EXISTS permit
(
    user_id INTEGER NOT NULL,
    permit  TEXT    NOT NULL,
    update_datetime TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (user_id, permit),
    FOREIGN KEY (user_id) REFERENCES users_discord (user_id)
);
