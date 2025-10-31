package com.citasmedicas.api.controllers;

import com.citasmedicas.api.dtos.AdminRegisterRequestDTO;
import com.citasmedicas.api.dtos.AuthRequestDTO;
import com.citasmedicas.api.dtos.HorarioDTO;
import com.citasmedicas.api.enums.DiaDeLaSemana;
import com.citasmedicas.api.models.Doctor;
import com.citasmedicas.api.models.Especialidad;
import com.citasmedicas.api.repositories.DoctorRepository;
import com.citasmedicas.api.repositories.EspecialidadRepository;
import com.citasmedicas.api.repositories.HorarioRepository;
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

import java.time.LocalTime;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@DisplayName("Pruebas de Integración para el Controlador de Horarios")
class HorarioControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private UsuarioRepository usuarioRepository;
    @Autowired
    private DoctorRepository doctorRepository;
    @Autowired
    private HorarioRepository horarioRepository;
    @Autowired
    private EspecialidadRepository especialidadRepository;

    @Value("${admin.secret-key}")
    private String adminSecretKey;

    private String doctorToken;
    private String pacienteToken;
    private Long testHorarioId;

    @BeforeEach
    void setUp() throws Exception {
        // Limpiar la base de datos
        horarioRepository.deleteAll();
        doctorRepository.deleteAll();
        usuarioRepository.deleteAll();
        especialidadRepository.deleteAll();

        // 1. Crear un ADMIN para poder registrar al doctor
        String adminToken = createAndLoginAdmin("admin.hr@integracion.com", "adminpass");

        // 2. Crear una especialidad necesaria para el doctor
        Especialidad esp = new Especialidad(null, "General", null);
        especialidadRepository.save(esp);

        // 3. Crear un DOCTOR usando el token del admin
        this.doctorToken = createAndLoginDoctor(adminToken, esp.getId());

        // 4. Crear un PACIENTE para pruebas de acceso denegado
        this.pacienteToken = createAndLoginPaciente("paciente.hr@integracion.com", "pacientepass");

        // 5. Crear un horario de prueba usando el token del doctor
        HorarioDTO horarioDTO = new HorarioDTO();
        horarioDTO.setDiaDeLaSemana(DiaDeLaSemana.LUNES);
        horarioDTO.setHoraInicio(LocalTime.of(9, 0));
        horarioDTO.setHoraFin(LocalTime.of(13, 0));

        MvcResult result = mockMvc.perform(post("/api/horarios")
                        .header("Authorization", "Bearer " + this.doctorToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(horarioDTO)))
                .andExpect(status().isCreated())
                .andReturn();

        JsonNode jsonNode = objectMapper.readTree(result.getResponse().getContentAsString());
        this.testHorarioId = jsonNode.get("id").asLong();
    }

    // Métodos de ayuda refactorizados
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

    private String createAndLoginPaciente(String email, String password) throws Exception {
        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"email\":\"" + email + "\",\"password\":\"" + password + "\",\"nombreCompleto\":\"Test Paciente\"}"));
        return loginAndGetToken(email, password);
    }

    private String createAndLoginDoctor(String adminToken, Long especialidadId) throws Exception {
        MvcResult doctorResult = mockMvc.perform(post("/api/doctores/register")
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"nombreCompleto\":\"Dr. Test Horario\",\"especialidadId\":" + especialidadId + "}"))
                .andExpect(status().isCreated())
                .andReturn();

        JsonNode doctorNode = objectMapper.readTree(doctorResult.getResponse().getContentAsString());
        Long newDoctorId = doctorNode.get("id").asLong();

        Doctor doctor = doctorRepository.findById(newDoctorId).get();
        String doctorEmail = doctor.getUsuario().getEmail();
        String doctorPassword = doctorNode.get("password").asText();

        return loginAndGetToken(doctorEmail, doctorPassword);
    }

    @Test
    @DisplayName("GET /api/horarios/mi-horario - Debería devolver el horario del doctor autenticado")
    void getMiHorario_asDoctor_shouldReturnSchedule() throws Exception {
        mockMvc.perform(get("/api/horarios/mi-horario")
                        .header("Authorization", "Bearer " + this.doctorToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].diaDeLaSemana", is("LUNES")));
    }

    @Test
    @DisplayName("GET /api/horarios/mi-horario - Debería denegar el acceso a un PACIENTE")
    void getMiHorario_asPaciente_shouldBeForbidden() throws Exception {
        mockMvc.perform(get("/api/horarios/mi-horario")
                        .header("Authorization", "Bearer " + this.pacienteToken))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("DELETE /api/horarios/{id} - Debería permitir a un DOCTOR eliminar su propio horario")
    void deleteHorario_asOwnerDoctor_shouldSucceed() throws Exception {
        mockMvc.perform(delete("/api/horarios/" + this.testHorarioId)
                        .header("Authorization", "Bearer " + this.doctorToken))
                .andExpect(status().isNoContent());

        assertTrue(horarioRepository.findById(this.testHorarioId).isEmpty());
    }

    @Test
    @DisplayName("DELETE /api/horarios/{id} - Debería denegar a un PACIENTE eliminar un horario")
    void deleteHorario_asPaciente_shouldBeForbidden() throws Exception {
        mockMvc.perform(delete("/api/horarios/" + this.testHorarioId)
                        .header("Authorization", "Bearer " + this.pacienteToken))
                .andExpect(status().isForbidden());
    }
}