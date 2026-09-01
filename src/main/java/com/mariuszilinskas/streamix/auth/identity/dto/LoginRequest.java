package com.mariuszilinskas.streamix.auth.identity.dto;

import jakarta.validation.constraints.NotBlank;

import static com.mariuszilinskas.streamix.web.constant.ValidationMessages.*;

public record LoginRequest(

        @NotBlank(message = "email" + CANNOT_BE_BLANK)
        String email,

        @NotBlank(message = "password" + CANNOT_BE_BLANK)
        String password

) {
        public LoginRequest {
                if (email != null) email = email.trim().toLowerCase();
                if (password != null) password = password.trim();
        }
}
