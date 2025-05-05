package backend.academy.bot.service;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.SendMessage;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class HandlerService {
    private final TelegramBot bot;
    private final FsmService fsmService;

    public void handle(@NotNull Update update) {
        String messageText = update.message().text();
        Long chatId = update.message().chat().id();

        bot.execute(new SendMessage(chatId, fsmService.handle(messageText, chatId)));
    }
}
