package backend.academy.scrapper.config.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "app.update-checker", ignoreUnknownFields = false)
public record UpdateCheckerProperties(Integer batchSize) {}
