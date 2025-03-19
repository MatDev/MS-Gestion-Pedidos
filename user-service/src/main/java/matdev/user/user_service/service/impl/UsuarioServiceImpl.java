package matdev.user.user_service.service.impl;

import java.util.Optional;
import java.util.logging.Logger;

import org.modelmapper.ModelMapper;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.ws.rs.InternalServerErrorException;
import jakarta.ws.rs.NotFoundException;
import lombok.RequiredArgsConstructor;
import matdev.user.user_service.dto.UsuarioDto;
import matdev.user.user_service.dto.request.RegisterRequest;
import matdev.user.user_service.entity.Usuario;
import matdev.user.user_service.exeption.PasswordIsNotEquals;
import matdev.user.user_service.repository.UsuarioRepository;
import matdev.user.user_service.service.UsuarioService;

@Service
@RequiredArgsConstructor
public class UsuarioServiceImpl implements UsuarioService{ 
    private final UsuarioRepository usuarioRepository;
    private static final Logger LOGGER = Logger.getLogger(UsuarioServiceImpl.class.getName());
    private final ModelMapper modelMapper;
    private final PasswordEncoder passwordEncoder;
    @Override
    public UsuarioDto crearUsuario(RegisterRequest registerRequest) {
        LOGGER.info("Creando usuario");
        
        if (isNotEquealPassword(registerRequest.getPassword(), registerRequest.getConfirmPassword())) {
            LOGGER.severe("Error al crear usuario: Las contraseñas no coinciden");
            throw new PasswordIsNotEquals("Las contraseñas no coinciden");
        }

        try {
            Usuario usuario = convertRegisterRequestToEntity(registerRequest);
            usuario.setPassword(passwordEncoder.encode(registerRequest.getPassword()));
            usuario = usuarioRepository.save(usuario);
            LOGGER.info("Usuario creado con éxito con id: " + usuario.getId());
            return convertEntityToDto(usuario);

        } catch (Exception e) {
            LOGGER.severe("Error al crear usuario: " + e.getMessage());
            throw new InternalServerErrorException("Error al crear usuario");
        }
    }
    @Override
    public Optional<UsuarioDto> obtenerUsuarioPorEmail(String email) {
        LOGGER.info("Obteniendo usuario por email: " + email);
        try {
            Optional<Usuario> usuario = usuarioRepository.findByEmail(email);
            if (usuario.isPresent()) {
                LOGGER.info("Usuario encontrado con éxito con id: " + usuario.get().getId());
                return Optional.of(convertEntityToDto(usuario.get()));
            } else {
                LOGGER.info("Usuario no encontrado con email: " + email);
                throw new NotFoundException("Usuario no encontrado con email: " + email);
            }
        } catch (Exception e) {
            LOGGER.severe("Error al obtener usuario por email: " + e.getMessage());
            throw new InternalServerErrorException("Error al obtener usuario por email");
        }
    }
    @Override
    public Optional<UsuarioDto> obtenerUsuarioPorId(Long id) {
        LOGGER.info("Obteniendo usuario por id: " + id);
        try {
            Optional<Usuario> usuario = usuarioRepository.findById(id);
            if (usuario.isPresent()) {
                LOGGER.info("Usuario encontrado con éxito con id: " + usuario.get().getId());
                return Optional.of(convertEntityToDto(usuario.get()));
            } else {
                LOGGER.info("Usuario no encontrado con id: " + id);
                throw new NotFoundException("Usuario no encontrado con id: " + id);
            }
        } catch (Exception e) {
            LOGGER.severe("Error al obtener usuario por id: " + e.getMessage());
            throw new InternalServerErrorException("Error al obtener usuario por id");
        }
    }
    @Override
    @Transactional
    public void eliminarUsuarioPorId(Long id) {
        LOGGER.info("Eliminando usuario por id: " + id);
        try {
            usuarioRepository.deleteById(id);
            LOGGER.info("Usuario eliminado con éxito con id: " + id);
        } catch (Exception e) {
            LOGGER.severe("Error al eliminar usuario por id: " + e.getMessage());
            throw new InternalServerErrorException("Error al eliminar usuario por id");
        }
    }
    @Override
    public UsuarioDto actualizarUsuario(Long id, UsuarioDto usuario) {
        LOGGER.info("Actualizando usuario por id: " + id);
        try {
            Optional<Usuario> usuarioOptional = usuarioRepository.findById(id);
            if (usuarioOptional.isPresent()) {
                Usuario usuarioEntity = usuarioOptional.get();
                usuarioEntity.setEmail(usuario.getEmail());
                usuarioEntity.setRole(usuario.getRole());
                usuarioEntity = usuarioRepository.save(usuarioEntity);
                LOGGER.info("Usuario actualizado con éxito con id: " + usuarioEntity.getId());
                return convertEntityToDto(usuarioEntity);
            } else {
                LOGGER.info("Usuario no encontrado con id: " + id);
                return null;
            }
        } catch (Exception e) {
            LOGGER.severe("Error al actualizar usuario por id: " + e.getMessage());
            throw new InternalServerErrorException("Error al actualizar usuario por id");
        }
    }

    private UsuarioDto convertEntityToDto(Usuario usuario) {
        return modelMapper.map(usuario, UsuarioDto.class);
    }



    private boolean isNotEquealPassword(String password, String confirmPassword) {
        return !password.equals(confirmPassword);
    }

    // mapeo solo los componentes que se necesitan para la creación de un usuario
    private Usuario convertRegisterRequestToEntity(RegisterRequest registerRequest) {
        Usuario usuario = new Usuario();
        usuario.setEmail(registerRequest.getEmail());
        usuario.setPassword(registerRequest.getPassword());
        usuario.setUsername(registerRequest.getUsername());
        usuario.setRole(registerRequest.getRole());
        return usuario;
    }

   



}
