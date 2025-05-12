package backend.academy.bot.repository;

import static backend.academy.bot.fsm.State.IDLE;
import static backend.academy.bot.fsm.State.NOT_REGISTERED;

import backend.academy.bot.domain.CacheChatContextRepository;
import backend.academy.bot.domain.ChatContext;
import backend.academy.bot.scrapper.ScrapperClient;
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
                ? ChatContext.builder(IDLE).build()
                : ChatContext.builder(NOT_REGISTERED).build();
    }
}
