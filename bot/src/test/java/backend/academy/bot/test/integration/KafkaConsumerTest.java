package backend.academy.bot.test.integration;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;

import backend.academy.bot.config.KafkaConsumerConfig;
import backend.academy.bot.config.KafkaDltProducerConfig;
import backend.academy.bot.consumer.LinkUpdateEventMessageConsumer;
import backend.academy.bot.service.update.UpdateService;
import backend.academy.bot.service.update.realtime.RealTimeUpdateService;
import backend.academy.dto.dto.LinkUpdateDto;
import java.time.Duration;
import java.util.HashMap;
import java.util.List;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.ByteArrayDeserializer;
import org.apache.kafka.common.serialization.LongDeserializer;
import org.apache.kafka.common.serialization.LongSerializer;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.kafka.KafkaAutoConfiguration;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.support.serializer.JsonSerializer;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.testcontainers.shaded.org.awaitility.Awaitility;

@SpringBootTest(
        classes = {
            KafkaConsumerConfig.class,
            KafkaDltProducerConfig.class,
            KafkaConsumerTest.TestKafkaProducerConfig.class,
            KafkaAutoConfiguration.class,
            LinkUpdateEventMessageConsumer.class,
            UpdateService.class,
            RealTimeUpdateService.class
        })
@EmbeddedKafka(
        partitions = 1,
        topics = {"link-update", "link-update.serde.dlt", "link-update.business.dlt"},
        brokerProperties = {"listeners=PLAINTEXT://127.0.0.1:0", "auto.create.topics.enable=false"})
public class KafkaConsumerTest {
    @TestConfiguration
    static class TestKafkaProducerConfig {

        @Bean
        ProducerFactory<Long, Object> producerFactory(KafkaProperties props) {
            var map = props.buildProducerProperties(null);
            map.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, LongSerializer.class);
            map.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);
            return new DefaultKafkaProducerFactory<>(map);
        }

        @Bean
        KafkaTemplate<Long, Object> kafkaTemplate(ProducerFactory<Long, Object> pf) {
            return new KafkaTemplate<>(pf);
        }

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
    UpdateService updateService;

    @Autowired
    KafkaTemplate<Long, Object> kafkaTemplate;

    @Autowired
    KafkaConsumer<Long, byte[]> consumer;

    @Test
    @DisplayName("Проверка настроек cosumer group. Позитивный сценарий.")
    void test1() {
        kafkaTemplate.send("link-update", 1L, new LinkUpdateDto(2L, "url", "desc", List.of(1L, 2L)));

        Awaitility.await().atMost(Duration.ofSeconds(30)).untilAsserted(() -> verify(updateService)
                .handleUpdate(any(LinkUpdateDto.class)));
    }

    @Test
    @DisplayName("Проверка отправки в serde.dlt при обработке невалидного сообщения. Негативный сценарий.")
    void test2() {
        kafkaTemplate.send("link-update", 1L, "Random Forest");
        consumer.subscribe(List.of("link-update.serde.dlt"));

        Awaitility.await()
                .atMost(Duration.ofSeconds(40))
                .until(() -> consumer.poll(Duration.ofMillis(200)).count() > 0);
    }
}
