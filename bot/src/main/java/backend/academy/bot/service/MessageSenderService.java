package backend.academy.bot.service;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.request.SendMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MessageSenderService {
    private final TelegramBot bot;

    public void sendMessage(String message, Long chatId) {
        bot.execute(new SendMessage(chatId, message));
    }
}
