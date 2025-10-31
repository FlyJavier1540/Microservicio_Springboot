package com.citasmedicas.api.services;

import com.citasmedicas.api.dtos.CitaAprobacionDTO;
import com.citasmedicas.api.dtos.CitaDTO;
import com.citasmedicas.api.dtos.CitaSolicitudDTO;
import com.citasmedicas.api.enums.DiaDeLaSemana;
import com.citasmedicas.api.enums.EstadoCita;
import com.citasmedicas.api.enums.RolNombre;
import com.citasmedicas.api.exceptions.ResourceNotFoundException;
import com.citasmedicas.api.mappers.CitaMapper;
import com.citasmedicas.api.models.*;
import com.citasmedicas.api.repositories.CitaRepository;
import com.citasmedicas.api.repositories.DoctorRepository;
import com.citasmedicas.api.repositories.HorarioRepository;
import com.citasmedicas.api.repositories.PacienteRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.access.AccessDeniedException;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Pruebas del Servicio de Citas (CitaService)")
class CitaServiceTest {

    @Mock
    private CitaRepository citaRepository;
    @Mock
    private PacienteRepository pacienteRepository;
    @Mock
    private DoctorRepository doctorRepository;
    @Mock
    private HorarioRepository horarioRepository;
    @Mock
    private CitaMapper citaMapper;

    @InjectMocks
    private CitaService citaService;

    // --- Datos de prueba reutilizables ---
    private Usuario usuarioPaciente, usuarioDoctor, usuarioAdmin;
    private Paciente paciente;
    private Doctor doctor;
    private Cita cita;
    private CitaDTO citaDTO;

    @BeforeEach
    void setUp() {
        usuarioPaciente = new Usuario(1L, "paciente@test.com", "pass", Set.of(new Rol(3L, RolNombre.PACIENTE)));
        usuarioDoctor = new Usuario(2L, "doctor@test.com", "pass", Set.of(new Rol(2L, RolNombre.DOCTOR)));
        usuarioAdmin = new Usuario(3L, "admin@test.com", "pass", Set.of(new Rol(1L, RolNombre.ADMIN)));

        paciente = new Paciente();
        paciente.setId(1L);
        paciente.setUsuario(usuarioPaciente);

        doctor = new Doctor();
        doctor.setId(1L);
        doctor.setUsuario(usuarioDoctor);

        cita = new Cita();
        cita.setId(1L);
        cita.setDoctor(doctor);
        cita.setPaciente(paciente);
        // Usamos una fecha fija para hacer la prueba determinista
        cita.setFechaHora(LocalDateTime.of(2025, 11, 3, 0, 0)); // Un Lunes
        cita.setEstado(EstadoCita.SOLICITADA);

        citaDTO = new CitaDTO();
        citaDTO.setId(1L);
    }

    @Nested
    @DisplayName("Pruebas para Solicitar Cita")
    class SolicitarCitaTests {

        private CitaSolicitudDTO solicitudDTO;

        @BeforeEach
        void setUp() {
            solicitudDTO = new CitaSolicitudDTO();
            solicitudDTO.setDoctorId(doctor.getId());
            solicitudDTO.setFecha(LocalDate.of(2025, 11, 10)); // Un Lunes
            solicitudDTO.setMotivoConsulta("Dolor de cabeza");
        }

        @Test
        @DisplayName("Debería solicitar una cita exitosamente")
        void solicitarCita_Success() {
            // Arrange
            when(pacienteRepository.findByUsuarioId(usuarioPaciente.getId())).thenReturn(Optional.of(paciente));
            when(doctorRepository.findById(doctor.getId())).thenReturn(Optional.of(doctor));
            when(citaRepository.save(any(Cita.class))).thenReturn(cita);
            when(citaMapper.toDto(any(Cita.class))).thenReturn(citaDTO);

            // Act
            CitaDTO resultado = citaService.solicitarCita(solicitudDTO, usuarioPaciente);

            // Assert
            assertNotNull(resultado);
            assertEquals(cita.getId(), resultado.getId());
            verify(citaRepository).save(any(Cita.class));
        }

