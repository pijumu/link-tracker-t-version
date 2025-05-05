package backend.academy.bot.fsm.transition.command;

import backend.academy.bot.exception.ScrapperException;
import backend.academy.bot.fsm.MessageConstants;
import backend.academy.bot.fsm.state.StateEntry;
import backend.academy.bot.fsm.transition.contract.IdleTransition;
import backend.academy.bot.fsm.transition.contract.NotRegisteredChangingState;
import backend.academy.bot.fsm.transition.contract.NotRegisteredTransition;
import backend.academy.bot.service.ScrapperClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;

@Component
@RequiredArgsConstructor
@Slf4j
public class StartCommand implements IdleTransition, NotRegisteredTransition, NotRegisteredChangingState {
    private static final String NAME = "/start";
    private static final String DESCRIPTION = "регистрация чата";

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
    public String formMessageFromIdleState(Long chatId) {
        return MessageConstants.ALREADY_REGISTERED;
    }

    @Override
    public String formMessageFromNotRegisteredState(Long chatId) throws ScrapperException {
        return getResponse(chatId);
    }

    @Override
    public StateEntry nextStateFromNotRegistered() {
        return StateEntry.IDLE;
    }

    private String getResponse(Long chatId) throws ScrapperException {
        try {
            scrapperClient.registerChat(chatId);
            return MessageConstants.REGISTERED;
        } catch (HttpClientErrorException e) {
            log.error("Ошибка при вызове registerChat с chatId {}: {}", chatId, e.getMessage());
            throw new ScrapperException(MessageConstants.UNAVAILABLE_SERVICE);
        }
    }
}
