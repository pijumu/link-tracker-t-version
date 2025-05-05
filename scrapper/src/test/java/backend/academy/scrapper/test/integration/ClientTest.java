package backend.academy.scrapper.test.integration;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;

import backend.academy.scrapper.client.github.GithubClientAdapter;
import backend.academy.scrapper.client.stackoverflow.StackOverflowClientAdapter;
import backend.academy.scrapper.client.util.UpdateDto;
import backend.academy.scrapper.client.util.UpdateMapper;
import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.core.WireMockConfiguration;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;

@SpringBootTest(
        classes = {
            UpdateMapper.class,
            GithubClientAdapter.class,
            StackOverflowClientAdapter.class,
        })
@Import(ClientTestConfig.class)
@ActiveProfiles("client-test")
public class ClientTest {
    @Autowired
    GithubClientAdapter githubClientAdapter;

    @Autowired
    StackOverflowClientAdapter stackOverflowClientAdapter;

    static WireMockServer wireMockGithub =
            new WireMockServer(WireMockConfiguration.options().dynamicPort());
    static WireMockServer wireMockStackOverflow =
            new WireMockServer(WireMockConfiguration.options().dynamicPort());

    @DynamicPropertySource
    static void registerPorts(DynamicPropertyRegistry reg) {
        reg.add("wiremock.port1", () -> wireMockGithub.port());
        reg.add("wiremock.port2", () -> wireMockStackOverflow.port());
    }

    @BeforeAll
    static void startServers() {
        wireMockGithub.start();
        wireMockStackOverflow.start();
    }

    @AfterAll
    static void stopServers() {
        wireMockGithub.stop();
        wireMockStackOverflow.stop();
    }

    @BeforeEach
    void setup() {
        // Arrange
        wireMockGithub.stubFor(
                get(urlEqualTo("/repos/octocat/Hello-World"))
                        .willReturn(
                                aResponse()
                                        .withHeader("Content-Type", "application/json")
                                        .withBody(
                                                """
                        {
                            "id": 1296269,
                            "node_id": "MDEwOlJlcG9zaXRvcnkxMjk2MjY5",
                            "name": "Hello-World",
                            "full_name": "octocat/Hello-World",
                            "private": false,
                            "owner": {
                                "login": "octocat",
                                "id": 583231,
                                "node_id": "MDQ6VXNlcjU4MzIzMQ==",
                                "avatar_url": "https://avatars.githubusercontent.com/u/583231?v=4",
                                "gravatar_id": "",
                                "url": "https://api.github.com/users/octocat",
                                "html_url": "https://github.com/octocat",
                                "followers_url": "https://api.github.com/users/octocat/followers",
                                "following_url": "https://api.github.com/users/octocat/following{/other_user}",
                                "gists_url": "https://api.github.com/users/octocat/gists{/gist_id}",
                                "starred_url": "https://api.github.com/users/octocat/starred{/owner}{/repo}",
                                "subscriptions_url": "https://api.github.com/users/octocat/subscriptions",
                                "organizations_url": "https://api.github.com/users/octocat/orgs",
                                "repos_url": "https://api.github.com/users/octocat/repos",
                                "events_url": "https://api.github.com/users/octocat/events{/privacy}",
                                "received_events_url": "https://api.github.com/users/octocat/received_events",
                                "type": "User",
                                "user_view_type": "public",
                                "site_admin": false
                            },
                            "updated_at": "2025-05-04T18:36:24Z",
                            "network_count": 2948,
                            "subscribers_count": 1733
                        }
                        """)));

        wireMockStackOverflow.stubFor(
                get(urlEqualTo("/questions/123?key=test-api-key&site=stackoverflow"))
                        .willReturn(
                                aResponse()
                                        .withHeader("Content-Type", "application/json")
                                        .withBody(
                                                """
                        {
                            "items": [
                                {
                                    "post_state": "Published",
                                    "last_activity_date": 1744364419,
                                    "question_id": 214741,
                                    "title": "What is a StackOverflowError?"
                                }
                            ]
                        }
                        """)));
    }

    @Test
    @DisplayName(
            "Проверка pipeline WireMock -> GithubClient -> UpdateMapper -> GithubClientAdapter -> Update. Позитивный сценарий")
    void test1() {
        // Act
        Map<String, String> params = new HashMap<>();
        params.put("owner", "octocat");
        params.put("repo", "Hello-World");
        UpdateDto update = githubClientAdapter.getUpdate(params);

        // Assert
        Assertions.assertEquals(update.name(), "Hello-World");
        Assertions.assertEquals(update.lastTimeUpdated(), Instant.parse("2025-05-04T18:36:24Z"));
    }

    @Test
    @DisplayName(
            "Проверка pipeline WireMock -> WireMock -> GithubClient -> UpdateMapper -> GithubClientAdapter -> Update. Позитивный сценарий")
    void test2() {
        // Act
        Map<String, String> params = new HashMap<>();
        params.put("questionId", "123");
        UpdateDto update = stackOverflowClientAdapter.getUpdate(params);

        // Assert
        Assertions.assertEquals(update.name(), "What is a StackOverflowError?");
        Assertions.assertEquals(update.lastTimeUpdated(), Instant.ofEpochSecond(1744364419));
    }
}
