package com.matdev.gateway;

import com.matdev.gateway.security.service.JwtService;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Base64;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;
class JwtServiceTest {
    private JwtService jwtService;

    // Clave generada en base64 (256 bits)
    private final String secretKey = "bG9jYWxfY29tZWRvcl9zZWNyZXRrZXlfZm9yX2dhdGV3YXlfamV0MTIzIQ==";

    private String validToken;

    @BeforeEach
    void setUp() {
        jwtService = new JwtService();
        jwtService.secretKey = secretKey;

        Key key = Keys.hmacShaKeyFor(Base64.getDecoder().decode(secretKey));

        validToken = Jwts.builder()
                .setSubject("user@example.com")
                .claim("tenantId", "tenant-x")
                .claim("role", "ADMIN")
                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 5)) // 5 min
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    @Test
    void shouldReturnFalseIfTokenIsInvalid() {
        String invalidToken = "invalid.token.value";
        assertFalse(jwtService.isValid(invalidToken));
    }

    @Test
    void shouldThrowIfMalformedToken() {
        assertThrows(Exception.class, () -> jwtService.extractAllClaims("bad.token"));
    }

    @Test
    void shouldExtractTenantIdFromValidToken() {
        String tenantId = jwtService.extractTenantId(validToken);
        assertEquals("tenant-x", tenantId);
    }

    @Test
    void shouldExtractRoleFromValidToken() {
        String role = jwtService.extractRole(validToken);
        assertEquals("ADMIN", role);
    }

    @Test
    void shouldExtractUsernameFromValidToken() {
        String username = jwtService.extractUsername(validToken);
        assertEquals("user@example.com", username);
    }

    @Test
    void shouldValidateTokenSuccessfully() {
        assertTrue(jwtService.isValid(validToken));
    }
}
