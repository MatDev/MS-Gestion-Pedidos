package matdev.user.user_service.config;

import java.util.Arrays;
import java.util.Collections;


import matdev.user.user_service.security.provider.TenantAuthenticationProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;


import lombok.RequiredArgsConstructor;
import matdev.user.user_service.repository.UsuarioRepository;

@Configuration
@RequiredArgsConstructor
public class SecurityBeansConfig {

    private final UsuarioRepository usuarioRepository;

    @Bean
    PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }



    @Bean
    public AuthenticationProvider authenticationProvider() {
        return new TenantAuthenticationProvider(usuarioRepository, passwordEncoder());
    }


    @Bean
    public AuthenticationManager authenticationManager(TenantAuthenticationProvider provider) {
        return new ProviderManager(Collections.singletonList(provider));
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedOriginPatterns(Collections.singletonList("*"));
        config.setAllowedMethods(Collections.singletonList("*"));
        config.setAllowCredentials(true);
        config.setAllowedHeaders(Arrays.asList("*"));
        config.setExposedHeaders(Arrays.asList("*"));
        config.setMaxAge(3600L); // 1 hora
        source.registerCorsConfiguration("/**", config);
        return source;
    }



}
