package backend.academy.scrapper.domain.dto;

import backend.academy.dto.validator.util.UrlType;
import java.time.Instant;
import java.util.Map;

public record UrlAddDto(String url, UrlType type, Instant lastUpdateTime, Map<String, String> meta) {}
