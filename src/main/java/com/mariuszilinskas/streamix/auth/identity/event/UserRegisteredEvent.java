package com.mariuszilinskas.streamix.auth.identity.event;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.time.Instant;
import java.util.UUID;

@JsonIgnoreProperties(ignoreUnknown = true)
public record UserRegisteredEvent(
        UUID eventId,
        String eventVersion,
        Instant occurredAt,
        UUID userId,
        String email,
        String firstName,
        String lastName,
        String country
) {}
