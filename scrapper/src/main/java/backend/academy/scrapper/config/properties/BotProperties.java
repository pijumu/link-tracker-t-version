package backend.academy.scrapper.config.properties;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@Validated
@ConfigurationProperties(prefix = "app.bot", ignoreUnknownFields = false)
@ConditionalOnProperty(prefix = "app", value = "message-transport", havingValue = "http")
public record BotProperties(String url) {}
