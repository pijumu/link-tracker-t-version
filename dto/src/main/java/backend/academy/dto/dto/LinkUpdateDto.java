package backend.academy.dto.dto;

import java.util.List;

public record LinkUpdateDto(String url, String description, List<Long> tgChatIds) {}
