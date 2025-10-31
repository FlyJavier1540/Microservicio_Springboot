package com.citasmedicas.api.services;

import com.citasmedicas.api.dtos.HistorialMedicoDTO;
import com.citasmedicas.api.enums.EstadoCita;
import com.citasmedicas.api.enums.RolNombre;
import com.citasmedicas.api.exceptions.ResourceNotFoundException;
import com.citasmedicas.api.mappers.HistorialMedicoMapper;
import com.citasmedicas.api.models.*;
import com.citasmedicas.api.repositories.CitaRepository;
import com.citasmedicas.api.repositories.DoctorRepository;
import com.citasmedicas.api.repositories.HistorialMedicoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.access.AccessDeniedException;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Pruebas del Servicio de Historial Médico (HistorialMedicoService)")
class HistorialMedicoServiceTest {

    @Mock
    private HistorialMedicoRepository historialMedicoRepository;
    @Mock
    private CitaRepository citaRepository;
    @Mock
    private DoctorRepository doctorRepository;
    @Mock
    private HistorialMedicoMapper historialMedicoMapper;

    @InjectMocks
    private HistorialMedicoService historialMedicoService;

    // --- Datos de prueba reutilizables ---
    private Usuario usuarioDoctor;
    private Doctor doctor;
    private Paciente paciente;
    private Cita citaCompletada;
    private HistorialMedico historial;
    private HistorialMedicoDTO historialDTO;

    @BeforeEach
    void setUp() {
        usuarioDoctor = new Usuario(1L, "doctor@test.com", "pass", Set.of(new Rol(2L, RolNombre.DOCTOR)));
        doctor = new Doctor();
        doctor.setId(1L);
        doctor.setUsuario(usuarioDoctor);

        paciente = new Paciente();
        paciente.setId(1L);

        citaCompletada = new Cita();
        citaCompletada.setId(1L);
        citaCompletada.setEstado(EstadoCita.COMPLETADA);
        citaCompletada.setDoctor(doctor);
        citaCompletada.setPaciente(paciente);

        historialDTO = new HistorialMedicoDTO();
        historialDTO.setCitaId(citaCompletada.getId());
        historialDTO.setDiagnostico("Migraña crónica");
        historialDTO.setTratamiento("Analgésicos");

        historial = new HistorialMedico();
        historial.setId(1L);
        historial.setCita(citaCompletada);
        historial.setDoctor(doctor);
        historial.setPaciente(paciente);
    }

    @Nested
    @DisplayName("Pruebas para Crear Historial")
    class CrearHistorialTests {

        @Test
        @DisplayName("Debería crear un registro de historial médico exitosamente")
        void crearHistorial_Success() {
            // Arrange
            when(citaRepository.findById(citaCompletada.getId())).thenReturn(Optional.of(citaCompletada));
            when(doctorRepository.findByUsuarioId(usuarioDoctor.getId())).thenReturn(Optional.of(doctor));
            when(historialMedicoMapper.toEntity(any(HistorialMedicoDTO.class))).thenReturn(new HistorialMedico());
            when(historialMedicoRepository.save(any(HistorialMedico.class))).thenReturn(historial);
            when(historialMedicoMapper.toDto(any(HistorialMedico.class))).thenReturn(historialDTO);

            // Act
            HistorialMedicoDTO resultado = historialMedicoService.crearHistorial(historialDTO, usuarioDoctor);

            // Assert
            assertNotNull(resultado);
            assertEquals(historialDTO.getDiagnostico(), resultado.getDiagnostico());
            verify(historialMedicoRepository).save(any(HistorialMedico.class));
        }

        @Test
        @DisplayName("Debería lanzar IllegalStateException si la cita no está completada")
        void crearHistorial_ShouldThrowException_WhenCitaIsNotCompletada() {
            // Arrange
            Cita citaProgramada = new Cita();
            citaProgramada.setId(2L);
            citaProgramada.setEstado(EstadoCita.PROGRAMADA); // Estado incorrecto
            historialDTO.setCitaId(citaProgramada.getId());

            when(citaRepository.findById(citaProgramada.getId())).thenReturn(Optional.of(citaProgramada));

            // Act & Assert
            IllegalStateException exception = assertThrows(IllegalStateException.class,
                    () -> historialMedicoService.crearHistorial(historialDTO, usuarioDoctor));
            assertEquals("Solo se puede crear un historial para citas completadas.", exception.getMessage());
        }

        @Test
        @DisplayName("Debería lanzar AccessDeniedException si el doctor no es el asignado a la cita")
        void crearHistorial_ShouldThrowException_WhenDoctorIsNotAssigned() {
            // Arrange
            Usuario otroDoctorUsuario = new Usuario(2L, "otro.doctor@test.com", "pass", Set.of(new Rol(2L, RolNombre.DOCTOR)));
            Doctor otroDoctor = new Doctor();
            otroDoctor.setId(2L);
            otroDoctor.setUsuario(otroDoctorUsuario);

            when(citaRepository.findById(citaCompletada.getId())).thenReturn(Optional.of(citaCompletada));
            when(doctorRepository.findByUsuarioId(otroDoctorUsuario.getId())).thenReturn(Optional.of(otroDoctor));

            // Act & Assert
            AccessDeniedException exception = assertThrows(AccessDeniedException.class,
                    () -> historialMedicoService.crearHistorial(historialDTO, otroDoctorUsuario));
            assertEquals("No tiene permiso para crear un historial para esta cita.", exception.getMessage());
        }

        @Test
        @DisplayName("Debería lanzar ResourceNotFoundException si la cita no existe")
        void crearHistorial_ShouldThrowException_WhenCitaNotFound() {
            // Arrange
            when(citaRepository.findById(anyLong())).thenReturn(Optional.empty());

            // Act & Assert
            assertThrows(ResourceNotFoundException.class,
                    () -> historialMedicoService.crearHistorial(historialDTO, usuarioDoctor));
        }
    }

    @Nested
    @DisplayName("Pruebas para Obtener Historial por Paciente")
    class ObtenerHistorialTests {

        @Test
        @DisplayName("Debería devolver el historial médico completo de un paciente")
        void obtenerHistorialPorPaciente_Success() {
            // Arrange
            when(historialMedicoRepository.findAllByPacienteId(paciente.getId())).thenReturn(List.of(historial));
            when(historialMedicoMapper.toDto(any(HistorialMedico.class))).thenReturn(historialDTO);

            // Act
            List<HistorialMedicoDTO> resultados = historialMedicoService.obtenerHistorialPorPaciente(paciente.getId());

            // Assert
            assertNotNull(resultados);
            assertFalse(resultados.isEmpty());
            assertEquals(1, resultados.size());
        }

        @Test
        @DisplayName("Debería devolver una lista vacía si el paciente no tiene historial")
        void obtenerHistorialPorPaciente_ShouldReturnEmptyList() {
            // Arrange
            when(historialMedicoRepository.findAllByPacienteId(paciente.getId())).thenReturn(Collections.emptyList());

            // Act
            List<HistorialMedicoDTO> resultados = historialMedicoService.obtenerHistorialPorPaciente(paciente.getId());

            // Assert
            assertNotNull(resultados);
            assertTrue(resultados.isEmpty());
        }
    }
}