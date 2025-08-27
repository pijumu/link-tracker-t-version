package backend.academy.dto.validator.service;

import static backend.academy.dto.validator.Constants.COMMAND_PREFIX;
import static backend.academy.dto.validator.Constants.TAGS_SIZE_CONSTRAINT;
import static backend.academy.dto.validator.Constants.TAG_LENGTH_CONSTRAINT;

import backend.academy.dto.validator.annotation.ValidTags;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.util.List;
import org.springframework.stereotype.Component;

@Component
public class TagsValidatorService implements ConstraintValidator<ValidTags, List<String>> {
    @Override
    public boolean isValid(List<String> tags, ConstraintValidatorContext context) {
        if (tags == null || tags.isEmpty()) {
            return true;
        }
        return checkSize(tags) && tags.stream().allMatch(this::checkTag);
    }

    public boolean checkSize(List<String> tags) {
        return tags.size() <= TAGS_SIZE_CONSTRAINT;
    }

    public boolean checkTag(String tag) {
        return tag != null
                && !tag.isEmpty()
                && !tag.startsWith(COMMAND_PREFIX)
                && tag.length() <= TAG_LENGTH_CONSTRAINT;
    }
}
