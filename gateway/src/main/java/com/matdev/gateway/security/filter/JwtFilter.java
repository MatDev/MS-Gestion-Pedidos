package com.matdev.gateway.security.filter;

import com.matdev.gateway.security.service.JwtService;
import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;


@RequiredArgsConstructor
public class JwtFilter extends OncePerRequestFilter {
    private final JwtService jwtService;
    private static final Logger LOGGER = LoggerFactory.getLogger(JwtFilter.class);
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        final String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            LOGGER.info("Authorization header is missing or does not start with 'Bearer '");
            return;
        }

        final String token = authHeader.substring(7);
        if (!jwtService.isValid(token)) {
            LOGGER.info("Invalid JWT token");
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }


        request.setAttribute("X-Tenant-ID", jwtService.extractTenantId(token));
        request.setAttribute("X-User-Role", jwtService.extractRole(token));
        request.setAttribute("X-User-Email", jwtService.extractUsername(token));
        LOGGER.info("Jwt token");

        filterChain.doFilter(request, response);
    }
}
