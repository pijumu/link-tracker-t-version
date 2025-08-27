package backend.academy.bot.config.properties;

import jakarta.validation.constraints.NotNull;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@Validated
@ConfigurationProperties(prefix = "app.scrapper", ignoreUnknownFields = false)
public record ScrapperProperties(@NotNull String url) {}
