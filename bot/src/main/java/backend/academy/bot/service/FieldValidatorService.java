package backend.academy.bot.service;

import static backend.academy.bot.fsm.Constants.SKIP;
import static backend.academy.dto.validator.Constants.COMMAND_PREFIX;
import static backend.academy.dto.validator.Constants.FILTERS_SIZE_CONSTRAINT;
import static backend.academy.dto.validator.Constants.FILTER_LENGTH_CONSTRAINT;
import static backend.academy.dto.validator.Constants.TAGS_SIZE_CONSTRAINT;
import static backend.academy.dto.validator.Constants.TAG_LENGTH_CONSTRAINT;

import backend.academy.bot.exception.ConstraintViolationException;
import backend.academy.dto.validator.service.FiltersValidatorService;
import backend.academy.dto.validator.service.TagsValidatorService;
import backend.academy.dto.validator.service.UrlValidatorService;
import java.util.Collections;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class FieldValidatorService {
    private final UrlValidatorService urlValidatorService;
    private final TagsValidatorService tagsValidatorService;
    private final FiltersValidatorService filtersValidatorService;

    public String validateUrl(String url) {
        if (!urlValidatorService.isValid(url)) {
            throw new ConstraintViolationException("Невалидная ссылка! Cмотри /help");
        }
        return url;
    }

    public List<String> validateTags(String tags) {
        if (SKIP.equals(tags)) {
            return Collections.emptyList();
        }
        List<String> split = List.of(tags.split(" "));

        if (!tagsValidatorService.checkSize(split)) {
            throw new ConstraintViolationException("Превышено количество тегов! Было передано %s, максимум возможно %s!"
                    .formatted(split.size(), TAGS_SIZE_CONSTRAINT));
        }

        split.forEach(tag -> {
            if (!tagsValidatorService.checkTag(tag)) {
                throw new ConstraintViolationException(
                        "Тег не может: начинаться на %s, иметь длину больше %s, быть пустым! Невалидный таг: %s!"
                                .formatted(COMMAND_PREFIX, TAG_LENGTH_CONSTRAINT, tag));
            }
        });
        return split;
    }

    public List<String> validateFilters(String filters) {
        if (SKIP.equals(filters)) {
            return Collections.emptyList();
        }
        List<String> split = List.of(filters.split(" "));
        if (!filtersValidatorService.checkSize(split)) {
            throw new ConstraintViolationException(
                    "Превышено количество фильтров! Было передано %s, максимум возможно %s!"
                            .formatted(split.size(), FILTERS_SIZE_CONSTRAINT));
        }

        split.forEach(filter -> {
            if (!filtersValidatorService.checkFilter(filter)) {
                throw new ConstraintViolationException(
                        "Фильтр не может: начинаться на %s, иметь длину больше %s, быть пустым! Невалидный фильтр: %s!"
                                .formatted(COMMAND_PREFIX, FILTER_LENGTH_CONSTRAINT, filter));
            }
        });
        return split;
    }
}
