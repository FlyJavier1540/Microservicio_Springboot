package com.citasmedicas.api.services;

import com.citasmedicas.api.dtos.EspecialidadDTO;
import com.citasmedicas.api.exceptions.ResourceNotFoundException;
import com.citasmedicas.api.mappers.EspecialidadMapper;
import com.citasmedicas.api.models.Especialidad;
import com.citasmedicas.api.repositories.EspecialidadRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Pruebas del Servicio de Especialidades (EspecialidadService)")
class EspecialidadServiceTest {

    @Mock
    private EspecialidadRepository especialidadRepository;

    @Mock
    private EspecialidadMapper especialidadMapper;

    @InjectMocks
    private EspecialidadService especialidadService;

    // --- Datos de prueba reutilizables ---
    private Especialidad especialidad;
    private EspecialidadDTO especialidadDTO;

    @BeforeEach
    void setUp() {
        especialidad = new Especialidad(1L, "Pediatría", "Atención médica a niños.");
        especialidadDTO = new EspecialidadDTO();
        especialidadDTO.setId(1L);
        especialidadDTO.setNombre("Pediatría");
        especialidadDTO.setDescripcion("Atención médica a niños.");
    }

    @Nested
    @DisplayName("Pruebas para Obtener Especialidades")
    class ObtenerEspecialidadesTests {

        @Test
        @DisplayName("Debería devolver una lista de todas las especialidades")
        void obtenerTodas_Success() {
            // Arrange
            when(especialidadRepository.findAll()).thenReturn(List.of(especialidad));
            when(especialidadMapper.toDto(any(Especialidad.class))).thenReturn(especialidadDTO);

            // Act
            List<EspecialidadDTO> resultados = especialidadService.obtenerTodas();

            // Assert
            assertNotNull(resultados);
            assertFalse(resultados.isEmpty());
            assertEquals(1, resultados.size());
            assertEquals(especialidad.getNombre(), resultados.get(0).getNombre());
        }

        @Test
        @DisplayName("Debería devolver una lista vacía si no hay especialidades")
        void obtenerTodas_ShouldReturnEmptyList() {
            // Arrange
            when(especialidadRepository.findAll()).thenReturn(Collections.emptyList());

            // Act
            List<EspecialidadDTO> resultados = especialidadService.obtenerTodas();

            // Assert
            assertNotNull(resultados);
            assertTrue(resultados.isEmpty());
        }

        @Test
        @DisplayName("Debería obtener una especialidad por su ID")
        void obtenerPorId_Success() {
            // Arrange
            when(especialidadRepository.findById(especialidad.getId())).thenReturn(Optional.of(especialidad));
            when(especialidadMapper.toDto(any(Especialidad.class))).thenReturn(especialidadDTO);

            // Act
            EspecialidadDTO resultado = especialidadService.obtenerPorId(especialidad.getId());

            // Assert
            assertNotNull(resultado);
            assertEquals(especialidad.getId(), resultado.getId());
        }

        @Test
        @DisplayName("Debería lanzar ResourceNotFoundException si la especialidad no se encuentra por ID")
        void obtenerPorId_ShouldThrowException_WhenNotFound() {
            // Arrange
            when(especialidadRepository.findById(anyLong())).thenReturn(Optional.empty());

            // Act & Assert
            assertThrows(ResourceNotFoundException.class, () -> especialidadService.obtenerPorId(999L));
        }
    }

    @Nested
    @DisplayName("Pruebas para Crear Especialidad")
    class CrearEspecialidadTests {

        @Test
        @DisplayName("Debería crear una nueva especialidad exitosamente")
        void crearEspecialidad_Success() {
            // Arrange
            EspecialidadDTO newEspecialidadDTO = new EspecialidadDTO();
            newEspecialidadDTO.setNombre("Dermatología");

            when(especialidadMapper.toEntity(any(EspecialidadDTO.class))).thenReturn(new Especialidad());
            when(especialidadRepository.save(any(Especialidad.class))).thenReturn(especialidad);
            when(especialidadMapper.toDto(any(Especialidad.class))).thenReturn(especialidadDTO);

            // Act
            EspecialidadDTO resultado = especialidadService.crearEspecialidad(newEspecialidadDTO);

            // Assert
            assertNotNull(resultado);
            assertEquals(especialidad.getId(), resultado.getId());
            verify(especialidadRepository).save(any(Especialidad.class));
        }
    }

    @Nested
    @DisplayName("Pruebas para Eliminar Especialidad")
    class EliminarEspecialidadTests {

        @Test
        @DisplayName("Debería eliminar una especialidad exitosamente")
        void eliminarEspecialidad_Success() {
            // Arrange
            when(especialidadRepository.existsById(especialidad.getId())).thenReturn(true);
            doNothing().when(especialidadRepository).deleteById(especialidad.getId());

            // Act & Assert
            assertDoesNotThrow(() -> especialidadService.eliminarEspecialidad(especialidad.getId()));
            verify(especialidadRepository).deleteById(especialidad.getId());
        }

        @Test
        @DisplayName("Debería lanzar ResourceNotFoundException si la especialidad a eliminar no existe")
        void eliminarEspecialidad_ShouldThrowException_WhenNotFound() {
            // Arrange
            when(especialidadRepository.existsById(anyLong())).thenReturn(false);

            // Act & Assert
            assertThrows(ResourceNotFoundException.class, () -> especialidadService.eliminarEspecialidad(999L));
            verify(especialidadRepository, never()).deleteById(anyLong());
        }
    }
}