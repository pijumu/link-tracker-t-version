package backend.academy.dto.validator.util;

public interface UrlValidator {
    String getPattern();

    UrlType isValid(String url);
}
