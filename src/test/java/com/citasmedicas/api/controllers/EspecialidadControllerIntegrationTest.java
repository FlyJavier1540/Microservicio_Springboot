package com.citasmedicas.api.controllers;

import com.citasmedicas.api.dtos.AdminRegisterRequestDTO;
import com.citasmedicas.api.dtos.AuthRequestDTO;
import com.citasmedicas.api.dtos.EspecialidadDTO;
import com.citasmedicas.api.models.Especialidad;
import com.citasmedicas.api.repositories.EspecialidadRepository;
import com.citasmedicas.api.repositories.UsuarioRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@DisplayName("Pruebas de Integración para el Controlador de Especialidades")
class EspecialidadControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private UsuarioRepository usuarioRepository;
    @Autowired
    private EspecialidadRepository especialidadRepository;

    @Value("${admin.secret-key}")
    private String adminSecretKey;

    private String adminToken;
    private String pacienteToken;
    private Long testEspecialidadId;

    @BeforeEach
    void setUp() throws Exception {
        // Limpiar la base de datos
        especialidadRepository.deleteAll();
        usuarioRepository.deleteAll();

        // 1. Crear Admin y obtener token
        AdminRegisterRequestDTO adminRequest = new AdminRegisterRequestDTO();
        adminRequest.setEmail("admin.esp@integracion.com");
        adminRequest.setPassword("adminpass123");
        adminRequest.setSecretKey(adminSecretKey);
        mockMvc.perform(post("/api/auth/register-admin")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(adminRequest)));
        this.adminToken = loginAndGetToken("admin.esp@integracion.com", "adminpass123");

        // 2. Crear Paciente y obtener token
        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"email\":\"paciente.esp@integracion.com\",\"password\":\"password123\",\"nombreCompleto\":\"Paciente Esp Test\"}"));
        this.pacienteToken = loginAndGetToken("paciente.esp@integracion.com", "password123");

        // 3. Crear una especialidad de prueba
        Especialidad especialidad = new Especialidad(null, "Pediatría", "Cuidado de niños");
        especialidadRepository.save(especialidad);
        this.testEspecialidadId = especialidad.getId();
    }

    private String loginAndGetToken(String email, String password) throws Exception {
        AuthRequestDTO loginRequest = new AuthRequestDTO(email, password);
        MvcResult result = mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andReturn();
        JsonNode jsonNode = objectMapper.readTree(result.getResponse().getContentAsString());
        return jsonNode.get("token").asText();
    }

    @Test
    @DisplayName("GET /api/especialidades - Debería devolver la lista de especialidades a un usuario autenticado")
    void getAllEspecialidades_shouldReturnList() throws Exception {
        mockMvc.perform(get("/api/especialidades")
                        .header("Authorization", "Bearer " + this.pacienteToken)) // Cualquier usuario autenticado puede ver
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].nombre", is("Pediatría")));
    }

    @Test
    @DisplayName("POST /api/especialidades - Debería permitir a un ADMIN crear una nueva especialidad")
    void createEspecialidad_asAdmin_shouldSucceed() throws Exception {
        EspecialidadDTO nuevaEspecialidad = new EspecialidadDTO();
        nuevaEspecialidad.setNombre("Ginecología");

        mockMvc.perform(post("/api/especialidades")
                        .header("Authorization", "Bearer " + this.adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(nuevaEspecialidad)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.nombre", is("Ginecología")));
    }

    @Test
    @DisplayName("POST /api/especialidades - Debería denegar a un PACIENTE crear una especialidad")
    void createEspecialidad_asPaciente_shouldBeForbidden() throws Exception {
        EspecialidadDTO nuevaEspecialidad = new EspecialidadDTO();
        nuevaEspecialidad.setNombre("Ginecología");

        mockMvc.perform(post("/api/especialidades")
                        .header("Authorization", "Bearer " + this.pacienteToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(nuevaEspecialidad)))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("DELETE /api/especialidades/{id} - Debería permitir a un ADMIN eliminar una especialidad")
    void deleteEspecialidad_asAdmin_shouldSucceed() throws Exception {
        mockMvc.perform(delete("/api/especialidades/" + this.testEspecialidadId)
                        .header("Authorization", "Bearer " + this.adminToken))
                .andExpect(status().isNoContent());

        assertTrue(especialidadRepository.findById(this.testEspecialidadId).isEmpty());
    }

    @Test
    @DisplayName("DELETE /api/especialidades/{id} - Debería denegar a un PACIENTE eliminar una especialidad")
    void deleteEspecialidad_asPaciente_shouldBeForbidden() throws Exception {
        mockMvc.perform(delete("/api/especialidades/" + this.testEspecialidadId)
                        .header("Authorization", "Bearer " + this.pacienteToken))
                .andExpect(status().isForbidden());
    }
}