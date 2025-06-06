package backend.academy.scrapper.service.notification;

import backend.academy.scrapper.bot.BotClient;
import backend.academy.scrapper.domain.dto.UpdateWithMessageDto;
import backend.academy.scrapper.service.data.UrlService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@ConditionalOnProperty(prefix = "app", value = "message-transport", havingValue = "http")
public class RestNotificationService implements NotificationService {
    private final BotClient botClient;
    private final UrlService urlService;

    @Override
    public void notify(List<List<UpdateWithMessageDto>> updates) {
        updates.forEach(updatesByUrlId -> updatesByUrlId.forEach(update -> {
            var dto = NotificationService.toLinkUpdateDto(update);
            urlService.updateLastTimeUpdated(update.url(), update.createdAt());
            botClient.sendUpdate(dto);
        }));
    }
}
