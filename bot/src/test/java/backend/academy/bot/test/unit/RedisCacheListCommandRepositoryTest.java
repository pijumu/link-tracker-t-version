package backend.academy.bot.test.unit;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import backend.academy.bot.repository.RedisCacheListCommandRepository;
import backend.academy.dto.dto.LinkResponse;
import backend.academy.dto.dto.ListLinksResponse;
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
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

@ExtendWith(MockitoExtension.class)
public class RedisCacheListCommandRepositoryTest {
    @Mock
    RedisTemplate<String, Object> redisTemplate;

    @Mock
    ValueOperations<String, Object> valueOperations;

    @InjectMocks
    RedisCacheListCommandRepository repository;

    @Test
    @DisplayName("Проверка метода добавление. Позитивный сценарий.")
    void test1() {
        // Arrange
        Long chatId = 1L;
        var links = new ListLinksResponse(
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
                2);
        var keyCaptor = ArgumentCaptor.forClass(String.class);
        var valueCaptor = ArgumentCaptor.forClass(ListLinksResponse.class);

        when(redisTemplate.opsForValue()).thenReturn(valueOperations);

        // Act
        repository.put(chatId, links);

        // Assert
        verify(valueOperations).set(keyCaptor.capture(), valueCaptor.capture());

        Assertions.assertEquals("list:1", keyCaptor.getValue());
        Assertions.assertEquals(links, valueCaptor.getValue());
    }

    @Test
    @DisplayName("Проверка метода получения. Позитивный сценарий.")
    void test2() {
        // Arrange
        Long chatId = 1L;
        var links = new ListLinksResponse(
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
                2);
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        when(valueOperations.get("list:1")).thenReturn(links);

        // Act
        var result = repository.get(chatId);

        // Assert
        Assertions.assertEquals(links, result);
    }

    @Test
    @DisplayName("Проверка метода инвалидации. Позитивный сценарий.")
    void test3() {
        // Arrange
        Long chatId = 1L;
        var keyCaptor = ArgumentCaptor.forClass(String.class);

        // Act
        repository.invalidate(chatId);

        // Assert
        verify(redisTemplate).delete(keyCaptor.capture());
        Assertions.assertEquals("list:1", keyCaptor.getValue());
    }
}
