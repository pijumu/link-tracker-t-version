package backend.academy.bot.test.integration;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import backend.academy.bot.config.RedisConfig;
import backend.academy.bot.repository.RedisCacheListCommandRepository;
import backend.academy.bot.scrapper.LinkScrapperClient;
import backend.academy.bot.service.data.LinkScrapperService;
import backend.academy.bot.test.integration.data.RedisTestContainer;
import backend.academy.dto.dto.AddLinkRequest;
import backend.academy.dto.dto.LinkResponse;
import backend.academy.dto.dto.ListLinksResponse;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.data.redis.RedisAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;
import org.testcontainers.junit.jupiter.Testcontainers;

@SpringBootTest(
        classes = {
            RedisConfig.class,
            RedisCacheListCommandRepository.class,
            LinkScrapperService.class,
            LinkScrapperClient.class,
            RedisAutoConfiguration.class
        })
@Testcontainers
public class RedisCacheListCommandTest extends RedisTestContainer {
    @MockitoBean
    LinkScrapperClient linkScrapperClient;

    @Autowired
    LinkScrapperService linkScrapperService;

    @MockitoSpyBean
    RedisCacheListCommandRepository redisCacheListCommandRepository;

    @Test
    @DisplayName("Проверка сохранения /list запроса. Позитивный сценарий.")
    void test1() {
        // Arrange
        Long chatId = 1L;
        LinkResponse requestFirst =
                new LinkResponse(1L, "myUrlFirst", List.of("tag1", "tag2"), List.of("filter1", "filter2"));

        LinkResponse requestSecond =
                new LinkResponse(2L, "myUrlSecond", List.of("tag1", "tag2"), List.of("filter1", "filter2"));

        ListLinksResponse links = new ListLinksResponse(List.of(requestFirst, requestSecond), 2);
        when(linkScrapperClient.getLinks(chatId, Collections.emptyList())).thenReturn(links);

        // Act
        var result1 = linkScrapperService.getLinks(chatId, Collections.emptyList());
        var result2 = linkScrapperService.getLinks(chatId, Collections.emptyList());

        // Assert
        verify(redisCacheListCommandRepository).put(chatId, links);
        verify(redisCacheListCommandRepository, times(2)).get(chatId);

        Assertions.assertEquals(result1, result2);
    }

    @Test
    @DisplayName("Проверка инвалидации /list запроса после добавления новой ссылки. Позитивный сценарий.")
    void test2() {
        // Arrange
        Long chatId = 2L;
        LinkResponse requestFirst =
                new LinkResponse(1L, "myUrlFirst", List.of("tag1", "tag2"), List.of("filter1", "filter2"));

        LinkResponse requestSecond =
                new LinkResponse(2L, "myUrlSecond", List.of("tag1", "tag2"), List.of("filter1", "filter2"));

        AddLinkRequest third = new AddLinkRequest("myUrlThird", List.of("tag1", "tag2"), List.of("filter1", "filter2"));

        ListLinksResponse links = new ListLinksResponse(List.of(requestFirst, requestSecond), 2);
        when(linkScrapperClient.getLinks(chatId, Collections.emptyList())).thenReturn(links);
        when(linkScrapperClient.getLinks(chatId, Collections.emptyList())).thenReturn(links);

        // Act
        var result1 = linkScrapperService.getLinks(chatId, Collections.emptyList());
        linkScrapperService.addLink(chatId, third);
        var result2 = linkScrapperService.getLinks(chatId, Collections.emptyList());

        // Assert
        verify(redisCacheListCommandRepository, times(2)).put(chatId, links);
        verify(redisCacheListCommandRepository, times(2)).get(chatId);

        Assertions.assertEquals(result1, result2);
    }
}
