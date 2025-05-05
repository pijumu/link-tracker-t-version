package backend.academy.bot.repository;

import backend.academy.bot.domain.CacheChatContextRepository;
import backend.academy.bot.domain.ChatContext;
import backend.academy.bot.fsm.state.StateEntry;
import backend.academy.bot.service.ScrapperClient;
import com.github.benmanes.caffeine.cache.Cache;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class CaffeineChatContextRepository implements CacheChatContextRepository {
    private final ScrapperClient scrapperClient;
    private final Cache<Long, ChatContext> cache;

    @Override
    public ChatContext get(Long chatId) {
        return cache.get(chatId, this::makeResponse);
    }

    @Override
    public void put(Long chatId, ChatContext chatContext) {
        cache.asMap().put(chatId, chatContext);
    }

    @Override
    public void evict(Long chatId) {
        cache.invalidate(chatId);
    }

    private ChatContext makeResponse(Long chaId) {
        return scrapperClient.isRegisteredChat(chaId)
                ? ChatContext.fromState(StateEntry.IDLE)
                : ChatContext.fromState(StateEntry.NOT_REGISTERED);
    }
}
