DROP TABLE IF EXISTS simdesk.user_permission;
DROP TABLE IF EXISTS simdesk.user;

CREATE TABLE simdesk.user
(
    user_id         SERIAL PRIMARY KEY,
    username        TEXT      NOT NULL UNIQUE,
    password        TEXT,
    type            TEXT      NOT NULL,
    insert_datetime TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE user_permission
(
    user_id INTEGER,
    role    TEXT,
    PRIMARY KEY (user_id, role),
    FOREIGN KEY (user_id) REFERENCES simdesk.user (user_id),
    FOREIGN KEY (role) REFERENCES simdesk.user_role (name)
);

CREATE TABLE IF NOT EXISTS user_api_key
(
    api_key_id        SERIAL PRIMARY KEY,
    user_id           INTEGER   NOT NULL,
    api_key           TEXT      NOT NULL UNIQUE,
    name              TEXT      NOT NULL,
    active            BOOLEAN   NOT NULL DEFAULT TRUE,
    creation_datetime TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES simdesk.user (user_id)
);

INSERT INTO simdesk.user (type, user_id, username)
VALUES ('SYSTEM', 1, 'admin');

INSERT INTO simdesk.user_permission (user_id, role)
VALUES (1, 'ROLE_ADMIN');

ALTER SEQUENCE simdesk.user_user_id_seq restart with 100;
