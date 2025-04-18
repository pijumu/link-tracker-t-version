package backend.academy.scrapper.parser;

import backend.academy.dto.validator.UrlType;
import java.util.Map;

public record ParsedUrl(UrlType urlType, Map<String, String> params) {}
