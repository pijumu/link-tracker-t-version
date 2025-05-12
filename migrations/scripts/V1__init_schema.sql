--liquibase formatted sql
--changeset pijumu:001
CREATE EXTENSION IF NOT EXISTS HSTORE;

CREATE TABLE url (
    id BIGSERIAL PRIMARY KEY,
    url_type VARCHAR(16) CHECK (url_type IN ('GITHUB', 'STACKOVERFLOW')),
    url VARCHAR(140) NOT NULL UNIQUE,
    last_time_updated TIMESTAMP WITH TIME ZONE,
    meta HSTORE --- параметры ссылки, которые вычисляются при парсинге
);

CREATE TABLE chat (
    id BIGINT PRIMARY KEY
);

CREATE TABLE link (
    id BIGSERIAL PRIMARY KEY,
    chat_id BIGINT NOT NULL,
    url_id BIGINT NOT NULL,
    filters TEXT[],
    tags TEXT[],
    FOREIGN KEY (chat_id) REFERENCES chat(id),
    FOREIGN KEY (url_id) REFERENCES url(id),
    UNIQUE (chat_id, url_id)
);
