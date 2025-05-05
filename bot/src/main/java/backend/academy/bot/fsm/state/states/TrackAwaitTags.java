package backend.academy.bot.fsm.state.states;

import backend.academy.bot.converter.ChatContextToAddLinkRequestConverter;
import backend.academy.bot.domain.CacheChatContextRepository;
import backend.academy.bot.domain.ChatContext;
import backend.academy.bot.fsm.MessageConstants;
import backend.academy.bot.fsm.state.State;
import backend.academy.bot.fsm.state.StateEntry;
import backend.academy.bot.service.ScrapperClient;
import backend.academy.dto.dto.ApiErrorResponse;
import jakarta.validation.constraints.NotBlank;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.client.HttpClientErrorException;

@Component
@Validated
@Slf4j
@RequiredArgsConstructor
public class TrackAwaitTags implements State {
    private final CacheChatContextRepository chatContextRepository;
    private final ChatContextToAddLinkRequestConverter converter;
    private final ScrapperClient scrapperClient;

    @Override
    public String handle(@NotBlank String input, Long chatId, ChatContext context) {
        if (MessageConstants.SKIP_MESSAGE.equals(input)) {
            chatContextRepository.put(chatId, ChatContext.fromOldChatContextAndState(context, StateEntry.IDLE));
        } else {
            chatContextRepository.put(
                    chatId,
                    ChatContext.fromOldChatContext(
                            context, StateEntry.TRACK_AWAIT_TAGS, MessageConstants.FILTERS, input));
        }
        String message = makeResponse(chatId);
        chatContextRepository.put(chatId, ChatContext.fromState(StateEntry.IDLE));
        return message;
    }

    private String makeResponse(Long chatId) {
        ChatContext context = chatContextRepository.get(chatId);
        try {
            scrapperClient.addLink(chatId, converter.convert(context));
            return MessageConstants.SUCCESSFULLY_ADDED;
        } catch (HttpClientErrorException e) {
            log.warn("Не OK статус ответа.", e);
            return Objects.requireNonNull(e.getResponseBodyAs(ApiErrorResponse.class))
                    .description();
        }
    }
}
