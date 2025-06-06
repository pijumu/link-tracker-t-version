package backend.academy.scrapper.domain;

import backend.academy.scrapper.domain.dto.UrlAddDto;
import backend.academy.scrapper.domain.dto.UrlInfoDto;
import java.time.Instant;
import java.util.List;
import java.util.Optional;

public interface UrlRepository {
    // Хотим вернуть id, чтобы потом добавить легко LinkDto. Также ссылка может кем-то отслеживаться.
    Long save(UrlAddDto url);

    Optional<Long> findByUrl(String url);

    void updateLastTimeUpdated(String url, Instant lastTimeUpdated);

    List<UrlInfoDto> getBatch(Long threshold, Integer limit);
}
