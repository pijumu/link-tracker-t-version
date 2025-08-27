package backend.academy.scrapper.domain.dto;

import java.time.Instant;
import java.util.List;

public record UpdateWithMessageDto(
        Long urlId, String url, Instant createdAt, String description, List<Long> tgChatIds) {}
