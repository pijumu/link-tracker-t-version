package backend.academy.bot.fsm.transition.command;

import backend.academy.bot.exception.ScrapperException;
import backend.academy.bot.fsm.MessageConstants;
import backend.academy.bot.fsm.transition.contract.IdleTransition;
import backend.academy.bot.service.ScrapperClient;
import backend.academy.dto.dto.ListLinksResponse;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;

@Component
@RequiredArgsConstructor
@Slf4j
public class ListCommand implements IdleTransition {
    private static final String NAME = "/list";
    private static final String DESCRIPTION = "список всех ссылок";

    private final ScrapperClient scrapperClient;

    @Override
    public String getName() {
        return NAME;
    }

    @Override
    public String getDescription() {
        return DESCRIPTION;
    }

    @Override
    public String formMessageFromIdleState(Long chatId) throws ScrapperException {
        return getResponse(chatId);
    }

    private String getResponse(Long chatId) throws ScrapperException {
        try {
            ListLinksResponse list = scrapperClient.getLinks(chatId);
            if (list == null || list.size() == 0) {
                return MessageConstants.NO_FOLLOWING_LINKS_MESSAGE;
            }

            String linksBlock = list.links().stream()
                    .map(link -> "🆔 ID: %s%n🔗 Ссылка: %s%n🏷 Теги: %s%n🎛 Фильтры: %s"
                            .formatted(link.id(), link.url(), formatList(link.tags()), formatList(link.filters())))
                    .collect(Collectors.joining("\n"));
            return String.format("Список отслеживаемых ссылок:%n%s", linksBlock);
        } catch (HttpClientErrorException e) {
            log.error("Ошибка при вызове getLinks с chatId {}: {}", chatId, e.getMessage());
            throw new ScrapperException(MessageConstants.UNAVAILABLE_SERVICE);
        }
    }

    private String formatList(List<String> list) {
        return list.isEmpty() ? "не указаны" : String.join(", ", list);
    }
}
