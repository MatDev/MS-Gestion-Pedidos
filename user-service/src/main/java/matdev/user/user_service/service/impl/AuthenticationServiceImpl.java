package matdev.user.user_service.service.impl;


import matdev.user.user_service.security.token.TenantUsernamePasswordAuthenticationToken;
import org.springframework.http.HttpHeaders;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.authentication.AuthenticationManager;

import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import matdev.user.user_service.dto.request.AuthenticationRequestDto;
import matdev.user.user_service.dto.response.JwtAuthResponse;
import matdev.user.user_service.entity.Token;
import matdev.user.user_service.entity.Usuario;
import matdev.user.user_service.exeption.NotFoundException;
import matdev.user.user_service.repository.TokenRepository;
import matdev.user.user_service.repository.UsuarioRepository;
import matdev.user.user_service.security.service.JwtService;
import matdev.user.user_service.service.AuthenticationService;
import matdev.user.user_service.utils.constants.AuthConstant;
import matdev.user.user_service.utils.enums.TokenType;



@Service
@RequiredArgsConstructor
public class AuthenticationServiceImpl implements AuthenticationService{

    private static final Logger LOGGER = LoggerFactory.getLogger(AuthenticationServiceImpl.class);
    private final TokenRepository tokenRepository;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final UsuarioRepository usuarioRepository;



    @Override
    public JwtAuthResponse auth(AuthenticationRequestDto request) {
        LOGGER.info("Authenticating user with email: {}", request.getUsername());
        Usuario usuario = usuarioRepository.findByEmailAndTenantId(
                request.getUsername(),
                request.getTenantId()
        ).orElseThrow(() -> new NotFoundException("BadCredentials"));

        LOGGER.info("User found with email: {}", request.getUsername());
        authenticateUser(request.getUsername(), request.getPassword(), request.getTenantId());
        LOGGER.info("Se inicia el proceso del JWT Token");
        String token = jwtService.generateToken(usuario);
        String refreshToken = jwtService.generateRefreshToken(usuario);
        LOGGER.info("User authenticated with email: {}", request.getUsername());
        revokeAllUserTOkens(usuario);
        saveUserToken(usuario, token);


        return buildAuthResponse(token, refreshToken);




    }

    private JwtAuthResponse buildAuthResponse(String token, String refreshToken) {
        LOGGER.info("Building JWT Auth Response");
        return JwtAuthResponse.builder()
        .accessToken(token)
        .refreshToken(refreshToken)
        .build();
    }

    private void saveUserToken(Usuario usuario, String jwttoken) {
        Token token = Token.builder()
        .usuario(usuario)
        .tokenType(TokenType.BEARER)
        .accessToken(jwttoken)
        .expired(false)
        .revoked(false)
        .build();
        try {
            tokenRepository.save(token);
        } catch (DataIntegrityViolationException e) {
            LOGGER.error("Error saving token for user with id: {}", usuario.getId());
           Token exisToken=tokenRepository.findByAccessToken(jwttoken).orElseThrow(()-> new RuntimeException("Token not found"));
           exisToken.setExpired(false);
           exisToken.setRevoked(false);
           tokenRepository.save(exisToken);

        }


    }

    private void revokeAllUserTOkens(Usuario usuario) {
        var validUserTokens = tokenRepository.findAllValidUserTokens(usuario.getId());
        if (validUserTokens.isEmpty()) {
            LOGGER.info("No tokens to revoke for user with id: {}", usuario.getId());
            return;
        }
        validUserTokens.forEach(token -> {
            token.setRevoked(true);
            token.setExpired(true);

        });
        tokenRepository.saveAll(validUserTokens);
        LOGGER.info("Revoked all tokens for user with id: {}", usuario.getId());
    }

    private void authenticateUser(String username, String password, String tenantId) {
        LOGGER.info("Authenticating user with email: {}", username);
        authenticationManager.authenticate(
           new TenantUsernamePasswordAuthenticationToken(
                   username,
                   password,
                   tenantId
           )
        );
        LOGGER.info("User authenticated successfully with email: {}", username);
   }

    @Override
    public void refreshToken(HttpServletRequest request, HttpServletResponse response) throws IOException {

        String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (isInvalidAuthHeader(authHeader)) {
          LOGGER.warn("Invalid authorization header during token refresh");
          return;
        }

        String refreshToken = extractToken(authHeader);
        String userEmail = jwtService.extractUsername(refreshToken);
        String tenantId = jwtService.extractTenantId(refreshToken);

        if (userEmail != null) {
          processRefreshToken(response, refreshToken, userEmail, tenantId);
        }
    }

    private boolean isInvalidAuthHeader(String authHeader) {
        return authHeader == null || !authHeader.startsWith(AuthConstant.BEARER_PREFIX);
    }

    private String extractToken(String authHeader) {
        return authHeader.substring(AuthConstant.BEARER_PREFIX.length());
    }

    private void processRefreshToken(HttpServletResponse response, String refreshToken, String userEmail, String tenantId) throws IOException {
        Usuario user = usuarioRepository.findByEmailAndTenantId(userEmail, tenantId)
                .orElseThrow(() -> new NotFoundException("Usuario no encontrado"));

        if (jwtService.isTokenValid(refreshToken, user)) {
          String accessToken = jwtService.generateToken(user);
          revokeAllUserTOkens(user);
          saveUserToken(user, accessToken);
          writeAuthResponse(response, buildAuthResponse(accessToken, refreshToken));
        }
    }

    private void writeAuthResponse(HttpServletResponse response, JwtAuthResponse authResponse) throws IOException {
    LOGGER.info("Writing authentication response to the output stream");
    response.setContentType(AuthConstant.CONTENT_TYPE_JSON);
    new ObjectMapper().writeValue(response.getOutputStream(), authResponse);
  }

}
