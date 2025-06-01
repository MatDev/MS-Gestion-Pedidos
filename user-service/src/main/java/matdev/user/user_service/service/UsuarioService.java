package matdev.user.user_service.service;

import java.util.Optional;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import matdev.user.user_service.dto.UsuarioDto;
import matdev.user.user_service.dto.request.RegisterRequest;
import org.springframework.data.web.PageableDefault;

/**
 * Servicio de usuario multi-tenant.
 * Todas las operaciones se ejecutan usando el tenantId extraído desde el contexto.
 */

public interface UsuarioService {
    UsuarioDto crearUsuario(RegisterRequest registerRequest);
    Optional<UsuarioDto> obtenerUsuarioPorEmail(@NotNull @Email @NotEmpty String email);
    Optional<UsuarioDto> obtenerUsuarioPorId(@NotNull Long id);
    void eliminarUsuarioPorId(@NotNull Long id);
    UsuarioDto actualizarUsuario(@NotNull Long id, @NotNull UsuarioDto usuario);

    Page<UsuarioDto> obtenerUsuarios(Pageable pageable);

}
