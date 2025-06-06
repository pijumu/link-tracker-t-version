package backend.academy.bot.fsm.command;

import static backend.academy.bot.fsm.Constants.COMMAND;
import static backend.academy.bot.fsm.Constants.ENTER_URL;
import static backend.academy.bot.fsm.Constants.SUCCESSFULLY_REMOVED;
import static backend.academy.bot.fsm.State.AWAIT_URL;
import static backend.academy.bot.fsm.State.IDLE;

import backend.academy.bot.domain.CacheChatContextRepository;
import backend.academy.bot.domain.ChatContext;
import backend.academy.bot.exception.UnknownStateException;
import backend.academy.bot.fsm.command.util.Command;
import backend.academy.bot.service.data.LinkScrapperService;
import backend.academy.dto.dto.ApiErrorResponse;
import backend.academy.dto.dto.RemoveLinkRequest;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;

@Component
@Slf4j
@RequiredArgsConstructor
public class UntrackCommand implements Command {
    private static final String NAME = "/untrack";
    private static final String DESCRIPTION = "остановить отслеживание ссылки";
    private final LinkScrapperService linkScrapperService;
    private final CacheChatContextRepository chatContextRepository;

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
            case AWAIT_URL -> handleAwaitUrl(chatId, input);
            default -> throw new UnknownStateException("Неожиданное состояние: " + context.state());
        };
    }

    private String handleIdle(Long chatId) {
        chatContextRepository.put(
                chatId, ChatContext.builder(AWAIT_URL).attribute(COMMAND, NAME).build());
        return ENTER_URL;
    }

    private String handleAwaitUrl(Long chatId, String input) {
        chatContextRepository.put(chatId, ChatContext.builder(IDLE).build());
        try {
            linkScrapperService.removeLink(chatId, new RemoveLinkRequest(input));
            return SUCCESSFULLY_REMOVED;
        } catch (HttpClientErrorException e) {
            log.warn("Ошибка при поиске url от chat {}", chatId);
            return Objects.requireNonNull(e.getResponseBodyAs(ApiErrorResponse.class))
                    .description();
        }
    }
}
