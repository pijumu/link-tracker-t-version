package backend.academy.bot.fsm.highhierarchystate;

import static backend.academy.bot.fsm.Constants.UNKNOWN_COMMAND;

import backend.academy.bot.domain.ChatContext;
import backend.academy.bot.fsm.CommandRegistry;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class Idle {
    private final CommandRegistry commands;

    public String handle(Long chatId, String input, ChatContext context) {
        var command = commands.getCommand(input);
        if (command == null) {
            return UNKNOWN_COMMAND;
        }
        return command.handle(chatId, input, context);
    }
}
