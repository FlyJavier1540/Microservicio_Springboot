package com.citasmedicas.api.mappers;

import com.citasmedicas.api.dtos.HistorialMedicoDTO;
import com.citasmedicas.api.models.Cita;
import com.citasmedicas.api.models.Doctor;
import com.citasmedicas.api.models.HistorialMedico;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Pruebas del Mapper de Historial Médico (HistorialMedicoMapper)")
class HistorialMedicoMapperTest {

    private HistorialMedicoMapper historialMedicoMapper;

    @BeforeEach
    void setUp() {
        historialMedicoMapper = HistorialMedicoMapper.INSTANCE;
    }

    @Nested
    @DisplayName("Pruebas de Mapeo de Entidad a DTO (toDto)")
    class EntityToDtoMappingTests {

        @Test
        @DisplayName("Debería mapear una entidad HistorialMedico a un HistorialMedicoDTO correctamente")
        void shouldMapHistorialMedicoToHistorialMedicoDTO() {
            // Arrange
            Doctor doctor = new Doctor();
            doctor.setId(1L);
            doctor.setNombreCompleto("Dr. James Wilson");

            Cita cita = new Cita();
            cita.setId(1L);

            HistorialMedico historial = new HistorialMedico();
            historial.setId(1L);
            historial.setDiagnostico("Gripe común");
            historial.setTratamiento("Reposo y fluidos");
            historial.setNotas("El paciente presenta síntomas leves");
            historial.setCita(cita);
            historial.setDoctor(doctor);

            // Act
            HistorialMedicoDTO historialDTO = historialMedicoMapper.toDto(historial);

            // Assert
            assertNotNull(historialDTO);
            assertEquals(historial.getId(), historialDTO.getId());
            assertEquals(historial.getDiagnostico(), historialDTO.getDiagnostico());
            assertEquals(historial.getTratamiento(), historialDTO.getTratamiento());
            assertEquals(historial.getNotas(), historialDTO.getNotas());
            assertEquals(historial.getCita().getId(), historialDTO.getCitaId());
            assertEquals(historial.getDoctor().getNombreCompleto(), historialDTO.getDoctorNombre());
        }

        @Test
        @DisplayName("Debería devolver null si la entidad HistorialMedico es nula")
        void shouldReturnNullWhenHistorialMedicoIsNull() {
            // Act
            HistorialMedicoDTO historialDTO = historialMedicoMapper.toDto(null);

            // Assert
            assertNull(historialDTO);
        }
    }

    @Nested
    @DisplayName("Pruebas de Mapeo de DTO a Entidad (toEntity)")
    class DtoToEntityMappingTests {

        @Test
        @DisplayName("Debería mapear un HistorialMedicoDTO a una entidad HistorialMedico correctamente")
        void shouldMapHistorialMedicoDTOToHistorialMedico() {
            // Arrange
            HistorialMedicoDTO historialDTO = new HistorialMedicoDTO();
            historialDTO.setId(1L);
            historialDTO.setDiagnostico("Gripe común");
            historialDTO.setTratamiento("Reposo y fluidos");
            historialDTO.setNotas("El paciente presenta síntomas leves");
            historialDTO.setCitaId(1L);
            // El campo doctorNombre no se mapea de DTO a entidad

            // Act
            HistorialMedico historial = historialMedicoMapper.toEntity(historialDTO);

            // Assert
            assertNotNull(historial);
            assertEquals(historialDTO.getId(), historial.getId());
            assertEquals(historialDTO.getDiagnostico(), historial.getDiagnostico());
            assertEquals(historialDTO.getTratamiento(), historial.getTratamiento());
            assertEquals(historialDTO.getNotas(), historial.getNotas());
            assertNotNull(historial.getCita());
            assertEquals(historialDTO.getCitaId(), historial.getCita().getId());
            assertNull(historial.getDoctor(), "El doctor no debería mapearse desde el DTO");
        }

        @Test
        @DisplayName("Debería devolver null si el HistorialMedicoDTO es nulo")
        void shouldReturnNullWhenHistorialMedicoDTOIsNull() {
            // Act
            HistorialMedico historial = historialMedicoMapper.toEntity(null);

            // Assert
            assertNull(historial);
        }
    }
}