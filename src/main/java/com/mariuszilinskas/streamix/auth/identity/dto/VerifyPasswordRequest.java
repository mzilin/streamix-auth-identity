package com.mariuszilinskas.streamix.auth.identity.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import static com.mariuszilinskas.streamix.web.constant.ValidationMessages.*;

import java.util.UUID;

public record VerifyPasswordRequest(

        @NotNull(message = "userId " + CANNOT_BE_NULL)
        UUID userId,

        @NotBlank(message = "password " + CANNOT_BE_BLANK)
        String password

) {}
