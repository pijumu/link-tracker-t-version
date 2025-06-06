package backend.academy.scrapper.test.unit;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import backend.academy.dto.dto.LinkUpdateDto;
import backend.academy.scrapper.config.properties.LinkUpdateEventProperties;
import backend.academy.scrapper.domain.dto.UpdateWithMessageDto;
import backend.academy.scrapper.service.data.UrlService;
import backend.academy.scrapper.service.notification.KafkaNotificationService;
import java.time.Instant;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.messaging.Message;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

@ExtendWith(MockitoExtension.class)
public class KafkaNotificationServiceTest {

    @Mock
    KafkaTemplate<Long, byte[]> kafkaTemplate;

    LinkUpdateEventProperties props;

    @Mock
    UrlService urlService;

    @Captor
    ArgumentCaptor<Message<LinkUpdateDto>> messageCaptor;

    ThreadPoolTaskExecutor executor;

    KafkaNotificationService kafkaService;

    @BeforeEach
    void setUp() {
        props = new LinkUpdateEventProperties("topic", 1, (short) 1);

        executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(1);
        executor.setMaxPoolSize(1);
        executor.initialize();

        kafkaService = new KafkaNotificationService(executor, kafkaTemplate, props, urlService);
    }

    @Test
    @DisplayName("Проверка отправки сообщения kafka сервисом. Позитивный сценарий")
    void test1() {
        // Arrange
        var update =
                new UpdateWithMessageDto(1L, "myUrl", Instant.parse("2025-06-06T12:34:56Z"), "myDesc", List.of(1L, 2L));
        var toNotify = List.of(List.of(update));
        CompletableFuture<SendResult<Long, byte[]>> future = CompletableFuture.completedFuture(mock(SendResult.class));
        when(kafkaTemplate.send(any(Message.class))).thenReturn(future);

        // Act
        kafkaService.notify(toNotify);

        // Assert
        verify(kafkaTemplate).send(messageCaptor.capture());
        Assertions.assertEquals(
                update.description(), messageCaptor.getValue().getPayload().description());
        Assertions.assertEquals(
                update.url(), messageCaptor.getValue().getPayload().url());
    }
}
