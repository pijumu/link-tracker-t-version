package backend.academy.bot.config;

import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.ByteArraySerializer;
import org.apache.kafka.common.serialization.LongSerializer;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.support.converter.RecordMessageConverter;

@Configuration
@RequiredArgsConstructor
public class KafkaDltProducerConfig {
    private final KafkaProperties kafkaProperties;

    @Bean(name = "businessErrorProducerFactory")
    public ProducerFactory<Long, byte[]> businessErrorProducerFactory() {
        var props = kafkaProperties.buildProducerProperties(null);

        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, LongSerializer.class);
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, ByteArraySerializer.class);

        return new DefaultKafkaProducerFactory<>(props);
    }

    @Bean(name = "businessErrorKafkaTemplate")
    public KafkaTemplate<Long, byte[]> businessErrorKafkaTemplate(
            @Qualifier("businessErrorProducerFactory") ProducerFactory<Long, byte[]> producerFactory,
            RecordMessageConverter kafkaJsonConverter) {
        return new KafkaTemplate<>(producerFactory);
    }

    @Bean(name = "serdeErrorProducerFactory")
    public ProducerFactory<Long, byte[]> serdeErrorProducerFactory() {
        var props = kafkaProperties.buildProducerProperties(null);

        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, LongSerializer.class);
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, ByteArraySerializer.class);

        return new DefaultKafkaProducerFactory<>(props);
    }

    @Bean(name = "serdeErrorKafkaTemplate")
    public KafkaTemplate<Long, byte[]> serdeErrorKafkaTemplate(
            @Qualifier("serdeErrorProducerFactory") ProducerFactory<Long, byte[]> producerFactory,
            RecordMessageConverter kafkaJsonConverter) {
        return new KafkaTemplate<>(producerFactory);
    }
}
