package backend.academy.scrapper.config;

import backend.academy.scrapper.bot.BotClient;
import backend.academy.scrapper.config.properties.BotProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.support.RestClientAdapter;
import org.springframework.web.service.invoker.HttpServiceProxyFactory;

@Configuration
@EnableConfigurationProperties(BotProperties.class)
@ConditionalOnProperty(prefix = "app", value = "message-transport", havingValue = "http")
@RequiredArgsConstructor
public class BotConfig {
    private final BotProperties botProperties;

    @Bean
    public BotClient botClient() {
        RestClient restClient =
                RestClient.builder().baseUrl(botProperties.url()).build();
        HttpServiceProxyFactory httpServiceProxyFactory = HttpServiceProxyFactory.builderFor(
                        RestClientAdapter.create(restClient))
                .build();
        return httpServiceProxyFactory.createClient(BotClient.class);
    }
}
