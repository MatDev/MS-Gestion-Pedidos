package matdev.user.user_service.service;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import matdev.user.user_service.dto.UsuarioDto;
import matdev.user.user_service.dto.request.RegisterRequest;
/**
 * Servicio de usuario multi-tenant.
 * Todas las operaciones se ejecutan usando el tenantId extraído desde el contexto.
 */

public interface UsuarioService {
    UsuarioDto crearUsuario(RegisterRequest registerRequest);
    Optional<UsuarioDto> obtenerUsuarioPorEmail(String email);
    Optional<UsuarioDto> obtenerUsuarioPorId(Long id);
    void eliminarUsuarioPorId(Long id);
    UsuarioDto actualizarUsuario(Long id,UsuarioDto usuario);

    Page<UsuarioDto> obtenerUsuarios(Pageable pageable);

}
