--liquibase formatted sql
--changeset pijumu:002
CREATE INDEX idx_link_filters_gin ON link USING GIN (filters);
CREATE INDEX idx_link_tags_gin ON link USING GIN (tags);

CREATE INDEX idx_url_url_hash ON url USING HASH (url);

CREATE INDEX idx_link_chat_id_hash ON link USING HASH (chat_id);
