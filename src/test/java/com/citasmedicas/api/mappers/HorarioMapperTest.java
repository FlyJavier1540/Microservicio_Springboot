package com.citasmedicas.api.mappers;

import com.citasmedicas.api.dtos.HorarioDTO;
import com.citasmedicas.api.enums.DiaDeLaSemana;
import com.citasmedicas.api.models.Doctor;
import com.citasmedicas.api.models.Horario;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.time.LocalTime;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Pruebas del Mapper de Horarios (HorarioMapper)")
class HorarioMapperTest {

    private HorarioMapper horarioMapper;

    @BeforeEach
    void setUp() {
        horarioMapper = HorarioMapper.INSTANCE;
    }

    @Nested
    @DisplayName("Pruebas de Mapeo de Entidad a DTO (toDto)")
    class EntityToDtoMappingTests {

        @Test
        @DisplayName("Debería mapear una entidad Horario a un HorarioDTO correctamente")
        void shouldMapHorarioToHorarioDTO() {
            // Arrange
            Doctor doctor = new Doctor();
            doctor.setId(1L);

            Horario horario = new Horario();
            horario.setId(1L);
            horario.setDiaDeLaSemana(DiaDeLaSemana.MARTES);
            horario.setHoraInicio(LocalTime.of(15, 0));
            horario.setHoraFin(LocalTime.of(19, 30));
            horario.setDoctor(doctor);

            // Act
            HorarioDTO horarioDTO = horarioMapper.toDto(horario);

            // Assert
            assertNotNull(horarioDTO);
            assertEquals(horario.getId(), horarioDTO.getId());
            assertEquals(horario.getDiaDeLaSemana(), horarioDTO.getDiaDeLaSemana());
            assertEquals(horario.getHoraInicio(), horarioDTO.getHoraInicio());
            assertEquals(horario.getHoraFin(), horarioDTO.getHoraFin());
            assertEquals(horario.getDoctor().getId(), horarioDTO.getDoctorId());
        }

        @Test
        @DisplayName("Debería devolver null si la entidad Horario es nula")
        void shouldReturnNullWhenHorarioIsNull() {
            // Act
            HorarioDTO horarioDTO = horarioMapper.toDto(null);

            // Assert
            assertNull(horarioDTO);
        }
    }

    @Nested
    @DisplayName("Pruebas de Mapeo de DTO a Entidad (toEntity)")
    class DtoToEntityMappingTests {

        @Test
        @DisplayName("Debería mapear un HorarioDTO a una entidad Horario correctamente")
        void shouldMapHorarioDTOToHorario() {
            // Arrange
            HorarioDTO horarioDTO = new HorarioDTO();
            horarioDTO.setId(1L);
            horarioDTO.setDiaDeLaSemana(DiaDeLaSemana.MARTES);
            horarioDTO.setHoraInicio(LocalTime.of(15, 0));
            horarioDTO.setHoraFin(LocalTime.of(19, 30));
            horarioDTO.setDoctorId(1L);

            // Act
            Horario horario = horarioMapper.toEntity(horarioDTO);

            // Assert
            assertNotNull(horario);
            assertEquals(horarioDTO.getId(), horario.getId());
            assertEquals(horarioDTO.getDiaDeLaSemana(), horario.getDiaDeLaSemana());
            assertEquals(horarioDTO.getHoraInicio(), horario.getHoraInicio());
            assertEquals(horarioDTO.getHoraFin(), horario.getHoraFin());
            assertNotNull(horario.getDoctor());
            assertEquals(horarioDTO.getDoctorId(), horario.getDoctor().getId());
        }

        @Test
        @DisplayName("Debería devolver null si el HorarioDTO es nulo")
        void shouldReturnNullWhenHorarioDTOIsNull() {
            // Act
            Horario horario = horarioMapper.toEntity(null);

            // Assert
            assertNull(horario);
        }
    }
}