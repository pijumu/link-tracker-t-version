package backend.academy.bot.test.unit;

import backend.academy.bot.converter.ChatContextToAddLinkRequestConverter;
import backend.academy.bot.converter.ChatContextToRemoveLinkRequestConverter;
import backend.academy.bot.domain.ChatContext;
import backend.academy.bot.fsm.state.StateEntry;
import backend.academy.dto.dto.AddLinkRequest;
import backend.academy.dto.dto.RemoveLinkRequest;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class ConverterTest {
    private static ChatContextToAddLinkRequestConverter converterAddLink;
    private static ChatContextToRemoveLinkRequestConverter converterRemoveLink;

    @BeforeAll
    static void init() {
        // Arrange
        converterAddLink = new ChatContextToAddLinkRequestConverter();
        converterRemoveLink = new ChatContextToRemoveLinkRequestConverter();
    }

    @Test
    @DisplayName("Проверка ToAddLink конвертера. Введена только ссылка. Позитивный сценарий.")
    void test1() {
        // Arrange a little bit more
        Map<String, String> attributes = new HashMap<>();
        String url = "https://example.com";
        attributes.put("url", url);
        ChatContext context = ChatContext.fromStateAndAttributes(StateEntry.IDLE, attributes);

        // Act
        AddLinkRequest request = converterAddLink.convert(context);

        // Assert
        Assertions.assertEquals(request.url(), url);
        Assertions.assertEquals(request.filters(), Collections.emptyList());
        Assertions.assertEquals(request.tags(), Collections.emptyList());
    }

    @Test
    @DisplayName("Проверка ToAddLink конвертера. Введена ссылка и теги. Позитивный сценарий.")
    void test2() {
        // Arrange a little bit more
        Map<String, String> attributes = new HashMap<>();
        String url = "https://example.com";
        String tags = "tag1 tag2";
        attributes.put("url", url);
        attributes.put("tags", tags);
        ChatContext context = ChatContext.fromStateAndAttributes(StateEntry.IDLE, attributes);

        // Act
        AddLinkRequest request = converterAddLink.convert(context);

        // Assert
        Assertions.assertEquals(request.url(), url);
        Assertions.assertEquals(request.filters(), Collections.emptyList());
        Assertions.assertEquals(request.tags().size(), 2);
        Assertions.assertTrue(request.tags().contains("tag1"));
        Assertions.assertTrue(request.tags().contains("tag2"));
    }

    @Test
    @DisplayName("Проверка ToAddLink конвертера. Введена ссылка и теги и фильтры. Позитивный сценарий.")
    void test3() {
        // Arrange a little bit more
        Map<String, String> attributes = new HashMap<>();
        String url = "https://example.com";
        String tags = "tag1 tag2";
        String filters = "filter1";
        attributes.put("url", url);
        attributes.put("tags", tags);
        attributes.put("filters", filters);
        ChatContext context = ChatContext.fromStateAndAttributes(StateEntry.IDLE, attributes);

        // Act
        AddLinkRequest request = converterAddLink.convert(context);

        // Assert
        Assertions.assertEquals(request.url(), url);
        Assertions.assertEquals(request.tags().size(), 2);
        Assertions.assertTrue(request.tags().contains("tag1"));
        Assertions.assertTrue(request.tags().contains("tag2"));
        Assertions.assertEquals(request.filters().size(), 1);
        Assertions.assertTrue(request.filters().contains("filter1"));
    }

    @Test
    @DisplayName("Проверка ToRemoveLink конвертера. Введена ссылка. Позитивный сценарий.")
    void test4() {
        // Arrange a little bit more
        Map<String, String> attributes = new HashMap<>();
        String url = "https://example.com";
        attributes.put("url", url);
        ChatContext context = ChatContext.fromStateAndAttributes(StateEntry.IDLE, attributes);

        // Act
        RemoveLinkRequest request = converterRemoveLink.convert(context);

        // Assert
        Assertions.assertEquals(request.url(), url);
    }
}
