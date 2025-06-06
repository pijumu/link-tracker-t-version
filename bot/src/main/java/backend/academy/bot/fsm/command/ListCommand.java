package backend.academy.bot.fsm.command;

import static backend.academy.bot.fsm.Constants.NO_FOLLOWING_LINKS_MESSAGE;
import static backend.academy.bot.fsm.Constants.UNAVAILABLE_SERVICE;

import backend.academy.bot.domain.ChatContext;
import backend.academy.bot.exception.ScrapperException;
import backend.academy.bot.exception.UnknownStateException;
import backend.academy.bot.fsm.command.util.Command;
import backend.academy.bot.service.data.LinkScrapperService;
import backend.academy.dto.dto.ListLinksResponse;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;

@Component
@Slf4j
@RequiredArgsConstructor
public class ListCommand implements Command {
    private static final String NAME = "/list";
    private static final String DESCRIPTION = "список всех ссылок";
    private final LinkScrapperService linkScrapperService;

    @Override
    public String getName() {
        return NAME;
    }

    @Override
    public String getDescription() {
        return DESCRIPTION;
    }

    @Override
    public String handle(Long chatId, String input, ChatContext context) {
        return switch (context.state()) {
            case IDLE -> handleIdle(chatId);
            default -> throw new UnknownStateException("Неожиданное состояние: " + context.state());
        };
    }

    private String handleIdle(Long chatId) {
        try {
            ListLinksResponse list = linkScrapperService.getLinks(chatId, Collections.emptyList());
            if (list == null || list.size() == 0) {
                return NO_FOLLOWING_LINKS_MESSAGE;
            }

            String linksBlock = list.links().stream()
                    .map(link -> "%n🆔 ID: %s%n🔗 Ссылка: %s%n🏷 Теги: %s%n🎛 Фильтры: %s"
                            .formatted(link.id(), link.url(), formatList(link.tags()), formatList(link.filters())))
                    .collect(Collectors.joining("\n"));
            return String.format("Список отслеживаемых ссылок:%s", linksBlock);
        } catch (HttpClientErrorException e) {
            log.error("Ошибка при вызове getLinks с chatId {}: {}", chatId, e.getMessage());
            throw new ScrapperException(UNAVAILABLE_SERVICE);
        }
    }

    private String formatList(List<String> list) {
        return list.isEmpty() ? "не указаны" : String.join(", ", list);
    }
}
