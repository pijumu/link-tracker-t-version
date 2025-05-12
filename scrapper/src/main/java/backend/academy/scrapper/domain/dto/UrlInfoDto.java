package backend.academy.scrapper.domain.dto;

import backend.academy.dto.validator.util.UrlType;
import java.time.Instant;
import java.util.List;
import java.util.Map;

public record UrlInfoDto(
        Long id, String url, UrlType type, Instant lastTimeUpdated, Map<String, String> meta, List<Long> chatIds) {}
