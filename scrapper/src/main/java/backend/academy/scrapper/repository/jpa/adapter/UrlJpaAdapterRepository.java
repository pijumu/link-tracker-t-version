package backend.academy.scrapper.repository.jpa.adapter;

import backend.academy.scrapper.domain.UrlRepository;
import backend.academy.scrapper.domain.dto.UrlAddDto;
import backend.academy.scrapper.domain.dto.UrlInfoDto;
import backend.academy.scrapper.repository.jpa.entity.UrlEntity;
import backend.academy.scrapper.repository.jpa.entity.views.UrlIdChatIdView;
import backend.academy.scrapper.repository.jpa.impl.LinkJpaRepository;
import backend.academy.scrapper.repository.jpa.impl.UrlJpaRepository;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Repository;

@ConditionalOnProperty(prefix = "app", value = "data-access", havingValue = "jpa")
@Repository
@RequiredArgsConstructor
public class UrlJpaAdapterRepository implements UrlRepository {
    private final UrlJpaRepository urlJpaRepository;
    private final LinkJpaRepository linkJpaRepository;

    @Override
    public Long save(UrlAddDto url) {
        UrlEntity urlEntity = new UrlEntity();
        urlEntity.url(url.url());
        urlEntity.type(url.type());
        urlEntity.lastTimeUpdated(url.lastUpdateTime());
        urlEntity.meta(url.meta());
        UrlEntity saved = urlJpaRepository.save(urlEntity);
        return saved.id();
    }

    @Override
    public Optional<Long> findByUrl(String url) {
        Optional<UrlEntity> urlEntity = urlJpaRepository.findByUrl(url);
        return urlEntity.map(UrlEntity::id);
    }

    @Override
    public void updateLastTimeUpdated(String url, Instant lastTimeUpdated) {
        urlJpaRepository.findByUrl(url).ifPresent(entity -> {
            entity.lastTimeUpdated(lastTimeUpdated);
            urlJpaRepository.save(entity);
        });
    }

    // Так потому что Jpa. Зато можно заменить легко на H2 или MySQL
    @Override
    public List<UrlInfoDto> findAllWithChatIds(Long threshold, Integer limit) {
        PageRequest pg = PageRequest.of(0, limit, Sort.by("id").ascending());
        Page<UrlEntity> page = urlJpaRepository.findByIdGreaterThan(threshold, pg);
        if (page.getTotalElements() == 0) {
            return Collections.emptyList();
        }

        List<UrlIdChatIdView> pairs = linkJpaRepository.findByUrlIdRange(
                page.getContent().getFirst().id(), page.getContent().getLast().id());
        Map<Long, List<Long>> pairsMap = new HashMap<>();
        pairs.forEach(pair -> {
            pairsMap.computeIfAbsent(pair.getUrlId(), unused -> new ArrayList<>())
                    .add(pair.getChatId());
        });
        return page.getContent().stream()
                .filter(urlEntity -> pairsMap.containsKey(urlEntity.id()))
                .map(urlEntity -> new UrlInfoDto(
                        urlEntity.id(),
                        urlEntity.url(),
                        urlEntity.type(),
                        urlEntity.lastTimeUpdated(),
                        urlEntity.meta(),
                        pairsMap.get(urlEntity.id())))
                .toList();
    }
}
