package matdev.user.user_service.controllers;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import matdev.user.user_service.dto.request.AuthenticationRequestDto;
import matdev.user.user_service.dto.response.JwtAuthResponse;

import matdev.user.user_service.service.impl.AuthenticationServiceImpl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;


@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthenticationControllers {
    private static final Logger LOGGER = LoggerFactory.getLogger(AuthenticationControllers.class);
    private final AuthenticationServiceImpl authenticationService;

    @PostMapping("/login")
    public ResponseEntity<JwtAuthResponse> login(@RequestBody AuthenticationRequestDto request) {
        LOGGER.info("Received request to authenticate user with email: {}", request.getUsername());
        JwtAuthResponse response = authenticationService.authentication(request);
        LOGGER.info("User authenticated successfully with email: {}", request.getUsername());
        return ResponseEntity.ok(response);
    }

    @PostMapping("/refresh-token")
    public void refreshToken(HttpServletRequest request, HttpServletResponse response) throws Exception { 
    LOGGER.info("Received request to refresh token");
    authenticationService.refreshToken(request, response);
    LOGGER.info("Token refreshed successfully");
    }

    


}
