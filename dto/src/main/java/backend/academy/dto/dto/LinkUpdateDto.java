package backend.academy.dto.dto;

import java.util.List;

public record LinkUpdateDto(Long urlId, String url, String description, List<Long> tgChatIds) {}
