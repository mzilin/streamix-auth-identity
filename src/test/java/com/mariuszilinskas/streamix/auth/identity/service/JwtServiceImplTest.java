package com.mariuszilinskas.streamix.auth.identity.service;

import com.mariuszilinskas.streamix.auth.identity.util.IdentityUtils;
import com.mariuszilinskas.streamix.auth.identity.util.TestUtils;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockHttpServletResponse;

import java.lang.reflect.Field;
import java.util.Collection;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
public class JwtServiceImplTest {

    @Mock
    private HttpServletResponse mockResponse;

    @InjectMocks
    private JwtServiceImpl jwtService;

    private static final String accessToken = TestUtils.validAccessToken;
    private static final String refreshToken = TestUtils.validRefreshToken;

    // ------------------------------------

    @BeforeEach
    void setup() throws NoSuchFieldException, IllegalAccessException {
        setPrivateField(jwtService, "environment", IdentityUtils.PRODUCTION_ENV);
        mockResponse = new MockHttpServletResponse();
    }

    private void setPrivateField(Object targetObject, String fieldName, Object value)
            throws NoSuchFieldException, IllegalAccessException {
        Field field = targetObject.getClass().getDeclaredField(fieldName);
        field.setAccessible(true);
        field.set(targetObject, value);
    }

    // ------------------------------------

    @Test
    void testSetAuthCookies() {
        // Act
        jwtService.setAuthCookies(mockResponse, accessToken, refreshToken);

        // Retrieve all cookies set on the response
        Collection<String> setCookieHeaders = ((MockHttpServletResponse) mockResponse).getHeaders("Set-Cookie");
        boolean accessTokenFound = false, refreshTokenFound = false;

        // Assert
        for (String header : setCookieHeaders) {
            if (header.contains(IdentityUtils.ACCESS_TOKEN_NAME)) {
                accessTokenFound = true;
                assertTrue(header.contains("HttpOnly"));
                assertTrue(header.contains("Path=/"));
                assertTrue(header.contains("Secure"));
                assertTrue(header.contains("SameSite=None"));
            }
            if (header.contains(IdentityUtils.REFRESH_TOKEN_NAME)) {
                refreshTokenFound = true;
                assertTrue(header.contains("HttpOnly"));
                assertTrue(header.contains("Path=/"));
                assertTrue(header.contains("Secure"));
                assertTrue(header.contains("SameSite=None"));
            }
        }

        assertTrue(accessTokenFound);
        assertTrue(refreshTokenFound);
    }

}
