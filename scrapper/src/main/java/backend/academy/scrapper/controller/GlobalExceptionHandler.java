package backend.academy.scrapper.controller;

import backend.academy.dto.dto.ApiErrorResponse;
import java.util.Arrays;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(Exception.class)
    public ApiErrorResponse handleException(Exception e) {
        log.error("Server error occurred", e);
        return new ApiErrorResponse(
                "Серверная ошибка",
                "CHAT_NOT_FOUND",
                e.getClass().getName(),
                e.getMessage(),
                Arrays.stream(e.getStackTrace()).map(Object::toString).collect(Collectors.toList()));
    }
}
