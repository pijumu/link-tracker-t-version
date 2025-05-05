package backend.academy.scrapper.client.github.util;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.Instant;

public record GithubDto(String name, @JsonProperty("updated_at") Instant updatedAt) {}
