package backend.academy.dto.validator.annotation;

import backend.academy.dto.validator.service.UrlValidatorService;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = UrlValidatorService.class)
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidUrl {
    String message() default "Неверный формат ссылки /help.";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
