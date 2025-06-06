package backend.academy.bot.config;

import backend.academy.bot.config.properties.ScrapperProperties;
import backend.academy.bot.scrapper.ChatScrapperClient;
import backend.academy.bot.scrapper.LinkScrapperClient;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.support.RestClientAdapter;
import org.springframework.web.service.invoker.HttpServiceProxyFactory;

@Configuration
@EnableConfigurationProperties(ScrapperProperties.class)
@RequiredArgsConstructor
public class ScrapperConfig {
    private final ScrapperProperties scrapperProperties;

    @Bean
    public ChatScrapperClient chatScrapperClient() {
        RestClient restClient =
                RestClient.builder().baseUrl(scrapperProperties.url()).build();
        HttpServiceProxyFactory httpServiceProxyFactory = HttpServiceProxyFactory.builderFor(
                        RestClientAdapter.create(restClient))
                .build();

        return httpServiceProxyFactory.createClient(ChatScrapperClient.class);
    }

    @Bean
    public LinkScrapperClient linkScrapperClient() {
        RestClient restClient =
                RestClient.builder().baseUrl(scrapperProperties.url()).build();
        HttpServiceProxyFactory httpServiceProxyFactory = HttpServiceProxyFactory.builderFor(
                        RestClientAdapter.create(restClient))
                .build();

        return httpServiceProxyFactory.createClient(LinkScrapperClient.class);
    }
}
