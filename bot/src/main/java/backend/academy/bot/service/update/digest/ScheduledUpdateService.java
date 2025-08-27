package backend.academy.bot.service.update.digest;

import backend.academy.bot.domain.ChatUpdate;
import backend.academy.bot.service.update.UpdateService;
import backend.academy.dto.dto.LinkUpdateDto;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
@ConditionalOnProperty(prefix = "app.notifications", value = "mode", havingValue = "digest")
@RequiredArgsConstructor
public class ScheduledUpdateService implements UpdateService {
    private final RedisCacheUpdateRepository updateRepository;

    @Override
    public void handleUpdate(LinkUpdateDto update) {
        update.tgChatIds()
                .forEach(chatId -> updateRepository.put(
                        chatId, new ChatUpdate(chatId, update.urlId(), update.url(), update.description())));
    }

    @Scheduled(cron = "${app.notifications.digest.cron}", zone = "${app.notifications.digest.zone}")
    public void notifyUser() {
        updateRepository.processCollectedUpdates();
    }
}
