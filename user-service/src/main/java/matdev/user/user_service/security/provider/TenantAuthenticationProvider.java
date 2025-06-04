package matdev.user.user_service.security.provider;

import lombok.RequiredArgsConstructor;
import matdev.user.user_service.entity.Usuario;
import matdev.user.user_service.repository.UsuarioRepository;
import matdev.user.user_service.security.token.TenantUsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;


import java.util.Collection;
import java.util.Collections;



@RequiredArgsConstructor

public class TenantAuthenticationProvider implements AuthenticationProvider {
    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        if (!(authentication instanceof TenantUsernamePasswordAuthenticationToken tenantAuth)) {
            throw new BadCredentialsException("Invalid authentication type");
        }

        String email = tenantAuth.getName();
        String password = tenantAuth.getCredentials().toString();
        String tenantId = tenantAuth.getTenantId();

        Usuario usuario = usuarioRepository.findByEmailAndTenantId(email, tenantId)
                .orElseThrow(() -> new BadCredentialsException("Usuario no encontrado para el tenant"));

        if (!passwordEncoder.matches(password, usuario.getPassword())) {
            throw new BadCredentialsException("Credenciales inválidas");
        }

        // Crear authorities basado en el rol
        Collection<GrantedAuthority> authorities = Collections.singletonList(
                new SimpleGrantedAuthority("ROLE_" + usuario.getRole())
        );

        // CAMBIO CRÍTICO: Retornar el token personalizado autenticado
        return new TenantUsernamePasswordAuthenticationToken(
                usuario.getEmail(),
                null, // Limpiar password por seguridad
                tenantId,
                authorities
        );
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return TenantUsernamePasswordAuthenticationToken.class.isAssignableFrom(authentication);
    }

}
