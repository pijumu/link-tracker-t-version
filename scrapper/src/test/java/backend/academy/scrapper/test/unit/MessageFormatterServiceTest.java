package backend.academy.scrapper.test.unit;

import backend.academy.scrapper.client.UpdateDto;
import backend.academy.scrapper.client.UpdateType;
import backend.academy.scrapper.service.checker.MessageFormatterService;
import java.time.Instant;
import java.util.List;
import java.util.TimeZone;
import org.apache.commons.lang3.tuple.Pair;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class MessageFormatterServiceTest {
    static {
        TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
    }

    MessageFormatterService messageFormatterService = new MessageFormatterService();

    @Test
    @DisplayName("Проверка форматирования сообщения. Позитивный сценарий")
    void test1() {
        // Arrange
        String expectedMessage = "Issue: myTopic\n" + "Пользователь: myUser\n"
                + "Время создания: 2025-06-06 12:34:56\n"
                + "Превью: i forgot what this field is";
        var update = new UpdateDto(
                1L,
                "url",
                "myTopic",
                "myUser",
                Instant.parse("2025-06-06T12:34:56Z"),
                Pair.of(UpdateType.GITHUB_ISSUE, "i forgot what this field is"));

        // Act
        var result = messageFormatterService.formUpdateMessageDto(update, List.of(1L, 2L, 3L));

        // Assert
        Assertions.assertEquals(expectedMessage, result.description());
    }
}
