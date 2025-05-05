package backend.academy.scrapper.test.integration;

import backend.academy.scrapper.client.github.GithubClient;
import backend.academy.scrapper.client.stackoverflow.StackOverflowClient;
import backend.academy.scrapper.client.util.UpdateMapper;
import java.util.Map;
import org.mapstruct.factory.Mappers;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.support.RestClientAdapter;
import org.springframework.web.service.invoker.HttpServiceProxyFactory;
import org.testcontainers.shaded.com.google.common.net.HttpHeaders;

@TestConfiguration
public class ClientTestConfig {
    @Value("${wiremock.port1}")
    private String port1;

    @Value("${wiremock.port2}")
    private String port2;

    private final String BASE_URL = "http://localhost:";

    @Bean
    public GithubClient githubClient() {
        String url = BASE_URL + port1;
        RestClient restClient = RestClient.builder()
                .baseUrl(url)
                .defaultHeader(HttpHeaders.ACCEPT, "application/vnd.github+json")
                .build();

        return HttpServiceProxyFactory.builderFor(RestClientAdapter.create(restClient))
                .build()
                .createClient(GithubClient.class);
    }

    @Bean
    public StackOverflowClient stackOverflowClient() {
        String url = BASE_URL + port2;
        RestClient restClient = RestClient.builder()
                .baseUrl(url)
                .defaultUriVariables(Map.of(
                        "key", "test-api-key",
                        "site", "stackoverflow"))
                .build();

        return HttpServiceProxyFactory.builderFor(RestClientAdapter.create(restClient))
                .build()
                .createClient(StackOverflowClient.class);
    }

    @Bean
    public UpdateMapper updateMapper() {
        return Mappers.getMapper(UpdateMapper.class);
    }
}
