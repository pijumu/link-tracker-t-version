package backend.academy.scrapper.service.notification;

import backend.academy.dto.dto.LinkUpdateDto;
import backend.academy.scrapper.config.properties.LinkUpdateEventProperties;
import backend.academy.scrapper.domain.dto.UpdateWithMessageDto;
import backend.academy.scrapper.service.data.UrlService;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@ConditionalOnProperty(prefix = "app", value = "message-transport", havingValue = "kafka")
public class KafkaNotificationService implements NotificationService {
    private final ThreadPoolTaskExecutor kafkaPool;
    private final KafkaTemplate<Long, byte[]> kafkaTemplate;
    private final LinkUpdateEventProperties props;
    private final UrlService urlService;

    @Autowired
    public KafkaNotificationService(
            @Qualifier("kafkaPool") ThreadPoolTaskExecutor kafkaPool,
            KafkaTemplate<Long, byte[]> kafkaTemplate,
            LinkUpdateEventProperties linkUpdateEventProperties,
            UrlService urlService) {
        this.kafkaPool = kafkaPool;
        this.kafkaTemplate = kafkaTemplate;
        this.props = linkUpdateEventProperties;
        this.urlService = urlService;
    }

    @Override
    public void notify(List<List<UpdateWithMessageDto>> updates) {
        var futures = updates.stream()
                .map(updateList -> CompletableFuture.runAsync(
                        () -> updateList.forEach(update -> {
                            try {
                                Message<LinkUpdateDto> msg = MessageBuilder.withPayload(
                                                NotificationService.toLinkUpdateDto(update))
                                        .setHeader(KafkaHeaders.KEY, update.urlId())
                                        .setHeader(KafkaHeaders.TOPIC, props.topic())
                                        .build();
                                kafkaTemplate.send(msg).get();
                            } catch (Exception e) {
                                log.error("Ошибка отправки в main topic: {}", update.urlId(), e);
                            }
                            urlService.updateLastTimeUpdated(update.url(), update.createdAt());
                        }),
                        kafkaPool))
                .toList();

        CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();
    }
}
