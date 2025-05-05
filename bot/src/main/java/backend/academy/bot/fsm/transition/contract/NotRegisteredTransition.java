package backend.academy.bot.fsm.transition.contract;

import backend.academy.bot.exception.ScrapperException;

public interface NotRegisteredTransition extends Command {
    String formMessageFromNotRegisteredState(Long chatId) throws ScrapperException;
}
