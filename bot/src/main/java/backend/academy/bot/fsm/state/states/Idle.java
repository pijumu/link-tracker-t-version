package backend.academy.bot.fsm.state.states;

import backend.academy.bot.domain.CacheChatContextRepository;
import backend.academy.bot.domain.ChatContext;
import backend.academy.bot.exception.ScrapperException;
import backend.academy.bot.fsm.MessageConstants;
import backend.academy.bot.fsm.state.State;
import backend.academy.bot.fsm.transition.contract.IdleChangingState;
import backend.academy.bot.fsm.transition.contract.IdleTransition;
import jakarta.validation.constraints.NotBlank;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

@Component
@Slf4j
@Validated
public class Idle implements State {
    private final ConcurrentMap<String, IdleTransition> commands;
    private final CacheChatContextRepository cacheChatContextRepository;

    @Autowired
    public Idle(List<IdleTransition> commands, CacheChatContextRepository cacheChatContextRepository) {
        this.cacheChatContextRepository = cacheChatContextRepository;
        this.commands = new ConcurrentHashMap<>();
        for (IdleTransition command : commands) {
            this.commands.put(command.getName(), command);
        }
    }

    @Override
    public String handle(@NotBlank String input, Long chatId, ChatContext context) {
        IdleTransition command = commands.get(input);

        if (command == null) {
            return MessageConstants.UNKNOWN_COMMAND;
        }
        try {
            String message = command.formMessageFromIdleState(chatId);
            if (command instanceof IdleChangingState) {
                ChatContext updated = ChatContext.fromState(((IdleChangingState) command).nextStateFromIdle());
                cacheChatContextRepository.put(chatId, updated);
            }
            return message;
        } catch (ScrapperException e) {
            log.warn("Scrapper недоступен", e);
            return MessageConstants.UNAVAILABLE_SERVICE;
        }
    }
}
