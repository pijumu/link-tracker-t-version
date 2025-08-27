package backend.academy.scrapper.config.properties;

import jakarta.validation.constraints.NotEmpty;

public record StackOverflowProperties(@NotEmpty String url, @NotEmpty String key) {}
