package backend.academy.bot.command.commands;

import backend.academy.bot.command.Command;
import backend.academy.bot.command.CommandMetadata;
import backend.academy.bot.service.ChatSessionService;
import backend.academy.bot.service.ScrapperClient;
import backend.academy.dto.dto.ApiErrorResponse;
import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.request.SendMessage;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;

@Component
@RequiredArgsConstructor
@CommandMetadata(name = "/start", description = "регистрация")
public class StartCommand implements Command {
    private final TelegramBot bot;
    private final ScrapperClient scrapperClient;
    private final ChatSessionService chatSessionService;

    @Override
    public void handle(Long chatId, String state, String message) {
        try {
            scrapperClient.registerChat(chatId);
            chatSessionService.register(chatId);
            bot.execute(new SendMessage(chatId, "Вы зарегестрированы."));
        } catch (HttpClientErrorException e) {
            bot.execute(new SendMessage(
                    chatId,
                    Objects.requireNonNull(e.getResponseBodyAs(ApiErrorResponse.class))
                            .description()));
        }
    }
}
