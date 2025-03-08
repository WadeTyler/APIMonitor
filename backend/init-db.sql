DROP TABLE IF EXISTS api_calls CASCADE;
DROP TABLE IF EXISTS applications CASCADE;
DROP TABLE IF EXISTS users CASCADE;

CREATE TABLE users
(
    id         VARCHAR(255) PRIMARY KEY NOT NULL,
    email      VARCHAR(100)             NOT NULL,
    password   VARCHAR(255)             NOT NULL
);


CREATE TABLE applications
(
    id           VARCHAR(255) PRIMARY KEY NOT NULL,
    public_token VARCHAR(255)             NOT NULL,
    user_id      VARCHAR(255)             NOT NULL,
    name         VARCHAR(50)              NOT NULL,
    FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE
);


CREATE TABLE api_calls
(
    id              BIGINT PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
    app_id      VARCHAR(255) NOT NULL,
    path            VARCHAR(255) NOT NULL,
    method          VARCHAR(10)  NOT NULL,
    response_status INT,
    remote_address  VARCHAR(100),
    timestamp       TIMESTAMP    NOT NULL,
    FOREIGN KEY (app_id) REFERENCES applications (id) ON DELETE CASCADE
);
