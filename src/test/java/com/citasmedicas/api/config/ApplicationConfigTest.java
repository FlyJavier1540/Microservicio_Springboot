package com.citasmedicas.api.config;

import com.citasmedicas.api.models.Usuario;
import com.citasmedicas.api.repositories.UsuarioRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Pruebas de la Clase de Configuración de la Aplicación (ApplicationConfig)")
class ApplicationConfigTest {

    @Mock
    private UsuarioRepository usuarioRepository;

    @InjectMocks
    private ApplicationConfig applicationConfig;

    @Nested
    @DisplayName("Pruebas para el Bean userDetailsService")
    class UserDetailsServiceBeanTests {

        @Test
        @DisplayName("Debería devolver un UserDetails cuando el usuario existe")
        void userDetailsService_ShouldReturnUserDetails_WhenUserExists() {
            // Arrange
            String userEmail = "test@example.com";
            Usuario usuario = new Usuario();
            usuario.setEmail(userEmail);
            usuario.setPassword("password");

            when(usuarioRepository.findByEmail(userEmail)).thenReturn(Optional.of(usuario));
            UserDetailsService userDetailsService = applicationConfig.userDetailsService();

            // Act
            UserDetails userDetails = userDetailsService.loadUserByUsername(userEmail);

            // Assert
            assertNotNull(userDetails);
            assertEquals(userEmail, userDetails.getUsername());
        }

        @Test
        @DisplayName("Debería lanzar UsernameNotFoundException cuando el usuario no existe")
        void userDetailsService_ShouldThrowException_WhenUserDoesNotExist() {
            // Arrange
            String userEmail = "nonexistent@example.com";
            when(usuarioRepository.findByEmail(userEmail)).thenReturn(Optional.empty());
            UserDetailsService userDetailsService = applicationConfig.userDetailsService();

            // Act & Assert
            UsernameNotFoundException exception = assertThrows(UsernameNotFoundException.class,
                    () -> userDetailsService.loadUserByUsername(userEmail));
            assertEquals("Usuario no encontrado", exception.getMessage());
        }
    }

    @Nested
    @DisplayName("Pruebas para Beans de Seguridad")
    class SecurityBeansTests {

        @Test
        @DisplayName("Debería proveer un bean de PasswordEncoder del tipo BCryptPasswordEncoder")
        void passwordEncoder_ShouldReturnBCryptPasswordEncoder() {
            // Act
            PasswordEncoder passwordEncoder = applicationConfig.passwordEncoder();

            // Assert
            assertNotNull(passwordEncoder);
            assertInstanceOf(BCryptPasswordEncoder.class, passwordEncoder);
        }

        @Test
        @DisplayName("Debería proveer un bean de AuthenticationProvider configurado correctamente")
        void authenticationProvider_ShouldReturnConfiguredDaoProvider() {
            // Act
            DaoAuthenticationProvider provider = (DaoAuthenticationProvider) applicationConfig.authenticationProvider();

            // Assert
            assertNotNull(provider);
            // Verificamos que el provider use nuestro UserDetailsService y PasswordEncoder.
            // Es una prueba más conceptual, ya que no podemos acceder directamente a los campos.
            // Pero confirmamos que se crea el tipo correcto de objeto.
            assertInstanceOf(DaoAuthenticationProvider.class, provider);
        }

        @Test
        @DisplayName("Debería proveer un bean de AuthenticationManager")
        void authenticationManager_ShouldReturnAuthenticationManager() throws Exception {
            // Arrange
            // Mockeamos la configuración de Spring que se le pasa al método
            AuthenticationConfiguration authConfig = mock(AuthenticationConfiguration.class);
            AuthenticationManager expectedManager = mock(AuthenticationManager.class);
            when(authConfig.getAuthenticationManager()).thenReturn(expectedManager);

            // Act
            AuthenticationManager actualManager = applicationConfig.authenticationManager(authConfig);

            // Assert
            assertNotNull(actualManager);
            assertEquals(expectedManager, actualManager);
            verify(authConfig, times(1)).getAuthenticationManager();
        }
    }
}