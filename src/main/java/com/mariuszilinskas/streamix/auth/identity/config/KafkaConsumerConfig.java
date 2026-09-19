package com.mariuszilinskas.streamix.auth.identity.config;

import com.mariuszilinskas.streamix.auth.identity.properties.KafkaProperties;
import com.mariuszilinskas.streamix.auth.identity.dto.UserRegisteredEvent;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.config.TopicBuilder;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.listener.DefaultErrorHandler;
import org.springframework.kafka.listener.DeadLetterPublishingRecoverer;
import org.springframework.kafka.support.serializer.JacksonJsonDeserializer;
import org.springframework.util.backoff.ExponentialBackOff;

import java.util.Map;

@Configuration
public class KafkaConsumerConfig {

    private final KafkaProperties kafkaProperties;

    public KafkaConsumerConfig(KafkaProperties kafkaProperties) {
        this.kafkaProperties = kafkaProperties;
    }

    @Bean
    public ConsumerFactory<String, UserRegisteredEvent> consumerFactory(
            org.springframework.boot.kafka.autoconfigure.KafkaProperties springKafkaProperties
    ) {
        JacksonJsonDeserializer<UserRegisteredEvent> deserializer =
                new JacksonJsonDeserializer<>(UserRegisteredEvent.class);

        deserializer.addTrustedPackages(
                "com.mariuszilinskas.streamix.auth.identity.dto"
        );
        deserializer.setRemoveTypeHeaders(true);
        deserializer.setUseTypeMapperForKey(false);

        return new DefaultKafkaConsumerFactory<String, UserRegisteredEvent>(
                Map.of(
                        ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG,
                        springKafkaProperties.getBootstrapServers(),
                        ConsumerConfig.GROUP_ID_CONFIG,
                        kafkaProperties.consumer().groups().identityRegistration(),
                        ConsumerConfig.AUTO_OFFSET_RESET_CONFIG,
                        kafkaProperties.consumer().autoOffsetReset()
                ),
                new StringDeserializer(),
                deserializer
        );
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, UserRegisteredEvent>
    kafkaListenerContainerFactory(
            ConsumerFactory<String, UserRegisteredEvent> consumerFactory,
            KafkaTemplate<String, Object> kafkaTemplate
    ) {
        var recoverer = new DeadLetterPublishingRecoverer(
                kafkaTemplate,
                (record, exception) -> new TopicPartition(
                        record.topic() + kafkaProperties.dlt().suffix(),
                        record.partition()
                )
        );

        var backOff = new ExponentialBackOff(1_000L, 2.0);
        backOff.setMaxAttempts(3);

        var errorHandler = new DefaultErrorHandler(recoverer, backOff);

        var factory =
                new ConcurrentKafkaListenerContainerFactory<String, UserRegisteredEvent>();

        factory.setConsumerFactory(consumerFactory);
        factory.setCommonErrorHandler(errorHandler);

        return factory;
    }

    @Bean
    public NewTopic userRegisteredDltTopic() {
        var dlt = kafkaProperties.dlt();

        return TopicBuilder
                .name(kafkaProperties.topics().userRegistered() + dlt.suffix())
                .partitions(dlt.partitions())
                .replicas(dlt.replicationFactor())
                .config(
                        org.apache.kafka.common.config.TopicConfig.RETENTION_MS_CONFIG,
                        String.valueOf(dlt.retention().toMillis())
                )
                .build();
    }
}