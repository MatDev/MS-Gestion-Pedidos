package matdev.user.user_service.security.filters;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;


import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import matdev.user.user_service.exeption.ExpireTokenException;
import matdev.user.user_service.exeption.InvalidTokenException;
import matdev.user.user_service.repository.TokenRepository;
import matdev.user.user_service.security.service.JwtService;
import matdev.user.user_service.utils.constants.AuthConstant;

@Component
@RequiredArgsConstructor
public class JwtAuthFilter extends OncePerRequestFilter {
    private static final Logger LOGGER = LoggerFactory.getLogger(JwtAuthFilter.class);
    private final JwtService jwtService;
    private final UserDetailsService userDetailsService;
    private final TokenRepository tokenRepository;


    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull FilterChain filterChain)
            throws ServletException, IOException {

       LOGGER.debug("processing authentication for '{}'", request.getRequestURL());
        if(isAuthPath(request)){
            filterChain.doFilter(request, response);
            return;
        }

        final String authHeader = request.getHeader(AuthConstant.AUTHORIZATION_HEADER);
        if (isInvalidAuthHeader(authHeader)) {
            LOGGER.debug("Invalid auth header, proceeding with filter chain");
            filterChain.doFilter(request, response);
        return;
        }

        final String jwt = extractJwtFromHeader(authHeader);
        final String userEmail;

        try {
            userEmail = jwtService.extractUsername(jwt);
        } catch (ExpireTokenException e) {
            LOGGER.warn("Expired token: {}", e.getMessage());
            handleException(response, e.getMessage(), HttpServletResponse.SC_UNAUTHORIZED);
            return;
        } catch (InvalidTokenException e) {
            LOGGER.warn("Invalid token: {}", e.getMessage());
            handleException(response, e.getMessage(), HttpServletResponse.SC_BAD_REQUEST);
            return;
        }

        if (userEmail != null && isNotAuthenticated()) {
            processTokenAuthentication(request, jwt, userEmail);
        }
      
        filterChain.doFilter(request, response);


    }


     private void processTokenAuthentication(HttpServletRequest request, String jwt, String userEmail) {
    UserDetails userDetails = userDetailsService.loadUserByUsername(userEmail);
    boolean isTokenValid = tokenRepository.findByAccessToken(jwt)
            .map(t -> !t.isExpired() && !t.isRevoked())
            .orElse(false);

    logger.debug("Processing token authentication for user: " + userEmail);
    logger.debug("Is token valid: " + isTokenValid);

    if (jwtService.isTokenValid(jwt, userDetails) && isTokenValid) {
      UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
              userDetails, null, userDetails.getAuthorities()
      );
      authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
      SecurityContextHolder.getContext().setAuthentication(authToken);
      logger.debug("Authentication set in SecurityContext for user: " + userEmail);
    } else {
      logger.debug("Token validation failed for user: " + userEmail);
    }
  }
    private boolean isNotAuthenticated(){
        return SecurityContextHolder.getContext().getAuthentication() == null;
    }


    private void handleException(HttpServletResponse response, String message, int status) throws IOException {
        response.setStatus(status);
        response.setContentType(AuthConstant.CONTENT_TYPE_JSON);
        response.getWriter().write("{\"error\": \"" + message + "\"}");
        response.getWriter().flush();
      }

    private String extractJwtFromHeader(String authHeader) {
        return authHeader.substring(AuthConstant.BEARER_PREFIX.length());
    }


    private boolean isInvalidAuthHeader(String authHeader) {
        return authHeader == null || !authHeader.startsWith(AuthConstant.BEARER_PREFIX);
    }


    private boolean isAuthPath(HttpServletRequest request) {
        return request.getServletPath().equals("/auth/login")||
        request.getServletPath().equals("/auth/refresh-token");
    }

}
