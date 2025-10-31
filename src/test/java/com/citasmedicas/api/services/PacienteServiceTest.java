package com.citasmedicas.api.services;

import com.citasmedicas.api.dtos.PacienteDTO;
import com.citasmedicas.api.exceptions.ResourceNotFoundException;
import com.citasmedicas.api.mappers.PacienteMapper;
import com.citasmedicas.api.models.Cita;
import com.citasmedicas.api.models.Paciente;
import com.citasmedicas.api.repositories.PacienteRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Pruebas del Servicio de Pacientes (PacienteService)")
class PacienteServiceTest {

    @Mock
    private PacienteRepository pacienteRepository;
    @Mock
    private PacienteMapper pacienteMapper;

    @InjectMocks
    private PacienteService pacienteService;

    // --- Datos de prueba reutilizables ---
    private Paciente paciente;
    private PacienteDTO pacienteDTO;

    @BeforeEach
    void setUp() {
        paciente = new Paciente();
        paciente.setId(1L);
        paciente.setNombreCompleto("John Doe");
        paciente.setFechaNacimiento(LocalDate.of(1990, 5, 15));
        paciente.setCitas(Collections.emptyList()); // Por defecto, sin citas

        pacienteDTO = new PacienteDTO();
        pacienteDTO.setId(1L);
        pacienteDTO.setNombreCompleto("John Doe");
    }

    @Nested
    @DisplayName("Pruebas para Obtener Pacientes")
    class ObtenerPacientesTests {

        @Test
        @DisplayName("Debería devolver una lista de todos los pacientes")
        void obtenerTodosLosPacientes_Success() {
            // Arrange
            when(pacienteRepository.findAll()).thenReturn(List.of(paciente));
            when(pacienteMapper.toDto(any(Paciente.class))).thenReturn(pacienteDTO);

            // Act
            List<PacienteDTO> resultados = pacienteService.obtenerTodosLosPacientes();

            // Assert
            assertNotNull(resultados);
            assertFalse(resultados.isEmpty());
            assertEquals(1, resultados.size());
        }

        @Test
        @DisplayName("Debería devolver una lista vacía si no hay pacientes")
        void obtenerTodosLosPacientes_ShouldReturnEmptyList() {
            // Arrange
            when(pacienteRepository.findAll()).thenReturn(Collections.emptyList());

            // Act
            List<PacienteDTO> resultados = pacienteService.obtenerTodosLosPacientes();

            // Assert
            assertNotNull(resultados);
            assertTrue(resultados.isEmpty());
        }

        @Test
        @DisplayName("Debería obtener un paciente por su ID")
        void obtenerPacientePorId_Success() {
            // Arrange
            when(pacienteRepository.findById(paciente.getId())).thenReturn(Optional.of(paciente));
            when(pacienteMapper.toDto(any(Paciente.class))).thenReturn(pacienteDTO);

            // Act
            PacienteDTO resultado = pacienteService.obtenerPacientePorId(paciente.getId());

            // Assert
            assertNotNull(resultado);
            assertEquals(paciente.getId(), resultado.getId());
        }

        @Test
        @DisplayName("Debería lanzar ResourceNotFoundException si el paciente no se encuentra por ID")
        void obtenerPacientePorId_ShouldThrowException_WhenNotFound() {
            // Arrange
            when(pacienteRepository.findById(anyLong())).thenReturn(Optional.empty());

            // Act & Assert
            assertThrows(ResourceNotFoundException.class, () -> pacienteService.obtenerPacientePorId(999L));
        }
    }

    @Nested
    @DisplayName("Pruebas para Eliminar Paciente")
    class EliminarPacienteTests {

        @Test
        @DisplayName("Debería eliminar un paciente exitosamente si no tiene citas asociadas")
        void eliminarPaciente_Success_WhenNoCitas() {
            // Arrange
            when(pacienteRepository.findById(paciente.getId())).thenReturn(Optional.of(paciente));
            doNothing().when(pacienteRepository).delete(paciente);

            // Act & Assert
            assertDoesNotThrow(() -> pacienteService.eliminarPaciente(paciente.getId()));
            verify(pacienteRepository).delete(paciente);
        }

        @Test
        @DisplayName("Debería lanzar IllegalStateException si el paciente a eliminar tiene citas")
        void eliminarPaciente_ShouldThrowException_WhenPatientHasCitas() {
            // Arrange
            paciente.setCitas(List.of(new Cita())); // Asignar una cita al paciente
            when(pacienteRepository.findById(paciente.getId())).thenReturn(Optional.of(paciente));

            // Act & Assert
            IllegalStateException exception = assertThrows(IllegalStateException.class,
                    () -> pacienteService.eliminarPaciente(paciente.getId()));
            assertEquals("No se puede eliminar un paciente con citas asociadas.", exception.getMessage());
            verify(pacienteRepository, never()).delete(any(Paciente.class));
        }

        @Test
        @DisplayName("Debería lanzar ResourceNotFoundException si el paciente a eliminar no existe")
        void eliminarPaciente_ShouldThrowException_WhenNotFound() {
            // Arrange
            when(pacienteRepository.findById(anyLong())).thenReturn(Optional.empty());

            // Act & Assert
            assertThrows(ResourceNotFoundException.class, () -> pacienteService.eliminarPaciente(999L));
            verify(pacienteRepository, never()).delete(any(Paciente.class));
        }
    }
}