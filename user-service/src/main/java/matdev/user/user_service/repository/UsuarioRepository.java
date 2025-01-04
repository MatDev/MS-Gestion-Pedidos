package matdev.user.user_service.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import matdev.user.user_service.entity.Usuario;



public interface UsuarioRepository extends JpaRepository<Usuario,Long>{
    Optional<Usuario> findByEmail(String email);

}
