package backend.academy.scrapper.controller;

import backend.academy.dto.dto.ApiErrorResponse;
import backend.academy.scrapper.exception.ChatAlreadyRegisteredException;
import backend.academy.scrapper.exception.ChatNotFoundException;
import backend.academy.scrapper.service.ChatService;
import java.util.Arrays;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/tg-chat")
public class ChatController {

    private final ChatService chatService;

    @PostMapping(value = "/{id}")
    @ResponseStatus(HttpStatus.OK)
    public void registerChat(@PathVariable("id") Long id) {
        chatService.registerChat(id);
    }

    @ExceptionHandler(ChatAlreadyRegisteredException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ApiErrorResponse handleChatAlreadyRegisteredException(ChatAlreadyRegisteredException e) {
        return new ApiErrorResponse(
                "Чат уже зарегистрирован",
                "CHAT_ALREADY_REGISTERED",
                e.getClass().getName(),
                e.getMessage(),
                Arrays.stream(e.getStackTrace()).map(Object::toString).collect(Collectors.toList()));
    }

    @ResponseStatus(HttpStatus.OK)
    @DeleteMapping(value = "/{id}")
    public void removeChat(@PathVariable("id") Long id) {
        chatService.removeChat(id);
    }

    @ExceptionHandler(ChatNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ApiErrorResponse handleChatNotFoundException(ChatNotFoundException e) {
        return new ApiErrorResponse(
                "Чат не существует",
                "CHAT_NOT_FOUND",
                e.getClass().getName(),
                e.getMessage(),
                Arrays.stream(e.getStackTrace()).map(Object::toString).collect(Collectors.toList()));
    }
}
