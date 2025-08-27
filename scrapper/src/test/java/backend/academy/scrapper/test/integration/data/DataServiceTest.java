package backend.academy.scrapper.test.integration.data;

import static org.junit.Assert.assertThrows;

import backend.academy.dto.dto.AddLinkRequest;
import backend.academy.dto.dto.RemoveLinkRequest;
import backend.academy.scrapper.exception.AlreadyTrackedUrlException;
import backend.academy.scrapper.service.data.ChatService;
import backend.academy.scrapper.service.data.LinkService;
import backend.academy.scrapper.service.data.UrlService;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;

@SpringBootTest(properties = {"app.data-access=jdbc"})
public class DataServiceTest extends PostgresTestContainer {
    @Autowired
    JdbcTemplate jdbcTemplate;

    @Autowired
    ChatService chatService;

    @Autowired
    LinkService linkService;

    @Autowired
    UrlService urlService;

    @Test
    @DisplayName("ChatService. Добавление пользователя. Позитивный сценарий.")
    void test1() {
        // Act
        chatService.registerChat(1L);

        // Assert
        int cnt = jdbcTemplate.queryForObject("select count(*) from chat where chat.id = ?", Integer.class, 1L);
        Assertions.assertEquals(cnt, 1);
    }

    @Test
    @DisplayName("ChatService. Удаление пользователя. Позитивный сценарий.")
    void test2() {
        // Act
        chatService.registerChat(2L);
        int cnt1 = jdbcTemplate.queryForObject("select count(*) from chat where chat.id = ?", Integer.class, 2L);

        chatService.removeChat(2L);
        int cnt2 = jdbcTemplate.queryForObject("select count(*) from chat where chat.id = ?", Integer.class, 2L);

        // Assert
        Assertions.assertEquals(cnt1, 1);
        Assertions.assertEquals(cnt2, 0);
    }

    @Test
    @DisplayName("ChatService. Удаление пользователя. Негативный сценарий.")
    void test3() {
        // Act
        boolean isReg = chatService.isRegisteredChat(3L);
        chatService.registerChat(3L);
        boolean isReg2 = chatService.isRegisteredChat(3L);
        chatService.removeChat(3L);
        boolean isReg3 = chatService.isRegisteredChat(3L);

        // Assert
        Assertions.assertFalse(isReg);
        Assertions.assertTrue(isReg2);
        Assertions.assertFalse(isReg3);
    }

    @Test
    @DisplayName("LinkService. Добавление ссылки. Позитивный сценарий.")
    void test4() {
        // Arrange
        Long chatId = 4L;

        // Act
        chatService.registerChat(chatId);
        linkService.addLink(
                chatId,
                new AddLinkRequest("https://github.com/rufy/ray", List.of("tag1", "tag2"), Collections.emptyList()));
        var result = linkService.getLinks(chatId);

        // Assert
        Assertions.assertEquals(1, result.size());
        Assertions.assertEquals(
                "https://github.com/rufy/ray", result.links().getFirst().url());
    }

    @Test
    @DisplayName("LinkService. Добавление уже отслеживаемой ссылки. Негативный сценарий.")
    void test5() {
        // Arrange
        Long chatId = 5L;

        // Act
        chatService.registerChat(chatId);
        linkService.addLink(
                chatId,
                new AddLinkRequest("https://github.com/rufy/ray", List.of("tag1", "tag2"), Collections.emptyList()));

        // Assert
        assertThrows(AlreadyTrackedUrlException.class, () -> {
            linkService.addLink(
                    chatId,
                    new AddLinkRequest(
                            "https://github.com/rufy/ray", List.of("tag1", "tag2"), Collections.emptyList()));
        });
    }

    @Test
    @DisplayName("LinkService. Удаление ссылки. Негативный сценарий.")
    void test6() {
        // Arrange
        Long chatId = 6L;

        // Act
        chatService.registerChat(chatId);
        linkService.addLink(
                chatId,
                new AddLinkRequest("https://github.com/rufy/ray", List.of("tag1", "tag2"), Collections.emptyList()));
        var result = linkService.getLinks(chatId).size();

        linkService.removeLink(chatId, new RemoveLinkRequest("https://github.com/rufy/ray"));
        var result2 = linkService.getLinks(chatId).size();

        // Assert
        Assertions.assertEquals(1, result);
        Assertions.assertEquals(0, result2);
    }

    @Test
    @DisplayName(
            "UrlService. Сохранение метаинформации о ссылке при добавление. Переиспользование метаинформации при последующих обновлениях. Негативный сценарий.")
    void test7() {
        // Arrange
        Long chatId1 = 7L;
        Long chatId2 = 8L;

        // Act
        chatService.registerChat(chatId1);
        chatService.registerChat(chatId2);
        linkService.addLink(
                chatId1,
                new AddLinkRequest("https://github.com/rufy/ray", List.of("tag1", "tag2"), Collections.emptyList()));

        linkService.addLink(
                chatId2,
                new AddLinkRequest("https://github.com/rufy/ray", List.of("tag1", "tag2"), Collections.emptyList()));

        var result = urlService.getUrls(0L, 100);

        // Assert
        Assertions.assertEquals(1, result.size());
    }

    @Test
    @DisplayName(
            "UrlService. Сохранение метаинформации о ссылке при добавление. Переиспользование метаинформации при последующих обновлениях. Негативный сценарий.")
    void test8() {
        // Arrange
        Long chatId1 = 9L;

        // Act
        chatService.registerChat(chatId1);
        linkService.addLink(
                chatId1,
                new AddLinkRequest("https://github.com/rufyyy/ray", List.of("tag1", "tag2"), Collections.emptyList()));

        var result = urlService.getUrls(0L, 100);

        // Assert
        Assertions.assertEquals(2, result.size());
        Assertions.assertEquals(result.get(1).url(), "https://github.com/rufyyy/ray");
    }
}
