package matdev.user.user_service.service.impl;

import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import matdev.user.user_service.context.TenantContext;
import matdev.user.user_service.exeption.InternalServerErrorException;
import matdev.user.user_service.exeption.NotFoundException;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import lombok.RequiredArgsConstructor;
import matdev.user.user_service.dto.UsuarioDto;
import matdev.user.user_service.dto.request.RegisterRequest;
import matdev.user.user_service.entity.Usuario;
import matdev.user.user_service.exeption.PasswordIsNotEquals;
import matdev.user.user_service.repository.UsuarioRepository;
import matdev.user.user_service.service.UsuarioService;
import org.springframework.validation.annotation.Validated;

@Service
@RequiredArgsConstructor
@Validated
public class UsuarioServiceImpl implements UsuarioService {
    private final UsuarioRepository usuarioRepository;
    private static final Logger LOGGER = LoggerFactory.getLogger(UsuarioServiceImpl.class);
    private final ModelMapper modelMapper;
    private final PasswordEncoder passwordEncoder;

    @Override
    public UsuarioDto crearUsuario(RegisterRequest registerRequest) {
        LOGGER.info("Creando usuario");

        if (isNotEquealPassword(registerRequest.getPassword(), registerRequest.getConfirmPassword())) {
            LOGGER.error("Error al crear usuario: Las contraseñas no coinciden");
            throw new PasswordIsNotEquals("Las contraseñas no coinciden");
        }

        try {
            Usuario usuario = convertRegisterRequestToEntity(registerRequest);
            usuario.setPassword(passwordEncoder.encode(registerRequest.getPassword()));
            usuario = usuarioRepository.save(usuario);
            LOGGER.info("Usuario creado con éxito con id: {}" , usuario.getId());
            return convertEntityToDto(usuario);

        } catch (Exception e) {
            LOGGER.error("Error al crear usuario: {}", e.getMessage());
            throw new InternalServerErrorException("Error al crear usuario");
        }
    }

    @Override
    public Optional<UsuarioDto> obtenerUsuarioPorEmail(
            @NotNull(message ="El email no puede ser null")
            @Email(message ="Formato de email invalido ")
            @NotEmpty(message ="Email no puede ser vacio") final String email) {
        LOGGER.info("Obteniendo usuario por email:{}", email);
        String tenantId = TenantContext.getTenantId();

        try {
            Usuario usuario = usuarioRepository.findByEmailAndTenantId(email, tenantId)
                    .orElseThrow(() -> new NotFoundException("Usuario no encontrado con email: " + email));

            LOGGER.info("Usuario encontrado con éxito con id: {} " , usuario.getId());
            return Optional.of(convertEntityToDto(usuario));

        } catch (Exception e) {
            LOGGER.error("Error al obtener usuario por email: {} " , e.getMessage());
            throw new InternalServerErrorException("Error al obtener usuario por email");
        }
    }

    @Override
    public Optional<UsuarioDto> obtenerUsuarioPorId(@NotNull final Long id) {
        LOGGER.info("Obteniendo usuario por id: {}" , id);
        String tenantId = TenantContext.getTenantId();

        try {
            Usuario usuario = usuarioRepository.findByIdAndTenantId(id, tenantId)
                    .orElseThrow(() -> new NotFoundException("Usuario no encontrado con id: " + id));

            LOGGER.info("Usuario encontrado con éxito con id: {}" , usuario.getId());
            return Optional.of(convertEntityToDto(usuario));

        } catch (Exception e) {
            LOGGER.error("Error al obtener usuario por id: {}" , e.getMessage());
            throw new InternalServerErrorException("Error al obtener usuario por id");
        }
    }

    @Override
    @Transactional
    public void eliminarUsuarioPorId(@NotNull final Long id) {
        LOGGER.info("Eliminando usuario por id: {} ", id);
        getUsuarioByIdHelper(id);
        usuarioRepository.deleteById(id);
        LOGGER.info("Usuario eliminado con éxito con id: {}" , id);
    }

    @Override
    public UsuarioDto actualizarUsuario(@NotNull final Long id, @NotNull final UsuarioDto usuario) {
        LOGGER.info("Actualizando usuario por id: {} " , id);
        getUsuarioByIdHelper(id);
        Usuario usuarioEntity = modelMapper.map(usuario, Usuario.class);
        usuarioEntity.setId(id);
        usuarioEntity.setUsername(usuario.getUsername());
        usuarioEntity.setEmail(usuario.getEmail());
        usuarioEntity = usuarioRepository.save(usuarioEntity);
        LOGGER.info("Usuario actualizado con éxito con id: {} " , usuarioEntity.getId());
        return convertEntityToDto(usuarioEntity);
    }

    @Override
    public Page<UsuarioDto> obtenerUsuarios(Pageable pageable) {
        LOGGER.info("Obteniendo usuarios");
        String tenantId = TenantContext.getTenantId();

        try {
            Page<Usuario> usuarios = usuarioRepository.findAllByTenantId(tenantId, pageable);
            LOGGER.info("Usuarios encontrados con éxito");
            return usuarios.map(this::convertEntityToDto);

        } catch (Exception e) {
            LOGGER.error("Error al obtener usuarios: {}" , e.getMessage());
            throw new InternalServerErrorException("Error al obtener usuarios");
        }
    }

    private Usuario getUsuarioByIdHelper(@NotNull Long id) {
        String tenantId = TenantContext.getTenantId();
        return usuarioRepository.findByIdAndTenantId(id, tenantId)
                .orElseThrow(() -> {
                    LOGGER.warn("Usuario no encontrado con id: {}  " , id);
                    return new NotFoundException("Usuario no encontrado");
                });
    }

    private UsuarioDto convertEntityToDto(Usuario usuario) {
        return modelMapper.map(usuario, UsuarioDto.class);
    }

    private boolean isNotEquealPassword(String password, String confirmPassword) {
        return !password.equals(confirmPassword);
    }

    private Usuario convertRegisterRequestToEntity(RegisterRequest registerRequest) {
        Usuario usuario = new Usuario();
        usuario.setEmail(registerRequest.getEmail());
        usuario.setPassword(registerRequest.getPassword());
        usuario.setUsername(registerRequest.getUsername());
        usuario.setRole(registerRequest.getRole());
        usuario.setTenantId(registerRequest.getTenantId());
        return usuario;
    }
}

