package backend.academy.scrapper.repository;

import backend.academy.scrapper.domain.ILinkRepository;
import backend.academy.scrapper.domain.dto.Link;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.stereotype.Repository;

@Repository
public class InMemoryLinkRepository implements ILinkRepository {
    private final Map<Long, List<Link>> links = new HashMap<>();

    @Override
    public void add(Long chatId, Link link) {
        links.computeIfAbsent(chatId, unused -> new ArrayList<>()).add(link);
    }

    @Override
    public Optional<Link> remove(Long chatId, String url) {
        List<Link> links = getLinksForChatId(chatId);
        Optional<Link> elemToRemove =
                links.stream().filter(link -> link.url().equals(url)).findFirst();
        elemToRemove.ifPresent(links::remove);
        return elemToRemove;
    }

    @Override
    public boolean isTracking(Long chatId, String url) {
        List<Link> links = getLinksForChatId(chatId);
        Optional<Link> elemToRemove =
                links.stream().filter(link -> link.url().equals(url)).findFirst();
        return elemToRemove.isPresent();
    }

    @Override
    public List<Link> getLinksForChatId(Long chatId) {
        return links.getOrDefault(chatId, Collections.emptyList());
    }

    @Override
    public Set<String> getUrls() {
        return links.values().stream().flatMap(List::stream).map(Link::url).collect(Collectors.toUnmodifiableSet());
    }

    @Override
    public List<Long> getFollowers(String url, Instant activityUpdate) {
        return links.entrySet().stream()
                .filter(entry -> entry.getValue().stream()
                        .anyMatch(link -> link.url().equals(url) && link.needUpdate(activityUpdate)))
                .map(Map.Entry::getKey)
                .toList();
    }
}
