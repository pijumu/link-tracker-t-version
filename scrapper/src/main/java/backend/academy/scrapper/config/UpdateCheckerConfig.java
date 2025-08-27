package backend.academy.scrapper.config;

import backend.academy.scrapper.config.properties.UpdateCheckerProperties;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

@Configuration
@EnableConfigurationProperties(UpdateCheckerProperties.class)
@RequiredArgsConstructor
@Getter
public class UpdateCheckerConfig {
    private final UpdateCheckerProperties updateCheckerProperties;

    @Bean(name = "httpPool")
    ThreadPoolTaskExecutor httpPool() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(8);
        executor.setMaxPoolSize(8);
        executor.setQueueCapacity(100);
        executor.initialize();
        return executor;
    }

    @Bean(name = "dbPool")
    ThreadPoolTaskExecutor dbPool() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(4);
        executor.setMaxPoolSize(4);
        executor.setQueueCapacity(0);
        executor.initialize();
        return executor;
    }
}
