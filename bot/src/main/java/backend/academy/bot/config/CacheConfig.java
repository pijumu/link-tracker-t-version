package backend.academy.bot.config;

import backend.academy.bot.domain.ChatContext;
import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import java.util.concurrent.TimeUnit;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CacheConfig {
    @Bean
    public Cache<Long, ChatContext> chatContextCache() {
        /*
            80_000 обусловлено поддержкой 100 RPS, возможно я ошибаюсь в понимание
            10 минут = 600 секунд
            80_000 / 600 > 100
        */
        return Caffeine.newBuilder()
                .expireAfterWrite(10, TimeUnit.MINUTES)
                .maximumSize(80_000)
                .build();
    }
}
