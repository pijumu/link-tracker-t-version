package backend.academy.scrapper.test;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import backend.academy.scrapper.client.ExternalClientManager;
import backend.academy.scrapper.client.Notifier;
import backend.academy.scrapper.model.Link;
import backend.academy.scrapper.repository.ILinkRepository;
import backend.academy.scrapper.service.NotificationService;
import backend.academy.scrapper.service.UpdateCheckerService;
import backend.academy.scrapper.service.UrlParserService;
import java.time.Instant;
import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

@ExtendWith(MockitoExtension.class)
@SpringBootTest(
        classes = {
            UpdateCheckerService.class,
            RepositoryTestConfig.class,
        })
public class UpdateCheckerTest {

    @Autowired
    private ILinkRepository linkRepository;

    @MockitoBean
    ExternalClientManager externalClientManager;

    @MockitoBean
    NotificationService notificationService;

    @MockitoBean
    UrlParserService urlParserService;

    @Autowired
    UpdateCheckerService updateChecker;

    @BeforeEach
    void setup() {
        String commonUrl = "https://github.com/me/mine";
        String anotherUrl = "https://github.com/notMe/notMine";
        Link link1 = new Link(
                1L, commonUrl, List.of("news", "tech"), List.of("positive"), Instant.parse("2025-04-18T10:00:00Z"));

        Link link2 = new Link(
                2L,
                commonUrl,
                List.of("tech", "AI", "blog", "trends"),
                List.of(),
                Instant.parse("2025-04-17T15:30:00Z"));

        Link link3 = new Link(
                3L,
                anotherUrl,
                List.of(),
                List.of("no-ads", "safe", "verified"),
                Instant.parse("2025-04-16T08:20:00Z"));

        Link link4 = new Link(
                4L,
                commonUrl,
                Arrays.asList("news", "tech"),
                Arrays.asList("positive", "safe"),
                Instant.parse("2025-04-17T15:30:00Z"));

        linkRepository.add(1L, link1);
        linkRepository.add(12L, link2);
        linkRepository.add(123L, link3);
        linkRepository.add(1234L, link4);
    }

    @Test
    @DisplayName("Проверка планировщика. Позитивный сценарий")
    void test1() {
        // Arrange
        Notifier notifier = mock(Notifier.class);
        String expectedUrl = "https://github.com/me/mine";
        List<Long> expectedFollowers = Arrays.asList(1L, 12L, 1234L);

        when(notifier.getFormattedTime()).thenReturn(Instant.parse("2028-04-16T08:20:00Z"));
        when(urlParserService.parse(any())).thenReturn(null);
        when(externalClientManager.getNotifier(any())).thenReturn(notifier);

        // Act
        updateChecker.checkUpdates();

        // Assert
        verify(notificationService)
                .notify(
                        eq(expectedUrl),
                        any(),
                        argThat(followers -> followers.containsAll(expectedFollowers) && followers.size() == 3));
    }
}
