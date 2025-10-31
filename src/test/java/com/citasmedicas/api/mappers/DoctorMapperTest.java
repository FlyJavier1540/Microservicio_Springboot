package com.citasmedicas.api.mappers;

import com.citasmedicas.api.dtos.DoctorDTO;
import com.citasmedicas.api.dtos.DoctorResponseDTO;
import com.citasmedicas.api.models.Doctor;
import com.citasmedicas.api.models.Especialidad;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Pruebas del Mapper de Doctores (DoctorMapper)")
class DoctorMapperTest {

    private DoctorMapper doctorMapper;

    @BeforeEach
    void setUp() {
        doctorMapper = DoctorMapper.INSTANCE;
    }

    @Nested
    @DisplayName("Pruebas de Mapeo de Entidad a DTO de Respuesta (toResponseDto)")
    class EntityToResponseDtoMappingTests {

        @Test
        @DisplayName("Debería mapear una entidad Doctor a un DoctorResponseDTO correctamente")
        void shouldMapDoctorToDoctorResponseDTO() {
            // Arrange
            Especialidad especialidad = new Especialidad();
            especialidad.setId(1L);
            especialidad.setNombre("Cardiología");

            Doctor doctor = new Doctor();
            doctor.setId(1L);
            doctor.setNombreCompleto("Dr. Gregory House");
            doctor.setTelefono("555-1234");
            doctor.setCedulaProfesional("123456");
            doctor.setEspecialidad(especialidad);

            // Act
            DoctorResponseDTO responseDTO = doctorMapper.toResponseDto(doctor);

            // Assert
            assertNotNull(responseDTO);
            assertEquals(doctor.getId(), responseDTO.getId());
            assertEquals(doctor.getNombreCompleto(), responseDTO.getNombreCompleto());
            assertEquals(doctor.getTelefono(), responseDTO.getTelefono());
            assertEquals(doctor.getCedulaProfesional(), responseDTO.getCedulaProfesional());
            assertEquals(doctor.getEspecialidad().getNombre(), responseDTO.getEspecialidadNombre());
            assertNull(responseDTO.getPassword(), "El password debería ser nulo en el DTO de respuesta");
        }

        @Test
        @DisplayName("Debería manejar correctamente una especialidad nula al mapear a DTO")
        void shouldHandleNullEspecialidadWhenMappingToDto() {
            // Arrange
            Doctor doctor = new Doctor();
            doctor.setId(1L);
            doctor.setEspecialidad(null);

            // Act
            DoctorResponseDTO responseDTO = doctorMapper.toResponseDto(doctor);

            // Assert
            assertNotNull(responseDTO);
            assertNull(responseDTO.getEspecialidadNombre());
        }

        @Test
        @DisplayName("Debería devolver null si la entidad Doctor es nula")
        void shouldReturnNullWhenDoctorIsNull() {
            // Act
            DoctorResponseDTO responseDTO = doctorMapper.toResponseDto(null);

            // Assert
            assertNull(responseDTO);
        }
    }

    @Nested
    @DisplayName("Pruebas de Mapeo de DTO a Entidad (toEntity)")
    class DtoToEntityMappingTests {

        @Test
        @DisplayName("Debería mapear un DoctorDTO a una entidad Doctor correctamente")
        void shouldMapDoctorDTOToDoctor() {
            // Arrange
            DoctorDTO doctorDTO = new DoctorDTO();
            doctorDTO.setId(1L);
            doctorDTO.setNombreCompleto("Dr. Lisa Cuddy");
            doctorDTO.setTelefono("555-5678");
            doctorDTO.setCedulaProfesional("654321");
            doctorDTO.setEspecialidadId(2L);

            // Act
            Doctor doctor = doctorMapper.toEntity(doctorDTO);

            // Assert
            assertNotNull(doctor);
            assertEquals(doctorDTO.getId(), doctor.getId());
            assertEquals(doctorDTO.getNombreCompleto(), doctor.getNombreCompleto());
            assertEquals(doctorDTO.getTelefono(), doctor.getTelefono());
            assertEquals(doctorDTO.getCedulaProfesional(), doctor.getCedulaProfesional());
            assertNotNull(doctor.getEspecialidad());
            assertEquals(doctorDTO.getEspecialidadId(), doctor.getEspecialidad().getId());
        }

        @Test
        @DisplayName("Debería devolver null si el DoctorDTO es nulo")
        void shouldReturnNullWhenDoctorDTOIsNull() {
            // Act
            Doctor doctor = doctorMapper.toEntity(null);

            // Assert
            assertNull(doctor);
        }
    }
}