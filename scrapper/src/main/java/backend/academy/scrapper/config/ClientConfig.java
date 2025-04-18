package backend.academy.scrapper.config;

import backend.academy.scrapper.client.BotClient;
import backend.academy.scrapper.client.github.GithubClient;
import backend.academy.scrapper.client.stackoverflow.StackOverflowClient;
import java.util.Map;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.support.RestClientAdapter;
import org.springframework.web.service.invoker.HttpServiceProxyFactory;

@Configuration
public class ClientConfig {

    @Bean
    public GithubClient githubClient(ScrapperConfig scrapperConfig) {
        RestClient restClient = RestClient.builder()
                .baseUrl(scrapperConfig.github().url())
                .defaultHeader(
                        "Authorization", "Bearer " + scrapperConfig.github().token())
                .defaultHeader("Accept", "application/vnd.github+json")
                .build();
        HttpServiceProxyFactory httpServiceProxyFactory = HttpServiceProxyFactory.builderFor(
                        RestClientAdapter.create(restClient))
                .build();
        return httpServiceProxyFactory.createClient(GithubClient.class);
    }

    @Bean
    public StackOverflowClient stackOverflowClient(ScrapperConfig scrapperConfig) {
        RestClient restClient = RestClient.builder()
                .baseUrl(scrapperConfig.stackOverflow().url())
                .defaultUriVariables(
                        Map.of("key", scrapperConfig.stackOverflow().key(), "site", "stackoverflow"))
                .build();
        HttpServiceProxyFactory httpServiceProxyFactory = HttpServiceProxyFactory.builderFor(
                        RestClientAdapter.create(restClient))
                .build();
        return httpServiceProxyFactory.createClient(StackOverflowClient.class);
    }

    @Bean
    public BotClient botClient(ScrapperConfig scrapperConfig) {
        RestClient restClient =
                RestClient.builder().baseUrl(scrapperConfig.botUrl()).build();
        HttpServiceProxyFactory httpServiceProxyFactory = HttpServiceProxyFactory.builderFor(
                        RestClientAdapter.create(restClient))
                .build();
        return httpServiceProxyFactory.createClient(BotClient.class);
    }
}