        @Test
        @DisplayName("Debería lanzar ResourceNotFoundException si el perfil del paciente no existe")
        void solicitarCita_ShouldThrowException_WhenPacienteProfileNotFound() {
            // Arrange
            when(pacienteRepository.findByUsuarioId(anyLong())).thenReturn(Optional.empty());

            // Act & Assert
            assertThrows(ResourceNotFoundException.class, () -> citaService.solicitarCita(solicitudDTO, usuarioPaciente));
        }
    }

    @Nested
    @DisplayName("Pruebas para Aprobar Cita")
    class AprobarCitaTests {
        private CitaAprobacionDTO aprobacionDTO;
        private LocalDateTime fechaHoraAprobada;

        @BeforeEach
        void setup() {
            aprobacionDTO = new CitaAprobacionDTO();
            aprobacionDTO.setHora(LocalTime.of(10, 0));

            // Aseguramos que la cita sea un lunes
            cita.setFechaHora(LocalDate.of(2025, 11, 3).atStartOfDay());
            fechaHoraAprobada = cita.getFechaHora().toLocalDate().atTime(aprobacionDTO.getHora());
        }

        @Test
        @DisplayName("Debería aprobar una cita exitosamente por el doctor asignado")
        void aprobarCita_Success_ByDoctor() {
            // Arrange
            // FIX: Usamos el enum correcto en español
            Horario horario = new Horario(1L, DiaDeLaSemana.LUNES, LocalTime.of(9, 0), LocalTime.of(17, 0), doctor);
            when(citaRepository.findById(cita.getId())).thenReturn(Optional.of(cita));
            when(doctorRepository.findByUsuarioId(usuarioDoctor.getId())).thenReturn(Optional.of(doctor));
            when(horarioRepository.findAllByDoctorId(doctor.getId())).thenReturn(List.of(horario));
            when(citaRepository.findByDoctorIdAndFechaHoraBetween(anyLong(), any(), any())).thenReturn(Collections.emptyList());
            when(citaRepository.save(any(Cita.class))).thenReturn(cita);
            when(citaMapper.toDto(any(Cita.class))).thenReturn(citaDTO);

            // Act
            CitaDTO resultado = citaService.aprobarCita(cita.getId(), aprobacionDTO, usuarioDoctor);

            // Assert
            assertNotNull(resultado);
            assertEquals(cita.getId(), resultado.getId());
            verify(citaRepository).save(argThat(c -> c.getEstado() == EstadoCita.PROGRAMADA && c.getFechaHora().equals(fechaHoraAprobada)));
        }

        @Test
        @DisplayName("Debería lanzar AccessDeniedException si un doctor no asignado intenta aprobar")
        void aprobarCita_ShouldThrowException_WhenDoctorIsNotAssigned() {
            // Arrange
            Usuario otroDoctorUsuario = new Usuario(99L, "otro@doctor.com", "pass", Set.of(new Rol(2L, RolNombre.DOCTOR)));
            Doctor otroDoctor = new Doctor();
            otroDoctor.setId(99L);
            otroDoctor.setUsuario(otroDoctorUsuario);

            when(citaRepository.findById(cita.getId())).thenReturn(Optional.of(cita));
            when(doctorRepository.findByUsuarioId(otroDoctorUsuario.getId())).thenReturn(Optional.of(otroDoctor));

            // Act & Assert
            assertThrows(AccessDeniedException.class, () -> citaService.aprobarCita(cita.getId(), aprobacionDTO, otroDoctorUsuario));
        }

        @Test
        @DisplayName("Debería lanzar IllegalArgumentException si la hora está fuera del horario del doctor")
        void aprobarCita_ShouldThrowException_WhenTimeIsOutsideSchedule() {
            // Arrange
            // FIX: Usamos el enum correcto en español
            Horario horario = new Horario(1L, DiaDeLaSemana.LUNES, LocalTime.of(14, 0), LocalTime.of(17, 0), doctor);
            when(citaRepository.findById(cita.getId())).thenReturn(Optional.of(cita));
            when(doctorRepository.findByUsuarioId(usuarioDoctor.getId())).thenReturn(Optional.of(doctor));
            when(horarioRepository.findAllByDoctorId(doctor.getId())).thenReturn(List.of(horario));

            // Act & Assert
            assertThrows(IllegalArgumentException.class, () -> citaService.aprobarCita(cita.getId(), aprobacionDTO, usuarioDoctor));
        }
    }

