package backend.academy.dto.validator;

public interface UrlValidator {
    String getPattern();

    UrlType isValid(String url);
}
