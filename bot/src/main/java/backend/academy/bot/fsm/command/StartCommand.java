package backend.academy.bot.fsm.command;

import static backend.academy.bot.fsm.Constants.ALREADY_REGISTERED;
import static backend.academy.bot.fsm.Constants.REGISTERED;
import static backend.academy.bot.fsm.State.IDLE;

import backend.academy.bot.domain.CacheChatContextRepository;
import backend.academy.bot.domain.ChatContext;
import backend.academy.bot.exception.UnknownStateException;
import backend.academy.bot.fsm.command.util.Command;
import backend.academy.bot.scrapper.ScrapperClient;
import backend.academy.dto.dto.ApiErrorResponse;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;

@Component
@Slf4j
@RequiredArgsConstructor
public class StartCommand implements Command {
    private static final String NAME = "/start";
    private static final String DESCRIPTION = "регистрация чата";
    private final ScrapperClient client;
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
            case NOT_REGISTERED -> handleNotRegistered(chatId);
            case IDLE -> handleIdle();
            default -> throw new UnknownStateException("Неожиданное состояние: " + context.state());
        };
    }

    private String handleNotRegistered(Long chatId) {
        try {
            client.registerChat(chatId);
            chatContextRepository.put(chatId, ChatContext.builder(IDLE).build());
            return REGISTERED;
        } catch (HttpClientErrorException e) {
            log.error("Ошибка при вызове registerChat с chatId {}: {}", chatId, e.getMessage());
            return Objects.requireNonNull(e.getResponseBodyAs(ApiErrorResponse.class))
                    .description();
        }
    }

    private String handleIdle() {
        return ALREADY_REGISTERED;
    }
}
