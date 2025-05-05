package backend.academy.bot.fsm.transition.contract;

import backend.academy.bot.fsm.state.StateEntry;

public interface NotRegisteredChangingState {
    StateEntry nextStateFromNotRegistered();
}
