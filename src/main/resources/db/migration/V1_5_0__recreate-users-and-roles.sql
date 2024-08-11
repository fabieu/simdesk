/** Recreate the user table and insert the default user */
DROP TABLE IF EXISTS main.users;

CREATE TABLE IF NOT EXISTS main.users
(
    user_id         INTEGER PRIMARY KEY AUTOINCREMENT,
    username        TEXT                                not null,
    password        TEXT                                not null,
    insert_datetime TIMESTAMP default CURRENT_TIMESTAMP not null,
    UNIQUE (username)
);

/** Create the roles table */
CREATE TABLE IF NOT EXISTS main.users_roles
(
    role_id         INTEGER PRIMARY KEY AUTOINCREMENT,
    user_id         INTEGER                             not null,
    role_name       TEXT                                not null,
    insert_datetime TIMESTAMP default CURRENT_TIMESTAMP not null,
    UNIQUE (user_id, role_name)
);
