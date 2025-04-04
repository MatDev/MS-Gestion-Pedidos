package matdev.user.user_service.security.service;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.stereotype.Service;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import matdev.user.user_service.repository.TokenRepository;
import matdev.user.user_service.utils.constants.AuthConstant;


@Service
@RequiredArgsConstructor
public class LogoutService implements LogoutHandler{
    private final TokenRepository tokenRepository;
    @Override

    public void logout(
        HttpServletRequest request,
        HttpServletResponse response,
        Authentication authentication
    ){
       final String authHeader = request.getHeader(AuthConstant.AUTHORIZATION_HEADER);
    final String jwt;
    if (authHeader == null || !authHeader.startsWith(AuthConstant.BEARER_PREFIX)) {
      return;
    }
    jwt = authHeader.substring(AuthConstant.BEARER_PREFIX.length());
    var storedToken = tokenRepository.findByToken(jwt).orElse(null);

    if (storedToken != null) {
      storedToken.setExpired(true);
      storedToken.setRevoked(true);
      tokenRepository.save(storedToken);
      SecurityContextHolder.clearContext();
    }
    }
  
   

}
