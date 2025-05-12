package backend.academy.bot.domain;

import backend.academy.bot.fsm.State;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import lombok.Getter;

@Getter
public class ChatContext {
    private final State state;
    private final Map<String, Object> attributes;

    private ChatContext(State state, Map<String, Object> attributes) {
        this.state = state;
        this.attributes = Collections.unmodifiableMap(attributes);
    }

    public static Builder builder(State state) {
        return new Builder(state);
    }

    public static class Builder {
        private final State state;
        private final Map<String, Object> attributes = new HashMap<>();

        public Builder(State state) {
            this.state = state;
        }

        public Builder attribute(String key, Object value) {
            attributes.put(key, value);
            return this;
        }

        public Builder attributes(Map<String, Object> attributes) {
            this.attributes.putAll(attributes);
            return this;
        }

        public ChatContext build() {
            return new ChatContext(state, attributes);
        }
    }
}
