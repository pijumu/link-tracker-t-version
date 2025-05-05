package backend.academy.bot.domain;

import backend.academy.bot.fsm.state.StateEntry;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import lombok.Getter;

@Getter
public class ChatContext {
    private final StateEntry state;
    private final Map<String, String> attributes;

    /*
        - Почему через статические методы?
        - По рекомендации Блоха.
    */
    private ChatContext(StateEntry state, Map<String, String> attributes) {
        this.state = state;
        this.attributes = Collections.unmodifiableMap(attributes);
    }

    public static ChatContext fromState(StateEntry state) {
        return new ChatContext(state, Collections.emptyMap());
    }

    public static ChatContext fromOldChatContextAndState(ChatContext context, StateEntry state) {
        return new ChatContext(state, context.attributes());
    }

    public static ChatContext fromOldChatContext(ChatContext context, StateEntry state, String key, String value) {
        Map<String, String> attributes = new HashMap<>(context.attributes());
        attributes.put(key, value);
        return new ChatContext(state, attributes);
    }

    public static ChatContext fromStateAndAttribute(StateEntry state, String key, String value) {
        Map<String, String> attributes = new HashMap<>();
        attributes.put(key, value);
        return new ChatContext(state, attributes);
    }

    public static ChatContext fromStateAndAttributes(StateEntry state, Map<String, String> attributes) {
        return new ChatContext(state, attributes);
    }
}
