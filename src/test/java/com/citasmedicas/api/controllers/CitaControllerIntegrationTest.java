package com.citasmedicas.api.controllers;

import com.citasmedicas.api.dtos.*;
import com.citasmedicas.api.enums.DiaDeLaSemana;
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

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@DisplayName("Pruebas de Integración para el Controlador de Citas (CitaController)")
class CitaControllerIntegrationTest {

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
    private EspecialidadRepository especialidadRepository;
    @Autowired
    private CitaRepository citaRepository;
    @Autowired
    private HorarioRepository horarioRepository;

    @Value("${admin.secret-key}")
    private String adminSecretKey;

    private String adminToken;
    private String pacienteToken;
    private String doctorToken;
    private Long doctorId;
    private Cita testCita;

    private final LocalDate futureMonday = LocalDate.of(2025, 11, 3);

    @BeforeEach
    void setUp() throws Exception {
        citaRepository.deleteAll();
        horarioRepository.deleteAll();
        pacienteRepository.deleteAll();
        doctorRepository.deleteAll();
        usuarioRepository.deleteAll();
        especialidadRepository.deleteAll();

        // Crear Admin
        AdminRegisterRequestDTO adminRequest = new AdminRegisterRequestDTO();
        adminRequest.setEmail("admin@integracion.com");
        adminRequest.setPassword("adminpass123");
        adminRequest.setSecretKey(adminSecretKey);
        mockMvc.perform(post("/api/auth/register-admin")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(adminRequest)));
        this.adminToken = loginAndGetToken("admin@integracion.com", "adminpass123");

        // Crear Especialidad
        Especialidad cardiologia = new Especialidad();
        cardiologia.setNombre("Cardiología");
        especialidadRepository.save(cardiologia);

        // Crear Paciente
        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"email\":\"paciente@integracion.com\",\"password\":\"password123\",\"nombreCompleto\":\"Paciente de Prueba\"}"));
        this.pacienteToken = loginAndGetToken("paciente@integracion.com", "password123");

        // Crear Doctor
        MvcResult doctorResult = mockMvc.perform(post("/api/doctores/register")
                        .header("Authorization", "Bearer " + this.adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"nombreCompleto\":\"Dr. Integracion\",\"especialidadId\":" + cardiologia.getId() + "}"))
                .andExpect(status().isCreated())
                .andReturn();
        JsonNode doctorNode = objectMapper.readTree(doctorResult.getResponse().getContentAsString());
        this.doctorId = doctorNode.get("id").asLong();

        // Crear Horario para Doctor
        Doctor doctor = doctorRepository.findById(this.doctorId).get();
        Horario horario = new Horario(null, DiaDeLaSemana.LUNES, LocalTime.of(9, 0), LocalTime.of(17, 0), doctor);
        horarioRepository.save(horario);

        // Loguear Doctor
        String doctorEmail = doctor.getUsuario().getEmail();
        String doctorPassword = doctorNode.get("password").asText();
        this.doctorToken = loginAndGetToken(doctorEmail, doctorPassword);

        // Crear una Cita base para usar en las pruebas
        Paciente paciente = pacienteRepository.findByUsuarioId(usuarioRepository.findByEmail("paciente@integracion.com").get().getId()).get();
        testCita = new Cita(null, futureMonday.atStartOfDay(), EstadoCita.SOLICITADA, "Dolor de cabeza", doctor, paciente);
        citaRepository.save(testCita);
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
    @DisplayName("Flujo completo: Paciente solicita, Doctor aprueba, completa y Admin elimina")
    void fullCitaFlow_Success() throws Exception {
        // 1. DOCTOR APRUEBA
        CitaAprobacionDTO aprobacionDTO = new CitaAprobacionDTO();
        aprobacionDTO.setHora(LocalTime.of(10, 30));

        mockMvc.perform(patch("/api/citas/" + testCita.getId() + "/aprobar")
                        .header("Authorization", "Bearer " + this.doctorToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(aprobacionDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.estado").value("PROGRAMADA"));

        // 2. DOCTOR COMPLETA LA CITA
        mockMvc.perform(patch("/api/citas/" + testCita.getId() + "/completar")
                        .header("Authorization", "Bearer " + this.doctorToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.estado").value("COMPLETADA"));

        // 3. ADMIN ELIMINA LA CITA
        mockMvc.perform(delete("/api/citas/" + testCita.getId())
                        .header("Authorization", "Bearer " + this.adminToken))
                .andExpect(status().isNoContent());

        // Verificar que la cita ya no existe
        assertTrue(citaRepository.findById(testCita.getId()).isEmpty());
    }

    @Test
    @DisplayName("Debería denegar a un Paciente el acceso a un endpoint de Doctor (aprobarCita)")
    void shouldForbidPacienteFromDoctorEndpoint() throws Exception {
        CitaAprobacionDTO aprobacionDTO = new CitaAprobacionDTO();
        aprobacionDTO.setHora(LocalTime.of(9, 0));

        mockMvc.perform(patch("/api/citas/" + testCita.getId() + "/aprobar")
                        .header("Authorization", "Bearer " + this.pacienteToken) // Token de paciente
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(aprobacionDTO)))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("Debería permitir al Doctor posponer una cita")
    void postponeCita_Success() throws Exception {
        // Arrange
        CitaPospuestaDTO pospuestaDTO = new CitaPospuestaDTO();
        pospuestaDTO.setNuevaFechaHora(LocalDateTime.of(2025, 11, 10, 14, 0)); // Otro Lunes

        // Crear un horario para el nuevo día
        Doctor doctor = doctorRepository.findById(doctorId).get();
        Horario nuevoHorario = new Horario(null, DiaDeLaSemana.LUNES, LocalTime.of(13, 0), LocalTime.of(18, 0), doctor);
        horarioRepository.save(nuevoHorario);

        // Act & Assert
        mockMvc.perform(patch("/api/citas/" + testCita.getId() + "/posponer")
                        .header("Authorization", "Bearer " + this.doctorToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(pospuestaDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.fechaHora").value("2025-11-10T14:00:00"));
    }

    @Test
    @DisplayName("Debería permitir al Admin rechazar una cita")
    void rejectCita_AsAdmin_Success() throws Exception {
        mockMvc.perform(patch("/api/citas/" + testCita.getId() + "/rechazar")
                        .header("Authorization", "Bearer " + this.adminToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.estado").value("RECHAZADA"));

        assertEquals(EstadoCita.RECHAZADA, citaRepository.findById(testCita.getId()).get().getEstado());
    }
}