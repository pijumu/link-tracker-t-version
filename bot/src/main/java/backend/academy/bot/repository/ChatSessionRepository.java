package backend.academy.bot.repository;

import backend.academy.bot.model.ChatSession;
import java.util.HashMap;
import java.util.Map;
import org.springframework.stereotype.Repository;

@Repository
public class ChatSessionRepository implements IChatSessionRepository {
    private final Map<Long, ChatSession> chatIdSession = new HashMap<>();

    @Override
    public void setAttribute(Long chatId, String key, String message) {
        chatIdSession.get(chatId).attributes().put(key, message);
    }

    @Override
    public void setState(Long chatId, String state) {
        chatIdSession.get(chatId).state(state);
    }

    @Override
    public ChatSession getChatSession(Long chatId) {
        return chatIdSession.get(chatId);
    }

    @Override
    public void cleanAttributes(Long chatId) {
        chatIdSession.get(chatId).attributes().clear();
    }

    @Override
    public boolean isRegistered(Long chatId) {
        return chatIdSession.containsKey(chatId);
    }

    @Override
    public String getState(Long chatId) {
        return chatIdSession.get(chatId).state();
    }

    @Override
    public void register(Long chatId, ChatSession chatSession) {
        chatIdSession.put(chatId, chatSession);
    }
}
