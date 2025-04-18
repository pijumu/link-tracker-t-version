package backend.academy.scrapper.config;

import jakarta.validation.constraints.NotEmpty;

public record StackOverflowConfig(@NotEmpty String url, @NotEmpty String key) {}
