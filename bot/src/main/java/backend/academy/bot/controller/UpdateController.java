package backend.academy.bot.controller;

import backend.academy.dto.dto.LinkUpdate;
import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.request.SendMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/updates")
public class UpdateController {
    private final TelegramBot bot;

    @PostMapping
    public void receiveUpdates(@RequestBody LinkUpdate update) {
        update.tgChatIds()
                .forEach(chatId -> bot.execute(new SendMessage(chatId, update.description() + " " + update.url())));
    }
}
