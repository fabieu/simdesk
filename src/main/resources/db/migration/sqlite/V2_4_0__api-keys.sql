CREATE TABLE IF NOT EXISTS user_api_key
(
    api_key_id        INTEGER PRIMARY KEY,
    user_id           INTEGER   NOT NULL,
    api_key           TEXT      NOT NULL UNIQUE,
    name              TEXT      NOT NULL,
    active            BOOLEAN   NOT NULL DEFAULT TRUE,
    creation_datetime TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES "user" (user_id)
);

ALTER TABLE user_permission
    RENAME role_name TO role;