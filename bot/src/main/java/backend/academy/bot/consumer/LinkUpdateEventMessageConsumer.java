package backend.academy.bot.consumer;

import backend.academy.bot.service.update.UpdateService;
import backend.academy.dto.dto.LinkUpdateDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class LinkUpdateEventMessageConsumer {
    private final UpdateService updateService;

    @KafkaListener(containerFactory = "multiThreadFactory", topics = "${app.link-update-events.topic}")
    public void consume(LinkUpdateDto payload, Acknowledgment acknowledgment) {
        updateService.handleUpdate(payload);
        acknowledgment.acknowledge();
    }
}
