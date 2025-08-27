package backend.academy.bot.config;

import backend.academy.bot.config.properties.LinkUpdateEventProperties;
import backend.academy.dto.dto.LinkUpdateDto;
import java.util.LinkedHashMap;
import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.RoundRobinAssignor;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.serialization.ByteArrayDeserializer;
import org.apache.kafka.common.serialization.LongDeserializer;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.config.KafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.listener.CommonDelegatingErrorHandler;
import org.springframework.kafka.listener.CommonErrorHandler;
import org.springframework.kafka.listener.ConcurrentMessageListenerContainer;
import org.springframework.kafka.listener.ContainerProperties;
import org.springframework.kafka.listener.DeadLetterPublishingRecoverer;
import org.springframework.kafka.listener.DefaultErrorHandler;
import org.springframework.kafka.support.converter.ByteArrayJsonMessageConverter;
import org.springframework.kafka.support.converter.ConversionException;
import org.springframework.kafka.support.converter.RecordMessageConverter;
import org.springframework.util.backoff.FixedBackOff;

@Configuration
@RequiredArgsConstructor
@EnableConfigurationProperties(LinkUpdateEventProperties.class)
public class KafkaConsumerConfig {
    private final KafkaProperties kafkaProperties;
    private final LinkUpdateEventProperties linkUpdateEventProperties;

    @Bean
    public RecordMessageConverter kafkaJsonConverter() {
        return new ByteArrayJsonMessageConverter();
    }

    @Bean("multiThreadFactory")
    public KafkaListenerContainerFactory<ConcurrentMessageListenerContainer<Long, LinkUpdateDto>>
            multiThreadedPartitionKafkaMessageListenerContainer(
                    CommonErrorHandler commonErrorHandler, RecordMessageConverter kafkaJsonConverter) {
        var listener = new ConcurrentKafkaListenerContainerFactory<Long, LinkUpdateDto>();

        listener.setConsumerFactory(consumerFactory());

        listener.getContainerProperties().setAckMode(ContainerProperties.AckMode.MANUAL);
        listener.setCommonErrorHandler(commonErrorHandler);
        listener.setAutoStartup(true);
        listener.setConcurrency(linkUpdateEventProperties.concurrency());
        listener.setRecordMessageConverter(kafkaJsonConverter);

        return listener;
    }

    private ConsumerFactory<Long, LinkUpdateDto> consumerFactory() {
        var props = kafkaProperties.buildConsumerProperties(null);

        props.put(ConsumerConfig.GROUP_ID_CONFIG, "default-consumer");
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, LongDeserializer.class);
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, ByteArrayDeserializer.class);
        props.put(ConsumerConfig.PARTITION_ASSIGNMENT_STRATEGY_CONFIG, RoundRobinAssignor.class.getName());

        return new DefaultKafkaConsumerFactory<>(props);
    }

    // По докладу на JPoint от Ивана Головко 2 месячной давности
    @Bean
    public CommonErrorHandler commonErrorHandler(
            @Qualifier("serdeErrorKafkaTemplate") KafkaTemplate<Long, byte[]> deserializationDltTemplate,
            @Qualifier("businessErrorKafkaTemplate") KafkaTemplate<Long, byte[]> businessDltTemplate) {
        CommonErrorHandler defaultErrorHandler = defaultErrorHandler(businessDltTemplate);
        CommonDelegatingErrorHandler delegatingErrorHandler = new CommonDelegatingErrorHandler(defaultErrorHandler);
        delegatingErrorHandler.setErrorHandlers(errorHandlingDelegates(deserializationDltTemplate));
        return delegatingErrorHandler;
    }

    // Обрабатывает бизнес ошибки
    private CommonErrorHandler defaultErrorHandler(KafkaTemplate<Long, byte[]> businessDltTemplate) {
        return new DefaultErrorHandler(
                new DeadLetterPublishingRecoverer(businessDltTemplate, (record, e) -> {
                    // topic.business.dlt
                    return new TopicPartition(
                            String.format("%s.%s.%s", linkUpdateEventProperties.topic(), "business", "dlt"),
                            record.partition());
                }),
                new FixedBackOff(0, 2));
    }

    // Обрабатывает ошибки сериализации и десериализации
    private CommonErrorHandler serDeErrorHandler(KafkaTemplate<Long, byte[]> deserializationDltTemplate) {
        return new DefaultErrorHandler(
                new DeadLetterPublishingRecoverer(deserializationDltTemplate, (record, e) -> {
                    // topic.serde.dlt
                    return new TopicPartition(
                            String.format("%s.%s.%s", linkUpdateEventProperties.topic(), "serde", "dlt"),
                            record.partition());
                }),
                new FixedBackOff(0, 0));
    }

    // По ошибке отдаёт handler. В будущем, если не доступен Telegram
    // или Redis, можно добавить прекращение вычитывания или добавлять exp backoff.
    private LinkedHashMap<Class<? extends Throwable>, CommonErrorHandler> errorHandlingDelegates(
            KafkaTemplate<Long, byte[]> deserializationDltTemplate) {
        LinkedHashMap<Class<? extends Throwable>, CommonErrorHandler> delegates = new LinkedHashMap<>();
        delegates.put(ConversionException.class, serDeErrorHandler(deserializationDltTemplate));
        return delegates;
    }
}
