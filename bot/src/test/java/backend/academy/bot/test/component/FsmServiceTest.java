package backend.academy.bot.test.component;

import static org.mockito.Mockito.when;

import backend.academy.dto.dto.LinkResponse;
import backend.academy.dto.dto.ListLinksResponse;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class FsmServiceTest extends FsmServiceContextTest {
    @Test
    @DisplayName("Проверка ответа fsm на неизвестную команду из Idle состояния. Негативный сценарий")
    void test1() {
        // Arrange
        String expected = "Неизвестная команда. Используйте /help команду.";

        Long chatId = 1L;
        String invalidCommand = "/invalid";
        when(scrapperClient.isRegisteredChat(chatId)).thenReturn(true);

        // Act
        String message = fsmService.handle(invalidCommand, chatId);

        // Assert
        Assertions.assertEquals(expected, message);
    }

    @Test
    @DisplayName("Проверка ответа fsm на команду /help из Idle состояния. Позитивный сценарий")
    void test2() {
        // Arrange
        String expected =
                """
            Доступные шаблоны ссылок:
            https://github.com/{owner}/{repo}
            https://stackoverflow.com/questions/{questionId}

            Доступные команды:
            /list - список всех ссылок
            /start - регистрация чата
            /track - добавить ссылку для отслеживания
            /untrack - остановить отслеживание ссылки
            /help - список команд
            """;

        Long chatId = 2L;
        String command = "/help";
        when(scrapperClient.isRegisteredChat(chatId)).thenReturn(true);

        // Act
        String message = fsmService.handle(command, chatId);

        // Assert
        Assertions.assertEquals(expected, message);
    }

    @Test
    @DisplayName("Проверка ответа fsm на команду /help из NotRegistered состояния. Позитивный сценарий")
    void test3() {
        // Arrange
        String expected =
                """
            Доступные шаблоны ссылок:
            https://github.com/{owner}/{repo}
            https://stackoverflow.com/questions/{questionId}

            Доступные команды:
            /list - список всех ссылок
            /start - регистрация чата
            /track - добавить ссылку для отслеживания
            /untrack - остановить отслеживание ссылки
            /help - список команд
            """;

        Long chatId = 3L;
        String command = "/help";
        when(scrapperClient.isRegisteredChat(chatId)).thenReturn(false);

        // Act
        String message = fsmService.handle(command, chatId);

        // Assert
        Assertions.assertEquals(expected, message);
    }

    @Test
    @DisplayName(
            "Проверка форматирования ответа fsm на команду /list из Idle состояния при отслеживание 2 ссылок. Компонентный сценарий. Позитивный сценарий")
    void test4() {
        // Arrange
        String expected =
                """
            Список отслеживаемых ссылок:
            🆔 ID: 4
            🔗 Ссылка: https://github.com/pijumu/ASL-Recognition-Model
            🏷 Теги: не указаны
            🎛 Фильтры: filter1, filter2

            🆔 ID: 4
            🔗 Ссылка: https://github.com/TaTaTa/PuPuPU
            🏷 Теги: tag1, tag2
            🎛 Фильтры: не указаны

            """;

        Long chatId = 4L;
        String command = "/list";
        when(scrapperClient.isRegisteredChat(chatId)).thenReturn(true);
        when(scrapperClient.getLinks(chatId))
                .thenReturn(new ListLinksResponse(
                        List.of(
                                new LinkResponse(
                                        4L,
                                        "https://github.com/pijumu/ASL-Recognition-Model",
                                        Collections.emptyList(),
                                        List.of("filter1", "filter2")),
                                new LinkResponse(
                                        4L,
                                        "https://github.com/TaTaTa/PuPuPU",
                                        List.of("tag1", "tag2"),
                                        Collections.emptyList())),
                        2));

        // Act
        String message = fsmService.handle(command, chatId);

        // Assert
        Assertions.assertEquals(expected, message);
    }

    @Test
    @DisplayName(
            "Проверка форматирования ответа fsm на команду /list из Idle состояния без отслеживания ссылок. Компонентный сценарий. Позитивный сценарий")
    void test5() {
        // Arrange
        String expected = "Вы не отслеживаете ссылок.";

        Long chatId = 5L;
        String command = "/list";
        when(scrapperClient.isRegisteredChat(chatId)).thenReturn(true);
        when(scrapperClient.getLinks(chatId)).thenReturn(new ListLinksResponse(Collections.emptyList(), 0));

        // Act
        String message = fsmService.handle(command, chatId);

        // Assert
        Assertions.assertEquals(expected, message);
    }

    @Test
    @DisplayName("Проверка сценария добавления /track из Idle состояния. Позитивный сценарий")
    void test6() {
        // Arrange
        String expected1 = "Введите ссылку.";
        String expected2 = "Введите фильтры через пробел. Используйте '-', если фильтры не нужны.";
        String expected3 = "Введите теги через пробел. Используйте '-', если фильтры не нужны.";
        String expected4 = "Ссылка успешна добавлена.";

        Long chatId = 6L;
        String command = "/track";
        String inputUrl = "https://github.com/pijumu/ASL-Recognition-Model";
        String inputFilters = "filter1";
        String inputTags = "-";
        when(scrapperClient.isRegisteredChat(chatId)).thenReturn(true);

        // Act
        String message1 = fsmService.handle(command, chatId);
        String message2 = fsmService.handle(inputUrl, chatId);
        String message3 = fsmService.handle(inputFilters, chatId);
        String message4 = fsmService.handle(inputTags, chatId);

        // Assert
        Assertions.assertEquals(expected1, message1);
        Assertions.assertEquals(expected2, message2);
        Assertions.assertEquals(expected3, message3);
        Assertions.assertEquals(expected4, message4);
    }

    @Test
    @DisplayName("Проверка сценария добавления /track из Idle состояния. Ввод невалидной ссылки. Негативный сценарий")
    void test7() {
        // Arrange
        String expected1 = "Введите ссылку.";
        String expected2 = "Ссылка не соответствует формату валидной ссылки. Используйте /help команду.";

        Long chatId = 7L;
        String command = "/track";
        String inputUrl = "https://invalidLink.com";
        when(scrapperClient.isRegisteredChat(chatId)).thenReturn(true);

        // Act
        String message1 = fsmService.handle(command, chatId);
        String message2 = fsmService.handle(inputUrl, chatId);

        // Assert
        Assertions.assertEquals(expected1, message1);
        Assertions.assertEquals(expected2, message2);
    }
}
