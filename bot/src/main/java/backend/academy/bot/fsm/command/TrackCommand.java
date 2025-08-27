package backend.academy.bot.fsm.command;

import static backend.academy.bot.fsm.Constants.COMMAND;
import static backend.academy.bot.fsm.Constants.ENTER_FILTERS;
import static backend.academy.bot.fsm.Constants.ENTER_TAGS;
import static backend.academy.bot.fsm.Constants.ENTER_URL;
import static backend.academy.bot.fsm.Constants.FILTERS;
import static backend.academy.bot.fsm.Constants.SUCCESSFULLY_ADDED;
import static backend.academy.bot.fsm.Constants.TAGS;
import static backend.academy.bot.fsm.Constants.URL;
import static backend.academy.bot.fsm.State.AWAIT_FILTERS;
import static backend.academy.bot.fsm.State.AWAIT_TAGS;
import static backend.academy.bot.fsm.State.AWAIT_URL;
import static backend.academy.bot.fsm.State.IDLE;

import backend.academy.bot.converter.ChatContextToAddLinkRequestConverter;
import backend.academy.bot.domain.CacheChatContextRepository;
import backend.academy.bot.domain.ChatContext;
import backend.academy.bot.exception.ConstraintViolationException;
import backend.academy.bot.exception.UnknownStateException;
import backend.academy.bot.fsm.Constants;
import backend.academy.bot.fsm.command.util.Command;
import backend.academy.bot.service.FieldValidatorService;
import backend.academy.bot.service.data.LinkScrapperService;
import backend.academy.dto.dto.ApiErrorResponse;
import java.util.List;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;

@Component
@Slf4j
@RequiredArgsConstructor
public class TrackCommand implements Command {
    private static final String NAME = "/track";
    private static final String DESCRIPTION = "добавить ссылку для отслеживания";
    private final LinkScrapperService linkScrapperService;
    private final ChatContextToAddLinkRequestConverter converter;
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
            case AWAIT_URL -> handleAwaitUrl(chatId, input, context);
            case AWAIT_TAGS -> handleAwaitTags(chatId, input, context);
            case AWAIT_FILTERS -> handleAwaitFilters(chatId, input, context);
            default -> throw new UnknownStateException("Неожиданное состояние: " + context.state());
        };
    }

    private String handleIdle(Long chatId) {
        chatContextRepository.put(
                chatId, ChatContext.builder(AWAIT_URL).attribute(COMMAND, NAME).build());
        return ENTER_URL;
    }

    private String handleAwaitUrl(Long chatId, String input, ChatContext context) {
        try {
            String url = fieldValidatorService.validateUrl(input);
            chatContextRepository.put(
                    chatId,
                    ChatContext.builder(AWAIT_TAGS)
                            .attributes(context.attributes())
                            .attribute(URL, url)
                            .build());
            return ENTER_TAGS;
        } catch (ConstraintViolationException e) {
            return Constants.repeatInput(e.getMessage());
        }
    }

    private String handleAwaitTags(Long chatId, String input, ChatContext context) {
        try {
            List<String> tags = fieldValidatorService.validateTags(input);
            chatContextRepository.put(
                    chatId,
                    ChatContext.builder(AWAIT_FILTERS)
                            .attributes(context.attributes())
                            .attribute(TAGS, tags)
                            .build());
            return ENTER_FILTERS;
        } catch (ConstraintViolationException e) {
            return Constants.repeatInput(e.getMessage());
        }
    }

    private String handleAwaitFilters(Long chatId, String input, ChatContext context) {
        try {
            List<String> filters = fieldValidatorService.validateFilters(input);
            ChatContext updated = ChatContext.builder(IDLE)
                    .attributes(context.attributes())
                    .attribute(FILTERS, filters)
                    .build();
            linkScrapperService.addLink(chatId, converter.convert(updated));
            chatContextRepository.put(chatId, ChatContext.builder(IDLE).build());
            return SUCCESSFULLY_ADDED;
        } catch (ConstraintViolationException e) {
            return Constants.repeatInput(e.getMessage());
        } catch (HttpClientErrorException e) {
            log.warn("Ошибка при поиске url от chat {}", chatId);
            chatContextRepository.put(chatId, ChatContext.builder(IDLE).build());
            return Objects.requireNonNull(e.getResponseBodyAs(ApiErrorResponse.class))
                    .description();
        }
    }
}
