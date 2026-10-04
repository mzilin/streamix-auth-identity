package com.mariuszilinskas.streamix.auth.identity.service;

import com.mariuszilinskas.streamix.auth.identity.dto.LoginRequest;
import jakarta.servlet.http.HttpServletResponse;

public interface AuthService {

    void authenticateUser(LoginRequest request, HttpServletResponse response);

}
