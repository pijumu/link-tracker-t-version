package backend.academy.scrapper.service.notification;

import backend.academy.dto.dto.LinkUpdateDto;
import backend.academy.scrapper.bot.BotClient;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RestNotificationService implements NotificationService {
    private final BotClient botClient;

    @Override
    public void notify(List<LinkUpdateDto> updates) {
        updates.forEach(botClient::sendUpdate);
    }
}
