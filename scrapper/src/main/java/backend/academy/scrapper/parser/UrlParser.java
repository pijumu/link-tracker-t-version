package backend.academy.scrapper.parser;

import backend.academy.dto.validator.UrlType;

public interface UrlParser {
    ParsedUrl parse(String url);

    UrlType getUrlType();
}
