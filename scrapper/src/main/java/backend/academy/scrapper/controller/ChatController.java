package backend.academy.scrapper.controller;

import backend.academy.scrapper.service.ChatService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
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

    @ResponseStatus(HttpStatus.OK)
    @PostMapping(value = "/{id}")
    public void registerChat(@PathVariable("id") Long id) {
        chatService.registerChat(id);
    }

    @ResponseStatus(HttpStatus.OK)
    @GetMapping(value = "/{id}", produces = "application/json")
    public boolean isRegisteredChat(@PathVariable("id") Long id) {
        return chatService.isRegisteredChat(id);
    }

    @ResponseStatus(HttpStatus.OK)
    @DeleteMapping(value = "/{id}")
    public void removeChat(@PathVariable("id") Long id) {
        chatService.removeChat(id);
    }
}
