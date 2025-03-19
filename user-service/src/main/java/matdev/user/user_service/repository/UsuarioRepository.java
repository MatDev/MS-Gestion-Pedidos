package matdev.user.user_service.repository;

import java.util.Optional;

import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;

import matdev.user.user_service.entity.Usuario;



public interface UsuarioRepository extends PagingAndSortingRepository<Usuario, Long> , CrudRepository<Usuario, Long>{
    Optional<Usuario> findByEmail(String email);

}
