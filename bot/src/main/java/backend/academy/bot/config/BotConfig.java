package backend.academy.bot.config;

import backend.academy.bot.config.properties.BotProperties;
import backend.academy.bot.domain.ChatContext;
import backend.academy.bot.scrapper.ScrapperClient;
import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.pengrad.telegrambot.TelegramBot;
import java.util.concurrent.TimeUnit;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.support.RestClientAdapter;
import org.springframework.web.service.invoker.HttpServiceProxyFactory;

@Configuration
@EnableConfigurationProperties(BotProperties.class)
public class BotConfig {

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

    @Bean
    public TelegramBot telegramBot(BotProperties botProperties) {
        return new TelegramBot(botProperties.telegramToken());
    }

    @Bean
    public ScrapperClient scrapperClient(BotProperties botProperties) {
        RestClient restClient =
                RestClient.builder().baseUrl(botProperties.scrapperUrl()).build();
        HttpServiceProxyFactory httpServiceProxyFactory = HttpServiceProxyFactory.builderFor(
                        RestClientAdapter.create(restClient))
                .build();

        return httpServiceProxyFactory.createClient(ScrapperClient.class);
    }
}
