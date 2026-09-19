package com.mariuszilinskas.streamix.auth.identity.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "rabbitmq")
public record RabbitMQProperties(
        String exchange,
        Queues queues,
        RoutingKeys routingKeys
) {

    public record Queues(
            String createCredentials,
            String resetPasscode,
            String deleteUserData
    ) {}

    public record RoutingKeys(
            String verifyAccount,
            String createCredentials,
            String platformEmails,
            String resetPasscode,
            String deleteUserData
    ) {}
}
