package com.citasmedicas.api.config;

import com.citasmedicas.api.services.JwtService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.io.IOException;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Pruebas del Filtro de Autenticación JWT (JwtAuthenticationFilter)")
class JwtAuthenticationFilterTest {

    @Mock
    private JwtService jwtService;
    @Mock
    private UserDetailsService userDetailsService;
    @Mock
    private HttpServletRequest request;
    @Mock
    private HttpServletResponse response;
    @Mock
    private FilterChain filterChain;

    @InjectMocks
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @BeforeEach
    void setUp() {
        // Limpiamos el contexto de seguridad antes de cada prueba
        SecurityContextHolder.clearContext();
    }

    @Nested
    @DisplayName("Pruebas de Flujo del Filtro")
    class FilterFlowTests {

        @Test
        @DisplayName("Debería continuar la cadena de filtros si no hay cabecera de autenticación")
        void doFilterInternal_ShouldContinueChain_WhenNoAuthHeader() throws ServletException, IOException {
            // Arrange
            when(request.getHeader("Authorization")).thenReturn(null);

            // Act
            jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

            // Assert
            verify(filterChain, times(1)).doFilter(request, response);
            assertNull(SecurityContextHolder.getContext().getAuthentication());
        }

        @Test
        @DisplayName("Debería continuar la cadena de filtros si la cabecera no empieza con 'Bearer '")
        void doFilterInternal_ShouldContinueChain_WhenHeaderDoesNotStartWithBearer() throws ServletException, IOException {
            // Arrange
            when(request.getHeader("Authorization")).thenReturn("Token 12345");

            // Act
            jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

            // Assert
            verify(filterChain, times(1)).doFilter(request, response);
            assertNull(SecurityContextHolder.getContext().getAuthentication());
        }

        @Test
        @DisplayName("Debería autenticar al usuario si el token es válido y no hay autenticación previa")
        void doFilterInternal_ShouldAuthenticateUser_WhenTokenIsValid() throws ServletException, IOException {
            // Arrange
            final String token = "valid-jwt-token";
            final String userEmail = "user@test.com";
            final UserDetails userDetails = new User(userEmail, "password", Collections.emptyList());

            when(request.getHeader("Authorization")).thenReturn("Bearer " + token);
            when(jwtService.extractUsername(token)).thenReturn(userEmail);
            when(userDetailsService.loadUserByUsername(userEmail)).thenReturn(userDetails);
            when(jwtService.isTokenValid(token, userDetails)).thenReturn(true);

            // Act
            jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

            // Assert
            assertNotNull(SecurityContextHolder.getContext().getAuthentication());
            assertEquals(userEmail, SecurityContextHolder.getContext().getAuthentication().getName());
            verify(filterChain, times(1)).doFilter(request, response);
        }

        @Test
        @DisplayName("No debería autenticar al usuario si el token no es válido")
        void doFilterInternal_ShouldNotAuthenticateUser_WhenTokenIsInvalid() throws ServletException, IOException {
            // Arrange
            final String token = "invalid-jwt-token";
            final String userEmail = "user@test.com";
            final UserDetails userDetails = new User(userEmail, "password", Collections.emptyList());

            when(request.getHeader("Authorization")).thenReturn("Bearer " + token);
            when(jwtService.extractUsername(token)).thenReturn(userEmail);
            when(userDetailsService.loadUserByUsername(userEmail)).thenReturn(userDetails);
            when(jwtService.isTokenValid(token, userDetails)).thenReturn(false);

            // Act
            jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

            // Assert
            assertNull(SecurityContextHolder.getContext().getAuthentication());
            verify(filterChain, times(1)).doFilter(request, response);
        }

        @Test
        @DisplayName("No debería hacer nada si el email del token es nulo")
        void doFilterInternal_ShouldDoNothing_WhenUsernameIsNull() throws ServletException, IOException {
            // Arrange
            final String token = "token-with-null-user";
            when(request.getHeader("Authorization")).thenReturn("Bearer " + token);
            when(jwtService.extractUsername(token)).thenReturn(null);

            // Act
            jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

            // Assert
            assertNull(SecurityContextHolder.getContext().getAuthentication());
            verify(userDetailsService, never()).loadUserByUsername(anyString());
            verify(filterChain, times(1)).doFilter(request, response);
        }
    }
}