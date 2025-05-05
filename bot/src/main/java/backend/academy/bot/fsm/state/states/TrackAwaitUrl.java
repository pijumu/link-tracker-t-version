package backend.academy.bot.fsm.state.states;

import backend.academy.bot.domain.CacheChatContextRepository;
import backend.academy.bot.domain.ChatContext;
import backend.academy.bot.fsm.MessageConstants;
import backend.academy.bot.fsm.state.State;
import backend.academy.bot.fsm.state.StateEntry;
import backend.academy.dto.validator.UrlValidatorService;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

@Component
@Slf4j
@Validated
@RequiredArgsConstructor
public class TrackAwaitUrl implements State {
    private final UrlValidatorService urlValidatorService;
    private final CacheChatContextRepository chatContextRepository;

    @Override
    public String handle(@NotBlank String input, Long chatId, ChatContext context) {
        if (!urlValidatorService.isValid(input)) {
            log.warn("Invalid URL: {}", input);
            chatContextRepository.put(chatId, ChatContext.fromState(StateEntry.IDLE));
            return MessageConstants.NOT_VALID_URL;
        }
        chatContextRepository.put(
                chatId, ChatContext.fromStateAndAttribute(StateEntry.TRACK_AWAIT_FILTERS, MessageConstants.URL, input));

        return MessageConstants.ENTER_FILTERS;
    }
}
