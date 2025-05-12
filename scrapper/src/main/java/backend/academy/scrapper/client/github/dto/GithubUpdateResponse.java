package backend.academy.scrapper.client.github.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.Instant;

@JsonIgnoreProperties(ignoreUnknown = true)
public record GithubUpdateResponse(
        String title, User user, @JsonProperty("created_at") Instant creationDate, String body) {
    @JsonIgnoreProperties(ignoreUnknown = true)
    public record User(@JsonProperty("login") String name) {}
}
