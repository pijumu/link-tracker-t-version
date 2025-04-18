package backend.academy.bot.repository;

import backend.academy.bot.model.ChatSession;

public interface IChatSessionRepository {
    void setAttribute(Long chatId, String key, String message);

    void setState(Long chatId, String state);

    ChatSession getChatSession(Long chatId);

    void cleanAttributes(Long chatId);

    boolean isRegistered(Long chatId);

    String getState(Long chatId);

    void register(Long chatId, ChatSession chatSession);
}
