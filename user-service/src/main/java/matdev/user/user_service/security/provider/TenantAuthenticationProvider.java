package matdev.user.user_service.security.provider;

import lombok.RequiredArgsConstructor;
import matdev.user.user_service.entity.Usuario;
import matdev.user.user_service.repository.UsuarioRepository;
import matdev.user.user_service.security.token.TenantUsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
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

        return new UsernamePasswordAuthenticationToken(usuario, null, List.of());
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return TenantUsernamePasswordAuthenticationToken.class.isAssignableFrom(authentication);
    }


}
