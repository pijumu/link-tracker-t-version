package backend.academy.scrapper.config;

import backend.academy.scrapper.client.github.util.GithubClient;
import backend.academy.scrapper.client.stackoverflow.util.StackOverflowClient;
import backend.academy.scrapper.config.properties.ClientProperties;
import java.util.Map;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.support.RestClientAdapter;
import org.springframework.web.service.invoker.HttpServiceProxyFactory;

@Configuration
@EnableConfigurationProperties(ClientProperties.class)
public class ClientConfig {

    @Bean
    public GithubClient githubClient(ClientProperties clientProperties) {
        RestClient restClient = RestClient.builder()
                .baseUrl(clientProperties.github().url())
                .defaultHeader(
                        HttpHeaders.AUTHORIZATION,
                        "Bearer " + clientProperties.github().token())
                .defaultHeader(HttpHeaders.ACCEPT, "application/vnd.github+json")
                .build();
        HttpServiceProxyFactory httpServiceProxyFactory = HttpServiceProxyFactory.builderFor(
                        RestClientAdapter.create(restClient))
                .build();
        return httpServiceProxyFactory.createClient(GithubClient.class);
    }

    @Bean
    public StackOverflowClient stackOverflowClient(ClientProperties clientProperties) {
        RestClient restClient = RestClient.builder()
                .baseUrl(clientProperties.stackOverflow().url())
                .defaultUriVariables(
                        Map.of("key", clientProperties.stackOverflow().key()))
                .build();
        HttpServiceProxyFactory httpServiceProxyFactory = HttpServiceProxyFactory.builderFor(
                        RestClientAdapter.create(restClient))
                .build();
        return httpServiceProxyFactory.createClient(StackOverflowClient.class);
    }
}
