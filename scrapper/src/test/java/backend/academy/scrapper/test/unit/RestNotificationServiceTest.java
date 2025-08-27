package backend.academy.scrapper.test.unit;

import static org.mockito.Mockito.verify;

import backend.academy.dto.dto.LinkUpdateDto;
import backend.academy.scrapper.bot.BotClient;
import backend.academy.scrapper.domain.dto.UpdateWithMessageDto;
import backend.academy.scrapper.service.data.UrlService;
import backend.academy.scrapper.service.notification.RestNotificationService;
import java.time.Instant;
import java.util.List;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class RestNotificationServiceTest {
    @Spy
    BotClient botClient;

    @Mock
    UrlService urlService;

    @InjectMocks
    RestNotificationService restNotificationService;

    @Test
    @DisplayName("Проверка отправки сообщения rest сервисом. Позитивный сценарий")
    void test1() {
        // Arrange
        var update =
                new UpdateWithMessageDto(1L, "myUrl", Instant.parse("2025-06-06T12:34:56Z"), "myDesc", List.of(1L, 2L));
        var toNotify = List.of(List.of(update));

        ArgumentCaptor<LinkUpdateDto> captor = ArgumentCaptor.forClass(LinkUpdateDto.class);

        // Act
        restNotificationService.notify(toNotify);

        // Assert
        verify(botClient).sendUpdate(captor.capture());
        Assertions.assertEquals(captor.getValue().description(), "myDesc");
    }
}
