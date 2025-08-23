ALTER TABLE simdesk.users
    RENAME TO "user";

DROP TABLE IF EXISTS simdesk.users_roles;
CREATE TABLE simdesk.user_role
(
    name            TEXT PRIMARY KEY,
    description     TEXT,
    discord_role_id BIGINT
);

CREATE TABLE IF NOT EXISTS simdesk.user_permission
(
    user_id         BIGINT,
    role_name       TEXT,
    insert_datetime TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    PRIMARY KEY (user_id, role_name),
    FOREIGN KEY (user_id) REFERENCES simdesk.user (user_id),
    FOREIGN KEY (role_name) REFERENCES simdesk.user_role (name)
);

/* Default data */
INSERT INTO simdesk.user_role (name, description)
VALUES ('ROLE_ADMIN', 'Role with full access to the application');

INSERT INTO simdesk.user_permission (user_id, role_name)
VALUES (10000, 'ROLE_ADMIN');