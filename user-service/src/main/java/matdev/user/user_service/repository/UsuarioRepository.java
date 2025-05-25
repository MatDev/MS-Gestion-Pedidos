package matdev.user.user_service.repository;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import matdev.user.user_service.entity.Usuario;



public interface UsuarioRepository extends JpaRepository<Usuario,Long> {
    Optional<Usuario> findByEmailAndTenantId(String email, String tenantId);
    Optional<Usuario> findByIdAndTenantId(Long id, String tenantId);
    Page<Usuario> findAllByTenantId(String tenantId, Pageable pageable);

}
