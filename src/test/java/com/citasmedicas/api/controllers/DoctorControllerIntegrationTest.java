package com.citasmedicas.api.controllers;

import com.citasmedicas.api.dtos.AdminRegisterRequestDTO;
import com.citasmedicas.api.dtos.AuthRequestDTO;
import com.citasmedicas.api.models.Especialidad;
import com.citasmedicas.api.repositories.DoctorRepository;
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
@DisplayName("Pruebas de Integración para el Controlador de Doctores")
class DoctorControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private UsuarioRepository usuarioRepository;
    @Autowired
    private DoctorRepository doctorRepository;
    @Autowired
    private EspecialidadRepository especialidadRepository;

    @Value("${admin.secret-key}")
    private String adminSecretKey;

    private String adminToken;
    private String pacienteToken;
    private Long testDoctorId;

    @BeforeEach
    void setUp() throws Exception {
        doctorRepository.deleteAll();
        usuarioRepository.deleteAll();
        especialidadRepository.deleteAll();

        AdminRegisterRequestDTO adminRequest = new AdminRegisterRequestDTO();
        adminRequest.setEmail("admin.doc@integracion.com");
        adminRequest.setPassword("adminpass123");
        adminRequest.setSecretKey(adminSecretKey);
        mockMvc.perform(post("/api/auth/register-admin")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(adminRequest)));
        this.adminToken = loginAndGetToken("admin.doc@integracion.com", "adminpass123");

        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"email\":\"paciente.doc@integracion.com\",\"password\":\"password123\",\"nombreCompleto\":\"Paciente Doctor Test\"}"));
        this.pacienteToken = loginAndGetToken("paciente.doc@integracion.com", "password123");

        Especialidad especialidad = new Especialidad(null, "Inmunología", null);
        especialidadRepository.save(especialidad);

        MvcResult doctorResult = mockMvc.perform(post("/api/doctores/register")
                        .header("Authorization", "Bearer " + this.adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"nombreCompleto\":\"Dr. House\",\"especialidadId\":" + especialidad.getId() + "}"))
                .andExpect(status().isCreated())
                .andReturn();
        JsonNode doctorNode = objectMapper.readTree(doctorResult.getResponse().getContentAsString());
        this.testDoctorId = doctorNode.get("id").asLong();
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
    @DisplayName("GET /api/doctores - Debería devolver la lista de doctores a cualquier usuario autenticado")
    void getAllDoctores_shouldReturnDoctorList() throws Exception {
        mockMvc.perform(get("/api/doctores")
                        .header("Authorization", "Bearer " + this.pacienteToken)) // Se necesita estar autenticado
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].nombreCompleto", is("Dr. House")));
    }

    @Test
    @DisplayName("GET /api/doctores/{id} - Debería devolver los detalles de un doctor a cualquier usuario autenticado")
    void getDoctorById_shouldReturnDoctorDetails() throws Exception {
        mockMvc.perform(get("/api/doctores/" + this.testDoctorId)
                        .header("Authorization", "Bearer " + this.pacienteToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(this.testDoctorId.intValue())))
                .andExpect(jsonPath("$.nombreCompleto", is("Dr. House")));
    }

    @Test
    @DisplayName("DELETE /api/doctores/{id} - Debería permitir a un ADMIN eliminar un doctor")
    void deleteDoctor_asAdmin_shouldSucceed() throws Exception {
        mockMvc.perform(delete("/api/doctores/" + this.testDoctorId)
                        .header("Authorization", "Bearer " + this.adminToken))
                .andExpect(status().isNoContent());

        assertTrue(doctorRepository.findById(this.testDoctorId).isEmpty());
    }

    @Test
    @DisplayName("DELETE /api/doctores/{id} - Debería denegar a un PACIENTE eliminar un doctor")
    void deleteDoctor_asPaciente_shouldBeForbidden() throws Exception {
        mockMvc.perform(delete("/api/doctores/" + this.testDoctorId)
                        .header("Authorization", "Bearer " + this.pacienteToken))
                .andExpect(status().isForbidden());
    }
}