package backend.academy.scrapper.domain.dto;

import backend.academy.dto.validator.util.UrlType;
import java.util.Map;

public record ParsedUrlDto(UrlType urlType, Map<String, String> params) {}
