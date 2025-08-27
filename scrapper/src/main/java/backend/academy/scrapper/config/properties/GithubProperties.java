package backend.academy.scrapper.config.properties;

import jakarta.validation.constraints.NotEmpty;

public record GithubProperties(@NotEmpty String url, @NotEmpty String token) {}
