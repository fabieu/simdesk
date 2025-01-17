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

/* Recreate user table */
DROP TABLE IF EXISTS "user";

CREATE TABLE IF NOT EXISTS "user"
(
    user_id         INTEGER PRIMARY KEY,
    username        TEXT      NOT NULL,
    password        TEXT,
    insert_datetime TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    UNIQUE (username)
);

/* Change default system user id to 1 */
DELETE
FROM user_permission
WHERE user_id = 10000;

INSERT INTO user_permission (user_id, role)
VALUES (1, 'ROLE_ADMIN');