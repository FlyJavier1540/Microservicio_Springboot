package com.citasmedicas.api.services;

import com.citasmedicas.api.dtos.AdminRegisterRequestDTO;
import com.citasmedicas.api.dtos.AuthRequestDTO;
import com.citasmedicas.api.dtos.AuthResponseDTO;
import com.citasmedicas.api.dtos.RegisterRequestDTO;
import com.citasmedicas.api.enums.RolNombre;
import com.citasmedicas.api.models.Paciente;
import com.citasmedicas.api.models.Rol;
import com.citasmedicas.api.models.Usuario;
import com.citasmedicas.api.repositories.PacienteRepository;
import com.citasmedicas.api.repositories.RolRepository;
import com.citasmedicas.api.repositories.UsuarioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Pruebas del Servicio de Autenticación (AuthService)")
class AuthServiceTest {

    @Mock
    private UsuarioRepository usuarioRepository;
    @Mock
    private PacienteRepository pacienteRepository;
    @Mock
    private RolRepository rolRepository;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private JwtService jwtService;
    @Mock
    private AuthenticationManager authenticationManager;

    @InjectMocks
    private AuthService authService;

    // --- Datos de prueba ---
    private final String userEmail = "paciente@test.com";
    private final String userPassword = "password123";
    private final String userName = "Juan Perez";
    private final String adminEmail = "admin@test.com";
    private final String adminSecretKey = "z#8qM!7jW@g$p4";

    @BeforeEach
    void setUp() {
        // CORRECTO: Inyectar el valor del campo @Value usando ReflectionTestUtils
        ReflectionTestUtils.setField(authService, "adminSecretKey", adminSecretKey);
    }

    // ... (El resto de tus pruebas anidadas se mantienen exactamente igual)
    @Nested
    @DisplayName("Pruebas para el Registro de Pacientes")
    class RegisterTests {

        private RegisterRequestDTO registerRequest;
        private Rol rolPaciente;

        @BeforeEach
        void setUp() {
            registerRequest = new RegisterRequestDTO(userEmail, userPassword, userName);
            rolPaciente = new Rol(1L, RolNombre.PACIENTE);
        }

        @Test
        @DisplayName("Debería registrar un nuevo paciente exitosamente")
        void register_Success() {
            // Arrange
            when(usuarioRepository.existsByEmail(userEmail)).thenReturn(false);
            when(rolRepository.findByNombre(RolNombre.PACIENTE)).thenReturn(Optional.of(rolPaciente));
            when(passwordEncoder.encode(userPassword)).thenReturn("encodedPassword");
            when(pacienteRepository.save(any(Paciente.class))).thenAnswer(invocation -> invocation.getArgument(0));
            when(jwtService.generateToken(any(Usuario.class))).thenReturn("fake-jwt-token");

            // Act
            AuthResponseDTO response = authService.register(registerRequest);

            // Assert
            assertNotNull(response);
            assertEquals("fake-jwt-token", response.getToken());
            verify(pacienteRepository, times(1)).save(any(Paciente.class));
        }

        @Test
        @DisplayName("Debería lanzar una excepción si el email ya está en uso")
        void register_ShouldThrowException_WhenEmailIsInUse() {
            // Arrange
            when(usuarioRepository.existsByEmail(userEmail)).thenReturn(true);

            // Act & Assert
            IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                    () -> authService.register(registerRequest));

            assertEquals("El email ya está en uso.", exception.getMessage());
            verify(pacienteRepository, never()).save(any());
        }

