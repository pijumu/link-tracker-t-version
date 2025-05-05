package backend.academy.bot.fsm.state.states;

import backend.academy.bot.converter.ChatContextToRemoveLinkRequestConverter;
import backend.academy.bot.domain.CacheChatContextRepository;
import backend.academy.bot.domain.ChatContext;
import backend.academy.bot.fsm.MessageConstants;
import backend.academy.bot.fsm.state.State;
import backend.academy.bot.fsm.state.StateEntry;
import backend.academy.bot.service.ScrapperClient;
import backend.academy.dto.dto.ApiErrorResponse;
import backend.academy.dto.validator.UrlValidatorService;
import jakarta.validation.constraints.NotBlank;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.client.HttpClientErrorException;

@Component
@Slf4j
@Validated
@RequiredArgsConstructor
public class UntrackAwaitUrl implements State {
    private final UrlValidatorService urlValidatorService;
    private final CacheChatContextRepository chatContextRepository;
    private final ChatContextToRemoveLinkRequestConverter converter;
    private final ScrapperClient scrapperClient;

    @Override
    public String handle(@NotBlank String input, Long chatId, ChatContext context) {
        if (!urlValidatorService.isValid(input)) {
            log.warn("Invalid URL: {}", input);
            chatContextRepository.put(chatId, ChatContext.fromState(StateEntry.IDLE));
            return MessageConstants.NOT_VALID_URL;
        }
        chatContextRepository.put(
                chatId, ChatContext.fromStateAndAttribute(StateEntry.IDLE, MessageConstants.URL, input));
        String message = makeResponse(chatId);
        chatContextRepository.put(chatId, ChatContext.fromState(StateEntry.IDLE));
        return message;
    }

    private String makeResponse(Long chatId) {
        ChatContext context = chatContextRepository.get(chatId);
        try {
            scrapperClient.removeLink(chatId, converter.convert(context));
            return MessageConstants.SUCCESSFULLY_REMOVED;
        } catch (HttpClientErrorException e) {
            log.warn("Не OK статус ответа.", e);
            return Objects.requireNonNull(e.getResponseBodyAs(ApiErrorResponse.class))
                    .description();
        }
    }
}
