CREATE TABLE users (
    id           UUID        NOT NULL PRIMARY KEY,
    email        VARCHAR     NOT NULL UNIQUE,
    display_name VARCHAR     NOT NULL,
    created_at   TIMESTAMPTZ NOT NULL
);

CREATE TABLE credentials (
    id            UUID    NOT NULL PRIMARY KEY,
    email         VARCHAR NOT NULL UNIQUE,
    password_hash VARCHAR NOT NULL,
    user_id       UUID    NOT NULL,
    created_at    TIMESTAMPTZ NOT NULL
);

CREATE TABLE refresh_tokens (
    id         UUID        NOT NULL PRIMARY KEY,
    token      VARCHAR     NOT NULL UNIQUE,
    user_id    UUID        NOT NULL,
    email      VARCHAR     NOT NULL,
    expires_at TIMESTAMPTZ NOT NULL,
    created_at TIMESTAMPTZ NOT NULL,
    revoked    BOOLEAN     NOT NULL DEFAULT FALSE
);

CREATE TABLE measurements (
    id          UUID        NOT NULL PRIMARY KEY,
    user_id     UUID        NOT NULL,
    type        VARCHAR     NOT NULL,
    value       FLOAT8      NOT NULL,
    unit        VARCHAR(20) NOT NULL,
    recorded_at TIMESTAMPTZ NOT NULL
);
