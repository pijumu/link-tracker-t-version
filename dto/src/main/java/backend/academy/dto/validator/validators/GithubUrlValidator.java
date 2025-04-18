package backend.academy.dto.validator.validators;

import backend.academy.dto.validator.SupportedUrl;
import backend.academy.dto.validator.UrlType;
import backend.academy.dto.validator.UrlValidator;
import java.util.regex.Pattern;
import org.springframework.stereotype.Component;

@Component
@SupportedUrl(helpPattern = "https://github.com/{owner}/{repo}")
public class GithubUrlValidator implements UrlValidator {
    public static final Pattern GITHUB_PATTERN = Pattern.compile("https://github\\.com/(?<owner>[^/]+)/(?<repo>[^/]+)");

    @Override
    public UrlType isValid(String url) {
        if (GITHUB_PATTERN.matcher(url).matches()) {
            return UrlType.GITHUB;
        }
        return UrlType.INVALID;
    }
}
