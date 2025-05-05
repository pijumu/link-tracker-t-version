package backend.academy.bot.fsm.transition.command;

import backend.academy.bot.fsm.MessageConstants;
import backend.academy.bot.fsm.state.StateEntry;
import backend.academy.bot.fsm.transition.contract.IdleChangingState;
import backend.academy.bot.fsm.transition.contract.IdleTransition;
import org.springframework.stereotype.Component;

@Component
public class UntrackCommand implements IdleTransition, IdleChangingState {
    private static final String NAME = "/untrack";
    private static final String DESCRIPTION = "остановить отслеживание ссылки";

    @Override
    public String getName() {
        return NAME;
    }

    @Override
    public String getDescription() {
        return DESCRIPTION;
    }

    @Override
    public StateEntry nextStateFromIdle() {
        return StateEntry.UNTRACK_AWAIT_URL;
    }

    @Override
    public String formMessageFromIdleState(Long chatId) {
        return MessageConstants.ENTER_URL;
    }
}
