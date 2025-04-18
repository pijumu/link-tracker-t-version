package backend.academy.scrapper.parser.parsers;

import static backend.academy.dto.validator.validators.GithubUrlValidator.GITHUB_PATTERN;

import backend.academy.dto.validator.UrlType;
import backend.academy.scrapper.parser.ParsedUrl;
import backend.academy.scrapper.parser.UrlParser;
import java.util.Map;
import java.util.regex.Matcher;
import org.springframework.stereotype.Component;

@Component
public class GithubUrlParser implements UrlParser {

    @Override
    public ParsedUrl parse(String url) {
        Matcher matcher = GITHUB_PATTERN.matcher(url);
        if (!matcher.matches()) {
            throw new IllegalArgumentException("Invalid GitHub URL");
        }
        Map<String, String> vars = Map.of(
                "owner", matcher.group("owner"),
                "repo", matcher.group("repo"));
        return new ParsedUrl(UrlType.GITHUB, vars);
    }

    @Override
    public UrlType getUrlType() {
        return UrlType.GITHUB;
    }
}
