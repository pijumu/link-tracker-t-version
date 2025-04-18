package backend.academy.bot.test;

import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import backend.academy.bot.service.ChatSessionService;
import backend.academy.bot.service.HandlerService;
import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.Chat;
import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.model.Update;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class HandlerServiceTest {

    @Mock
    TelegramBot bot;

    @Mock
    ChatSessionService chatSessionService;

    @InjectMocks
    HandlerService handlerService;

    @Test
    @DisplayName("Проверка поведения при неизвестной комнады. Негативный сценарий")
    void test1() {
        // Arrange
        Long chatId = 123L;
        String invalidCommand = "/invalid";
        Update update = mock(Update.class);
        Message message = mock(Message.class);
        Chat chat = mock(Chat.class);

        when(update.message()).thenReturn(message);
        when(message.chat()).thenReturn(chat);
        when(message.text()).thenReturn(invalidCommand);
        when(chat.id()).thenReturn(chatId);
        when(chatSessionService.getState(chatId)).thenReturn("IDLE");

        // Act
        handlerService.handle(update);

        // Assert
        verify(bot)
                .execute(argThat(sendMessage ->
                        sendMessage.getParameters().get("chat_id").equals(chatId)
                                && sendMessage.getParameters().get("text").equals("Неизвестная команда.")));
    }
}
