package com.mariuszilinskas.streamix.auth.identity.config;

import com.mariuszilinskas.streamix.observability.kafka.KafkaConsumerInterceptor;
import lombok.RequiredArgsConstructor;
import org.apache.kafka.common.TopicPartition;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.listener.DefaultErrorHandler;
import org.springframework.kafka.listener.DeadLetterPublishingRecoverer;
import org.springframework.kafka.support.serializer.DeserializationException;
import org.springframework.util.backoff.FixedBackOff;

@Configuration
@RequiredArgsConstructor
public class KafkaConfig {

    private final KafkaConsumerInterceptor kafkaConsumerInterceptor;
    private final KafkaTemplate<Object, Object> kafkaTemplate;

    @Bean
    public ConcurrentKafkaListenerContainerFactory<Object, Object>
            kafkaListenerContainerFactory(
                    ConsumerFactory<Object, Object> consumerFactory,
                    DefaultErrorHandler kafkaErrorHandler) {
        var factory = new ConcurrentKafkaListenerContainerFactory<Object, Object>();
        factory.setConsumerFactory(consumerFactory);
        factory.setRecordInterceptor(kafkaConsumerInterceptor);
        factory.setCommonErrorHandler(kafkaErrorHandler);
        return factory;
    }

    @Bean
    public DefaultErrorHandler kafkaErrorHandler() {
        var recoverer = new DeadLetterPublishingRecoverer(kafkaTemplate,
                (record, ex) -> new TopicPartition(record.topic() + ".dlt", record.partition()));
        var backoff = new FixedBackOff(1_000L, 3L);
        var handler = new DefaultErrorHandler(recoverer, backoff);
        handler.addNotRetryableExceptions(DeserializationException.class);
        return handler;
    }

}
