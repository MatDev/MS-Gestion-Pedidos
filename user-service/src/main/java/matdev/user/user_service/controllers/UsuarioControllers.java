package matdev.user.user_service.controllers;

import java.util.logging.Logger;

import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import matdev.user.user_service.service.UsuarioService;

import org.apache.hc.core5.http.HttpStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import matdev.user.user_service.dto.UsuarioDto;
import matdev.user.user_service.dto.request.RegisterRequest;
import matdev.user.user_service.exeption.UnauthorizedUserException;

import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.GetMapping;





@RequiredArgsConstructor
@RestController
public class UsuarioControllers {
    private final UsuarioService usuarioService;
    private static final Logger LOGGER = Logger.getLogger(UsuarioControllers.class.getName());

    @PostMapping("users/register")
    public ResponseEntity<UsuarioDto> register(@RequestBody RegisterRequest registerRequest) {
        LOGGER.info("Reques recibida para crear usuario con email: " + registerRequest.getEmail());
        try {
            UsuarioDto usuario = usuarioService.crearUsuario(registerRequest);
            LOGGER.info("Usuario creado con éxito con id: " + usuario.getId());
            return ResponseEntity.status(HttpStatus.SC_CREATED).body(usuario);
            
        } catch (IllegalArgumentException e) {
            LOGGER.severe("Datos incorrectos: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.SC_FORBIDDEN).build();
           
        } catch (UnauthorizedUserException e) {
            LOGGER.severe("Usuario no autorizado: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.SC_UNAUTHORIZED).build();
        } catch (Exception e) {
            LOGGER.severe("Error al crear usuario: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.SC_INTERNAL_SERVER_ERROR).build();
        }
       

    }
    //con paginacion
    @GetMapping("users")
    public ResponseEntity<Page<UsuarioDto>> getUsers(@PageableDefault(page = 0, size = 10) Pageable pageable) {
        LOGGER.info("Reques recibida para obtener usuarios");
        try {
            Page<UsuarioDto> usuarios = usuarioService.obtenerUsuarios(pageable);
            LOGGER.info("Usuarios obtenidos con éxito");
            return ResponseEntity.ok(usuarios);
        } catch (Exception e) {
            LOGGER.severe("Error al obtener usuarios: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.SC_INTERNAL_SERVER_ERROR).build();
        }
    }
    
    @PutMapping("users/{id}")
    public ResponseEntity<UsuarioDto> putMethodName(@PathVariable String id, @RequestBody UsuarioDto usuarioDto) {
        LOGGER.info("Reques recibida para actualizar usuario con id: " + id);
        try {
            UsuarioDto usuario = usuarioService.actualizarUsuario(Long.parseLong(id), usuarioDto);
            LOGGER.info("Usuario actualizado con éxito con id: " + usuario.getId());
            return ResponseEntity.ok(usuario);
        } catch (Exception e) {
            LOGGER.severe("Error al actualizar usuario: " + e.getMessage());
            throw new RuntimeException("Error al actualizar usuario");
        }
    }



}
