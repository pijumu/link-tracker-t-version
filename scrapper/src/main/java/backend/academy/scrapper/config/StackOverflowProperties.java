package backend.academy.scrapper.config;

import jakarta.validation.constraints.NotEmpty;

public record StackOverflowProperties(@NotEmpty String url, @NotEmpty String key) {}
