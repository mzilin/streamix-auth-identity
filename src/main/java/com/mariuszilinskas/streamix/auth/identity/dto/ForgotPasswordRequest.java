package com.mariuszilinskas.streamix.auth.identity.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

import static com.mariuszilinskas.streamix.web.constant.ValidationMessages.*;

public record ForgotPasswordRequest(

        @NotBlank(message = "email" + CANNOT_BE_BLANK)
        @Email(message = INVALID_EMAIL)
        String email

) {
        public ForgotPasswordRequest {
                email = email.trim().toLowerCase();
        }
}
