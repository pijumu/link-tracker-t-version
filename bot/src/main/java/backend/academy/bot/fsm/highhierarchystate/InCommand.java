package backend.academy.bot.fsm.highhierarchystate;

import static backend.academy.bot.fsm.Constants.COMMAND;

import backend.academy.bot.domain.ChatContext;
import backend.academy.bot.fsm.CommandRegistry;
import backend.academy.bot.fsm.command.CancelCommand;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class InCommand {
    private final CommandRegistry commands;
    private final CancelCommand cancelCommand;

    public String handle(Long chatId, String input, ChatContext context) {
        if (cancelCommand.getName().equals(input)) {
            return cancelCommand.handle(chatId, input, context);
        }
        return commands.getCommand((String) context.attributes().get(COMMAND)).handle(chatId, input, context);
    }
}
