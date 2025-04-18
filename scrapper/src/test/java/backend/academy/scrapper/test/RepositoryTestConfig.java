package backend.academy.scrapper.test;

import backend.academy.scrapper.repository.ILinkRepository;
import backend.academy.scrapper.repository.InMemoryLinkRepository;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

@TestConfiguration
public class RepositoryTestConfig {
    @Bean
    public ILinkRepository linkRepository() {
        return new InMemoryLinkRepository();
    }
}
