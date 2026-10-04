package com.mariuszilinskas.streamix.auth.identity.service;

import jakarta.servlet.http.HttpServletResponse;

public interface JwtService {

    void setAuthCookies(HttpServletResponse response, String accessToken, String refreshToken);

}
