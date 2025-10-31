package com.citasmedicas.api.mappers;

import com.citasmedicas.api.dtos.PacienteDTO;
import com.citasmedicas.api.models.Paciente;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Pruebas del Mapper de Pacientes (PacienteMapper)")
class PacienteMapperTest {

    private PacienteMapper pacienteMapper;

    @BeforeEach
    void setUp() {
        pacienteMapper = PacienteMapper.INSTANCE;
    }

    @Nested
    @DisplayName("Pruebas de Mapeo de Entidad a DTO (toDto)")
    class EntityToDtoMappingTests {

        @Test
        @DisplayName("Debería mapear una entidad Paciente a un PacienteDTO correctamente")
        void shouldMapPacienteToPacienteDTO() {
            // Arrange
            Paciente paciente = new Paciente();
            paciente.setId(1L);
            paciente.setNombreCompleto("Jane Doe");
            paciente.setFechaNacimiento(LocalDate.of(1985, 10, 20));
            paciente.setTelefono("555-8765");
            paciente.setDireccion("123 Main St");

            // Act
            PacienteDTO pacienteDTO = pacienteMapper.toDto(paciente);

            // Assert
            assertNotNull(pacienteDTO);
            assertEquals(paciente.getId(), pacienteDTO.getId());
            assertEquals(paciente.getNombreCompleto(), pacienteDTO.getNombreCompleto());
            assertEquals(paciente.getFechaNacimiento(), pacienteDTO.getFechaNacimiento());
            assertEquals(paciente.getTelefono(), pacienteDTO.getTelefono());
            assertEquals(paciente.getDireccion(), pacienteDTO.getDireccion());
        }

        @Test
        @DisplayName("Debería devolver null si la entidad Paciente es nula")
        void shouldReturnNullWhenPacienteIsNull() {
            // Act
            PacienteDTO pacienteDTO = pacienteMapper.toDto(null);

            // Assert
            assertNull(pacienteDTO);
        }
    }

    @Nested
    @DisplayName("Pruebas de Mapeo de DTO a Entidad (toEntity)")
    class DtoToEntityMappingTests {

        @Test
        @DisplayName("Debería mapear un PacienteDTO a una entidad Paciente correctamente")
        void shouldMapPacienteDTOToPaciente() {
            // Arrange
            PacienteDTO pacienteDTO = new PacienteDTO();
            pacienteDTO.setId(1L);
            pacienteDTO.setNombreCompleto("Jane Doe");
            pacienteDTO.setFechaNacimiento(LocalDate.of(1985, 10, 20));
            pacienteDTO.setTelefono("555-8765");
            pacienteDTO.setDireccion("123 Main St");

            // Act
            Paciente paciente = pacienteMapper.toEntity(pacienteDTO);

            // Assert
            assertNotNull(paciente);
            assertEquals(pacienteDTO.getId(), paciente.getId());
            assertEquals(pacienteDTO.getNombreCompleto(), paciente.getNombreCompleto());
            assertEquals(pacienteDTO.getFechaNacimiento(), paciente.getFechaNacimiento());
            assertEquals(pacienteDTO.getTelefono(), paciente.getTelefono());
            assertEquals(pacienteDTO.getDireccion(), paciente.getDireccion());
        }

        @Test
        @DisplayName("Debería devolver null si el PacienteDTO es nulo")
        void shouldReturnNullWhenPacienteDTOIsNull() {
            // Act
            Paciente paciente = pacienteMapper.toEntity(null);

            // Assert
            assertNull(paciente);
        }
    }
}