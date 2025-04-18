package backend.academy.bot.test;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import backend.academy.bot.command.commands.ListCommand;
import backend.academy.bot.service.ChatSessionService;
import backend.academy.bot.service.ScrapperClient;
import backend.academy.dto.dto.LinkResponse;
import backend.academy.dto.dto.ListLinksResponse;
import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.request.SendMessage;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

@ExtendWith(MockitoExtension.class)
public class ListCommandTest {
    @Mock
    TelegramBot bot;

    @Mock
    ScrapperClient scrapperClient;

    @Mock
    ChatSessionService chatSessionService;

    @InjectMocks
    ListCommand listCommand;

    @Test
    @DisplayName("Проверка форматирования /list. Позитивный сценарий")
    void test1() {
        // Arrange
        Long chatId = 123L;
        List<LinkResponse> links = List.of(
                new LinkResponse(1L, "https://example.com/1", List.of("news"), List.of("safe")),
                new LinkResponse(2L, "https://example.com/2", List.of("tech"), Collections.emptyList()));
        String expectedMessage =
                """
            Список отслеживаемых ссылок:

            🆔 ID: 1
            🔗 Ссылка: https://example.com/1
            🏷 Теги: news
            🎛 Фильтры: safe

            🆔 ID: 2
            🔗 Ссылка: https://example.com/2
            🏷 Теги: tech
            🎛 Фильтры: не указаны

            """;
        when(scrapperClient.getLinks(chatId)).thenReturn(ResponseEntity.ok(new ListLinksResponse(links, links.size())));
        ArgumentCaptor<SendMessage> messageCaptor = ArgumentCaptor.forClass(SendMessage.class);

        // Act
        listCommand.handle(chatId, "IDLE", "/list");

        // Assert
        verify(bot).execute(messageCaptor.capture());
        SendMessage message = messageCaptor.getValue();
        Assertions.assertEquals(expectedMessage, message.getParameters().get("text"));
    }
}
