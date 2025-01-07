package matdev.user.user_service.controllers;

import java.util.logging.Logger;

import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import matdev.user.user_service.service.UsuarioService;

import org.apache.hc.core5.http.HttpStatus;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import matdev.user.user_service.dto.UsuarioDto;
import matdev.user.user_service.dto.request.RegisterRequest;


@RequiredArgsConstructor
@RestController
public class UsuarioControllers {
    private final UsuarioService usuarioService;
    private static final Logger LOGGER = Logger.getLogger(UsuarioControllers.class.getName());

    @PostMapping("/register")
    public ResponseEntity<UsuarioDto> register(@RequestBody RegisterRequest registerRequest) {
        LOGGER.info("Reques recibida para crear usuario con email: " + registerRequest.getEmail());
        try {
            UsuarioDto usuario = usuarioService.crearUsuario(registerRequest);
            LOGGER.info("Usuario creado con éxito con id: " + usuario.getId());
            return ResponseEntity.status(HttpStatus.SC_CREATED).body(usuario);
            
        } catch (Exception e) {
            LOGGER.severe("Error al crear usuario: " + e.getMessage());
            throw new RuntimeException("Error al crear usuario");
        }
       

    }



}
