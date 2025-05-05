package backend.academy.bot.fsm.state;

import backend.academy.bot.domain.ChatContext;
import jakarta.validation.constraints.NotBlank;

public interface State {
    String handle(@NotBlank String input, Long chatId, ChatContext chat);
}
