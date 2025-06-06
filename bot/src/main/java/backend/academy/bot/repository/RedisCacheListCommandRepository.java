package backend.academy.bot.repository;

import backend.academy.dto.dto.ListLinksResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class RedisCacheListCommandRepository {
    public static final String LIST_CACHE_PREFIX = "list:";
    private final RedisTemplate<String, Object> redisTemplate;

    public void put(Long chatId, ListLinksResponse listLinksResponse) {
        redisTemplate.opsForValue().set(LIST_CACHE_PREFIX + chatId, listLinksResponse);
    }

    public ListLinksResponse get(Long chatId) {
        return (ListLinksResponse) redisTemplate.opsForValue().get(LIST_CACHE_PREFIX + chatId);
    }

    public void invalidate(Long chatId) {
        redisTemplate.delete(LIST_CACHE_PREFIX + chatId);
    }
}
