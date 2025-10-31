package com.citasmedicas.api.services;

import io.jsonwebtoken.ExpiredJwtException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Pruebas del Servicio JWT (JwtService)")
class JwtServiceTest {

    private JwtService jwtService;
    private UserDetails userDetails;

    // --- Datos de prueba reutilizables ---
    private final String secretKey = "4D6251655468576D5A7134743777217A25432A462D4A614E645267556B587032";
    private final long expirationTime = 3600000; // 1 hour

    @BeforeEach
    void setUp() {
        jwtService = new JwtService();
        // Inyectamos los valores de @Value usando ReflectionTestUtils
        ReflectionTestUtils.setField(jwtService, "secretKey", secretKey);
        ReflectionTestUtils.setField(jwtService, "jwtExpiration", expirationTime);

        userDetails = new User("testuser@example.com", "password", Collections.emptyList());
    }

    @Nested
    @DisplayName("Pruebas de Generación y Extracción de Token")
    class GenerationAndExtractionTests {

        @Test
        @DisplayName("Debería generar un token JWT válido")
        void generateToken_Success() {
            // Act
            String token = jwtService.generateToken(userDetails);

            // Assert
            assertNotNull(token);
            assertFalse(token.isEmpty());
        }

        @Test
        @DisplayName("Debería extraer el nombre de usuario (email) correctamente de un token")
        void extractUsername_Success() {
            // Arrange
            String token = jwtService.generateToken(userDetails);

            // Act
            String extractedUsername = jwtService.extractUsername(token);

            // Assert
            assertEquals(userDetails.getUsername(), extractedUsername);
        }
    }

    @Nested
    @DisplayName("Pruebas de Validación de Token")
    class ValidationTests {

        @Test
        @DisplayName("Debería validar un token correcto como válido")
        void isTokenValid_ShouldReturnTrue_ForValidToken() {
            // Arrange
            String token = jwtService.generateToken(userDetails);

            // Act
            boolean isValid = jwtService.isTokenValid(token, userDetails);

            // Assert
            assertTrue(isValid);
        }

        @Test
        @DisplayName("Debería invalidar un token si el nombre de usuario no coincide")
        void isTokenValid_ShouldReturnFalse_ForWrongUsername() {
            // Arrange
            String token = jwtService.generateToken(userDetails);
            UserDetails otherUserDetails = new User("otheruser@example.com", "password", Collections.emptyList());

            // Act
            boolean isValid = jwtService.isTokenValid(token, otherUserDetails);

            // Assert
            assertFalse(isValid);
        }

        @Test
        @DisplayName("Debería invalidar un token que ha expirado")
        void isTokenValid_ShouldReturnFalse_ForExpiredToken() {
            // Arrange
            // Creamos una instancia de JwtService con expiración CERO para generar un token expirado
            JwtService expiredJwtService = new JwtService();
            ReflectionTestUtils.setField(expiredJwtService, "secretKey", secretKey);
            ReflectionTestUtils.setField(expiredJwtService, "jwtExpiration", 0L);
            String expiredToken = expiredJwtService.generateToken(userDetails);

            // Act & Assert
            // La validación del token expirado puede lanzar una excepción o devolver false
            // dependiendo de cómo se maneje internamente.
            // En este caso, la librería lanza ExpiredJwtException al intentar extraer el claim.
            assertThrows(ExpiredJwtException.class, () -> jwtService.isTokenValid(expiredToken, userDetails));
        }

        @Test
        @DisplayName("Debería invalidar un token malformado o con una firma incorrecta")
        void isTokenValid_ShouldReturnFalse_ForInvalidSignature() {
            // Arrange
            String validToken = jwtService.generateToken(userDetails);
            // Creamos un token inválido simplemente añadiendo caracteres extra
            String invalidToken = validToken + "invalid";

            // Act & Assert
            // La librería debe detectar que la firma no es válida y lanzar una excepción.
            // El assertThrows verifica que la operación esperada (lanzar una excepción) ocurra.
            assertThrows(io.jsonwebtoken.security.SignatureException.class,
                    () -> jwtService.isTokenValid(invalidToken, userDetails));
        }
    }
}