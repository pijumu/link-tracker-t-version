package backend.academy.scrapper.config.properties;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "app.link-update-events", ignoreUnknownFields = false)
@ConditionalOnProperty(prefix = "app", value = "message-transport", havingValue = "kafka")
public record LinkUpdateEventProperties(String topic, int partitions, short replicas) {}
