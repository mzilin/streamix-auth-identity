package com.mariuszilinskas.streamix.auth.identity.service;

import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.stereotype.Service;

import java.util.Locale;

/**
 * Service implementation for Token generations.
 *
 * @author Marius Zilinskas
 */
@Service
public class TokenGenerationService {

    public String generatePasscode() {
        String allowedChars = "ABCDEFGHJKLMNPQRSTUVWXYZ23456789"; // Excludes 0, O, I and 1
        return RandomStringUtils.secure()
                .next(6, allowedChars)
                .toUpperCase(Locale.ROOT);
    }

    public String generateResetToken() {
        return RandomStringUtils.secure()
                .nextAlphanumeric(20)
                .toLowerCase(Locale.ROOT);
    }

}
