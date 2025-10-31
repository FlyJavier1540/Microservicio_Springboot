package com.citasmedicas.api.controllers;

import com.citasmedicas.api.dtos.AdminRegisterRequestDTO;
import com.citasmedicas.api.dtos.AuthRequestDTO;
import com.citasmedicas.api.enums.EstadoCita;
import com.citasmedicas.api.models.Cita;
import com.citasmedicas.api.models.Doctor;
import com.citasmedicas.api.models.Paciente;
import com.citasmedicas.api.repositories.CitaRepository;
import com.citasmedicas.api.repositories.DoctorRepository;
import com.citasmedicas.api.repositories.PacienteRepository;
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

import java.time.LocalDateTime;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@DisplayName("Pruebas de Integración para el Controlador de Pacientes")
class PacienteControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private UsuarioRepository usuarioRepository;
    @Autowired
    private PacienteRepository pacienteRepository;
    @Autowired
    private CitaRepository citaRepository;
    @Autowired
    private DoctorRepository doctorRepository;

    @Value("${admin.secret-key}")
    private String adminSecretKey;

    private String adminToken;
    private String pacienteToken;
    private Long testPacienteId;

    @BeforeEach
    void setUp() throws Exception {
        citaRepository.deleteAll();
        pacienteRepository.deleteAll();
        doctorRepository.deleteAll();
        usuarioRepository.deleteAll();

        // Crear Admin
        this.adminToken = createAndLoginAdmin("admin.pac@integracion.com", "adminpass123");

        // Crear Paciente
        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"email\":\"paciente.test@integracion.com\",\"password\":\"password123\",\"nombreCompleto\":\"Paciente de Prueba\"}"));
        this.pacienteToken = loginAndGetToken("paciente.test@integracion.com", "password123");

        Paciente paciente = pacienteRepository.findByUsuarioId(
                usuarioRepository.findByEmail("paciente.test@integracion.com").get().getId()
        ).get();
        this.testPacienteId = paciente.getId();
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

    private String createAndLoginAdmin(String email, String password) throws Exception {
        AdminRegisterRequestDTO adminRequest = new AdminRegisterRequestDTO();
        adminRequest.setEmail(email);
        adminRequest.setPassword(password);
        adminRequest.setSecretKey(adminSecretKey);
        mockMvc.perform(post("/api/auth/register-admin")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(adminRequest)));
        return loginAndGetToken(email, password);
    }

    @Test
    @DisplayName("GET /api/pacientes - Debería permitir a un ADMIN ver la lista de pacientes")
    void getAllPacientes_asAdmin_shouldReturnList() throws Exception {
        mockMvc.perform(get("/api/pacientes")
                        .header("Authorization", "Bearer " + this.adminToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].nombreCompleto", is("Paciente de Prueba")));
    }

    @Test
    @DisplayName("GET /api/pacientes - Debería denegar a un PACIENTE ver la lista de pacientes")
    void getAllPacientes_asPaciente_shouldBeForbidden() throws Exception {
        mockMvc.perform(get("/api/pacientes")
                        .header("Authorization", "Bearer " + this.pacienteToken))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("GET /api/pacientes/{id} - Debería permitir a un ADMIN ver los detalles de cualquier paciente")
    void getPacienteById_asAdmin_shouldReturnDetails() throws Exception {
        mockMvc.perform(get("/api/pacientes/" + this.testPacienteId)
                        .header("Authorization", "Bearer " + this.adminToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(this.testPacienteId.intValue())));
    }

    @Test
    @DisplayName("DELETE /api/pacientes/{id} - Debería devolver error si el paciente tiene citas")
    void deletePaciente_withCitas_shouldFail() throws Exception {
        // 1. Crear un Doctor y guardarlo para tener un ID válido
        Doctor doctor = new Doctor();
        doctor.setNombreCompleto("Dr. de Prueba para Cita");
        doctorRepository.save(doctor);

        // 2. Obtener el paciente que vamos a probar
        Paciente paciente = pacienteRepository.findById(testPacienteId).get();

        // 3. Crear la nueva cita
        Cita cita = new Cita(null, LocalDateTime.now(), EstadoCita.SOLICITADA, "Motivo", doctor, paciente);

        // --- SOLUCIÓN: Añadir la cita a la lista del paciente ---
        // Esto sincroniza el lado "inverso" de la relación en memoria.
        paciente.getCitas().add(cita);

        // 4. Guardar la cita (esto actualizará la relación en la BD)
        citaRepository.save(cita);

        // 5. Ahora, al llamar a delete, el servicio encontrará la cita y fallará como se espera
        mockMvc.perform(delete("/api/pacientes/" + this.testPacienteId)
                        .header("Authorization", "Bearer " + this.adminToken))
                .andExpect(status().isInternalServerError());
    }

    @Test
    @DisplayName("DELETE /api/pacientes/{id} - Debería permitir a un ADMIN eliminar un paciente sin citas")
    void deletePaciente_asAdmin_shouldSucceed() throws Exception {
        mockMvc.perform(delete("/api/pacientes/" + this.testPacienteId)
                        .header("Authorization", "Bearer " + this.adminToken))
                .andExpect(status().isNoContent());

        assertTrue(pacienteRepository.findById(this.testPacienteId).isEmpty());
    }

    @Test
    @DisplayName("DELETE /api/pacientes/{id} - Debería denegar a un PACIENTE eliminar a otro paciente")
    void deletePaciente_asPaciente_shouldBeForbidden() throws Exception {
        mockMvc.perform(delete("/api/pacientes/" + this.testPacienteId)
                        .header("Authorization", "Bearer " + this.pacienteToken))
                .andExpect(status().isForbidden());
    }
}