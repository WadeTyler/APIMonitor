DROP TABLE IF EXISTS api_calls;
DROP TABLE IF EXISTS services;
DROP TABLE IF EXISTS users;

CREATE TABLE users
(
    id         VARCHAR(255) PRIMARY KEY NOT NULL,
    first_name VARCHAR(75)              NOT NULL,
    last_name  VARCHAR(75)              NOT NULL,
    email      VARCHAR(100)             NOT NULL,
    password   VARCHAR(255)             NOT NULL
);


CREATE TABLE services
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
    service_id      VARCHAR(255) NOT NULL,
    path            VARCHAR(255) NOT NULL,
    method          VARCHAR(10)  NOT NULL,
    response_status INT,
    remote_address  VARCHAR(100),
    timestamp       TIMESTAMP    NOT NULL,
    FOREIGN KEY (service_id) REFERENCES services (id) ON DELETE CASCADE
);
