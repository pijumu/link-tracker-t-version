package backend.academy.scrapper.service;

import backend.academy.dto.validator.service.UrlValidatorService;
import backend.academy.dto.validator.util.UrlType;
import backend.academy.scrapper.domain.dto.ParsedUrlDto;
import backend.academy.scrapper.parser.UrlParser;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UrlParserService {
    private final UrlValidatorService urlValidatorService;
    private final Map<UrlType, UrlParser> parsers;

    @Autowired
    public UrlParserService(UrlValidatorService urlValidatorService, List<UrlParser> parsers) {
        this.urlValidatorService = urlValidatorService;
        this.parsers = parsers.stream().collect(Collectors.toMap(UrlParser::getUrlType, parser -> parser));
    }

    public ParsedUrlDto parse(String url) {
        UrlType urlType = urlValidatorService.determineUrlType(url);
        if (urlType == UrlType.INVALID) {
            throw new IllegalArgumentException("Не поддерживаемая ссылка: " + url);
        }
        UrlParser parser = parsers.get(urlType);
        if (parser == null) {
            throw new IllegalArgumentException("Парсер для типа " + urlType.toString() + " не найден.");
        }
        return parser.parse(url);
    }
}
