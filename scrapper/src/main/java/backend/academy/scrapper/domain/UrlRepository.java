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

    // На первый взгляд нужно обновлять по url, так как в UrlDto нет поля id, так как при добавлении у нас поля id нет.
    // Чтобы не менять Dto для разных операций. Для производительности просто будет индекс на url.
    void updateLastTimeUpdated(String url, Instant lastTimeUpdated);

    List<UrlInfoDto> findAllWithChatIds(Long threshold, Integer limit);
}
