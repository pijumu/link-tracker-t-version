package backend.academy.scrapper.config;

import java.util.Map;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.producer.Partitioner;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.Cluster;
import org.apache.kafka.common.serialization.ByteArraySerializer;
import org.apache.kafka.common.serialization.LongSerializer;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.converter.ByteArrayJsonMessageConverter;
import org.springframework.kafka.support.converter.RecordMessageConverter;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

@Configuration
@ConditionalOnProperty(prefix = "app", value = "message-transport", havingValue = "kafka")
@RequiredArgsConstructor
public class KafkaProducerConfig {

    private final KafkaProperties properties;

    @Bean
    public RecordMessageConverter kafkaJsonConverter() {
        return new ByteArrayJsonMessageConverter();
    }

    @Bean
    public KafkaTemplate<Long, byte[]> kafkaTemplate(RecordMessageConverter kafkaJsonConverter) {
        var props = properties.buildProducerProperties(null);

        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, LongSerializer.class);

        // Возможно стоит заменить из-за timestamp конвертации. (На будущее)
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, ByteArraySerializer.class);

        props.put(ProducerConfig.PARTITIONER_CLASS_CONFIG, CustomUserPartitioner.class);

        var factory = new DefaultKafkaProducerFactory<Long, byte[]>(props);

        var template = new KafkaTemplate<>(factory);
        template.setMessageConverter(kafkaJsonConverter);
        return template;
    }

    @Bean(name = "kafkaPool")
    ThreadPoolTaskExecutor kafkaProducerExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(8);
        executor.setMaxPoolSize(8);
        executor.setQueueCapacity(500);
        executor.initialize();
        return executor;
    }

    public static class CustomUserPartitioner implements Partitioner {

        @Override
        public int partition(
                String topic, Object key, byte[] keyBytes, Object value, byte[] valueBytes, Cluster cluster) {

            var linkId = Optional.ofNullable(key)
                    .filter(Long.class::isInstance)
                    .map(Long.class::cast)
                    .orElse(0L);
            return (int) (linkId % cluster.partitionCountForTopic(topic));
        }

        @Override
        public void close() {}

        @Override
        public void configure(Map<String, ?> map) {}
    }
}
