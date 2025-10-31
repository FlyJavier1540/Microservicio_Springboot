package com.citasmedicas.api.controllers;

import com.citasmedicas.api.dtos.AdminRegisterRequestDTO;
import com.citasmedicas.api.dtos.AuthRequestDTO;
import com.citasmedicas.api.dtos.HistorialMedicoDTO;
import com.citasmedicas.api.enums.EstadoCita;
import com.citasmedicas.api.models.*;
import com.citasmedicas.api.repositories.*;
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@DisplayName("Pruebas de Integración para el Controlador de Historial Médico")
class HistorialMedicoControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private UsuarioRepository usuarioRepository;
    @Autowired
    private PacienteRepository pacienteRepository;
    @Autowired
    private DoctorRepository doctorRepository;
    @Autowired
    private CitaRepository citaRepository;
    @Autowired
    private HistorialMedicoRepository historialMedicoRepository;
    @Autowired // <-- Importante
    private EspecialidadRepository especialidadRepository;

    @Value("${admin.secret-key}")
    private String adminSecretKey;

    private String doctorToken;
    private String pacienteToken;
    private Long pacienteId;
    private Long citaCompletadaId;

    @BeforeEach
    void setUp() throws Exception {
        // Limpiar la base de datos
        historialMedicoRepository.deleteAll();
        citaRepository.deleteAll();
        pacienteRepository.deleteAll();
        doctorRepository.deleteAll();
        usuarioRepository.deleteAll();
        especialidadRepository.deleteAll();

        // 1. Crear un ADMIN
        String adminToken = createAndLoginAdmin("admin.hm@integracion.com", "adminpass");

        // 2. Crear un PACIENTE
        // (El código para crear paciente se mantiene igual)
        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"email\":\"paciente.hm@integracion.com\",\"password\":\"password123\",\"nombreCompleto\":\"Paciente HM Test\"}"));
        this.pacienteToken = loginAndGetToken("paciente.hm@integracion.com", "password123");
        Paciente paciente = pacienteRepository.findByUsuarioId(usuarioRepository.findByEmail("paciente.hm@integracion.com").get().getId()).get();
        this.pacienteId = paciente.getId();

        // --- SOLUCIÓN: Crear una Especialidad antes de crear el Doctor ---
        Especialidad especialidad = new Especialidad(null, "Medicina General", null);
        especialidadRepository.save(especialidad);

        // 3. Crear un DOCTOR con la especialidad creada
        MvcResult doctorResult = mockMvc.perform(post("/api/doctores/register")
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"nombreCompleto\":\"Dr. HM Test\",\"especialidadId\":" + especialidad.getId() + "}")) // Usar el ID de la especialidad
                .andExpect(status().isCreated()) // Esperar 201 Created
                .andReturn();
        JsonNode doctorNode = objectMapper.readTree(doctorResult.getResponse().getContentAsString());
        Doctor doctor = doctorRepository.findById(doctorNode.get("id").asLong()).get();
        String doctorEmail = doctor.getUsuario().getEmail();
        String doctorPassword = doctorNode.get("password").asText();
        this.doctorToken = loginAndGetToken(doctorEmail, doctorPassword);

        // 4. Crear una cita y marcarla como COMPLETADA
        Cita cita = new Cita(null, LocalDateTime.now().minusDays(1), EstadoCita.COMPLETADA, "Consulta previa", doctor, paciente);
        citaRepository.save(cita);
        this.citaCompletadaId = cita.getId();
    }

    private String loginAndGetToken(String email, String password) throws Exception {
        AuthRequestDTO loginRequest = new AuthRequestDTO(email, password);
        MvcResult result = mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andReturn();
        return objectMapper.readTree(result.getResponse().getContentAsString()).get("token").asText();
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
    @DisplayName("POST /api/historial - Debería permitir a un DOCTOR crear un nuevo historial médico")
    void createHistorial_asDoctor_shouldSucceed() throws Exception {
        HistorialMedicoDTO historialDTO = new HistorialMedicoDTO();
        historialDTO.setCitaId(this.citaCompletadaId);
        historialDTO.setDiagnostico("Gripe estacional");
        historialDTO.setTratamiento("Reposo e hidratación");

        mockMvc.perform(post("/api/historial")
                        .header("Authorization", "Bearer " + this.doctorToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(historialDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.diagnostico", is("Gripe estacional")));
    }

    @Test
    @DisplayName("POST /api/historial - Debería denegar a un PACIENTE crear un historial médico")
    void createHistorial_asPaciente_shouldBeForbidden() throws Exception {
        HistorialMedicoDTO historialDTO = new HistorialMedicoDTO();
        historialDTO.setCitaId(this.citaCompletadaId);
        historialDTO.setDiagnostico("Autodiagnóstico");

        mockMvc.perform(post("/api/historial")
                        .header("Authorization", "Bearer " + this.pacienteToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(historialDTO)))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("GET /api/historial/paciente/{id} - Debería permitir a un DOCTOR ver el historial de un paciente")
    void getHistorialByPaciente_asDoctor_shouldSucceed() throws Exception {
        // Primero, creamos un historial para que haya algo que consultar
        createHistorial_asDoctor_shouldSucceed();

        mockMvc.perform(get("/api/historial/paciente/" + this.pacienteId)
                        .header("Authorization", "Bearer " + this.doctorToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].diagnostico", is("Gripe estacional")));
    }

    @Test
    @DisplayName("GET /api/historial/paciente/{id} - Debería denegar a un PACIENTE ver su propio historial a través de este endpoint")
    void getHistorialByPaciente_asPaciente_shouldBeForbidden() throws Exception {
        mockMvc.perform(get("/api/historial/paciente/" + this.pacienteId)
                        .header("Authorization", "Bearer " + this.pacienteToken))
                .andExpect(status().isForbidden());
    }
}