package backend.academy.scrapper.test.unit;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import backend.academy.dto.validator.UrlType;
import backend.academy.scrapper.client.ExternalClientManager;
import backend.academy.scrapper.client.util.UpdateDto;
import backend.academy.scrapper.parser.ParsedUrl;
import backend.academy.scrapper.repository.InMemoryLinkRepository;
import backend.academy.scrapper.service.NotificationService;
import backend.academy.scrapper.service.UpdateCheckerService;
import backend.academy.scrapper.service.UrlParserService;
import java.time.Instant;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class UpdateCheckerTest {
    @Mock
    ExternalClientManager externalClientManager;

    @Mock
    InMemoryLinkRepository inMemoryLinkRepository;

    @Mock
    NotificationService notificationService;

    @Mock
    UrlParserService urlParserService;

    @InjectMocks
    UpdateCheckerService updateCheckerService;

    @Test
    @DisplayName("Проверка updateChecker. Позитивный сценарий.")
    void test1() {
        // PreArrange
        String url1 = "https://url-first.com";
        String url2 = "https://url-second.com";
        Set<String> urls = new HashSet<>();
        urls.add(url1);
        urls.add(url2);
        ParsedUrl first = new ParsedUrl(UrlType.GITHUB, Map.of());
        ParsedUrl second = new ParsedUrl(UrlType.GITHUB, Map.of());
        UpdateDto firstDto = new UpdateDto("first", Instant.parse("2019-07-18T13:47:52.234Z"));
        UpdateDto secondDto = new UpdateDto("second", Instant.parse("2009-07-18T13:47:52.234Z"));

        // Arrange
        when(inMemoryLinkRepository.getUrls()).thenReturn(urls);
        when(urlParserService.parse(url1)).thenReturn(first);
        when(urlParserService.parse(url2)).thenReturn(second);

        when(externalClientManager.getUpdate(first)).thenReturn(firstDto);
        when(externalClientManager.getUpdate(second)).thenReturn(secondDto);

        when(inMemoryLinkRepository.getFollowers(eq(url1), any())).thenReturn(List.of(1L, 2L));
        when(inMemoryLinkRepository.getFollowers(eq(url2), any())).thenReturn(List.of());

        // Act
        updateCheckerService.checkUpdates();

        // Assert
        verify(notificationService).notify(eq(url1), eq("Произошло обновление по " + url1), eq(List.of(1L, 2L)));
    }
}
