package backend.academy.bot.fsm.command;

import backend.academy.bot.domain.ChatContext;
import backend.academy.bot.exception.UnknownStateException;
import backend.academy.bot.fsm.command.util.Command;
import backend.academy.dto.validator.util.UrlValidator;
import java.util.List;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class HelpCommand implements Command {
    private static final String NAME = "/help";
    private static final String DESCRIPTION = "список команд";
    private final String message;

    @Autowired
    public HelpCommand(@Lazy List<Command> commands, List<UrlValidator> validators) {
        String validatorInfo = validators.stream().map(UrlValidator::getPattern).collect(Collectors.joining("\n"));

        String commandInfo = commands.stream()
                .filter(cmd -> !cmd.getName().equals(NAME))
                .map(cmd -> "%s - %s".formatted(cmd.getName(), cmd.getDescription()))
                .collect(Collectors.joining("\n"));
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
    public String handle(Long chatId, String input, ChatContext context) {
        return switch (context.state()) {
            case NOT_REGISTERED -> handleNotRegistered();
            case IDLE -> handleIdle();
            default -> throw new UnknownStateException("Неожиданное состояние: " + context.state());
        };
    }

    private String handleNotRegistered() {
        return message;
    }

    private String handleIdle() {
        return message;
    }
}
