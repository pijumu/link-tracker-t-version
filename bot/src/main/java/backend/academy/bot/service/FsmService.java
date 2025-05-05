package backend.academy.bot.service;

import backend.academy.bot.domain.CacheChatContextRepository;
import backend.academy.bot.domain.ChatContext;
import backend.academy.bot.fsm.MessageConstants;
import backend.academy.bot.fsm.state.State;
import backend.academy.bot.fsm.state.StateEntry;
import java.util.Arrays;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class FsmService {
    private final ConcurrentMap<StateEntry, State> states;
    private final CacheChatContextRepository cacheChatContextRepository;

    public FsmService(ApplicationContext applicationContext, CacheChatContextRepository cacheChatContextRepository) {
        this.states = new ConcurrentHashMap<>();
        Arrays.stream(StateEntry.values()).forEach(entry -> {
            log.debug("Creating state entry: {}", entry);
            Class<?> clazz = entry.stateHandlerClass();
            State stateBean = (State) applicationContext.getBean(clazz);
            states.put(entry, stateBean);
        });
        this.cacheChatContextRepository = cacheChatContextRepository;
    }

    /*
        Есть ли смысл в несколько потоков делать?
        Тогда по userId нужно выбирать поток. Хотя мы же не ожидаем сообщения без нашего ответа на предыдущее.
    */
    public String handle(String input, Long chatId) {
        try {
            ChatContext context = cacheChatContextRepository.get(chatId);
            return states.get(context.state()).handle(input, chatId, context);
        } catch (Exception e) {
            log.error("Ошибка при обработке сообщения {} с chatId {}: {}", input, chatId, e.getMessage());
            return MessageConstants.ERROR_MESSAGE;
        }
    }
}
