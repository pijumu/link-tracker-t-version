package backend.academy.scrapper.service;

import backend.academy.dto.dto.LinkUpdateDto;
import backend.academy.dto.validator.util.UrlType;
import backend.academy.scrapper.client.util.ClientAdapter;
import backend.academy.scrapper.config.UpdateCheckerConfig;
import backend.academy.scrapper.domain.dto.UpdateInfoDto;
import backend.academy.scrapper.domain.dto.UrlInfoDto;
import backend.academy.scrapper.service.data.UrlService;
import backend.academy.scrapper.service.notification.RestNotificationService;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.TimeUnit;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class UpdateCheckerService {
    private final ConcurrentMap<UrlType, ClientAdapter> clients;
    private final UrlService urlService;
    private final Integer batchSize;
    private final ThreadPoolTaskExecutor httpPool;
    private final ThreadPoolTaskExecutor dbPool;
    private final RestNotificationService notificationService;

    @Autowired
    public UpdateCheckerService(
            List<ClientAdapter> clients,
            UrlService urlService,
            @Qualifier("httpPool") ThreadPoolTaskExecutor httpPool,
            @Qualifier("dbPool") ThreadPoolTaskExecutor dbPool,
            UpdateCheckerConfig updateCheckerConfig,
            RestNotificationService restNotificationService) {
        this.clients = new ConcurrentHashMap<>();
        clients.forEach(client -> this.clients.put(client.getUrlType(), client));
        this.urlService = urlService;
        this.httpPool = httpPool;
        this.dbPool = dbPool;
        this.batchSize = updateCheckerConfig.updateCheckerProperties().batchSize();
        this.notificationService = restNotificationService;
    }

    // 5 часов = 5 * 60 * 60 * 1000 мс
    @Scheduled(fixedRate = 18_000_000)
    public void checkUpdate() {
        Long lastId = 0L;
        log.info("Checking for updates...");
        while (true) {
            List<UrlInfoDto> urls = urlService.getUrls(lastId, batchSize);

            // Условие выхода, выглядит возможно костыльно, но зато быстро вытаскиваем batch из бд
            if (urls.isEmpty()) {
                break;
            }

            List<LinkUpdateDto> updates = checkBatchUpdate(urls)
                    .orTimeout(1, TimeUnit.MINUTES)
                    .handle((result, ex) -> {
                        if (ex != null) {
                            log.error("Ошибка обработки batch.", ex);
                            return Collections.<LinkUpdateDto>emptyList();
                        }
                        return result;
                    })
                    .join();

            lastId = urls.getLast().id();

            notificationService.notify(updates);
        }
    }

    private CompletableFuture<List<LinkUpdateDto>> checkBatchUpdate(List<UrlInfoDto> urls) {

        List<CompletableFuture<Optional<List<LinkUpdateDto>>>> futures = urls.stream()
                .map(url -> CompletableFuture.supplyAsync(
                                () -> clients.get(url.type()).getUpdate(url), httpPool)
                        .exceptionally((ex) -> {
                            log.error("Ошибка взаимодействия с внешним API {}: ", url.url());
                            return Optional.empty();
                        })
                        .thenApplyAsync(
                                updateOpt -> {
                                    updateOpt.ifPresent(
                                            u -> urlService.updateLastTimeUpdated(url.url(), u.lastUpdateTime()));
                                    return updateOpt.map(UpdateInfoDto::updates);
                                },
                                dbPool)
                        .exceptionally((ex) -> {
                            log.error("Ошибка взаимодействия с бд {}: ", url.url());
                            return Optional.empty();
                        }))
                .toList();

        return CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]))
                .thenApply(unused -> futures.stream()
                        .map(CompletableFuture::join)
                        .flatMap(Optional::stream)
                        .flatMap(List::stream)
                        .toList());
    }
}
