package backend.academy.dto.validator.validators;

import backend.academy.dto.validator.UrlType;
import backend.academy.dto.validator.UrlValidator;
import java.util.regex.Pattern;
import org.springframework.stereotype.Component;

@Component
public class StackOverflowUrlValidator implements UrlValidator {
    private static final String PATTERN = "https://stackoverflow.com/questions/{questionId}";

    // используется в StackOverflowUrlParser
    public static final Pattern STACKOVERFLOW_PATTERN =
            Pattern.compile("https://stackoverflow\\.com/questions/(?<questionId>\\d+)");

    @Override
    public String getPattern() {
        return PATTERN;
    }

    @Override
    public UrlType isValid(String url) {
        if (STACKOVERFLOW_PATTERN.matcher(url).matches()) {
            return UrlType.STACKOVERFLOW;
        }
        return UrlType.INVALID;
    }
}
