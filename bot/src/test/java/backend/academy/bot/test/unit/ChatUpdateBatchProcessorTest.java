package backend.academy.bot.test.unit;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;

import backend.academy.bot.domain.ChatUpdate;
import backend.academy.bot.service.MessageSenderService;
import backend.academy.bot.service.update.digest.ChatUpdateBatchProcessor;
import com.pengrad.telegrambot.TelegramBot;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class ChatUpdateBatchProcessorTest {

    @Mock
    TelegramBot bot;

    MessageSenderService sender;

    ChatUpdateBatchProcessor chatUpdateBatchProcessor;

    @BeforeEach
    void setUp() {
        sender = spy(new MessageSenderService(bot));
        chatUpdateBatchProcessor = new ChatUpdateBatchProcessor(sender);
    }

    @Test
    @DisplayName("Проверка формирования digest. Позитивный сценарий.")
    void test1() {
        // Arrange
        String expected =
                """
            🔔 Обновление по ссылке:
            https://example.com/page1

            Первое сообщение
            Второе сообщение

            🔔 Обновление по ссылке:
            https://another-site.net/item

            Третье сообщение""";
        Long chatId = 1L;

        ChatUpdate update1 = new ChatUpdate(chatId, 1L, "https://example.com/page1", "Первое сообщение");
        ChatUpdate update2 = new ChatUpdate(chatId, 1L, "https://example.org/page1", "Второе сообщение");
        ChatUpdate update3 = new ChatUpdate(chatId, 21L, "https://another-site.net/item", "Третье сообщение");
        List<ChatUpdate> updates = List.of(update1, update2, update3);

        // Act
        chatUpdateBatchProcessor.process(updates);

        // Assert
        verify(sender).sendMessage(eq(expected), eq(chatId));
    }
}
