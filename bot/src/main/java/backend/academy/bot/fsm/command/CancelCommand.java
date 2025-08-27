package backend.academy.bot.fsm.command;

import static backend.academy.bot.fsm.Constants.CANCELED;
import static backend.academy.bot.fsm.State.IDLE;

import backend.academy.bot.domain.CacheChatContextRepository;
import backend.academy.bot.domain.ChatContext;
import backend.academy.bot.fsm.command.util.Command;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CancelCommand implements Command {
    private static final String NAME = "/cancel";
    private static final String DESCRIPTION = "переход в меню команд";
    private final CacheChatContextRepository cacheChatContextRepository;

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
        cacheChatContextRepository.put(chatId, ChatContext.builder(IDLE).build());
        return CANCELED;
    }
}
