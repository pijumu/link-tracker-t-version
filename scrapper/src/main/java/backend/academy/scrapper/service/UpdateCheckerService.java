package backend.academy.scrapper.service;

import backend.academy.scrapper.client.ExternalClientManager;
import backend.academy.scrapper.client.util.UpdateDto;
import backend.academy.scrapper.domain.ILinkRepository;
import backend.academy.scrapper.parser.ParsedUrl;
import backend.academy.scrapper.util.Constants;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@AllArgsConstructor
public class UpdateCheckerService {
    private final ILinkRepository linkRepository;
    private final UrlParserService urlParserService;
    private final NotificationService notificationService;
    private final ExternalClientManager externalClientManager;

    @Scheduled(fixedRate = 5000)
    public void checkUpdates() {
        for (String url : linkRepository.getUrls()) {
            try {
                ParsedUrl parsedUrl = urlParserService.parse(url);
                UpdateDto update = externalClientManager.getUpdate(parsedUrl);
                List<Long> followersToUpdate = linkRepository.getFollowers(url, update.lastTimeUpdated());
                if (!followersToUpdate.isEmpty()) {
                    notificationService.notify(url, Constants.UPDATE_MESSAGE.formatted(url), followersToUpdate);
                }
            } catch (Exception e) {
                log.error("Failed to parse link {}", url, e);
            }
        }
    }
}
