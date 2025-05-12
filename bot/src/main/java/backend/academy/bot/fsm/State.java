package backend.academy.bot.fsm;

import lombok.Getter;

@Getter
public enum State {
    NOT_REGISTERED,
    IDLE,
    AWAIT_URL,
    AWAIT_TAGS,
    AWAIT_FILTERS
}
