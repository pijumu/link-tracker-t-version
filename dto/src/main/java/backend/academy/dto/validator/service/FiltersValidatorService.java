package backend.academy.dto.validator.service;

import static backend.academy.dto.validator.Constants.COMMAND_PREFIX;
import static backend.academy.dto.validator.Constants.FILTERS_SIZE_CONSTRAINT;
import static backend.academy.dto.validator.Constants.FILTER_LENGTH_CONSTRAINT;

import backend.academy.dto.validator.annotation.ValidFilters;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.util.List;
import org.springframework.stereotype.Component;

@Component
public class FiltersValidatorService implements ConstraintValidator<ValidFilters, List<String>> {

    @Override
    public boolean isValid(List<String> filters, ConstraintValidatorContext context) {
        if (filters == null || filters.isEmpty()) {
            return true;
        }
        return checkSize(filters) && filters.stream().allMatch(this::checkFilter);
    }

    public boolean checkSize(List<String> filters) {
        return filters.size() <= FILTERS_SIZE_CONSTRAINT;
    }

    public boolean checkFilter(String filter) {
        return filter != null
                && !filter.isEmpty()
                && !filter.startsWith(COMMAND_PREFIX)
                && filter.length() <= FILTER_LENGTH_CONSTRAINT;
    }
}
