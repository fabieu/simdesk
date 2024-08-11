DROP TABLE IF EXISTS users;

CREATE TABLE IF NOT EXISTS users
(
    user_id         INTEGER PRIMARY KEY AUTOINCREMENT,
    username        TEXT                                not null,
    password        TEXT                                not null,
    insert_datetime TIMESTAMP default CURRENT_TIMESTAMP not null,
    UNIQUE (username)
);

CREATE TABLE IF NOT EXISTS users_roles
(
    role_id         INTEGER PRIMARY KEY AUTOINCREMENT,
    user_id         INTEGER                             not null,
    role_name       TEXT                                not null,
    insert_datetime TIMESTAMP default CURRENT_TIMESTAMP not null,
    UNIQUE (user_id, role_name)
);

INSERT INTO users_roles (user_id, role_name)
VALUES (10000, 'ROLE_ADMIN')
ON CONFLICT DO NOTHING;