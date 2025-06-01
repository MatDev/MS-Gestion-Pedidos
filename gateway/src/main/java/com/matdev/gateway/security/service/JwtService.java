package com.matdev.gateway.security.service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Date;

@Service
@ConfigurationProperties(prefix = "gateway.security.jwt")
public class JwtService {

    private static final Logger LOGGER = LoggerFactory.getLogger(JwtService.class);

    @Value("${gateway.security.jwt.secret-key}")
    public String secretKey;


    private Key getSignInKey() {
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        return Keys.hmacShaKeyFor(keyBytes);
    }
    public String extractTenantId(String token) {
        return extractAllClaims(token).get("tenantId", String.class);
    }

    public String extractRole(String token) {
        return extractAllClaims(token).get("role", String.class);
    }

    public String extractUsername(String token) {
        return extractAllClaims(token).getSubject();
    }

    public Claims extractAllClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSignInKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }


    public boolean isValid(String token) {
        try {
            Claims claims = extractAllClaims(token);

            // Validar expiración explícita
            Date expiration = claims.getExpiration();
            if (expiration == null || expiration.before(new Date())) {
                LOGGER.warn("Token expirado");
                return false;
            }

            // Validar presencia de claims obligatorios
            if (claims.getSubject() == null ||
                    claims.get("tenantId", String.class) == null ||
                    claims.get("role", String.class) == null) {
                LOGGER.warn("Claims requeridos ausentes: sub, tenantId o role");
                return false;
            }

            return true;

        } catch (JwtException | IllegalArgumentException e) {
            LOGGER.warn("Token inválido: {}", e.getMessage());
            return false;
        }
    }
    
    
}
