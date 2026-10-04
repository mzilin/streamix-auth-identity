package com.mariuszilinskas.streamix.auth.identity.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "kafka")
public record KafkaProperties(
        Topics topics,
        Groups groups
) {

    public record Topics(
            String userRegistered
    ) {}

    public record Groups(
            String identityRegistration
    ) {}
}