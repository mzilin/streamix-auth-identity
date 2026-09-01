package com.mariuszilinskas.streamix.auth.identity.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import static com.mariuszilinskas.streamix.web.constant.ValidationMessages.*;

public record ResetPasswordRequest(

        @NotBlank(message = "password" + CANNOT_BE_BLANK)
        @Size(min = 8, max = 64, message = PASSWORD_INCORRECT_LENGTH)
        @Pattern.List({
                @Pattern(regexp = ".*[a-z].*", message = PASSWORD_MISSING_LOWERCASE),
                @Pattern(regexp = ".*[A-Z].*", message = PASSWORD_MISSING_UPPERCASE),
                @Pattern(regexp = ".*\\d.*", message = PASSWORD_MISSING_DIGIT),
                @Pattern(regexp = ".*[!@#$%^&*(),.?\":{}|<>].*", message = PASSWORD_MISSING_SPECIAL)
        })
        String password,

        @NotBlank(message = "resetToken" + CANNOT_BE_BLANK)
        String resetToken

) {}
