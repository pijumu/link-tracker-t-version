package backend.academy.bot.command.commands;

import backend.academy.bot.command.Command;
import backend.academy.bot.command.CommandMetadata;
import backend.academy.bot.converter.ChatSessionToRemoveLinkRequestConverter;
import backend.academy.bot.model.ChatSession;
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
@CommandMetadata(
        name = "/untrack",
        description = "остановить отслеживание.",
        states = {"UNTRACK_AWAIT_URL"})
public class UntrackCommand implements Command {
    private final TelegramBot bot;
    private final ScrapperClient scrapperClient;
    private final ChatSessionService chatSessionService;
    private final ChatSessionToRemoveLinkRequestConverter converter;

    @Override
    public void handle(Long chatId, String state, String message) {
        switch (state) {
            case "NOT_REGISTERED":
                resolveNotRegistered(chatId);
                break;
            case "UNTRACK_AWAIT_URL":
                untrackAwaitUrl(chatId, message);
                break;
            default:
                untrackCommand(chatId);
        }
    }

    private void resolveNotRegistered(Long chatId) {
        bot.execute(new SendMessage(
                chatId, "Вы не можете использовать команду, будучи не зарегистрированным пользователем."));
    }

    private void untrackAwaitUrl(Long chatId, String message) {
        chatSessionService.setAttribute(chatId, "url", message);
        ChatSession chatSession = chatSessionService.getChatSession(chatId);
        chatSessionService.setState(chatId, "IDLE");

        bot.execute(new SendMessage(chatId, sendRequest(chatId, chatSession)));
        chatSessionService.cleanAttributes(chatId);
    }

    private void untrackCommand(Long chatId) {
        chatSessionService.setState(chatId, "UNTRACK_AWAIT_URL");
        chatSessionService.cleanAttributes(chatId);
        bot.execute(new SendMessage(chatId, "Введите cсылку:"));
    }

    private String sendRequest(Long chatId, ChatSession chatSession) {
        try {
            scrapperClient.removeLink(chatId, converter.convert(chatSession));
            return "Ссылка успешна удалены";
        } catch (HttpClientErrorException e) {
            return Objects.requireNonNull(e.getResponseBodyAs(ApiErrorResponse.class))
                    .description();
        }
    }
}
