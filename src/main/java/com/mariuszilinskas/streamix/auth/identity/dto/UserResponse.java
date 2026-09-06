package com.mariuszilinskas.streamix.auth.identity.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

import static com.mariuszilinskas.streamix.web.constant.ValidationMessages.*;

public record UserResponse(

        @NotBlank(message = "firstName " + CANNOT_BE_BLANK)
        String firstName,

        @NotBlank(message = "lastName " + CANNOT_BE_BLANK)
        String lastName,

        @NotBlank(message = "email " + CANNOT_BE_BLANK)
        @Email(message = INVALID_EMAIL)
        String email

) {}
