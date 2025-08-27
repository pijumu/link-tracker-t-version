package backend.academy.scrapper.config;

import backend.academy.scrapper.config.properties.LinkUpdateEventProperties;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.KafkaAdmin;
import org.springframework.kafka.core.KafkaAdmin.NewTopics;

@Configuration
@EnableConfigurationProperties(LinkUpdateEventProperties.class)
@ConditionalOnProperty(prefix = "app", value = "message-transport", havingValue = "kafka")
@RequiredArgsConstructor
public class KafkaConfig {
    private final LinkUpdateEventProperties props;

    private KafkaAdmin.NewTopics toNewTopics() {
        return new KafkaAdmin.NewTopics(
                new NewTopic(props.topic(), props.partitions(), props.replicas()),
                new NewTopic(
                        String.format("%s.%s.%s", props.topic(), "business", "dlt"),
                        props.partitions(),
                        props.replicas()),
                new NewTopic(
                        String.format("%s.%s.%s", props.topic(), "serde", "dlt"),
                        props.partitions(),
                        props.replicas()));
    }

    @Bean
    @SneakyThrows
    NewTopics linkUpdateEventsTopic() {
        return toNewTopics();
    }
}
