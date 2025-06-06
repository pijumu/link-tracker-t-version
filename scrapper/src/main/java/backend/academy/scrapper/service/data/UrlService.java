package backend.academy.scrapper.service.data;

import backend.academy.scrapper.domain.LinkRepository;
import backend.academy.scrapper.domain.UrlRepository;
import backend.academy.scrapper.domain.dto.UrlInfoDto;
import backend.academy.scrapper.exception.UpdateException;
import java.time.Instant;
import java.util.Collections;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
@RequiredArgsConstructor
public class UrlService {
    private final UrlRepository urlRepository;
    private final LinkRepository linkRepository;

    @Transactional
    public void updateLastTimeUpdated(String url, Instant lastTimeUpdated) {
        try {
            urlRepository.updateLastTimeUpdated(url, lastTimeUpdated);
        } catch (DataAccessException e) {
            log.error("Не удалось обновить время для {}", url, e);
            throw new UpdateException("Ошибка при обновлении времени обновления ссылки");
        }
    }

    public List<Long> getChatIdsByUrlIdAndFilterLogin(Long urlId, String filterLogin) {
        return linkRepository.getChatIdsByUrlIdAndFilterLogin(urlId, filterLogin);
    }

    public List<UrlInfoDto> getUrls(Long threshold, Integer limit) {
        try {
            return urlRepository.getBatch(threshold, limit);
        } catch (DataAccessException e) {
            log.warn("Не удалось получить список url threshold={} и limit={}.", threshold, limit);
            return Collections.emptyList();
        }
    }
}
