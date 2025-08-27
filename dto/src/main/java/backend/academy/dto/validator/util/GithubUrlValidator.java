package backend.academy.dto.validator.util;

import java.util.regex.Pattern;
import org.springframework.stereotype.Component;

@Component
public class GithubUrlValidator implements UrlValidator {
    private static final String PATTERN = "https://github.com/{owner}/{repo}";

    // используется в GithubUrlParser.
    public static final Pattern GITHUB_PATTERN = Pattern.compile("https://github\\.com/(?<owner>[^/]+)/(?<repo>[^/]+)");

    @Override
    public String getPattern() {
        return PATTERN;
    }

    @Override
    public UrlType isValid(String url) {
        if (GITHUB_PATTERN.matcher(url).matches()) {
            return UrlType.GITHUB;
        }
        return UrlType.INVALID;
    }
}
