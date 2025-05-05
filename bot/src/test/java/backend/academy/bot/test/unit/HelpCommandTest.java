package backend.academy.bot.test.unit;

import static org.mockito.Mockito.when;

import backend.academy.bot.fsm.transition.command.HelpCommand;
import backend.academy.bot.fsm.transition.command.ListCommand;
import backend.academy.bot.fsm.transition.command.StartCommand;
import backend.academy.bot.fsm.transition.command.TrackCommand;
import backend.academy.bot.fsm.transition.command.UntrackCommand;
import backend.academy.dto.validator.validators.GithubUrlValidator;
import backend.academy.dto.validator.validators.StackOverflowUrlValidator;
import java.util.List;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class HelpCommandTest {
    @Mock
    ListCommand listCommand;

    @Mock
    TrackCommand trackCommand;

    @Mock
    UntrackCommand untrackCommand;

    @Mock
    StartCommand startCommand;

    @Mock
    StackOverflowUrlValidator stackOverflowUrlValidator;

    @Mock
    GithubUrlValidator githubUrlValidator;

    HelpCommand helpCommand;

    @BeforeEach
    void init() {
        when(listCommand.getName()).thenReturn("/list");
        when(trackCommand.getName()).thenReturn("/track");
        when(untrackCommand.getName()).thenReturn("/untrack");
        when(startCommand.getName()).thenReturn("/start");

        when(listCommand.getDescription()).thenReturn("список всех ссылок");
        when(trackCommand.getDescription()).thenReturn("добавить ссылку для отслеживания");
        when(untrackCommand.getDescription()).thenReturn("остановить отслеживание ссылки");
        when(startCommand.getDescription()).thenReturn("регистрация чата");

        when(listCommand.getDescription()).thenReturn("список всех ссылок");
        when(trackCommand.getDescription()).thenReturn("добавить ссылку для отслеживания");
        when(untrackCommand.getDescription()).thenReturn("остановить отслеживание ссылки");
        when(startCommand.getDescription()).thenReturn("регистрация чата");

        when(githubUrlValidator.getPattern()).thenReturn("https://github.com/{owner}/{repo}");
        when(stackOverflowUrlValidator.getPattern()).thenReturn("https://stackoverflow.com/questions/{questionId}");
        helpCommand = new HelpCommand(
                List.of(listCommand, startCommand, trackCommand, untrackCommand),
                List.of(githubUrlValidator, stackOverflowUrlValidator));
    }

    @Test
    @DisplayName("Проверка сообщения команды /help. Модульный сценарий. Позитивный сценарий.")
    void test1() {
        // Arrange
        Long chatId = 1L;
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

        // Act
        String message = helpCommand.formMessageFromIdleState(chatId);

        // Assert
        Assertions.assertEquals(expected, message);
    }
}
