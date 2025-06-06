package backend.academy.bot.test.unit;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import backend.academy.bot.domain.ChatUpdate;
import backend.academy.bot.service.update.digest.ChatUpdateBatchProcessor;
import backend.academy.bot.service.update.digest.RedisCacheUpdateRepository;
import java.util.List;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.Cursor;
import org.springframework.data.redis.core.ListOperations;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;

@ExtendWith(MockitoExtension.class)
public class RedisCacheUpdateRepositoryTest {
    @Mock
    private RedisTemplate<String, Object> redisTemplate;

    @Mock
    private ChatUpdateBatchProcessor processor;

    @Mock
    private ListOperations<String, Object> mockListOps;

    @InjectMocks
    private RedisCacheUpdateRepository repository;

    @Test
    @DisplayName("Проверка добавление. Позитивный сценарий.")
    void test1() {
        // Arrange
        Long chatId = 1L;
        ChatUpdate update1 =
                new ChatUpdate(chatId, 201L, "https://example.com/chat/1001", "Привет! Это первое сообщение.");
        var keyCaptor = ArgumentCaptor.forClass(String.class);
        var valueCaptor = ArgumentCaptor.forClass(ChatUpdate.class);
        when(redisTemplate.opsForList()).thenReturn(mockListOps);

        // Act
        repository.put(chatId, update1);

        // Assert
        verify(redisTemplate.opsForList(), times(1)).rightPush(keyCaptor.capture(), valueCaptor.capture());
        Assertions.assertEquals("update:1", keyCaptor.getValue());
        Assertions.assertEquals(update1, valueCaptor.getValue());
    }

    @Test
    @DisplayName("Проверка процессинга. Позитивный сценарий.")
    void test2() {
        // Arrange
        Long chatId1 = 1L;
        Long chatId2 = 2L;
        ChatUpdate update1 =
                new ChatUpdate(chatId1, 201L, "https://example.com/chat/1001", "Привет! Это первое сообщение.");

        ChatUpdate update2 =
                new ChatUpdate(chatId1, 2002L, "https://example.com/chat/1002", "Второе сообщение для другого чата.");

        ChatUpdate update3 =
                new ChatUpdate(chatId2, 2003L, "https://example.com/chat/1003", "Третье сообщение - проверка записи.");

        Cursor<String> cursor = mock(Cursor.class);
        when(redisTemplate.scan(any())).thenReturn(cursor);
        when(cursor.hasNext()).thenReturn(true, true, false);
        when(cursor.next()).thenReturn("update:1", "update:2");

        var list1 = List.of(update1, update2);
        var list2 = List.of(update3);

        when(redisTemplate.executePipelined(any(RedisCallback.class))).thenReturn(List.of(list1, list2));

        // Act
        repository.processCollectedUpdates();

        // Assert
        verify(processor, times(1)).process(list1);
        verify(processor, times(1)).process(list2);
    }
}
