package backend.academy.bot.fsm.state.states;

import backend.academy.bot.domain.CacheChatContextRepository;
import backend.academy.bot.domain.ChatContext;
import backend.academy.bot.fsm.MessageConstants;
import backend.academy.bot.fsm.state.State;
import backend.academy.bot.fsm.state.StateEntry;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

@Component
@Validated
@RequiredArgsConstructor
public class TrackAwaitFilters implements State {
    private final CacheChatContextRepository chatContextRepository;

    @Override
    public String handle(@NotBlank String input, Long chatId, ChatContext context) {
        if (MessageConstants.SKIP_MESSAGE.equals(input)) {
            chatContextRepository.put(
                    chatId, ChatContext.fromOldChatContextAndState(context, StateEntry.TRACK_AWAIT_TAGS));
        } else {
            chatContextRepository.put(
                    chatId,
                    ChatContext.fromOldChatContext(
                            context, StateEntry.TRACK_AWAIT_TAGS, MessageConstants.FILTERS, input));
        }
        return MessageConstants.ENTER_TAGS;
    }
}
