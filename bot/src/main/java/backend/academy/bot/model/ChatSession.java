package backend.academy.bot.model;

import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@AllArgsConstructor
public class ChatSession {
    @Setter
    private String state;

    private Map<String, String> attributes;
}
