package com.mariuszilinskas.streamix.auth.identity.service;

import com.mariuszilinskas.streamix.auth.identity.util.IdentityUtils;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Service;

/**
 * Service implementation for setting JWT auth cookies.
 * Token generation and validation are owned by the Session Service.
 *
 * @author Marius Zilinskas
 */
@Service
public class JwtServiceImpl implements JwtService {

    private static final Logger logger = LoggerFactory.getLogger(JwtServiceImpl.class);

    @Value("${app.environment:production}")
    private String environment;

    @Override
    public void setAuthCookies(HttpServletResponse response, String accessToken, String refreshToken) {
        int accessMaxAge = (int) (IdentityUtils.ACCESS_TOKEN_EXPIRATION_MILLIS / 1000);
        int refreshMaxAge = (int) (IdentityUtils.REFRESH_TOKEN_EXPIRATION_MILLIS / 1000);
        response.addHeader("Set-Cookie", buildCookie(IdentityUtils.ACCESS_TOKEN_NAME, accessToken, accessMaxAge).toString());
        response.addHeader("Set-Cookie", buildCookie(IdentityUtils.REFRESH_TOKEN_NAME, refreshToken, refreshMaxAge).toString());
        logger.info("Auth cookies set");
    }

    private ResponseCookie buildCookie(String name, String value, int maxAge) {
        boolean isSecure = IdentityUtils.PRODUCTION_ENV.equals(environment);
        return ResponseCookie.from(name, value)
                .path("/")
                .maxAge(maxAge)
                .httpOnly(true)
                .secure(isSecure)
                .sameSite(isSecure ? "None" : "Lax")
                .build();
    }

}
