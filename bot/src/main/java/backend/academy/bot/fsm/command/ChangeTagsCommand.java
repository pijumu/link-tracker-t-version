package backend.academy.bot.fsm.command;

import static backend.academy.bot.fsm.Constants.COMMAND;
import static backend.academy.bot.fsm.Constants.ENTER_TAGS;
import static backend.academy.bot.fsm.Constants.ENTER_URL;
import static backend.academy.bot.fsm.Constants.LINK_ID;
import static backend.academy.bot.fsm.Constants.SUCCESSFULLY_TAGS_UPDATED;
import static backend.academy.bot.fsm.Constants.TAGS;
import static backend.academy.bot.fsm.Constants.UNAVAILABLE_SERVICE;
import static backend.academy.bot.fsm.State.AWAIT_TAGS;
import static backend.academy.bot.fsm.State.AWAIT_URL;
import static backend.academy.bot.fsm.State.IDLE;

import backend.academy.bot.converter.ChatContextToUpdateTagsRequestConverter;
import backend.academy.bot.domain.CacheChatContextRepository;
import backend.academy.bot.domain.ChatContext;
import backend.academy.bot.exception.ConstraintViolationException;
import backend.academy.bot.exception.ScrapperException;
import backend.academy.bot.exception.UnknownStateException;
import backend.academy.bot.fsm.Constants;
import backend.academy.bot.fsm.command.util.Command;
import backend.academy.bot.scrapper.ScrapperClient;
import backend.academy.bot.service.FieldValidatorService;
import backend.academy.dto.dto.ListLinksResponse;
import java.util.Collections;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;

@Component
@Slf4j
@RequiredArgsConstructor
public class ChangeTagsCommand implements Command {
    private static final String NAME = "/change_tags";
    private static final String DESCRIPTION = "поменять теги для ссылки";
    private final ScrapperClient client;
    private final CacheChatContextRepository chatContextRepository;
    private final ChatContextToUpdateTagsRequestConverter converter;
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
            ListLinksResponse list = client.getLinks(chatId, Collections.emptyList());
            if (list == null || list.size() == 0) {
                return Constants.repeatUrl(input);
            }
            return list.links().stream()
                    .filter(link -> link.url().equals(input))
                    .findFirst()
                    .map(link -> {
                        chatContextRepository.put(
                                chatId,
                                ChatContext.builder(AWAIT_TAGS)
                                        .attributes(context.attributes())
                                        .attribute(LINK_ID, link.id())
                                        .build());
                        return ENTER_TAGS;
                    })
                    .orElse(Constants.repeatUrl(input));
        } catch (HttpClientErrorException e) {
            log.error("Ошибка при вызове getLinks с chatId {}: {}", chatId, e.getMessage());
            throw new ScrapperException(UNAVAILABLE_SERVICE);
        }
    }

    private String handleAwaitTags(Long chatId, String input, ChatContext context) {
        try {
            List<String> tags = fieldValidatorService.validateTags(input);
            ChatContext updated = ChatContext.builder(IDLE)
                    .attributes(context.attributes())
                    .attribute(TAGS, tags)
                    .build();
            client.updateLinkTags(chatId, (Long) updated.attributes().get(LINK_ID), converter.convert(updated));
            chatContextRepository.put(chatId, ChatContext.builder(IDLE).build());
            return SUCCESSFULLY_TAGS_UPDATED;
        } catch (ConstraintViolationException e) {
            return Constants.repeatInput(e.getMessage());
        }
    }
}
