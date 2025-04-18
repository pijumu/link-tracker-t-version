package backend.academy.bot.service;

import backend.academy.bot.exception.ChatAlreadyRegisteredException;
import backend.academy.bot.exception.ChatNotRegisteredException;
import backend.academy.bot.model.ChatSession;
import backend.academy.bot.repository.IChatSessionRepository;
import java.util.HashMap;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class ChatSessionService {
    private final IChatSessionRepository chatSessionRepository;

    private void checkIsRegistered(Long chatId) {
        if (!chatSessionRepository.isRegistered(chatId)) {
            log.error("Чат с id {} не зарегистрирован!", chatId);
            throw new ChatNotRegisteredException("Чат с id " + chatId + " не зарегистрирован");
        }
    }

    @Transactional
    public String getState(Long chatId) {
        if (!chatSessionRepository.isRegistered(chatId)) {
            return "NOT_REGISTERED";
        }
        return chatSessionRepository.getState(chatId);
    }

    @Transactional
    public void setState(Long chatId, String state) {
        checkIsRegistered(chatId);
        chatSessionRepository.setState(chatId, state);
    }

    @Transactional
    public void setAttribute(Long chatId, String key, String value) {
        checkIsRegistered(chatId);
        chatSessionRepository.setAttribute(chatId, key, value);
    }

    @Transactional
    public void cleanAttributes(Long chatId) {
        checkIsRegistered(chatId);
        chatSessionRepository.cleanAttributes(chatId);
    }

    @Transactional
    public ChatSession getChatSession(Long chatId) {
        checkIsRegistered(chatId);
        return chatSessionRepository.getChatSession(chatId);
    }

    @Transactional
    public void register(Long chatId) {
        if (chatSessionRepository.isRegistered(chatId)) {
            log.error("Чат с id {} уже зарегистрирован!", chatId);
            throw new ChatAlreadyRegisteredException("Чат с id" + chatId + " уже зарегистрирован");
        }
        ChatSession chatSession = new ChatSession("IDLE", new HashMap<>());
        chatSessionRepository.register(chatId, chatSession);
    }
}
