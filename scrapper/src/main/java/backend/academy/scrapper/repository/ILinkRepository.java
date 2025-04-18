package backend.academy.scrapper.repository;

import backend.academy.scrapper.model.Link;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface ILinkRepository {
    void add(Long chatId, Link link);

    Optional<Link> remove(Long chatId, String url);

    boolean isTracking(Long chatId, String url);

    List<Link> getLinksForChatId(Long chatId);

    Set<String> getUrls();

    List<Long> getFollowers(String url, Instant activityUpdate);
}
