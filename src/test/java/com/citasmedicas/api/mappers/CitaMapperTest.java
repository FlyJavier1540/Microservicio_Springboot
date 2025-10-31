package com.citasmedicas.api.mappers;

import com.citasmedicas.api.dtos.CitaDTO;
import com.citasmedicas.api.enums.EstadoCita;
import com.citasmedicas.api.models.Cita;
import com.citasmedicas.api.models.Doctor;
import com.citasmedicas.api.models.Paciente;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Pruebas del Mapper de Citas (CitaMapper)")
class CitaMapperTest {

    private CitaMapper citaMapper;

    @BeforeEach
    void setUp() {
        // Obtenemos la implementación generada por MapStruct
        citaMapper = CitaMapper.INSTANCE;
    }

    @Nested
    @DisplayName("Pruebas de Mapeo de Entidad a DTO (toDto)")
    class EntityToDtoMappingTests {

        @Test
        @DisplayName("Debería mapear una entidad Cita a un CitaDTO correctamente")
        void shouldMapCitaToCitaDTO() {
            // Arrange
            Doctor doctor = new Doctor();
            doctor.setId(1L);
            doctor.setNombreCompleto("Dr. Strange");

            Paciente paciente = new Paciente();
            paciente.setId(1L);
            paciente.setNombreCompleto("Stephen Strange");

            Cita cita = new Cita();
            cita.setId(1L);
            cita.setFechaHora(LocalDateTime.now());
            cita.setEstado(EstadoCita.PROGRAMADA);
            cita.setMotivoConsulta("Revisión anual");
            cita.setDoctor(doctor);
            cita.setPaciente(paciente);

            // Act
            CitaDTO citaDTO = citaMapper.toDto(cita);

            // Assert
            assertNotNull(citaDTO);
            assertEquals(cita.getId(), citaDTO.getId());
            assertEquals(cita.getFechaHora(), citaDTO.getFechaHora());
            assertEquals(cita.getEstado(), citaDTO.getEstado());
            assertEquals(cita.getMotivoConsulta(), citaDTO.getMotivoConsulta());
            assertEquals(cita.getDoctor().getId(), citaDTO.getDoctorId());
            assertEquals(cita.getDoctor().getNombreCompleto(), citaDTO.getDoctorNombre());
            assertEquals(cita.getPaciente().getId(), citaDTO.getPacienteId());
            assertEquals(cita.getPaciente().getNombreCompleto(), citaDTO.getPacienteNombre());
        }

        @Test
        @DisplayName("Debería devolver null si la entidad Cita es nula")
        void shouldReturnNullWhenCitaIsNull() {
            // Act
            CitaDTO citaDTO = citaMapper.toDto(null);

            // Assert
            assertNull(citaDTO);
        }
    }

    @Nested
    @DisplayName("Pruebas de Mapeo de DTO a Entidad (toEntity)")
    class DtoToEntityMappingTests {

        @Test
        @DisplayName("Debería mapear un CitaDTO a una entidad Cita correctamente")
        void shouldMapCitaDTOToCita() {
            // Arrange
            CitaDTO citaDTO = new CitaDTO();
            citaDTO.setId(1L);
            citaDTO.setFechaHora(LocalDateTime.now());
            citaDTO.setEstado(EstadoCita.PROGRAMADA);
            citaDTO.setMotivoConsulta("Revisión anual");
            citaDTO.setDoctorId(1L);
            citaDTO.setPacienteId(1L);
            // Los campos de nombre no se mapean de DTO a entidad, así que los ignoramos

            // Act
            Cita cita = citaMapper.toEntity(citaDTO);

            // Assert
            assertNotNull(cita);
            assertEquals(citaDTO.getId(), cita.getId());
            assertEquals(citaDTO.getFechaHora(), cita.getFechaHora());
            assertEquals(citaDTO.getEstado(), cita.getEstado());
            assertEquals(citaDTO.getMotivoConsulta(), cita.getMotivoConsulta());
            assertNotNull(cita.getDoctor());
            assertEquals(citaDTO.getDoctorId(), cita.getDoctor().getId());
            assertNotNull(cita.getPaciente());
            assertEquals(citaDTO.getPacienteId(), cita.getPaciente().getId());
        }

        @Test
        @DisplayName("Debería devolver null si el CitaDTO es nulo")
        void shouldReturnNullWhenCitaDTOIsNull() {
            // Act
            Cita cita = citaMapper.toEntity(null);

            // Assert
            assertNull(cita);
        }
    }
}