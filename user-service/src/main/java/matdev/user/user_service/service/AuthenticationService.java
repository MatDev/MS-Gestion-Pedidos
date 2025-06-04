package matdev.user.user_service.service;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import matdev.user.user_service.dto.request.AuthenticationRequestDto;
import matdev.user.user_service.dto.response.JwtAuthResponse;

import java.io.IOException;

public interface AuthenticationService {
    JwtAuthResponse auth(AuthenticationRequestDto request);
    void refreshToken(HttpServletRequest request, HttpServletResponse response) throws IOException;
} 
