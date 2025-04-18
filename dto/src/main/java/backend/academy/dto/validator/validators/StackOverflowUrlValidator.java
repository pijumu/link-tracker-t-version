package backend.academy.dto.validator.validators;

import backend.academy.dto.validator.SupportedUrl;
import backend.academy.dto.validator.UrlType;
import backend.academy.dto.validator.UrlValidator;
import java.util.regex.Pattern;
import org.springframework.stereotype.Component;

@Component
@SupportedUrl(helpPattern = "https://stackoverflow.com/questions/{questionId}")
public class StackOverflowUrlValidator implements UrlValidator {
    public static final Pattern STACKOVERFLOW_PATTERN =
            Pattern.compile("https://stackoverflow\\.com/questions/(?<questionId>\\d+)");

    @Override
    public UrlType isValid(String url) {
        if (STACKOVERFLOW_PATTERN.matcher(url).matches()) {
            return UrlType.STACKOVERFLOW;
        }
        return UrlType.INVALID;
    }
}
