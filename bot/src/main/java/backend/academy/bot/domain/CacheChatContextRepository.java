package backend.academy.bot.domain;

public interface CacheChatContextRepository {
    ChatContext get(Long chatId);

    void put(Long chatId, ChatContext chatContext);

    void evict(Long chatId);
}
