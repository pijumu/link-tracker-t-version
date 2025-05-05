package backend.academy.scrapper.config;

import backend.academy.scrapper.client.bot.BotClient;
import backend.academy.scrapper.client.github.GithubClient;
import backend.academy.scrapper.client.stackoverflow.StackOverflowClient;
import backend.academy.scrapper.client.util.UpdateMapper;
import java.util.Map;
import org.mapstruct.factory.Mappers;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.support.RestClientAdapter;
import org.springframework.web.service.invoker.HttpServiceProxyFactory;

@Configuration
public class ScrapperConfig {

    @Bean
    public UpdateMapper updateMapper() {
        return Mappers.getMapper(UpdateMapper.class);
    }

    @Bean
    public GithubClient githubClient(ScrapperProperties scrapperProperties) {
        RestClient restClient = RestClient.builder()
                .baseUrl(scrapperProperties.github().url())
                .defaultHeader(
                        HttpHeaders.AUTHORIZATION,
                        "Bearer " + scrapperProperties.github().token())
                .defaultHeader(HttpHeaders.ACCEPT, "application/vnd.github+json")
                .build();
        HttpServiceProxyFactory httpServiceProxyFactory = HttpServiceProxyFactory.builderFor(
                        RestClientAdapter.create(restClient))
                .build();
        return httpServiceProxyFactory.createClient(GithubClient.class);
    }

    @Bean
    public StackOverflowClient stackOverflowClient(ScrapperProperties scrapperProperties) {
        RestClient restClient = RestClient.builder()
                .baseUrl(scrapperProperties.stackOverflow().url())
                .defaultUriVariables(
                        Map.of("key", scrapperProperties.stackOverflow().key(), "site", "stackoverflow"))
                .build();
        HttpServiceProxyFactory httpServiceProxyFactory = HttpServiceProxyFactory.builderFor(
                        RestClientAdapter.create(restClient))
                .build();
        return httpServiceProxyFactory.createClient(StackOverflowClient.class);
    }

    @Bean
    public BotClient botClient(ScrapperProperties scrapperProperties) {
        RestClient restClient =
                RestClient.builder().baseUrl(scrapperProperties.botUrl()).build();
        HttpServiceProxyFactory httpServiceProxyFactory = HttpServiceProxyFactory.builderFor(
                        RestClientAdapter.create(restClient))
                .build();
        return httpServiceProxyFactory.createClient(BotClient.class);
    }
}
