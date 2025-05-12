package backend.academy.bot.controller;

import backend.academy.bot.service.UpdateService;
import backend.academy.dto.dto.LinkUpdateDto;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/updates")
public class UpdateController {
    private final UpdateService updateService;

    @PostMapping
    public void receiveUpdates(@RequestBody LinkUpdateDto update) {
        updateService.handleUpdate(update);
    }
}
