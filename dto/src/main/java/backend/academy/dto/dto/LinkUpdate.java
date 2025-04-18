package backend.academy.dto.dto;

import java.util.List;

public record LinkUpdate(String url, String description, List<Long> tgChatIds) {}
