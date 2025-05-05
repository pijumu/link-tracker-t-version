package backend.academy.bot.fsm.transition.contract;

import backend.academy.bot.exception.ScrapperException;

public interface IdleTransition extends Command {
    String formMessageFromIdleState(Long chatId) throws ScrapperException;
}
