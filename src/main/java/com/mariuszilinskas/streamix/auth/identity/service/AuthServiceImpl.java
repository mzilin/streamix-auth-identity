package com.mariuszilinskas.streamix.auth.identity.service;

import com.mariuszilinskas.streamix.auth.identity.client.SessionFeignClient;
import com.mariuszilinskas.streamix.auth.identity.dto.*;
import com.mariuszilinskas.streamix.auth.identity.exception.CredentialsValidationException;
import com.mariuszilinskas.streamix.auth.identity.exception.ResourceNotFoundException;
import com.mariuszilinskas.streamix.auth.identity.util.IdentityUtils;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/**
 * Service implementation for managing User authentication.
 *
 * @author Marius Zilinskas
 */
@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private static final Logger logger = LoggerFactory.getLogger(AuthServiceImpl.class);
    private final JwtService jwtService;
    private final PasswordService passwordService;
    private final SessionFeignClient sessionFeignClient;
    private final UserService userService;

    @Override
    public void authenticateUser(LoginRequest request, HttpServletResponse response) {
        logger.info("Authenticating User [email: {}]", request.email());

        AuthDetails authDetails = fetchAuthDetails(request.email());
        IdentityUtils.checkUserSuspended(authDetails.status());
        passwordService.verifyPassword(new VerifyPasswordRequest(authDetails.userId(), request.password()));

        TokenIssueResponse tokens = sessionFeignClient.issueTokens(authDetails);
        jwtService.setAuthCookies(response, tokens.accessToken(), tokens.refreshToken());
    }

    private AuthDetails fetchAuthDetails(String email) {
        try {
            return userService.getUserAuthDetailsWithEmail(email);
        } catch (ResourceNotFoundException ex) {
            throw new CredentialsValidationException();
        }
    }

}
