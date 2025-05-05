package backend.academy.bot.fsm.transition.command;

import backend.academy.bot.fsm.transition.contract.Command;
import backend.academy.bot.fsm.transition.contract.IdleTransition;
import backend.academy.bot.fsm.transition.contract.NotRegisteredTransition;
import backend.academy.dto.validator.UrlValidator;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

@Component
public class HelpCommand implements IdleTransition, NotRegisteredTransition {
    private static final String NAME = "/help";
    private static final String DESCRIPTION = "список команд";
    private final String message;

    @Autowired
    public HelpCommand(@Lazy List<Command> commands, List<UrlValidator> validators) {
        String validatorInfo = validators.stream().map(UrlValidator::getPattern).collect(Collectors.joining("\n"));

        String commandInfo = commands.stream()
                .filter(cmd -> cmd != this)
                .map(cmd -> "%s - %s".formatted(cmd.getName(), cmd.getDescription()))
                .collect(Collectors.joining("\n"));
        // Если делать это по другому без %n, то кидается баг
        this.message = "Доступные шаблоны ссылок:%n%s%n%nДоступные команды:%n%s%n%s - %s"
                .formatted(validatorInfo, commandInfo, NAME, DESCRIPTION);
    }

    @Override
    public String getName() {
        return NAME;
    }

    @Override
    public String getDescription() {
        return DESCRIPTION;
    }

    @Override
    public String formMessageFromIdleState(Long chatId) {
        return getMessage();
    }

    @Override
    public String formMessageFromNotRegisteredState(Long chatId) {
        return getMessage();
    }

    private String getMessage() {
        return message;
    }
}
