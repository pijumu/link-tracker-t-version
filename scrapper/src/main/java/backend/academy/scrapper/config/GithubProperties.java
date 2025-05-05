package backend.academy.scrapper.config;

import jakarta.validation.constraints.NotEmpty;

public record GithubProperties(@NotEmpty String url, @NotEmpty String token) {}
