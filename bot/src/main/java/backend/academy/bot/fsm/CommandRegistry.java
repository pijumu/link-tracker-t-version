package backend.academy.bot.fsm;

import backend.academy.bot.fsm.command.CancelCommand;
import backend.academy.bot.fsm.command.util.Command;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.function.Predicate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CommandRegistry {
    private final ConcurrentMap<String, Command> commands;

    @Autowired
    public CommandRegistry(List<Command> commands) {
        this.commands = new ConcurrentHashMap<>();
        commands.stream()
                .filter(Predicate.not(CancelCommand.class::isInstance))
                .forEach(command -> this.commands.put(command.getName(), command));
    }

    public Command getCommand(String commandName) {
        return commands.get(commandName);
    }
}
