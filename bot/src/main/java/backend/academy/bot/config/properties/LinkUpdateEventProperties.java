package backend.academy.bot.config.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "app.link-update-events", ignoreUnknownFields = false)
public record LinkUpdateEventProperties(String topic, int concurrency) {}
