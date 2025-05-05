package matdev.user.user_service;


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

	@Test
	void testCreateUserWhitPasswordNotEquals(){
		RegisterRequest request = new RegisterRequest();
		request.setUsername("testuser");
		request.setEmail("admin@admin.com");
		request.setPassword("password");
		request.setConfirmPassword("password12");

		assertThrows(PasswordIsNotEquals.class,()-> {
			usuarioService.crearUsuario(request);
		});

	}

	@Test
	void testCreateUserErrorRepository(){
		RegisterRequest request = new RegisterRequest();
		request.setUsername("testuser");
		request.setEmail("test@example.com");
		request.setPassword("password123");
		request.setConfirmPassword("password123");


		// configura el mock para lanzar una exception
		when(passwordEncoder.encode(any())).thenReturn("encodedPassword");
		when(usuarioRepository.save(any(Usuario.class))).thenThrow(new InternalServerErrorException("Error al guardar el usuario"));

		assertThrows(InternalServerErrorException.class,()->{
			usuarioService.crearUsuario(request);
		});

	}

	@Test
	void   testGetUsuarioPorEmail() {
		// Crear un Usuario simulado
		Usuario usuario = new Usuario();
		usuario.setEmail("admin@admin.com");
		usuario.setPassword("password");
		usuario.setId(1L);

		// Configurar el comportamiento simulado del repositorio
		when(usuarioRepository.findByEmail("admin@admin.com")).thenReturn(java.util.Optional.of(usuario));
		when(modelMapper.map(usuario, UsuarioDto.class)).thenReturn(new UsuarioDto());
		when(modelMapper.map(usuario, UsuarioDto.class)).thenAnswer(invocation -> {
			Usuario user = invocation.getArgument(0);
			UsuarioDto dto = new UsuarioDto();
			dto.setEmail(user.getEmail());
			dto.setId(user.getId());
			return dto;
		});
		// Llamar al método
		UsuarioDto usuarioDto = usuarioService.obtenerUsuarioPorEmail("admin@admin.com").orElse(null);
		assertThat(usuarioDto).isNotNull();
		assertThat(usuarioDto.getEmail()).isEqualTo("admin@admin.com");
		verify(usuarioRepository).findByEmail("admin@admin.com");

	}
	@Test
	void GetUsuarioPorEmailNotfound() {
		// Configurar el comportamiento simulado del repositorio
		when(usuarioRepository.findByEmail("admin@admin.com")).thenReturn(java.util.Optional.empty());
		//verificamos que se lance la exception
		assertThrows(InternalServerErrorException.class, () ->{
			usuarioService.obtenerUsuarioPorEmail("admin@admin.com");
		});
		// Llamar al método
		verify(usuarioRepository).findByEmail("admin@admin.com");

	}

	@Test
	void testUserWhitExceptionFindEmail(){
		String email = "admin@admin.com";
		//Configurar el mock para lanzar una exception
		when(usuarioRepository.findByEmail(email)).thenThrow(new InternalServerErrorException("Error al buscar el usuario por email"));
		// se verifica que se lance la exception
		assertThrows(InternalServerErrorException.class, () -> {
			usuarioService.obtenerUsuarioPorEmail(email);
		});

		// Verificar que el método findByEmail fue llamado
		verify(usuarioRepository).findByEmail(email);
	}

	@Test
	void testGetUsuarioPotId(){
		// Crea el usuario simulado
		Usuario usuario = new Usuario();
		usuario.setId(1L);
		usuario.setEmail("admin@admin.cl");
		usuario.setPassword("password");
		// Configura el comportamiento simulado del repositorio
		when(usuarioRepository.findById(1L)).thenReturn(java.util.Optional.of(usuario));
		when(modelMapper.map(usuario, UsuarioDto.class)).thenAnswer(invocation -> {
			Usuario user = invocation.getArgument(0);
			UsuarioDto dto = new UsuarioDto();
			dto.setEmail(user.getEmail());
			dto.setId(user.getId());
			return dto;
		});
		// Llama al método
		UsuarioDto usuarioDto = usuarioService.obtenerUsuarioPorId(1L).orElse(null);
		assertThat(usuarioDto).isNotNull();
		assertThat(usuarioDto.getEmail()).isEqualTo("admin@admin.cl");
		verify(usuarioRepository).findById(1L);

	}

	@Test
	void testGetUsuarioPorIdNotFound() {
		// Configurar el comportamiento simulado del repositorio
		when(usuarioRepository.findById(1L)).thenReturn(java.util.Optional.empty());
		//verificamos que se lance la exception
		assertThrows(InternalServerErrorException.class, () ->{
			usuarioService.obtenerUsuarioPorId(1L);
		});
		// Llamar al método
		verify(usuarioRepository).findById(1L);

	}

	@Test
	void testGetUsuarioPorIdWhitException() {
		Long id = 1L;
		//Configurar el mock para lanzar una exception
		when(usuarioRepository.findById(id)).thenThrow(new InternalServerErrorException("Error al buscar el usuario por id"));
		// se verifica que se lance la exception
		assertThrows(InternalServerErrorException.class, () -> {
			usuarioService.obtenerUsuarioPorId(id);
		});

		// Verificar que el método findById fue llamado
		verify(usuarioRepository).findById(id);
	}

	@Test
	void testDeleteUsuarioPorId() {
		Long id = 1L;
		// Configurar que el usuario existe
		when(usuarioRepository.findById(id)).thenReturn(java.util.Optional.of(new Usuario()));
		// Llamar al método
		usuarioService.eliminarUsuarioPorId(id);
		verify(usuarioRepository).deleteById(id);
	}

	@Test

	void testDeleteUsuarioPorIdNotFound() {
		Long id = 1L;
		// Simular que no existe el usuario
		when(usuarioRepository.findById(id)).thenReturn(java.util.Optional.empty());

		// Verificar que se lanza NotFoundException
		assertThrows(NotFoundException.class, () -> {
			usuarioService.eliminarUsuarioPorId(id);
		});

		// Verificar que deleteById nunca se llamó
		verify(usuarioRepository, never()).deleteById(id);
	}

	@Test
	void testDeleteUsuarioPorIdWhitException() {
		Long id = 1L;
		// Simular que el usuario existe
		when(usuarioRepository.findById(id)).thenReturn(java.util.Optional.of(new Usuario()));

		// Simular error al eliminar
		doThrow(new InternalServerErrorException("Error al eliminar el usuario"))
				.when(usuarioRepository).deleteById(id);

		// Verificar que se lanza InternalServerErrorException
		assertThrows(InternalServerErrorException.class, () -> {
			usuarioService.eliminarUsuarioPorId(id);
		});
	}
	@Test
	void testActualizarUsuarioExistente() {
		Long id = 1L;
		UsuarioDto usuarioDto = new UsuarioDto();
		usuarioDto.setEmail("actualizado@example.com");
		usuarioDto.setId(id);

		// Configurar comportamiento simulado del repositorio
		when(usuarioRepository.findById(id)).thenReturn(java.util.Optional.of(new Usuario()));
		when(modelMapper.map(usuarioDto, Usuario.class)).thenAnswer(invocation -> {
			UsuarioDto dto = invocation.getArgument(0);
			Usuario user = new Usuario();
			user.setEmail(dto.getEmail());
			user.setId(dto.getId());
			return user;
		});
		when(usuarioRepository.save(any(Usuario.class))).thenAnswer(invocation -> invocation.getArgument(0));
		when(modelMapper.map(any(Usuario.class), eq(UsuarioDto.class))).thenAnswer(invocation -> {
			Usuario user = invocation.getArgument(0);
			UsuarioDto dto = new UsuarioDto();
			dto.setEmail(user.getEmail());
			dto.setId(user.getId());
			return dto;
		});

		// Ejecutar método y verificar resultado
		UsuarioDto resultado = usuarioService.actualizarUsuario(id, usuarioDto);

		assertThat(resultado).isNotNull();
		assertThat(resultado.getEmail()).isEqualTo("actualizado@example.com");
		assertThat(resultado.getId()).isEqualTo(id);
		verify(usuarioRepository).findById(id);
		verify(usuarioRepository).save(any(Usuario.class));
	}

	@Test
	void testActualizarUsuarioNoExistente() {
		Long id = 999L;
		UsuarioDto usuarioDto = new UsuarioDto();
		usuarioDto.setEmail("noexiste@example.com");

		// Configurar comportamiento para usuario no encontrado
		when(usuarioRepository.findById(id)).thenReturn(java.util.Optional.empty());
		// Verificar que se lanza NotFoundException
		assertThrows(NotFoundException.class, () -> {
			usuarioService.actualizarUsuario(id, usuarioDto);
		});
		verify(usuarioRepository).findById(id);
		verify(usuarioRepository, never()).save(any(Usuario.class));

	}

	@Test
	void testActualizarUsuarioExcepcionFindById() {
		Long id = 1L;
		UsuarioDto usuarioDto = new UsuarioDto();

		// Configurar excepción en findById
		when(usuarioRepository.findById(id)).thenThrow(new NotFoundException("Error de base de datos"));

		// Verificar que se lanza NotFoundException
		assertThrows(NotFoundException.class, () -> {
			usuarioService.actualizarUsuario(id, usuarioDto);
		});

		verify(usuarioRepository).findById(id);
	}

	@Test
	void testObtenerUsuariosPaginados(){
		Pageable pageable = PageRequest.of(0,10);
		// Crear una lista de usuarios simulados
		List<Usuario> listaUsuarios = Arrays.asList(
				crearUsuario(1L, "user1", "user1@example.com", "password1"),
				crearUsuario(2L, "user2", "user2@example.com", "password2")
		);
		Page<Usuario> paginaUsuarios = new PageImpl<>(listaUsuarios, pageable, listaUsuarios.size());

		// Configurar comportamiento simulado
		when(usuarioRepository.findAll(pageable)).thenReturn(paginaUsuarios);
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
		verify(usuarioRepository).findAll(pageable);
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