        @Test
        @DisplayName("Debería lanzar una excepción si el rol de paciente no se encuentra")
        void register_ShouldThrowException_WhenRoleNotFound() {
            // Arrange
            when(usuarioRepository.existsByEmail(userEmail)).thenReturn(false);
            when(rolRepository.findByNombre(RolNombre.PACIENTE)).thenReturn(Optional.empty());

            // Act & Assert
            RuntimeException exception = assertThrows(RuntimeException.class,
                    () -> authService.register(registerRequest));

            assertEquals("Error: Rol de paciente no encontrado.", exception.getMessage());
        }
    }

    @Nested
    @DisplayName("Pruebas para el Inicio de Sesión (Login)")
    class LoginTests {

        private AuthRequestDTO loginRequest;
        private Usuario usuario;

        @BeforeEach
        void setUp() {
            loginRequest = new AuthRequestDTO(userEmail, userPassword);
            usuario = new Usuario(1L, userEmail, "encodedPassword", Set.of(new Rol(1L, RolNombre.PACIENTE)));
        }

        @Test
        @DisplayName("Debería iniciar sesión y devolver un token JWT exitosamente")
        void login_Success() {
            // Arrange
            when(usuarioRepository.findByEmail(userEmail)).thenReturn(Optional.of(usuario));
            when(jwtService.generateToken(usuario)).thenReturn("fake-jwt-token");

            // Act
            AuthResponseDTO response = authService.login(loginRequest);

            // Assert
            assertNotNull(response);
            assertEquals("fake-jwt-token", response.getToken());
            verify(authenticationManager, times(1)).authenticate(
                    new UsernamePasswordAuthenticationToken(userEmail, userPassword)
            );
        }

        @Test
        @DisplayName("Debería lanzar una excepción si el usuario no se encuentra")
        void login_ShouldThrowException_WhenUserNotFound() {
            // Arrange
            when(usuarioRepository.findByEmail(userEmail)).thenReturn(Optional.empty());

            // Act & Assert
            IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                    () -> authService.login(loginRequest));

            assertEquals("Usuario no encontrado.", exception.getMessage());
        }
    }

    @Nested
    @DisplayName("Pruebas para el Registro de Administradores")
    class RegisterAdminTests {

        private AdminRegisterRequestDTO adminRequest;
        private Rol rolAdmin;

        @BeforeEach
        void setUp() {
            adminRequest = new AdminRegisterRequestDTO();
            adminRequest.setEmail(adminEmail);
            adminRequest.setPassword(userPassword);
            adminRequest.setSecretKey(adminSecretKey);
            rolAdmin = new Rol(2L, RolNombre.ADMIN);
        }

        @Test
        @DisplayName("Debería registrar un nuevo administrador exitosamente")
        void registerAdmin_Success() {
            // Arrange
            when(usuarioRepository.existsByEmail(adminEmail)).thenReturn(false);
            when(rolRepository.findByNombre(RolNombre.ADMIN)).thenReturn(Optional.of(rolAdmin));
            when(passwordEncoder.encode(userPassword)).thenReturn("encodedPassword");
            when(usuarioRepository.save(any(Usuario.class))).thenAnswer(invocation -> invocation.getArgument(0));
            when(jwtService.generateToken(any(Usuario.class))).thenReturn("fake-admin-jwt-token");

            // Act
            AuthResponseDTO response = authService.registerAdmin(adminRequest);

            // Assert
            assertNotNull(response);
            assertEquals("fake-admin-jwt-token", response.getToken());
            verify(usuarioRepository, times(1)).save(any(Usuario.class));
        }

        @Test
        @DisplayName("Debería lanzar AccessDeniedException si la clave secreta es inválida")
        void registerAdmin_ShouldThrowException_WhenSecretKeyIsInvalid() {
            // Arrange
            adminRequest.setSecretKey("invalid-key");

            // Act & Assert
            AccessDeniedException exception = assertThrows(AccessDeniedException.class,
                    () -> authService.registerAdmin(adminRequest));

            assertEquals("Clave secreta inválida.", exception.getMessage());
            verify(usuarioRepository, never()).save(any());
        }

        @Test
        @DisplayName("Debería lanzar una excepción si el email del admin ya está en uso")
        void registerAdmin_ShouldThrowException_WhenEmailIsInUse() {
            // Arrange
            when(usuarioRepository.existsByEmail(adminEmail)).thenReturn(true);

            // Act & Assert
            IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                    () -> authService.registerAdmin(adminRequest));

            assertEquals("El email ya está en uso.", exception.getMessage());
        }

        @Test
        @DisplayName("Debería lanzar una excepción si el rol de admin no se encuentra")
        void registerAdmin_ShouldThrowException_WhenRoleNotFound() {
            // Arrange
            when(usuarioRepository.existsByEmail(adminEmail)).thenReturn(false);
            when(rolRepository.findByNombre(RolNombre.ADMIN)).thenReturn(Optional.empty());

            // Act & Assert
            RuntimeException exception = assertThrows(RuntimeException.class,
                    () -> authService.registerAdmin(adminRequest));

            assertEquals("Error: Rol de ADMIN no encontrado.", exception.getMessage());
        }
    }
}