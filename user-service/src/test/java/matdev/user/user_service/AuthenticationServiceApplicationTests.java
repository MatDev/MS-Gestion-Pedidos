package matdev.user.user_service;


import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import matdev.user.user_service.dto.request.AuthenticationRequestDto;
import matdev.user.user_service.dto.response.JwtAuthResponse;
import matdev.user.user_service.entity.Usuario;
import matdev.user.user_service.exeption.NotFoundException;
import matdev.user.user_service.repository.TokenRepository;
import matdev.user.user_service.repository.UsuarioRepository;
import matdev.user.user_service.security.service.JwtService;
import matdev.user.user_service.service.impl.AuthenticationServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.http.HttpHeaders;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;


import java.util.Optional;

import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthenticationServiceApplicationTests {

    @Mock
    private UsuarioRepository usuarioRepository;
    @Mock private TokenRepository tokenRepository;
    @Mock private JwtService jwtService;
    @Mock private AuthenticationManager authenticationManager;

    @InjectMocks
    private AuthenticationServiceImpl authenticationService;

    @Test
    void testAuthenticationSuccess() {
        var request = new AuthenticationRequestDto("user@example.com", "password", "tenant-123");

        Usuario usuario = new Usuario();
        usuario.setEmail("user@example.com");
        usuario.setTenantId("tenant-123");
        usuario.setPassword("hashed");

        when(usuarioRepository.findByEmailAndTenantId("user@example.com", "tenant-123"))
                .thenReturn(Optional.of(usuario));
        when(jwtService.generateToken(usuario)).thenReturn("access-token");
        when(jwtService.generateRefreshToken(usuario)).thenReturn("refresh-token");

        JwtAuthResponse response = authenticationService.authentication(request);

        assertThat(response.getAccessToken()).isEqualTo("access-token");
        assertThat(response.getRefreshToken()).isEqualTo("refresh-token");

        verify(authenticationManager).authenticate(any());
        verify(tokenRepository).save(any());
    }

    @Test
    void testAuthenticationUserNotFound() {
        var request = new AuthenticationRequestDto("missing@example.com", "x", "tenant-x");

        when(usuarioRepository.findByEmailAndTenantId("missing@example.com", "tenant-x"))
                .thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> authenticationService.authentication(request));
    }

    @Test
    void testRefreshTokenValid() throws Exception {
        String token = "refresh-token";
        String email = "user@example.com";
        String tenant = "tenant-123";

        Usuario usuario = new Usuario();
        usuario.setEmail(email);
        usuario.setTenantId(tenant);

        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        ServletOutputStream outputStream = mock(ServletOutputStream.class);

        when(request.getHeader(HttpHeaders.AUTHORIZATION)).thenReturn("Bearer " + token);
        when(jwtService.extractUsername(token)).thenReturn(email);
        when(jwtService.extractTenantId(token)).thenReturn(tenant);
        when(usuarioRepository.findByEmailAndTenantId(email, tenant)).thenReturn(Optional.of(usuario));
        when(jwtService.isTokenValid(token, usuario)).thenReturn(true);
        when(jwtService.generateToken(usuario)).thenReturn("new-access-token");
        when(response.getOutputStream()).thenReturn(outputStream);

        authenticationService.refreshToken(request, response);

        verify(tokenRepository).save(any());
        verify(response).setContentType("application/json");
    }

    @Test
    void testRefreshTokenWithInvalidHeader() throws Exception {
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);

        when(request.getHeader(HttpHeaders.AUTHORIZATION)).thenReturn(null);

        authenticationService.refreshToken(request, response);

        verify(response, never()).setContentType(any());
    }

    @Test
    void testRefreshTokenUserNotFound() {
        String token = "refresh-token";

        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);

        when(request.getHeader(HttpHeaders.AUTHORIZATION)).thenReturn("Bearer " + token);
        when(jwtService.extractUsername(token)).thenReturn("user@example.com");
        when(jwtService.extractTenantId(token)).thenReturn("tenant-x");
        when(usuarioRepository.findByEmailAndTenantId("user@example.com", "tenant-x")).thenReturn(Optional.empty());
        assertThrows(NotFoundException.class, () -> {
            authenticationService.refreshToken(request, response);
        });

        verify(usuarioRepository).findByEmailAndTenantId("user@example.com", "tenant-x");
    }

    @Test
    void testRefreshTokenInvalidToken() throws Exception {
        String token = "refresh-token";
        String email = "user@example.com";
        String tenant = "tenant-123";

        Usuario usuario = new Usuario();
        usuario.setEmail(email);
        usuario.setTenantId(tenant);

        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);

        when(request.getHeader(HttpHeaders.AUTHORIZATION)).thenReturn("Bearer " + token);
        when(jwtService.extractUsername(token)).thenReturn(email);
        when(jwtService.extractTenantId(token)).thenReturn(tenant);
        when(usuarioRepository.findByEmailAndTenantId(email, tenant)).thenReturn(Optional.of(usuario));
        when(jwtService.isTokenValid(token, usuario)).thenReturn(false);

        authenticationService.refreshToken(request, response);

        verify(jwtService).isTokenValid(token, usuario);
        verify(tokenRepository, never()).save(any());
    }
}
