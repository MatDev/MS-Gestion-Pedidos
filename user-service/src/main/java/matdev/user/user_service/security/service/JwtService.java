package matdev.user.user_service.security.service;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import matdev.user.user_service.entity.Usuario;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import matdev.user.user_service.exeption.ExpireTokenException;
import matdev.user.user_service.exeption.InvalidTokenException;
import matdev.user.user_service.utils.constants.AuthConstant;

@Service
@ConfigurationProperties(prefix = "user-service.security.jwt")
public class JwtService {

    private static final Logger LOGGER = LoggerFactory.getLogger(JwtService.class);

    @Value("${user-service.security.jwt.secret-key}")
    private String secretKey;
    @Value("${user-service.security.jwt.expiration}")
    private long jwtExpiration;
    @Value("${user-service.security.jwt.refresh-token.expiration}")
    private long refreshExpiration;
   
    //proceso de extraccion de claims

    //funcion para extraer el username del token
    public String extractUsername(String token){
        // se pasa a la funcion extractClaim el token y la funcion que se encargara de extraer el claim necesario
        return extractClaim(token, Claims::getSubject);
    }
    // funcion para extraer el tenantId del token
    public String extractTenantId(String token){
        // se pasa a la funcion extractClaim el token y la funcion que se encargara de extraer el claim necesario
        return extractClaim(token, claims -> claims.get(AuthConstant.TENANT_ID_CLAIM, String.class));
    }

    // funcion para extrar los claims del token
    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    private Claims extractAllClaims(String token) {
    try {
      return Jwts
              .parserBuilder()
              .setSigningKey(getSignInKey())
              .build()
              .parseClaimsJws(token)
              .getBody();
    } catch (ExpiredJwtException e) {
      LOGGER.warn("Expired token: {}", e.getMessage());
      throw new ExpireTokenException("EXPIRED TOKEN");
    } catch (IllegalArgumentException e) {
      LOGGER.warn("Invalid token: {}", e.getMessage());
      throw new InvalidTokenException("INVALID TOKEN");
    }
  }


  // proceso de creacion de tokens
    public String generateToken(UserDetails userDetails) {
    Map<String, Object> claims = new HashMap<>();
    claims.put(AuthConstant.ROLE_CLAIM, userDetails.getAuthorities().stream()
            .map(GrantedAuthority::getAuthority)
            .findFirst().orElseThrow(() -> {
              LOGGER.error("ROLE NOT FOUND");
              return new RuntimeException("ROLE NOT FOUND");
            }));

    return generateToken(claims, userDetails);
  }

  public String generateToken (Map<String,Object> claims, UserDetails userDetails){
   return buildToken(claims, userDetails, jwtExpiration);
  }

  private String buildToken(Map<String, Object> claims, UserDetails userDetails, long expiration) {
    LOGGER.info("Building token for user: {}", userDetails.getUsername());
    return Jwts.builder()
            .setClaims(claims)
            .setSubject(userDetails.getUsername())
            .claim(AuthConstant.TENANT_ID_CLAIM, ((Usuario) userDetails).getTenantId())
            .setIssuedAt(new java.util.Date(System.currentTimeMillis()))
            .setExpiration(new java.util.Date(System.currentTimeMillis() + expiration))
            .signWith(getSignInKey(), SignatureAlgorithm.HS256)
            .compact();
  }

  public boolean isTokenValid(String token, UserDetails userDetails) {
    final String username = extractUsername(token);
    boolean isValid = (username.equals(userDetails.getUsername())) && !isTokenExpired(token);
    LOGGER.info("Token validation for user {}: {}", username, isValid);
    return isValid;
  }

  private boolean isTokenExpired(String token) {
    return extractExpiration(token).before(new Date());
  }

  private Date extractExpiration(String token) {
    return extractClaim(token, Claims::getExpiration);
  }


  public String generateRefreshToken(UserDetails userDetails) {
    return buildToken(new HashMap<>(), userDetails, refreshExpiration);
  }






  // funcion para obtener la clave secreta
  private Key getSignInKey() {
    byte[] keyBytes = Decoders.BASE64.decode(secretKey);
    return Keys.hmacShaKeyFor(keyBytes);
  }
  
}
