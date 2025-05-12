package backend.academy.scrapper.parser.parsers;

import static backend.academy.dto.validator.util.GithubUrlValidator.GITHUB_PATTERN;

import backend.academy.dto.validator.util.UrlType;
import backend.academy.scrapper.domain.dto.ParsedUrlDto;
import backend.academy.scrapper.parser.UrlParser;
import backend.academy.scrapper.util.Constants;
import java.util.Map;
import java.util.regex.Matcher;
import org.springframework.stereotype.Component;

@Component
public class GithubUrlParser implements UrlParser {

    @Override
    public ParsedUrlDto parse(String url) {
        Matcher matcher = GITHUB_PATTERN.matcher(url);
        if (!matcher.matches()) {
            throw new IllegalArgumentException("Invalid GitHub URL");
        }
        Map<String, String> vars = Map.of(
                Constants.OWNER, matcher.group("owner"),
                Constants.REPO, matcher.group("repo"));
        return new ParsedUrlDto(UrlType.GITHUB, vars);
    }

    @Override
    public UrlType getUrlType() {
        return UrlType.GITHUB;
    }
}