    @Nested
    @DisplayName("Pruebas para Cambiar Estado de Cita")
    class CambiarEstadoTests {

        @Test
        @DisplayName("Debería cambiar el estado de la cita a RECHAZADA")
        void cambiarEstadoCita_ToRechazada_Success() {
            // Arrange
            when(citaRepository.findById(cita.getId())).thenReturn(Optional.of(cita));
            when(doctorRepository.findByUsuarioId(usuarioDoctor.getId())).thenReturn(Optional.of(doctor));
            when(citaRepository.save(any(Cita.class))).thenReturn(cita);
            when(citaMapper.toDto(any(Cita.class))).thenReturn(citaDTO);

            // Act
            CitaDTO resultado = citaService.cambiarEstadoCita(cita.getId(), EstadoCita.RECHAZADA, usuarioDoctor);

            // Assert
            assertNotNull(resultado);
            verify(citaRepository).save(argThat(c -> c.getEstado() == EstadoCita.RECHAZADA));
        }

        @Test
        @DisplayName("Debería cambiar el estado de la cita a COMPLETADA por un ADMIN")
        void cambiarEstadoCita_ToCompletada_ByAdmin_Success() {
            // Arrange
            when(citaRepository.findById(cita.getId())).thenReturn(Optional.of(cita));
            when(citaRepository.save(any(Cita.class))).thenReturn(cita);
            when(citaMapper.toDto(any(Cita.class))).thenReturn(citaDTO);

            // Act
            CitaDTO resultado = citaService.cambiarEstadoCita(cita.getId(), EstadoCita.COMPLETADA, usuarioAdmin);

            // Assert
            assertNotNull(resultado);
            verify(citaRepository).save(argThat(c -> c.getEstado() == EstadoCita.COMPLETADA));
        }
    }

    @Nested
    @DisplayName("Pruebas para Obtener Citas")
    class ObtenerCitasTests {

        @Test
        @DisplayName("Debería obtener las citas del paciente autenticado")
        void obtenerCitasDelPacienteAutenticado_Success() {
            // Arrange
            when(pacienteRepository.findByUsuarioId(usuarioPaciente.getId())).thenReturn(Optional.of(paciente));
            when(citaRepository.findAllByPacienteId(paciente.getId())).thenReturn(List.of(cita));
            when(citaMapper.toDto(any(Cita.class))).thenReturn(citaDTO);

            // Act
            List<CitaDTO> resultados = citaService.obtenerCitasDelPacienteAutenticado(usuarioPaciente);

            // Assert
            assertNotNull(resultados);
            assertFalse(resultados.isEmpty());
            assertEquals(1, resultados.size());
        }

        @Test
        @DisplayName("Debería devolver una lista vacía si el paciente no tiene citas")
        void obtenerCitasDelPacienteAutenticado_ShouldReturnEmptyList_WhenNoCitas() {
            // Arrange
            when(pacienteRepository.findByUsuarioId(usuarioPaciente.getId())).thenReturn(Optional.of(paciente));
            when(citaRepository.findAllByPacienteId(paciente.getId())).thenReturn(Collections.emptyList());

            // Act
            List<CitaDTO> resultados = citaService.obtenerCitasDelPacienteAutenticado(usuarioPaciente);

            // Assert
            assertNotNull(resultados);
            assertTrue(resultados.isEmpty());
        }
    }

    @Nested
    @DisplayName("Pruebas para Eliminar Cita")
    class EliminarCitaTests {

        @Test
        @DisplayName("Debería eliminar una cita exitosamente")
        void eliminarCita_Success() {
            // Arrange
            when(citaRepository.existsById(cita.getId())).thenReturn(true);
            doNothing().when(citaRepository).deleteById(cita.getId());

            // Act
            assertDoesNotThrow(() -> citaService.eliminarCita(cita.getId()));

            // Assert
            verify(citaRepository).deleteById(cita.getId());
        }

        @Test
        @DisplayName("Debería lanzar ResourceNotFoundException si la cita a eliminar no existe")
        void eliminarCita_ShouldThrowException_WhenCitaNotFound() {
            // Arrange
            when(citaRepository.existsById(anyLong())).thenReturn(false);

            // Act & Assert
            assertThrows(ResourceNotFoundException.class, () -> citaService.eliminarCita(999L));
        }
    }
}