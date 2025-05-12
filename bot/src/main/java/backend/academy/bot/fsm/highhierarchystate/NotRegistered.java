package backend.academy.bot.fsm.highhierarchystate;

import backend.academy.bot.domain.ChatContext;
import backend.academy.bot.fsm.command.HelpCommand;
import backend.academy.bot.fsm.command.StartCommand;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class NotRegistered {
    private final StartCommand startCommand;
    private final HelpCommand helpCommand;

    public String handle(Long chatId, String input, ChatContext context) {
        if (startCommand.getName().equals(input)) {
            return startCommand.handle(chatId, input, context);
        }
        return helpCommand.handle(chatId, input, context);
    }
}
