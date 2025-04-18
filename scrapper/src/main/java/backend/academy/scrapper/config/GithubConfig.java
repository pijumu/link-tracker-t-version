package backend.academy.scrapper.config;

import jakarta.validation.constraints.NotEmpty;

public record GithubConfig(@NotEmpty String url, @NotEmpty String token) {}
