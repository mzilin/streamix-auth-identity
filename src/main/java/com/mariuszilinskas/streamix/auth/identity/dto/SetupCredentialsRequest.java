package com.mariuszilinskas.streamix.auth.identity.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

import static com.mariuszilinskas.streamix.web.constant.ValidationMessages.*;

public record SetupCredentialsRequest(

        @NotNull(message = "userId " + CANNOT_BE_NULL)
        UUID userId,

        @NotBlank(message = "firstName " + CANNOT_BE_BLANK)
        String firstName,

        @NotBlank(message = "email " + CANNOT_BE_BLANK)
        String email,

        @NotBlank(message = "passwordHash " + CANNOT_BE_BLANK)
        String passwordHash

) {}
