package com.mariuszilinskas.streamix.auth.identity.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.time.Duration;

@ConfigurationProperties(prefix = "kafka")
public record KafkaProperties(
        Topics topics,
        Consumer consumer
) {

    public record Topics(
            UserRegistered userRegistered
    ) {}

    public record UserRegistered(
            String name,
            Dlt dlt
    ) {}

    public record Dlt(
            String name,
            int partitions,
            int replicationFactor,
            Duration retention
    ) {}

    public record Consumer(
            Groups groups,
            String autoOffsetReset
    ) {}

    public record Groups(
            String identityRegistration
    ) {}
}