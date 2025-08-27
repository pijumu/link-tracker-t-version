package backend.academy.scrapper.test.integration;

import backend.academy.scrapper.config.KafkaConfig;
import backend.academy.scrapper.config.KafkaProducerConfig;
import backend.academy.scrapper.domain.dto.UpdateWithMessageDto;
import backend.academy.scrapper.service.data.UrlService;
import backend.academy.scrapper.service.notification.KafkaNotificationService;
import java.time.Duration;
import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.serialization.ByteArrayDeserializer;
import org.apache.kafka.common.serialization.LongDeserializer;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.kafka.KafkaAutoConfiguration;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.testcontainers.shaded.org.awaitility.Awaitility;

@SpringBootTest(
        classes = {
            KafkaProducerConfig.class,
            KafkaProducerTest.TestKafkaProducerConfig.class,
            KafkaAutoConfiguration.class,
            KafkaNotificationService.class,
            UrlService.class,
            KafkaConfig.class
        })
@EmbeddedKafka(
        partitions = 1,
        topics = {"link-update"},
        brokerProperties = {"listeners=PLAINTEXT://127.0.0.1:0", "auto.create.topics.enable=false"})
public class KafkaProducerTest {
    @TestConfiguration
    static class TestKafkaProducerConfig {
        @Bean
        KafkaConsumer<Long, byte[]> kafkaConsumer(KafkaProperties kafkaProperties) {
            var cProps = new HashMap<String, Object>();
            cProps.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaProperties.getBootstrapServers());
            cProps.put(ConsumerConfig.GROUP_ID_CONFIG, "dlt-test");
            cProps.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
            cProps.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, LongDeserializer.class);
            cProps.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, ByteArrayDeserializer.class);

            return new KafkaConsumer<>(cProps);
        }
    }

    @MockitoBean
    UrlService urlService;

    @Autowired
    KafkaNotificationService kafkaService;

    @Autowired
    KafkaConsumer<Long, byte[]> consumer;

    @Test
    @DisplayName("Проверка отправки сообщения kafka сервисом. Позитивный сценарий")
    void test1() {
        // Arrange
        var update =
                new UpdateWithMessageDto(1L, "myUrl", Instant.parse("2025-06-06T12:34:56Z"), "myDesc", List.of(1L, 2L));
        var toNotify = List.of(List.of(update));

        // Act
        kafkaService.notify(toNotify);

        // Assert
        consumer.subscribe(List.of("link-update"));

        Awaitility.await()
                .atMost(Duration.ofSeconds(40))
                .until(() -> consumer.poll(Duration.ofMillis(200)).count() > 0);
    }
}
