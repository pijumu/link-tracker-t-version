package backend.academy.scrapper.parser;

import backend.academy.dto.validator.util.UrlType;
import backend.academy.scrapper.domain.dto.ParsedUrlDto;

public interface UrlParser {
    ParsedUrlDto parse(String url);

    UrlType getUrlType();
}
