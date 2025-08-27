package backend.academy.scrapper.client.github.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;
import java.time.Instant;

@JsonIgnoreProperties(ignoreUnknown = true)
public record GithubUpdateResponse(
        String title, User user, @JsonProperty("created_at") Instant creationDate, String body, boolean isPullRequest) {
    @JsonCreator
    public GithubUpdateResponse(
            @JsonProperty("title") String title,
            @JsonProperty("user") User user,
            @JsonProperty("created_at") Instant creationDate,
            @JsonProperty("body") String body,
            @JsonProperty("pull_request") JsonNode prNode) {
        this(title, user, creationDate, body, prNode != null);
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record User(@JsonProperty("login") String name) {}
}
