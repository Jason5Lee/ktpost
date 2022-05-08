CREATE TABLE users (
    user_id BIGINT UNSIGNED NOT NULL PRIMARY KEY,
    user_name VARCHAR(20) NOT NULL,
    password CHAR(60) NOT NULL,
    creation_time BIGINT UNSIGNED NOT NULL
);
CREATE UNIQUE INDEX idx_user_name
ON users (user_name);

CREATE TABLE posts (
    post_id BIGINT UNSIGNED NOT NULL PRIMARY KEY,
    creator BIGINT UNSIGNED NOT NULL,
    creation_time BIGINT UNSIGNED NOT NULL,
    last_modified BIGINT UNSIGNED,
    title VARCHAR(255) NOT NULL,
    url TEXT,
    post TEXT,
    CONSTRAINT UC_title UNIQUE (title)
);
CREATE INDEX idx_creator
ON posts (creator);

CREATE TABLE admins (
    admin_id BIGINT UNSIGNED NOT NULL PRIMARY KEY,
    password CHAR(60) NOT NULL
);
