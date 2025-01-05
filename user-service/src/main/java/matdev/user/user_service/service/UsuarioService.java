package matdev.user.user_service.service;

import java.util.Optional;

import matdev.user.user_service.dto.UsuarioDto;
import matdev.user.user_service.dto.request.RegisterRequest;


public interface UsuarioService {
    UsuarioDto crearUsuario(RegisterRequest registerRequest);
    Optional<UsuarioDto> obtenerUsuarioPorEmail(String email);
    Optional<UsuarioDto> obtenerUsuarioPorId(Long id);
    void eliminarUsuarioPorId(Long id);
    UsuarioDto actualizarUsuario(Long id,UsuarioDto usuario);

}
