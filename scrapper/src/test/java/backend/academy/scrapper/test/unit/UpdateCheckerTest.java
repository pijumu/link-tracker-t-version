package backend.academy.scrapper.test.unit;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import backend.academy.dto.dto.LinkUpdateDto;
import backend.academy.dto.validator.util.UrlType;
import backend.academy.scrapper.bot.BotClient;
import backend.academy.scrapper.client.util.ClientAdapter;
import backend.academy.scrapper.config.UpdateCheckerConfig;
import backend.academy.scrapper.config.properties.UpdateCheckerProperties;
import backend.academy.scrapper.domain.dto.UpdateInfoDto;
import backend.academy.scrapper.domain.dto.UrlInfoDto;
import backend.academy.scrapper.service.UpdateCheckerService;
import backend.academy.scrapper.service.data.UrlService;
import backend.academy.scrapper.service.notification.RestNotificationService;
import java.time.Instant;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

@ExtendWith(MockitoExtension.class)
public class UpdateCheckerTest {
    ClientAdapter clientAdapter;
    UrlService urlService;
    BotClient botClient;
    RestNotificationService notificationService;

    ThreadPoolTaskExecutor httpPool;
    ThreadPoolTaskExecutor dbPool;

    UpdateCheckerConfig updateCheckerConfig;
    UpdateCheckerProperties updateCheckerProperties;

    UpdateCheckerService updateCheckerService;

    @BeforeEach
    public void setup() {
        // PreArrange
        clientAdapter = mock(ClientAdapter.class);
        when(clientAdapter.getUrlType()).thenReturn(UrlType.GITHUB);

        urlService = mock(UrlService.class);
        updateCheckerConfig = mock(UpdateCheckerConfig.class);
        updateCheckerProperties = mock(UpdateCheckerProperties.class);

        botClient = spy(BotClient.class);
        notificationService = spy(new RestNotificationService(botClient));

        httpPool = new ThreadPoolTaskExecutor();
        dbPool = new ThreadPoolTaskExecutor();
        httpPool.initialize();
        dbPool.initialize();

        Integer batchSize = 2;

        when(updateCheckerConfig.updateCheckerProperties()).thenReturn(updateCheckerProperties);
        when(updateCheckerProperties.batchSize()).thenReturn(batchSize);

        updateCheckerService = new UpdateCheckerService(
                List.of(clientAdapter), urlService, httpPool, dbPool, updateCheckerConfig, notificationService);
    }

    @Test
    @DisplayName("Проверка updateChecker. Позитивный сценарий.")
    void test1() {
        var first = new UrlInfoDto(
                1L,
                "https://github.com/user1/repo1",
                UrlType.GITHUB,
                Instant.parse("2024-05-01T10:15:30Z"),
                Map.of("owner", "user1", "repo", "repo1"),
                List.of(1L, 2L));

        var second = new UrlInfoDto(
                2L,
                "https://github.com/user2/repo2",
                UrlType.GITHUB,
                Instant.parse("2024-04-20T08:00:00Z"),
                Map.of("owner", "user2", "repo", "repo2"),
                List.of(3L));

        var third = new UrlInfoDto(
                3L,
                "https://github.com/user3/repo3",
                UrlType.GITHUB,
                Instant.parse("2024-03-15T13:45:00Z"),
                Map.of("owner", "user3", "repo", "repo3"),
                List.of(1L, 3L));

        List<UrlInfoDto> firstBatch = List.of(first, second);
        List<UrlInfoDto> secondBatch = List.of(third);
        List<UrlInfoDto> thirdBatch = Collections.emptyList();

        LinkUpdateDto dto1 = new LinkUpdateDto("https://github.com/user2/repo2", "Первое описание", List.of(3L));
        LinkUpdateDto dto2 = new LinkUpdateDto("https://github.com/user3/repo3", "Второе описание", List.of(1L, 3L));

        var inst1 = Instant.now();
        var inst2 = Instant.now();
        var list1 = List.of(dto1);
        var list2 = List.of(dto2);

        Optional<UpdateInfoDto> firstUpdates = Optional.of(new UpdateInfoDto(inst1, list1));
        Optional<UpdateInfoDto> secondUpdates = Optional.of(new UpdateInfoDto(inst2, list2));
        Optional<UpdateInfoDto> thirdUpdates = Optional.empty();

        when(urlService.getUrls(0L, 2)).thenReturn(firstBatch);

        when(urlService.getUrls(2L, 2)).thenReturn(secondBatch);

        when(urlService.getUrls(3L, 2)).thenReturn(thirdBatch);

        when(clientAdapter.getUpdate(first)).thenReturn(firstUpdates);
        when(clientAdapter.getUpdate(second)).thenReturn(secondUpdates);
        when(clientAdapter.getUpdate(third)).thenReturn(thirdUpdates);

        doNothing().when(urlService).updateLastTimeUpdated(anyString(), any(Instant.class));

        // Act
        updateCheckerService.checkUpdate();

        // Assert
        verify(botClient).sendUpdate(dto1);
        verify(botClient).sendUpdate(dto2);
    }
}
