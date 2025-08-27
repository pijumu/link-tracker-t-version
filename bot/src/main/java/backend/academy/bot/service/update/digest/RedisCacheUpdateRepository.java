package backend.academy.bot.service.update.digest;

import backend.academy.bot.domain.ChatUpdate;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.data.redis.connection.DefaultStringRedisConnection;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.connection.StringRedisConnection;
import org.springframework.data.redis.core.Cursor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ScanOptions;
import org.springframework.stereotype.Repository;

@Repository
@ConditionalOnProperty(prefix = "app.notifications", value = "mode", havingValue = "digest")
@RequiredArgsConstructor
// Может стоит на redis стримы переделать.
public class RedisCacheUpdateRepository {
    private static final String UPDATE_CACHE_PREFIX = "update:";
    private static final int KEY_BATCH_SIZE = 20;
    private static final int DIGEST_BATCH_SIZE = 10;
    private final RedisTemplate<String, Object> redisTemplate;
    private final ChatUpdateBatchProcessor processor;

    public void put(Long chatId, ChatUpdate update) {
        redisTemplate.opsForList().rightPush(UPDATE_CACHE_PREFIX + chatId, update);
    }

    public void processCollectedUpdates() {
        String pattern = UPDATE_CACHE_PREFIX + "*";
        ScanOptions opts =
                ScanOptions.scanOptions().match(pattern).count(KEY_BATCH_SIZE).build();
        Set<String> keys = new HashSet<>();

        try (Cursor<String> cursor = redisTemplate.scan(opts)) {
            Set<String> batch = new HashSet<>(KEY_BATCH_SIZE);
            while (cursor.hasNext()) {
                String key = cursor.next();

                if (!keys.contains(key)) {
                    keys.add(key);
                    batch.add(key);
                }

                if (batch.size() >= KEY_BATCH_SIZE) {
                    processUpdatesByKeys(batch);
                    batch.clear();
                }
            }

            if (!batch.isEmpty()) {
                processUpdatesByKeys(batch);
            }
        }
    }

    @SuppressWarnings("unchecked")
    private void processUpdatesByKeys(Set<String> keys) {
        List<Object> results = redisTemplate.executePipelined((RedisConnection rawConn) -> {
            StringRedisConnection conn = new DefaultStringRedisConnection(rawConn);
            for (String k : keys) {
                conn.lPop(k, DIGEST_BATCH_SIZE);
            }
            return null;
        });

        results.stream().map(updates -> (List<ChatUpdate>) updates).forEach(processor::process);
    }
}
