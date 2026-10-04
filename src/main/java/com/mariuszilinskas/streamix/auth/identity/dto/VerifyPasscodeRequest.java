package com.mariuszilinskas.streamix.auth.identity.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import static com.mariuszilinskas.streamix.web.constant.ValidationMessages.CANNOT_BE_BLANK;

public record VerifyPasscodeRequest(

        @NotBlank(message = "passcode " + CANNOT_BE_BLANK)
        @Size(min = 6, max = 6, message = "passcode must be 6 characters")
        String passcode

) {
        public VerifyPasscodeRequest {
                if (passcode != null) passcode = passcode.trim().toUpperCase();
        }
}
