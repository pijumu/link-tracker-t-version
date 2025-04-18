package backend.academy.scrapper.service;

import backend.academy.dto.dto.LinkUpdate;
import backend.academy.scrapper.client.BotClient;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class NotificationService {
    private final BotClient botClient;

    public void notify(String url, String message, List<Long> followersToUpdate) {
        LinkUpdate linkUpdate = new LinkUpdate(url, message, followersToUpdate);
        botClient.sendUpdate(linkUpdate);
    }
}
