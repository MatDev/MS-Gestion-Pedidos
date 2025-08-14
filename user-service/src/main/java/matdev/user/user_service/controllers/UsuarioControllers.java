package matdev.user.user_service.controllers;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.web.bind.annotation.*;

import lombok.RequiredArgsConstructor;
import matdev.user.user_service.service.UsuarioService;

import org.apache.hc.core5.http.HttpStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;

import matdev.user.user_service.dto.UsuarioDto;
import matdev.user.user_service.dto.request.RegisterRequest;
import matdev.user.user_service.exeption.UnauthorizedUserException;


@RequiredArgsConstructor
@RestController
@RequestMapping("/api/user")
public class UsuarioControllers {
    private final UsuarioService usuarioService;
    private static final Logger LOGGER = LoggerFactory.getLogger(UsuarioControllers.class);

    @PostMapping("/register")
    public ResponseEntity<UsuarioDto> register(@RequestBody RegisterRequest registerRequest) {
        LOGGER.info("Reques recibida para crear usuario con email: {}" , registerRequest.getEmail());
        try {
            UsuarioDto usuario = usuarioService.crearUsuario(registerRequest);
            LOGGER.info("Usuario creado con éxito con id: {}" , usuario.getId());
            return ResponseEntity.status(HttpStatus.SC_CREATED).body(usuario);
            
        } catch (IllegalArgumentException e) {
            LOGGER.error("Datos incorrectos: {} " , e.getMessage());
            return ResponseEntity.status(HttpStatus.SC_FORBIDDEN).build();
           
        } catch (UnauthorizedUserException e) {
            LOGGER.error("Usuario no autorizado: {} " , e.getMessage());
            return ResponseEntity.status(HttpStatus.SC_UNAUTHORIZED).build();
        } catch (Exception e) {
            LOGGER.error("Error al crear usuario: {} " , e.getMessage());
            return ResponseEntity.status(HttpStatus.SC_INTERNAL_SERVER_ERROR).build();
        }
       

    }
    //con paginacion
    @GetMapping("/users")
    public ResponseEntity<Page<UsuarioDto>> getUsers(@PageableDefault(page = 0, size = 10) Pageable pageable) {
        LOGGER.info("Reques recibida para obtener usuarios");
        try {
            Page<UsuarioDto> usuarios = usuarioService.obtenerUsuarios(pageable);
            LOGGER.info("Usuarios obtenidos con éxito");
            return ResponseEntity.ok(usuarios);
        } catch (Exception e) {
            LOGGER.error("Error al obtener usuarios: {}" , e.getMessage());
            return ResponseEntity.status(HttpStatus.SC_INTERNAL_SERVER_ERROR).build();
        }
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<UsuarioDto> updateUser(@PathVariable String id, @RequestBody UsuarioDto usuarioDto) {
        LOGGER.info("Reques recibida para actualizar usuario con id: {}" , id);
        try {
            UsuarioDto usuario = usuarioService.actualizarUsuario(Long.parseLong(id), usuarioDto);
            LOGGER.info("Usuario actualizado con éxito con id:{} " , usuario.getId());
            return ResponseEntity.ok(usuario);
        } catch (Exception e) {
            LOGGER.error("Error al actualizar usuario: {}" , e.getMessage());
            return ResponseEntity.status(HttpStatus.SC_INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<UsuarioDto> getUserById(@PathVariable String id) {
        LOGGER.info("Reques recibida para obtener usuario por id: {}" , id);
        try {
            UsuarioDto usuario = usuarioService.obtenerUsuarioPorId(Long.parseLong(id))
                    .orElseThrow(() -> new UnauthorizedUserException("Usuario no encontrado"));
            LOGGER.info("Usuario encontrado con id: {}", usuario.getId());
            return ResponseEntity.ok(usuario);
        } catch (Exception e) {
            LOGGER.error("Error al obtener usuario por id: {}" , e.getMessage());
            return ResponseEntity.status(HttpStatus.SC_INTERNAL_SERVER_ERROR).build();
        }
    }



}
