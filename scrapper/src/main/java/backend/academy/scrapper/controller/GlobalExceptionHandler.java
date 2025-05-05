package backend.academy.scrapper.controller;

import backend.academy.dto.dto.ApiErrorResponse;
import backend.academy.scrapper.exception.AlreadyTrackedUrlException;
import backend.academy.scrapper.exception.ChatAlreadyRegisteredException;
import backend.academy.scrapper.exception.ChatNotFoundException;
import backend.academy.scrapper.exception.UrlNotFoundException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {
    @ResponseStatus(HttpStatus.CONFLICT)
    @ExceptionHandler(ChatAlreadyRegisteredException.class)
    public ApiErrorResponse handleChatAlreadyRegisteredException(ChatAlreadyRegisteredException e) {
        return new ApiErrorResponse(
                "Чат уже зарегистрирован",
                "CHAT_ALREADY_REGISTERED",
                e.getClass().getName(),
                e.getMessage(),
                Arrays.stream(e.getStackTrace()).map(Object::toString).collect(Collectors.toList()));
    }

    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(ChatNotFoundException.class)
    public ApiErrorResponse handleChatNotFoundException(ChatNotFoundException e) {
        return new ApiErrorResponse(
                "Чат не существует",
                "CHAT_NOT_FOUND",
                e.getClass().getName(),
                e.getMessage(),
                Arrays.stream(e.getStackTrace()).map(Object::toString).collect(Collectors.toList()));
    }

    @ResponseStatus(HttpStatus.CONFLICT)
    @ExceptionHandler(AlreadyTrackedUrlException.class)
    public ApiErrorResponse handlerAlreadyTrackedUrlException(AlreadyTrackedUrlException e) {
        return new ApiErrorResponse(
                "Ссылка уже отслеживается.",
                "URL_ALREADY_TRACKED",
                e.getClass().getName(),
                e.getMessage(),
                Arrays.stream(e.getStackTrace()).map(Object::toString).collect(Collectors.toList()));
    }

    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(UrlNotFoundException.class)
    public ApiErrorResponse handleUrlNotFoundException(UrlNotFoundException e) {
        return new ApiErrorResponse(
                "Ссылка не найдена",
                "URL_NOT_FOUND",
                e.getClass().getName(),
                e.getMessage(),
                Arrays.stream(e.getStackTrace()).map(Object::toString).collect(Collectors.toList()));
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ApiErrorResponse handleValidationExceptions(MethodArgumentNotValidException e) {
        List<String> errors = e.getBindingResult().getFieldErrors().stream()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .collect(Collectors.toList());

        return new ApiErrorResponse(
                "Некорректные параметры запроса:\n" + String.join("; ", errors),
                "VALIDATION_ERROR",
                e.getClass().getName(),
                e.getMessage(),
                Arrays.stream(e.getStackTrace()).map(Object::toString).collect(Collectors.toList()));
    }

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
