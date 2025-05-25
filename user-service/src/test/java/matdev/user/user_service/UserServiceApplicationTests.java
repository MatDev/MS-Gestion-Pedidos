package matdev.user.user_service;


import matdev.user.user_service.context.TenantContext;
import matdev.user.user_service.dto.request.RegisterRequest;
import matdev.user.user_service.entity.Usuario;
import matdev.user.user_service.exeption.InternalServerErrorException;
import matdev.user.user_service.exeption.NotFoundException;
import matdev.user.user_service.exeption.PasswordIsNotEquals;
import matdev.user.user_service.repository.UsuarioRepository;
import matdev.user.user_service.service.impl.UsuarioServiceImpl;
import static org.mockito.ArgumentMatchers.eq;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.assertj.core.api.Assertions.assertThat;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import matdev.user.user_service.dto.UsuarioDto;


import java.util.Arrays;
import java.util.List;
import java.util.Optional;

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
	void testCreateCorrectUser() {
		RegisterRequest request = new RegisterRequest();
		request.setUsername("testuser");
		request.setEmail("admin@admin.com");
		request.setPassword("password");
		request.setConfirmPassword("password");
		request.setTenantId("tenant-test");

		Usuario usuarioResultado = new Usuario();
		usuarioResultado.setUsername("testuser");
		usuarioResultado.setEmail("admin@admin.com");
		usuarioResultado.setPassword("encodedPassword");
		usuarioResultado.setTenantId("tenant-test");

		when(passwordEncoder.encode("password")).thenReturn("encodedPassword");
		when(usuarioRepository.save(any(Usuario.class))).thenReturn(usuarioResultado);
		when(modelMapper.map(any(Usuario.class), eq(UsuarioDto.class))).thenAnswer(invocation -> {
			Usuario usuario = invocation.getArgument(0);
			UsuarioDto dto = new UsuarioDto();
			dto.setEmail(usuario.getEmail());
			return dto;
		});

		UsuarioDto createdUsuario = usuarioService.crearUsuario(request);
		assertThat(createdUsuario.getEmail()).isEqualTo("admin@admin.com");
		verify(usuarioRepository).save(any(Usuario.class));
	}

	@Test
	void testCreateUserWhitPasswordNotEquals() {
		RegisterRequest request = new RegisterRequest();
		request.setUsername("testuser");
		request.setEmail("admin@admin.com");
		request.setPassword("password");
		request.setConfirmPassword("password12");
		request.setTenantId("tenant-test");

		assertThrows(PasswordIsNotEquals.class, () -> usuarioService.crearUsuario(request));
	}

	@Test
	void testCreateUserErrorRepository() {
		RegisterRequest request = new RegisterRequest();
		request.setUsername("testuser");
		request.setEmail("test@example.com");
		request.setPassword("password123");
		request.setConfirmPassword("password123");
		request.setTenantId("tenant-test");

		when(passwordEncoder.encode(any())).thenReturn("encodedPassword");
		when(usuarioRepository.save(any(Usuario.class)))
				.thenThrow(new InternalServerErrorException("Error al guardar el usuario"));

		assertThrows(InternalServerErrorException.class, () -> usuarioService.crearUsuario(request));
	}
	@Test
	void testGetUsuarioPorEmail() {
		TenantContext.setTenantId("tenant-test");

		Usuario usuario = new Usuario();
		usuario.setEmail("admin@admin.com");
		usuario.setId(1L);

		when(usuarioRepository.findByEmailAndTenantId("admin@admin.com", "tenant-test"))
				.thenReturn(Optional.of(usuario));

		when(modelMapper.map(usuario, UsuarioDto.class)).thenAnswer(invocation -> {
			Usuario user = invocation.getArgument(0);
			UsuarioDto dto = new UsuarioDto();
			dto.setEmail(user.getEmail());
			dto.setId(user.getId());
			return dto;
		});

		UsuarioDto usuarioDto = usuarioService.obtenerUsuarioPorEmail("admin@admin.com").orElse(null);
		assertThat(usuarioDto).isNotNull();
		assertThat(usuarioDto.getEmail()).isEqualTo("admin@admin.com");
		verify(usuarioRepository).findByEmailAndTenantId("admin@admin.com", "tenant-test");
	}

	@Test
	void GetUsuarioPorEmailNotfound() {
		TenantContext.setTenantId("tenant-test");

		when(usuarioRepository.findByEmailAndTenantId("admin@admin.com", "tenant-test"))
				.thenReturn(Optional.empty());

		assertThrows(InternalServerErrorException.class, () ->
				usuarioService.obtenerUsuarioPorEmail("admin@admin.com")
		);

		verify(usuarioRepository).findByEmailAndTenantId("admin@admin.com", "tenant-test");
	}

	@Test
	void testUserWhitExceptionFindEmail() {
		TenantContext.setTenantId("tenant-test");

		when(usuarioRepository.findByEmailAndTenantId("admin@admin.com", "tenant-test"))
				.thenThrow(new InternalServerErrorException("Error"));

		assertThrows(InternalServerErrorException.class, () ->
				usuarioService.obtenerUsuarioPorEmail("admin@admin.com")
		);

		verify(usuarioRepository).findByEmailAndTenantId("admin@admin.com", "tenant-test");
	}


	@Test
	void testGetUsuarioPotId() {
		TenantContext.setTenantId("tenant-test");

		Usuario usuario = new Usuario();
		usuario.setId(1L);
		usuario.setEmail("admin@admin.cl");

		when(usuarioRepository.findByIdAndTenantId(1L, "tenant-test"))
				.thenReturn(Optional.of(usuario));

		when(modelMapper.map(usuario, UsuarioDto.class)).thenAnswer(invocation -> {
			Usuario user = invocation.getArgument(0);
			UsuarioDto dto = new UsuarioDto();
			dto.setEmail(user.getEmail());
			dto.setId(user.getId());
			return dto;
		});

		UsuarioDto usuarioDto = usuarioService.obtenerUsuarioPorId(1L).orElse(null);
		assertThat(usuarioDto).isNotNull();
		assertThat(usuarioDto.getEmail()).isEqualTo("admin@admin.cl");
		verify(usuarioRepository).findByIdAndTenantId(1L, "tenant-test");
	}

	@Test
	void testGetUsuarioPorIdNotFound() {
		TenantContext.setTenantId("tenant-test");
		when(usuarioRepository.findByIdAndTenantId(1L, "tenant-test")).thenReturn(Optional.empty());

		assertThrows(InternalServerErrorException.class, () -> {
			usuarioService.obtenerUsuarioPorId(1L);
		});

		verify(usuarioRepository).findByIdAndTenantId(1L, "tenant-test");
	}


	@Test
	void testGetUsuarioPorIdWhitException() {
		TenantContext.setTenantId("tenant-test");
		Long id = 1L;
		when(usuarioRepository.findByIdAndTenantId(id, "tenant-test"))
				.thenThrow(new InternalServerErrorException("Error"));

		assertThrows(InternalServerErrorException.class, () -> {
			usuarioService.obtenerUsuarioPorId(id);
		});

		verify(usuarioRepository).findByIdAndTenantId(id, "tenant-test");
	}

	@Test
	void testDeleteUsuarioPorId() {
		TenantContext.setTenantId("tenant-test");
		Long id = 1L;
		when(usuarioRepository.findByIdAndTenantId(id, "tenant-test"))
				.thenReturn(Optional.of(new Usuario()));

		usuarioService.eliminarUsuarioPorId(id);

		verify(usuarioRepository).deleteById(id);
	}

	@Test

	void testDeleteUsuarioPorIdNotFound() {
		TenantContext.setTenantId("tenant-test");
		Long id = 1L;
		// Simular que no existe el usuario
		when(usuarioRepository.findByIdAndTenantId(id, "tenant-test"))
				.thenReturn(Optional.empty());
		// Verificar que se lanza NotFoundException
		assertThrows(NotFoundException.class, () -> {
			usuarioService.eliminarUsuarioPorId(id);
		});

		// Verificar que deleteById nunca se llamó
		verify(usuarioRepository).findByIdAndTenantId(id, "tenant-test");
		verify(usuarioRepository, never()).deleteById(id);
	}

	@Test
	void testDeleteUsuarioPorIdWhitException() {
		TenantContext.setTenantId("tenant-test");
		Long id = 1L;
		when(usuarioRepository.findByIdAndTenantId(id, "tenant-test"))
				.thenReturn(Optional.of(new Usuario()));

		doThrow(new InternalServerErrorException("Error al eliminar"))
				.when(usuarioRepository).deleteById(id);

		assertThrows(InternalServerErrorException.class, () -> {
			usuarioService.eliminarUsuarioPorId(id);
		});
	}
	@Test
	void testActualizarUsuarioExistente() {
		TenantContext.setTenantId("tenant-test");
		Long id = 1L;
		UsuarioDto dto = new UsuarioDto();
		dto.setEmail("actualizado@example.com");
		dto.setId(id);

		when(usuarioRepository.findByIdAndTenantId(id, "tenant-test")).thenReturn(Optional.of(new Usuario()));
		when(modelMapper.map(dto, Usuario.class)).thenAnswer(inv -> {
			Usuario u = new Usuario();
			u.setId(dto.getId());
			u.setEmail(dto.getEmail());
			return u;
		});
		when(usuarioRepository.save(any(Usuario.class))).thenAnswer(inv -> inv.getArgument(0));
		when(modelMapper.map(any(Usuario.class), eq(UsuarioDto.class))).thenAnswer(inv -> {
			Usuario u = inv.getArgument(0);
			UsuarioDto result = new UsuarioDto();
			result.setEmail(u.getEmail());
			result.setId(u.getId());
			return result;
		});

		UsuarioDto result = usuarioService.actualizarUsuario(id, dto);
		assertThat(result).isNotNull();
		assertThat(result.getEmail()).isEqualTo("actualizado@example.com");
		verify(usuarioRepository).findByIdAndTenantId(id, "tenant-test");
		verify(usuarioRepository).save(any(Usuario.class));
	}

	@Test
	void testActualizarUsuarioNoExistente() {
		TenantContext.setTenantId("tenant-test");
		Long id = 999L;
		UsuarioDto dto = new UsuarioDto();
		dto.setEmail("noexiste@example.com");

		when(usuarioRepository.findByIdAndTenantId(id, "tenant-test")).thenReturn(Optional.empty());

		assertThrows(NotFoundException.class, () -> {
			usuarioService.actualizarUsuario(id, dto);
		});

		verify(usuarioRepository).findByIdAndTenantId(id, "tenant-test");
		verify(usuarioRepository, never()).save(any(Usuario.class));
	}

	@Test
	void testActualizarUsuarioExcepcionFindById() {
		TenantContext.setTenantId("tenant-test");
		Long id = 1L;
		UsuarioDto dto = new UsuarioDto();

		when(usuarioRepository.findByIdAndTenantId(id, "tenant-test"))
				.thenThrow(new NotFoundException("Error de base de datos"));

		assertThrows(NotFoundException.class, () -> {
			usuarioService.actualizarUsuario(id, dto);
		});

		verify(usuarioRepository).findByIdAndTenantId(id, "tenant-test");
	}


	@Test
	void testObtenerUsuariosPaginados(){
		Pageable pageable = PageRequest.of(0,10);
		TenantContext.setTenantId("tenant-test");
		// Crear una lista de usuarios simulados
		List<Usuario> listaUsuarios = Arrays.asList(
				crearUsuario(1L, "user1", "user1@example.com", "password1"),
				crearUsuario(2L, "user2", "user2@example.com", "password2")
		);
		Page<Usuario> paginaUsuarios = new PageImpl<>(listaUsuarios, pageable, listaUsuarios.size());

		// Configurar comportamiento simulado
		when(usuarioRepository.findAllByTenantId(eq("tenant-test"), eq(pageable)))
				.thenReturn(paginaUsuarios);
		when(modelMapper.map(any(Usuario.class), eq(UsuarioDto.class))).thenAnswer(invocation -> {
			Usuario usuario = invocation.getArgument(0);
			UsuarioDto dto = new UsuarioDto();
			dto.setId(usuario.getId());
			dto.setEmail(usuario.getEmail());
			dto.setUsername(usuario.getUsername());
			return dto;
		});

		// Ejecutar método
		Page<UsuarioDto> resultado = usuarioService.obtenerUsuarios(pageable);

		// Verificar resultados
		assertThat(resultado).isNotNull();
		assertThat(resultado.getTotalElements()).isEqualTo(2);
		assertThat(resultado.getContent().get(0).getEmail()).isEqualTo("user1@example.com");
		assertThat(resultado.getContent().get(1).getEmail()).isEqualTo("user2@example.com");

		// Verificar interacciones
		verify(usuarioRepository).findAllByTenantId("tenant-test", pageable);
	}

	private Usuario crearUsuario(Long id, String username, String email, String password) {
		Usuario usuario = new Usuario();
		usuario.setId(id);
		usuario.setUsername(username);
		usuario.setEmail(email);
		usuario.setPassword(password);
		return usuario;
	}

	@Test
	void testObtenerUsuariosConExcepcion() {
		// Configurar datos de prueba
		Pageable pageable = PageRequest.of(0, 10);

		// Configurar comportamiento simulado (lanzar excepción)
		when(usuarioRepository.findAll(pageable)).thenThrow(new RuntimeException("Error en la base de datos"));

		// Verificar que se lanza InternalServerErrorException
		assertThrows(InternalServerErrorException.class, () -> {
			usuarioService.obtenerUsuarios(pageable);
		});

		// Verificar interacciones
		verify(usuarioRepository).findAll(pageable);
	}


}
