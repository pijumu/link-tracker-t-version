package backend.academy.bot.config.properties;

import jakarta.validation.constraints.NotEmpty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@Validated
@ConfigurationProperties(prefix = "app.bot", ignoreUnknownFields = false)
public record BotProperties(@NotEmpty String telegramToken) {}
