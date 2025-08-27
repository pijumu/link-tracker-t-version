package backend.academy.dto.validator.annotation;

import backend.academy.dto.validator.service.TagsValidatorService;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Documented
@Constraint(validatedBy = TagsValidatorService.class)
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidTags {
    String message() default "Некорректные теги";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
