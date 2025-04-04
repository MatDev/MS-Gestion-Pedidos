package matdev.user.user_service.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import matdev.user.user_service.entity.Token;

public interface TokenRepository extends JpaRepository<Token, Long> {
    @Query("""
        SELECT t FROM Token t 
        WHERE t.usuario.id = :userId AND (t.expired = false AND t.revoked = false)
        """)
    List< Token> findAllValidUserTokens(Long userId);
    Optional <Token> findByAccessToken(String token);



    


}
