package backend.academy.dto.validator;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UrlValidatorService implements ConstraintValidator<ValidUrl, String> {
    private final List<UrlValidator> urlValidators;

    @Override
    public boolean isValid(String url, ConstraintValidatorContext constraintValidatorContext) {
        return determineUrlType(url) != UrlType.INVALID;
    }

    public UrlType determineUrlType(String url) {
        if (url == null) {
            return UrlType.INVALID;
        }
        return urlValidators.stream()
                .map(urlValidator -> urlValidator.isValid(url))
                .filter(urlType -> urlType != UrlType.INVALID)
                .findFirst()
                .orElse(UrlType.INVALID);
    }
}
