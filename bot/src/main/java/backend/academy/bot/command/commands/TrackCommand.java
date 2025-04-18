package backend.academy.bot.command.commands;

import backend.academy.bot.command.Command;
import backend.academy.bot.command.CommandMetadata;
import backend.academy.bot.converter.ChatSessionToAddLinkRequestConverter;
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
        name = "/track",
        description = "начать отслежвание",
        states = {"TRACK_AWAIT_URL", "TRACK_AWAIT_TAGS", "TRACK_AWAIT_FILTERS"})
public class TrackCommand implements Command {
    private final TelegramBot bot;
    private final ScrapperClient scrapperClient;
    private final ChatSessionService chatSessionService;
    private final ChatSessionToAddLinkRequestConverter converter;

    @Override
    public void handle(Long chatId, String state, String message) {
        switch (state) {
            case "NOT_REGISTERED":
                resolveNotRegistered(chatId);
                break;
            case "TRACK_AWAIT_URL":
                trackAwaitUrl(chatId, message);
                break;
            case "TRACK_AWAIT_TAGS":
                trackAwaitTags(chatId, message);
                break;
            case "TRACK_AWAIT_FILTERS":
                trackAwaitFilters(chatId, message);
                break;
            default:
                trackCommand(chatId);
        }
    }

    private void resolveNotRegistered(Long chatId) {
        bot.execute(new SendMessage(chatId, "Сначала зарегистрируейтесь - /start."));
    }

    private void trackAwaitUrl(Long chatId, String message) {
        chatSessionService.setAttribute(chatId, "url", message);
        chatSessionService.setState(chatId, "TRACK_AWAIT_TAGS");
        bot.execute(new SendMessage(chatId, "Введите теги через пробел('-' если не нужны):"));
    }

    private void trackAwaitTags(Long chatId, String message) {
        if (!"-".equals(message)) {
            chatSessionService.setAttribute(chatId, "tags", message);
        }
        chatSessionService.setState(chatId, "TRACK_AWAIT_FILTERS");
        bot.execute(new SendMessage(chatId, "Введите фильтры через пробел('-' если не нужны):"));
    }

    private void trackAwaitFilters(Long chatId, String message) {
        if (!"-".equals(message)) {
            chatSessionService.setAttribute(chatId, "filters", message);
        }

        ChatSession chatSession = chatSessionService.getChatSession(chatId);

        chatSessionService.setState(chatId, "IDLE");

        bot.execute(new SendMessage(chatId, sendRequest(chatId, chatSession)));
        chatSessionService.cleanAttributes(chatId);
    }

    private void trackCommand(Long chatId) {
        chatSessionService.setState(chatId, "TRACK_AWAIT_URL");
        chatSessionService.cleanAttributes(chatId);
        bot.execute(new SendMessage(chatId, "Введите cсылку:"));
    }

    private String sendRequest(Long chatId, ChatSession chatSession) {
        try {
            scrapperClient.addLink(chatId, converter.convert(chatSession));
            return "Ссылка успешна добавлена";
        } catch (HttpClientErrorException e) {
            return Objects.requireNonNull(e.getResponseBodyAs(ApiErrorResponse.class))
                    .description();
        }
    }
}
