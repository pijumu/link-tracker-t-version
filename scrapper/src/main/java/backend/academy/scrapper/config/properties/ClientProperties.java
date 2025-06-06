package backend.academy.scrapper.config.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@Validated
@ConfigurationProperties(prefix = "app.client", ignoreUnknownFields = false)
public record ClientProperties(GithubProperties github, StackOverflowProperties stackOverflow) {}
