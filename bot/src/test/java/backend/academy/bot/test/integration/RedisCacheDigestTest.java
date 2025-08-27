package backend.academy.bot.test.integration;

import static org.mockito.Mockito.verify;

import backend.academy.bot.config.RedisConfig;
import backend.academy.bot.domain.ChatUpdate;
import backend.academy.bot.service.MessageSenderService;
import backend.academy.bot.service.update.digest.ChatUpdateBatchProcessor;
import backend.academy.bot.service.update.digest.RedisCacheUpdateRepository;
import backend.academy.bot.test.integration.data.RedisTestContainer;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.data.redis.RedisAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;

@SpringBootTest(
        properties = "app.notifications.mode=digest",
        classes = {
            RedisConfig.class,
            RedisAutoConfiguration.class,
            RedisCacheUpdateRepository.class,
            ChatUpdateBatchProcessor.class,
            MessageSenderService.class,
        })
public class RedisCacheDigestTest extends RedisTestContainer {
    @MockitoBean
    MessageSenderService sender;

    @MockitoSpyBean
    ChatUpdateBatchProcessor processor;

    @Autowired
    RedisCacheUpdateRepository updateRepository;

    @Test
    @DisplayName("Проверка сохранения и вычитывания из redis. Позитивный сценарий.")
    void test1() {
        // Arrange
        Long chatId = 1L;

        ChatUpdate update1 =
                new ChatUpdate(chatId, 201L, "https://example.com/chat/1001", "Привет! Это первое сообщение.");

        ChatUpdate update2 =
                new ChatUpdate(chatId, 2002L, "https://example.com/chat/1002", "Второе сообщение для другого чата.");

        ChatUpdate update3 =
                new ChatUpdate(chatId, 2003L, "https://example.com/chat/1003", "Третье сообщение - проверка записи.");

        // Act
        updateRepository.put(chatId, update1);
        updateRepository.put(chatId, update2);
        updateRepository.put(chatId, update3);
        updateRepository.processCollectedUpdates();

        // Assert
        verify(processor).process(List.of(update1, update2, update3));
    }
}
