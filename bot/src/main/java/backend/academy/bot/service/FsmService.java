package backend.academy.bot.service;

import static backend.academy.bot.fsm.Constants.ERROR_MESSAGE;

import backend.academy.bot.domain.CacheChatContextRepository;
import backend.academy.bot.domain.ChatContext;
import backend.academy.bot.fsm.highhierarchystate.Idle;
import backend.academy.bot.fsm.highhierarchystate.InCommand;
import backend.academy.bot.fsm.highhierarchystate.NotRegistered;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class FsmService {
    private final CacheChatContextRepository cacheChatContextRepository;
    private final Idle idle;
    private final NotRegistered notRegistered;
    private final InCommand inCommand;

    public String handle(String input, Long chatId) {
        try {
            ChatContext context = cacheChatContextRepository.get(chatId);
            return switch (context.state()) {
                case IDLE -> idle.handle(chatId, input, context);
                case NOT_REGISTERED -> notRegistered.handle(chatId, input, context);
                default -> inCommand.handle(chatId, input, context);
            };
        } catch (Exception e) {
            log.error("Ошибка при обработке сообщения {} с chatId {}: {}", input, chatId, e.getMessage());
            return ERROR_MESSAGE;
        }
    }
}
