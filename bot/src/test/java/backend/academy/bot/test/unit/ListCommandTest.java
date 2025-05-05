package backend.academy.bot.test.unit;

import static org.mockito.Mockito.when;

import backend.academy.bot.fsm.transition.command.ListCommand;
import backend.academy.bot.service.ScrapperClient;
import backend.academy.dto.dto.LinkResponse;
import backend.academy.dto.dto.ListLinksResponse;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class ListCommandTest {
    @Mock
    ScrapperClient scrapperClient;

    @InjectMocks
    ListCommand listCommand;

    @Test
    @DisplayName("Проверка форматирования 2 ссылки. Модульный сценарий. Позитивный сценарий.")
    void test1() {
        // Arrange
        Long chatId = 1L;
        String expected =
                """
            Список отслеживаемых ссылок:
            🆔 ID: 1
            🔗 Ссылка: https://github.com/pijumu/ASL-Recognition-Model
            🏷 Теги: не указаны
            🎛 Фильтры: filter1, filter2

            🆔 ID: 1
            🔗 Ссылка: https://github.com/TaTaTa/PuPuPU
            🏷 Теги: tag1, tag2
            🎛 Фильтры: не указаны

            """;
        when(scrapperClient.getLinks(chatId))
                .thenReturn(new ListLinksResponse(
                        List.of(
                                new LinkResponse(
                                        1L,
                                        "https://github.com/pijumu/ASL-Recognition-Model",
                                        Collections.emptyList(),
                                        List.of("filter1", "filter2")),
                                new LinkResponse(
                                        1L,
                                        "https://github.com/TaTaTa/PuPuPU",
                                        List.of("tag1", "tag2"),
                                        Collections.emptyList())),
                        2));

        // Act
        String message = listCommand.formMessageFromIdleState(chatId);

        // Assert
        Assertions.assertEquals(expected, message);
    }

    @Test
    @DisplayName("Проверка форматирования без ссылок. Модульный сценарий. Позитивный сценарий.")
    void test2() {
        // Arrange
        Long chatId = 1L;
        String expected = "Вы не отслеживаете ссылок.";
        when(scrapperClient.getLinks(chatId)).thenReturn(new ListLinksResponse(Collections.emptyList(), 0));

        // Act
        String message = listCommand.formMessageFromIdleState(chatId);

        // Assert
        Assertions.assertEquals(expected, message);
    }
}
