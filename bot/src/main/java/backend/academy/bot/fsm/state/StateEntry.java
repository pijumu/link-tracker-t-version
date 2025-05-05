package backend.academy.bot.fsm.state;

import backend.academy.bot.fsm.state.states.Idle;
import backend.academy.bot.fsm.state.states.NotRegistered;
import backend.academy.bot.fsm.state.states.TrackAwaitFilters;
import backend.academy.bot.fsm.state.states.TrackAwaitTags;
import backend.academy.bot.fsm.state.states.TrackAwaitUrl;
import backend.academy.bot.fsm.state.states.UntrackAwaitUrl;
import lombok.Getter;

@Getter
public enum StateEntry {
    NOT_REGISTERED(NotRegistered.class),
    IDLE(Idle.class),
    TRACK_AWAIT_URL(TrackAwaitUrl.class),
    TRACK_AWAIT_FILTERS(TrackAwaitFilters.class),
    TRACK_AWAIT_TAGS(TrackAwaitTags.class),
    UNTRACK_AWAIT_URL(UntrackAwaitUrl.class);

    private final Class<?> stateHandlerClass;

    StateEntry(Class<?> stateHandlerClass) {
        this.stateHandlerClass = stateHandlerClass;
    }
}
