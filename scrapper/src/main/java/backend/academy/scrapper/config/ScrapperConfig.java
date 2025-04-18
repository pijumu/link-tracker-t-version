package backend.academy.scrapper.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.validation.annotation.Validated;

@Validated
@ConfigurationProperties(prefix = "app", ignoreUnknownFields = false)
@EnableScheduling
public record ScrapperConfig(GithubConfig github, StackOverflowConfig stackOverflow, String botUrl) {}
