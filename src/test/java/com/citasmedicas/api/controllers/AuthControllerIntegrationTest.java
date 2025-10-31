package com.citasmedicas.api.controllers;

import com.citasmedicas.api.dtos.AuthRequestDTO;
import com.citasmedicas.api.dtos.RegisterRequestDTO;
import com.citasmedicas.api.repositories.UsuarioRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest // Carga toda la aplicación
@AutoConfigureMockMvc // Configura MockMvc para hacer peticiones HTTP
@Transactional // Hace que cada prueba se ejecute en una transacción que se revierte al final
@DisplayName("Pruebas de Integración para el Flujo de Autenticación")
class AuthControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @BeforeEach
    void setUp() {
        // Limpiamos la base de datos antes de cada prueba para asegurar el aislamiento
        usuarioRepository.deleteAll();
    }

    @Test
    @DisplayName("Debería registrar un nuevo paciente, guardarlo en la base de datos y devolver un token")
    void registerEndpoint_shouldCreateUserAndReturnToken() throws Exception {
        // Arrange
        RegisterRequestDTO registerRequest = new RegisterRequestDTO(
                "nuevo.paciente@test.com",
                "passwordFuerte123",
                "Nuevo Paciente"
        );

        // Act & Assert
        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registerRequest)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.token").isString());

        // Verificar en la base de datos
        var usuarioGuardado = usuarioRepository.findByEmail("nuevo.paciente@test.com");
        assertTrue(usuarioGuardado.isPresent(), "El usuario debería existir en la base de datos");
        assertTrue(passwordEncoder.matches("passwordFuerte123", usuarioGuardado.get().getPassword()), "La contraseña guardada debería estar encriptada");
    }

    @Test
    @DisplayName("Debería permitir el login a un usuario existente y devolver un token")
    void loginEndpoint_shouldAuthenticateAndReturnToken() throws Exception {
        // Arrange: Primero, creamos un usuario directamente en la base de datos
        RegisterRequestDTO registerRequest = new RegisterRequestDTO(
                "paciente.existente@test.com",
                "password123",
                "Paciente Existente"
        );
        // Usamos el servicio real para registrar al usuario
        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(registerRequest)));

        AuthRequestDTO loginRequest = new AuthRequestDTO("paciente.existente@test.com", "password123");

        // Act & Assert
        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").isString());
    }

    @Test
    @DisplayName("Debería devolver 400 Bad Request si los datos de registro son inválidos")
    void registerEndpoint_shouldReturnBadRequest_forInvalidData() throws Exception {
        // Arrange
        RegisterRequestDTO invalidRequest = new RegisterRequestDTO("email-invalido", "corta", "");

        // Act & Assert
        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());
    }
}