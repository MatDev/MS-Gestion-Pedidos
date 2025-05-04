package matdev.user.user_service;

import static org.mockito.Mockito.verify;
import matdev.user.user_service.dto.request.RegisterRequest;
import matdev.user.user_service.entity.Usuario;
import matdev.user.user_service.repository.UsuarioRepository;
import matdev.user.user_service.service.impl.UsuarioServiceImpl;
import static org.mockito.ArgumentMatchers.eq;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.assertj.core.api.Assertions.assertThat;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import matdev.user.user_service.dto.UsuarioDto;

@ExtendWith(MockitoExtension.class)
class UserServiceApplicationTests {
	@Mock
	private UsuarioRepository usuarioRepository;

	@Mock
	private PasswordEncoder passwordEncoder;
	@Mock
	private ModelMapper modelMapper;

	@InjectMocks
	private UsuarioServiceImpl usuarioService;


	// Configurar comportamiento de ModelMapper

	@Test
	void testCreateUsuario() {
		// Crear un RegisterRequest
		RegisterRequest request = new RegisterRequest();
		request.setUsername("testuser");
		request.setEmail("admin@admin.com");
		request.setPassword("password");
		request.setConfirmPassword("password");


		// Crear un Usuario para el resultado simulado
		Usuario usuarioResultado = new Usuario();
		usuarioResultado.setUsername("testuser");
		usuarioResultado.setEmail("admin@admin.com");
		usuarioResultado.setPassword("encodedPassword");

		when(passwordEncoder.encode("password")).thenReturn("encodedPassword");
		when(usuarioRepository.save(any(Usuario.class))).thenReturn(usuarioResultado);
		when(modelMapper.map(any(Usuario.class), eq(UsuarioDto.class))).thenAnswer(invocation -> {
			Usuario usuario = invocation.getArgument(0);
			UsuarioDto dto = new UsuarioDto();
			dto.setEmail(usuario.getEmail());
			return dto;
		});

		// Llamar al método con el RegisterRequest
		UsuarioDto createdUsuario = usuarioService.crearUsuario(request);
		assertThat(createdUsuario.getEmail()).isEqualTo("admin@admin.com");
		verify(usuarioRepository).save(any(Usuario.class));


	}


}
