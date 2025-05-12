package backend.academy.bot.test.unit;

import static backend.academy.bot.fsm.State.IDLE;

import backend.academy.bot.converter.ChatContextToAddLinkRequestConverter;
import backend.academy.bot.converter.ChatContextToUpdateTagsRequestConverter;
import backend.academy.bot.domain.ChatContext;
import backend.academy.dto.dto.AddLinkRequest;
import backend.academy.dto.dto.UpdateTagsRequest;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class ConverterTest {
    private static ChatContextToAddLinkRequestConverter converterAddLink;
    private static ChatContextToUpdateTagsRequestConverter converterUpdateTags;

    @BeforeAll
    static void init() {
        // Arrange
        converterAddLink = new ChatContextToAddLinkRequestConverter();
        converterUpdateTags = new ChatContextToUpdateTagsRequestConverter();
    }

    @Test
    @DisplayName("Проверка ToAddLink конвертера. Введена только ссылка. Позитивный сценарий.")
    void test1() {
        // Arrange a little bit more
        Map<String, Object> attributes = new HashMap<>();
        String url = "https://example.com";
        attributes.put("url", url);
        ChatContext context = ChatContext.builder(IDLE).attributes(attributes).build();

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
        Map<String, Object> attributes = new HashMap<>();
        String url = "https://example.com";
        List<String> tags = List.of("tags1", "tag2");
        attributes.put("url", url);
        attributes.put("tags", tags);
        ChatContext context = ChatContext.builder(IDLE).attributes(attributes).build();

        // Act
        AddLinkRequest request = converterAddLink.convert(context);

        // Assert
        Assertions.assertEquals(request.url(), url);
        Assertions.assertEquals(request.filters(), Collections.emptyList());
        Assertions.assertEquals(request.tags().size(), 2);
        Assertions.assertTrue(request.tags().contains("tags1"));
        Assertions.assertTrue(request.tags().contains("tag2"));
    }

    @Test
    @DisplayName("Проверка ToAddLink конвертера. Введена ссылка и теги и фильтры. Позитивный сценарий.")
    void test3() {
        // Arrange a little bit more
        Map<String, Object> attributes = new HashMap<>();
        String url = "https://example.com";
        List<String> tags = List.of("tags1", "tag2");
        List<String> filters = List.of("filter1");
        attributes.put("url", url);
        attributes.put("tags", tags);
        attributes.put("filters", filters);
        ChatContext context = ChatContext.builder(IDLE).attributes(attributes).build();

        // Act
        AddLinkRequest request = converterAddLink.convert(context);

        // Assert
        Assertions.assertEquals(request.url(), url);
        Assertions.assertEquals(request.tags().size(), 2);
        Assertions.assertTrue(request.tags().contains("tags1"));
        Assertions.assertTrue(request.tags().contains("tag2"));
        Assertions.assertEquals(request.filters().size(), 1);
        Assertions.assertTrue(request.filters().contains("filter1"));
    }

    @Test
    @DisplayName("Проверка ToUpdateTags конвертера. Введены теги. Позитивный сценарий.")
    void test4() {
        // Arrange a little bit more
        Map<String, Object> attributes = new HashMap<>();
        List<String> tags = List.of("tags1", "tag2");
        attributes.put("tags", tags);
        ChatContext context = ChatContext.builder(IDLE).attributes(attributes).build();

        // Act
        UpdateTagsRequest request = converterUpdateTags.convert(context);

        // Assert
        Assertions.assertEquals(request.tags(), tags);
    }
}
