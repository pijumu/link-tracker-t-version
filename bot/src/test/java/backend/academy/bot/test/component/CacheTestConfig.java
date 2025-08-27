package backend.academy.bot.test.component;

import backend.academy.bot.domain.ChatContext;
import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import java.util.concurrent.TimeUnit;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

@TestConfiguration
public class CacheTestConfig {
    @Bean
    public Cache<Long, ChatContext> chatContextCache() {
        return Caffeine.newBuilder()
                .expireAfterWrite(3, TimeUnit.MINUTES)
                .maximumSize(1000)
                .build();
    }
}
