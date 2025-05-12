package backend.academy.bot.fsm.command;

import static backend.academy.bot.fsm.Constants.COMMAND;
import static backend.academy.bot.fsm.Constants.ENTER_TAGS;
import static backend.academy.bot.fsm.Constants.NO_FOLLOWING_LINKS_MESSAGE_WITH_TAGS;
import static backend.academy.bot.fsm.State.AWAIT_TAGS;
import static backend.academy.bot.fsm.State.IDLE;

import backend.academy.bot.domain.CacheChatContextRepository;
import backend.academy.bot.domain.ChatContext;
import backend.academy.bot.exception.ConstraintViolationException;
import backend.academy.bot.exception.UnknownStateException;
import backend.academy.bot.fsm.Constants;
import backend.academy.bot.fsm.command.util.Command;
import backend.academy.bot.scrapper.ScrapperClient;
import backend.academy.bot.service.FieldValidatorService;
import backend.academy.dto.dto.ApiErrorResponse;
import backend.academy.dto.dto.ListLinksResponse;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;

@Component
@Slf4j
@RequiredArgsConstructor
public class ByTagsCommand implements Command {
    private static final String NAME = "/by_tags";
    private static final String DESCRIPTION = "получить все ссылки по тегам";
    private final ScrapperClient client;
    private final CacheChatContextRepository chatContextRepository;
    private final FieldValidatorService fieldValidatorService;

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
            case AWAIT_TAGS -> handleAwaitTags(chatId, input, context);
            default -> throw new UnknownStateException("Неожиданное состояние: " + context.state());
        };
    }

    private String handleIdle(Long chatId) {
        chatContextRepository.put(
                chatId, ChatContext.builder(AWAIT_TAGS).attribute(COMMAND, NAME).build());
        return ENTER_TAGS;
    }

    public String handleAwaitTags(Long chatId, String input, ChatContext context) {
        try {
            List<String> tags = fieldValidatorService.validateTags(input);
            ListLinksResponse list = client.getLinks(chatId, tags);
            String tagsInfo = String.join(", ", tags);
            if (list == null || list.size() == 0) {
                return NO_FOLLOWING_LINKS_MESSAGE_WITH_TAGS.formatted(tagsInfo);
            }
            String linksBlock = list.links().stream()
                    .map(link -> "%n🆔 ID: %s%n🔗 Ссылка: %s%n".formatted(link.id(), link.url()))
                    .collect(Collectors.joining("\n"));
            chatContextRepository.put(chatId, ChatContext.builder(IDLE).build());
            return "Список отслеживаемых ссылок с тегами %s:%s".formatted(tagsInfo, linksBlock);
        } catch (ConstraintViolationException e) {
            return Constants.repeatInput(e.getMessage());
        } catch (HttpClientErrorException e) {
            log.warn("Ошибка при поиске url от chat {}", chatId);
            return Objects.requireNonNull(e.getResponseBodyAs(ApiErrorResponse.class))
                    .description();
        }
    }
}
