package com.mariuszilinskas.streamix.auth.identity.service;

import com.mariuszilinskas.streamix.auth.identity.client.SessionFeignClient;
import com.mariuszilinskas.streamix.auth.identity.dto.*;
import com.mariuszilinskas.streamix.auth.identity.enums.UserRole;
import com.mariuszilinskas.streamix.auth.identity.enums.UserStatus;
import com.mariuszilinskas.streamix.auth.identity.exception.*;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockHttpServletResponse;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AuthServiceImplTest {

    @Mock
    private JwtService jwtService;

    @Mock
    private PasswordService passwordService;

    @Mock
    private SessionFeignClient sessionFeignClient;

    @Mock
    private UserService userService;

    @InjectMocks
    private AuthServiceImpl authService;

    private final UUID userId = UUID.randomUUID();
    private HttpServletResponse mockResponse;
    private AuthDetails authDetails;

    // ------------------------------------

    @BeforeEach
    void setUp() {
        mockResponse = new MockHttpServletResponse();
        authDetails = new AuthDetails(userId, List.of(UserRole.USER), List.of(), UserStatus.ACTIVE);
    }

    // ------------------------------------

    @Test
    void testAuthenticateUser_Success() {
        // Arrange
        String email = "user@email.com";
        String password = "Password1!";
        LoginRequest loginRequest = new LoginRequest(email, password);
        var passwordRequest = new VerifyPasswordRequest(userId, password);
        var tokens = new TokenIssueResponse("access_token", "refresh_token");

        when(userService.getUserAuthDetailsWithEmail(email)).thenReturn(authDetails);
        doNothing().when(passwordService).verifyPassword(passwordRequest);
        when(sessionFeignClient.issueTokens(authDetails)).thenReturn(tokens);
        doNothing().when(jwtService).setAuthCookies(mockResponse, tokens.accessToken(), tokens.refreshToken());

        // Act
        authService.authenticateUser(loginRequest, mockResponse);

        // Assert
        verify(userService, times(1)).getUserAuthDetailsWithEmail(email);
        verify(passwordService, times(1)).verifyPassword(passwordRequest);
        verify(sessionFeignClient, times(1)).issueTokens(authDetails);
        verify(jwtService, times(1)).setAuthCookies(mockResponse, tokens.accessToken(), tokens.refreshToken());
    }

    @Test
    void testAuthenticateUser_InvalidCredentials() {
        // Arrange
        String email = "user@email.com";
        String password = "wrongPassword";
        LoginRequest loginRequest = new LoginRequest(email, password);
        var passwordRequest = new VerifyPasswordRequest(userId, password);

        when(userService.getUserAuthDetailsWithEmail(email)).thenReturn(authDetails);
        doThrow(CredentialsValidationException.class).when(passwordService).verifyPassword(passwordRequest);

        // Act & Assert
        assertThrows(CredentialsValidationException.class, () -> authService.authenticateUser(loginRequest, mockResponse));

        // Assert
        verify(userService, times(1)).getUserAuthDetailsWithEmail(email);
        verify(passwordService, times(1)).verifyPassword(passwordRequest);
        verify(sessionFeignClient, never()).issueTokens(any(AuthDetails.class));
        verify(jwtService, never()).setAuthCookies(any(HttpServletResponse.class), anyString(), anyString());
    }

    @Test
    void testAuthenticateUser_NonExistingUser() {
        // Arrange
        String email = "user@email.com";
        LoginRequest loginRequest = new LoginRequest(email, "Password1!");

        when(userService.getUserAuthDetailsWithEmail(email)).thenThrow(ResourceNotFoundException.class);

        // Act & Assert
        assertThrows(CredentialsValidationException.class, () -> authService.authenticateUser(loginRequest, mockResponse));

        // Assert
        verify(userService, times(1)).getUserAuthDetailsWithEmail(email);
        verify(passwordService, never()).verifyPassword(any(VerifyPasswordRequest.class));
        verify(sessionFeignClient, never()).issueTokens(any(AuthDetails.class));
        verify(jwtService, never()).setAuthCookies(any(HttpServletResponse.class), anyString(), anyString());
    }

    @Test
    void testAuthenticateUser_UserSuspended() {
        // Arrange
        String email = "user@email.com";
        LoginRequest loginRequest = new LoginRequest(email, "Password1!");
        authDetails = new AuthDetails(userId, List.of(UserRole.USER), List.of(), UserStatus.SUSPENDED);

        when(userService.getUserAuthDetailsWithEmail(email)).thenReturn(authDetails);

        // Act & Assert
        assertThrows(UserStatusAccessException.class, () -> authService.authenticateUser(loginRequest, mockResponse));

        // Assert
        verify(userService, times(1)).getUserAuthDetailsWithEmail(loginRequest.email());
        verify(passwordService, never()).verifyPassword(any(VerifyPasswordRequest.class));
        verify(sessionFeignClient, never()).issueTokens(any(AuthDetails.class));
        verify(jwtService, never()).setAuthCookies(any(HttpServletResponse.class), anyString(), anyString());
    }

}
