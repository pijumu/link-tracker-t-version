package backend.academy.bot.command.commands;

import backend.academy.bot.command.Command;
import backend.academy.bot.command.CommandMetadata;
import backend.academy.bot.service.ChatSessionService;
import backend.academy.bot.service.ScrapperClient;
import backend.academy.dto.dto.ApiErrorResponse;
import backend.academy.dto.dto.ListLinksResponse;
import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.request.SendMessage;
import java.util.List;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;

@Component
@RequiredArgsConstructor
@CommandMetadata(name = "/list", description = "список отслеживаемых ссылок")
public class ListCommand implements Command {
    private final TelegramBot bot;
    private final ScrapperClient scrapperClient;
    private final ChatSessionService chatSessionService;

    @Override
    public void handle(Long chatId, String state, String message) {
        if ("NOT_REGISTERED".equals(state)) {
            bot.execute(new SendMessage(chatId, "Сначала зарегистрируейтесь - /start. "));
            return;
        }
        chatSessionService.setState(chatId, "IDLE");
        chatSessionService.cleanAttributes(chatId);
        bot.execute(new SendMessage(chatId, sendRequest(chatId)));
    }

    private String sendRequest(Long chatId) {
        try {
            ResponseEntity<ListLinksResponse> response = scrapperClient.getLinks(chatId);
            ListLinksResponse listLinksResponse = response.getBody();

            if (listLinksResponse == null || listLinksResponse.size() == 0) {
                return "Вы не отслеживаете ссылок.";
            }
            StringBuilder message = new StringBuilder("Список отслеживаемых ссылок:\n\n");
            listLinksResponse.links().forEach(link -> {
                message.append("🆔 ID: ")
                        .append(link.id())
                        .append("\n")
                        .append("🔗 Ссылка: ")
                        .append(link.url())
                        .append("\n")
                        .append("🏷 Теги: ")
                        .append(formatList(link.tags()))
                        .append("\n")
                        .append("🎛 Фильтры: ")
                        .append(formatList(link.filters()))
                        .append("\n\n");
            });

            return message.toString();
        } catch (HttpClientErrorException e) {
            return Objects.requireNonNull(e.getResponseBodyAs(ApiErrorResponse.class))
                    .description();
        }
    }

    private String formatList(List<String> list) {
        return list.isEmpty() ? "не указаны" : String.join(", ", list);
    }
}
