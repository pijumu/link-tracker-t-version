package backend.academy.bot.service;

import backend.academy.dto.dto.LinkUpdate;
import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.request.SendMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UpdateService {
    private final TelegramBot bot;

    public void handleUpdate(LinkUpdate update) {
        update.tgChatIds()
                .forEach(chatId -> bot.execute(new SendMessage(chatId, update.description() + " " + update.url())));
    }
}
