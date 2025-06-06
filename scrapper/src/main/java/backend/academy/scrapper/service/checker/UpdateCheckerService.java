package backend.academy.scrapper.service.checker;

import static backend.academy.scrapper.util.Constants.USER;

import backend.academy.dto.validator.util.UrlType;
import backend.academy.scrapper.client.util.ClientAdapter;
import backend.academy.scrapper.config.UpdateCheckerConfig;
import backend.academy.scrapper.domain.dto.UpdateWithMessageDto;
import backend.academy.scrapper.domain.dto.UrlInfoDto;
import backend.academy.scrapper.service.data.UrlService;
import backend.academy.scrapper.service.notification.NotificationService;
import java.util.Collections;
import java.util.List;
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
    private final NotificationService notificationService;
    private final MessageFormatterService messageFormatterService;

    @Autowired
    public UpdateCheckerService(
            List<ClientAdapter> clients,
            UrlService urlService,
            @Qualifier("httpPool") ThreadPoolTaskExecutor httpPool,
            @Qualifier("dbPool") ThreadPoolTaskExecutor dbPool,
            UpdateCheckerConfig updateCheckerConfig,
            NotificationService notificationService,
            MessageFormatterService messageFormatterService) {
        this.clients = new ConcurrentHashMap<>();
        clients.forEach(client -> this.clients.put(client.getUrlType(), client));
        this.urlService = urlService;
        this.httpPool = httpPool;
        this.dbPool = dbPool;
        this.batchSize = updateCheckerConfig.updateCheckerProperties().batchSize();
        this.notificationService = notificationService;
        this.messageFormatterService = messageFormatterService;
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

            var updates = checkBatchUpdate(urls)
                    .orTimeout(1, TimeUnit.MINUTES)
                    .handle((result, ex) -> {
                        if (ex != null) {
                            log.error("Ошибка обработки batch.", ex);
                            return Collections.<List<UpdateWithMessageDto>>emptyList();
                        }
                        return result;
                    })
                    .join();

            lastId = urls.getLast().id();

            notificationService.notify(updates);
        }
        log.info("Finished...");
    }

    private CompletableFuture<List<List<UpdateWithMessageDto>>> checkBatchUpdate(List<UrlInfoDto> urls) {

        List<CompletableFuture<List<UpdateWithMessageDto>>> futures = urls.stream()
                .map(url -> CompletableFuture.supplyAsync(
                                () -> clients.get(url.type()).getUpdates(url), httpPool)
                        .exceptionally((ex) -> {
                            log.error("Ошибка взаимодействия с внешним API {}: ", url.url());
                            return Collections.emptyList();
                        })
                        .thenApplyAsync(
                                updateList -> updateList.stream()
                                        .map(updateDto -> {
                                            var chatIds = urlService.getChatIdsByUrlIdAndFilterLogin(
                                                    updateDto.urlId(), USER + updateDto.user());
                                            return messageFormatterService.formUpdateMessageDto(updateDto, chatIds);
                                        })
                                        .toList(),
                                dbPool)
                        .exceptionally((ex) -> {
                            log.error("Ошибка взаимодействия с бд {}: ", url.url());
                            return Collections.emptyList();
                        }))
                .toList();
        // Мы возвращаем List<List<LinkUpdateDto>> потому что хотим сохранить группировку по urlId
        return CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]))
                .thenApply(
                        unused -> futures.stream().map(CompletableFuture::join).toList());
    }
}
