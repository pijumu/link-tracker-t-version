package backend.academy.scrapper.test.integration;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.equalTo;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.urlPathEqualTo;

import backend.academy.dto.validator.util.UrlType;
import backend.academy.scrapper.client.UpdateDto;
import backend.academy.scrapper.client.github.GithubClientAdapter;
import backend.academy.scrapper.client.stackoverflow.StackOverflowClientAdapter;
import backend.academy.scrapper.domain.dto.UrlInfoDto;
import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.core.WireMockConfiguration;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;

@SpringBootTest(
        classes = {
            GithubClientAdapter.class,
            StackOverflowClientAdapter.class,
        })
@Import(ClientTestConfig.class)
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
        TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
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
        // PreArrange
        wireMockGithub.stubFor(
                get(urlPathEqualTo("/repos/octocat/Hello-World/issues"))
                        .withQueryParam("since", equalTo("2000-04-10T20:09:31Z"))
                        .withQueryParam("per_page", equalTo("5"))
                        .withQueryParam("direction", equalTo("asc"))
                        .willReturn(
                                aResponse()
                                        .withHeader("Content-Type", "application/json")
                                        .withBody(
                                                """
                                            [
                                                {
                                                    "url": "https://api.github.com/repos/octocat/Hello-World/issues/3941",
                                                    "repository_url": "https://api.github.com/repos/octocat/Hello-World",
                                                    "labels_url": "https://api.github.com/repos/octocat/Hello-World/issues/3941/labels{/name}",
                                                    "comments_url": "https://api.github.com/repos/octocat/Hello-World/issues/3941/comments",
                                                    "events_url": "https://api.github.com/repos/octocat/Hello-World/issues/3941/events",
                                                    "html_url": "https://github.com/octocat/Hello-World/issues/3941",
                                                    "title": "Test Issue from MCP Server (Docker)",
                                                    "user": {
                                                        "login": "SCSSC5678",
                                                        "id": 172073832,
                                                        "node_id": "U_kgDOCkGjaA",
                                                        "avatar_url": "https://avatars.githubusercontent.com/u/172073832?v=4",
                                                        "gravatar_id": "",
                                                        "type": "User",
                                                        "user_view_type": "public",
                                                        "site_admin": false
                                                    },
                                                    "labels": [],
                                                    "state": "open",
                                                    "locked": false,
                                                    "assignee": null,
                                                    "assignees": [],
                                                    "milestone": null,
                                                    "comments": 0,
                                                    "created_at": "2025-05-09T18:24:34Z",
                                                    "updated_at": "2025-05-09T18:24:34Z",
                                                    "body": "123",
                                                    "closed_by": null,
                                                    "reactions": {
                                                        "url": "https://api.github.com/repos/octocat/Hello-World/issues/3941/reactions",
                                                        "total_count": 0,
                                                        "+1": 0,
                                                        "-1": 0,
                                                        "laugh": 0,
                                                        "hooray": 0,
                                                        "confused": 0,
                                                        "heart": 0,
                                                        "rocket": 0,
                                                        "eyes": 0
                                                    },
                                                    "timeline_url": "https://api.github.com/repos/octocat/Hello-World/issues/3941/timeline",
                                                    "performed_via_github_app": null,
                                                    "state_reason": null
                                                }
                                            ]
                                            """)));

        wireMockStackOverflow.stubFor(
                get(urlPathEqualTo("/questions/214741/comments"))
                        .withQueryParam("site", equalTo("stackoverflow"))
                        .withQueryParam("fromdate", equalTo("955397371"))
                        .withQueryParam("sort", equalTo("creation"))
                        .withQueryParam("order", equalTo("asc"))
                        .withQueryParam("pagesize", equalTo("5"))
                        .withQueryParam("filter", equalTo("!szx-Dsx)YFm7RenuUsIW(gxHfTtAMj8"))
                        .willReturn(
                                aResponse()
                                        .withHeader("Content-Type", "application/json")
                                        .withBody(
                                                """
                                            {
                                                "items": [
                                                    {
                                                        "owner": {
                                                            "display_name": "John McClane"
                                                        },
                                                        "post_state": "Published",
                                                        "creation_date": 1543889009,
                                                        "body": "One non-obvious way to get it: add the line <code>new Object() {{getClass().newInstance();}};</code> to some static context (e.g. <code>main</code> method). Doesn&#39;t work from instance context (throws only <code>InstantiationException</code>)."
                                                    },
                                                    {
                                                        "owner": {
                                                            "display_name": "Danial Jalalnouri"
                                                        },
                                                        "post_state": "Published",
                                                        "creation_date": 1537329695,
                                                        "body": "Stack size in java is small. And some times such as many recursive call you face this problem. You can redesign your code by loop. You can find general design pattern to do it in this url: <a href=\\"http://www.jndanial.com/73/\\" rel=\\"nofollow noreferrer\\">jndanial.com/73</a>"
                                                    }
                                                ]
                                            }
                                            """)));

        wireMockStackOverflow.stubFor(
                get(urlPathEqualTo("/questions/214741/answers"))
                        .withQueryParam("site", equalTo("stackoverflow"))
                        .withQueryParam("fromdate", equalTo("955397371"))
                        .withQueryParam("sort", equalTo("creation"))
                        .withQueryParam("order", equalTo("asc"))
                        .withQueryParam("pagesize", equalTo("5"))
                        .withQueryParam("filter", equalTo("!WWsh2-5LBtfz3iQjzv*iVb*lGN4D)VPVL6K0NHu"))
                        .willReturn(
                                aResponse()
                                        .withHeader("Content-Type", "application/json")
                                        .withBody(
                                                """
                            {
                                "items": [
                                     {
                                         "owner": {
                                             "display_name": "C. K. Young"
                                         },
                                         "creation_date": 1224317839,
                                         "body": "123"
                                     },
                                     {
                                         "owner": {
                                             "display_name": "Greg"
                                         },
                                         "creation_date": 1224317955,
                                         "body": "1234"
                                     },
                                     {
                                         "owner": {
                                             "display_name": "Khoth"
                                         },
                                         "creation_date": 1224318696,
                                         "body": "12345"
                                     },
                                     {
                                         "owner": {
                                             "display_name": "Sean"
                                         },
                                         "creation_date": 1224318871,
                                         "body": "123456"
                                     },
                                     {
                                         "owner": {
                                             "display_name": "splattne"
                                         },
                                         "creation_date": 1224319411,
                                         "body": "1234567"
                                     }
                                ]
                            }
                            """)));

        wireMockStackOverflow.stubFor(
                get(urlPathEqualTo("/questions/214741"))
                        .withQueryParam("site", equalTo("stackoverflow"))
                        .withQueryParam("filter", equalTo("!-tS9ZTnrNw22UJ*9TjRq"))
                        .willReturn(
                                aResponse()
                                        .withHeader("Content-Type", "application/json")
                                        .withBody(
                                                """
                            {
                                "items": [
                                    {
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
            "Проверка внешних клиентов. Проверка pipeline WireMock -> GithubClient  -> GithubClientAdapter -> Update. Позитивный сценарий")
    void test1() {

        // Act
        List<UpdateDto> updates = githubClientAdapter.getUpdates(new UrlInfoDto(
                1L,
                "https://xmpl.com",
                UrlType.GITHUB,
                Instant.parse("2000-04-10T20:09:31Z"),
                Map.of("owner", "octocat", "repo", "Hello-World")));

        // Assert
        Assertions.assertFalse(updates.isEmpty());
        Assertions.assertEquals(updates.size(), 1);
        Assertions.assertEquals(updates.getFirst().url(), "https://xmpl.com");
        Assertions.assertEquals(updates.getFirst().topic(), "Test Issue from MCP Server (Docker)");
        Assertions.assertEquals(updates.getFirst().preview().getLeft().toString(), "GITHUB_ISSUE");
    }

    @Test
    @DisplayName(
            "Проверка внешних клиентов. Проверка pipeline WireMock -> StackOverflowClient -> StackOverflowClientAdapter -> Update. Позитивный сценарий")
    void test2() {
        // Act
        List<UpdateDto> updates = stackOverflowClientAdapter.getUpdates(new UrlInfoDto(
                1L,
                "https://xmpl.com",
                UrlType.STACKOVERFLOW,
                Instant.parse("2000-04-10T20:09:31Z"),
                Map.of("questionId", "214741")));

        // Assert
        Assertions.assertFalse(updates.isEmpty());
        Assertions.assertEquals(updates.size(), 7);
        Assertions.assertEquals(updates.getFirst().url(), "https://xmpl.com");
        Assertions.assertEquals(updates.getFirst().topic(), "What is a StackOverflowError?");
        Assertions.assertEquals(updates.getFirst().preview().getLeft().toString(), "STACKOVERFLOW_ANSWER");
    }
}
