package backend.academy.scrapper.controller;

import backend.academy.dto.dto.AddLinkRequest;
import backend.academy.dto.dto.ApiErrorResponse;
import backend.academy.dto.dto.LinkResponse;
import backend.academy.dto.dto.ListLinksResponse;
import backend.academy.dto.dto.RemoveLinkRequest;
import backend.academy.scrapper.exception.AlreadyTrackedUrlException;
import backend.academy.scrapper.exception.UrlNotFoundException;
import backend.academy.scrapper.service.LinkService;
import jakarta.validation.Valid;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/links")
public class LinkController {
    private final LinkService linkService;

    @ResponseStatus(HttpStatus.OK)
    @GetMapping(produces = "application/json")
    public ListLinksResponse getLinks(@RequestHeader("Tg-Chat-Id") Long chatId) {
        return linkService.getLinks(chatId);
    }

    @ResponseStatus(HttpStatus.OK)
    @PostMapping(produces = "application/json", consumes = "application/json")
    public LinkResponse addLink(
            @RequestHeader("Tg-Chat-Id") Long chatId, @Valid @RequestBody AddLinkRequest addLinkRequest) {
        return linkService.addLink(chatId, addLinkRequest);
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

    @ResponseStatus(HttpStatus.OK)
    @DeleteMapping(produces = "application/json", consumes = "application/json")
    public LinkResponse removeLink(
            @RequestHeader("Tg-Chat-Id") Long chatId, @Valid @RequestBody RemoveLinkRequest removeLinkRequest) {
        return linkService.removeLink(chatId, removeLinkRequest);
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
}
