package backend.academy.scrapper.parser.parsers;

import static backend.academy.dto.validator.validators.StackOverflowUrlValidator.STACKOVERFLOW_PATTERN;

import backend.academy.dto.validator.UrlType;
import backend.academy.scrapper.parser.ParsedUrl;
import backend.academy.scrapper.parser.UrlParser;
import java.util.Map;
import java.util.regex.Matcher;
import org.springframework.stereotype.Component;

@Component
public class StackOverflowUrlParser implements UrlParser {

    @Override
    public ParsedUrl parse(String url) {
        Matcher matcher = STACKOVERFLOW_PATTERN.matcher(url);
        if (!matcher.matches()) {
            throw new IllegalArgumentException("Invalid StackOverflow URL");
        }
        Map<String, String> vars = Map.of("questionId", matcher.group("questionId"));
        return new ParsedUrl(UrlType.STACKOVERFLOW, vars);
    }

    @Override
    public UrlType getUrlType() {
        return UrlType.STACKOVERFLOW;
    }
}
