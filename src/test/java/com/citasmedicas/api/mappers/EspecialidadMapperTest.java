package com.citasmedicas.api.mappers;

import com.citasmedicas.api.dtos.EspecialidadDTO;
import com.citasmedicas.api.models.Especialidad;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Pruebas del Mapper de Especialidades (EspecialidadMapper)")
class EspecialidadMapperTest {

    private EspecialidadMapper especialidadMapper;

    @BeforeEach
    void setUp() {
        especialidadMapper = EspecialidadMapper.INSTANCE;
    }

    @Nested
    @DisplayName("Pruebas de Mapeo de Entidad a DTO (toDto)")
    class EntityToDtoMappingTests {

        @Test
        @DisplayName("Debería mapear una entidad Especialidad a un EspecialidadDTO correctamente")
        void shouldMapEspecialidadToEspecialidadDTO() {
            // Arrange
            Especialidad especialidad = new Especialidad();
            especialidad.setId(1L);
            especialidad.setNombre("Neurología");
            especialidad.setDescripcion("Estudio del sistema nervioso");

            // Act
            EspecialidadDTO especialidadDTO = especialidadMapper.toDto(especialidad);

            // Assert
            assertNotNull(especialidadDTO);
            assertEquals(especialidad.getId(), especialidadDTO.getId());
            assertEquals(especialidad.getNombre(), especialidadDTO.getNombre());
            assertEquals(especialidad.getDescripcion(), especialidadDTO.getDescripcion());
        }

        @Test
        @DisplayName("Debería devolver null si la entidad Especialidad es nula")
        void shouldReturnNullWhenEspecialidadIsNull() {
            // Act
            EspecialidadDTO especialidadDTO = especialidadMapper.toDto(null);

            // Assert
            assertNull(especialidadDTO);
        }
    }

    @Nested
    @DisplayName("Pruebas de Mapeo de DTO a Entidad (toEntity)")
    class DtoToEntityMappingTests {

        @Test
        @DisplayName("Debería mapear un EspecialidadDTO a una entidad Especialidad correctamente")
        void shouldMapEspecialidadDTOToEspecialidad() {
            // Arrange
            EspecialidadDTO especialidadDTO = new EspecialidadDTO();
            especialidadDTO.setId(1L);
            especialidadDTO.setNombre("Neurología");
            especialidadDTO.setDescripcion("Estudio del sistema nervioso");

            // Act
            Especialidad especialidad = especialidadMapper.toEntity(especialidadDTO);

            // Assert
            assertNotNull(especialidad);
            assertEquals(especialidadDTO.getId(), especialidad.getId());
            assertEquals(especialidadDTO.getNombre(), especialidad.getNombre());
            assertEquals(especialidadDTO.getDescripcion(), especialidad.getDescripcion());
        }

        @Test
        @DisplayName("Debería devolver null si el EspecialidadDTO es nulo")
        void shouldReturnNullWhenEspecialidadDTOIsNull() {
            // Act
            Especialidad especialidad = especialidadMapper.toEntity(null);

            // Assert
            assertNull(especialidad);
        }
    }
}