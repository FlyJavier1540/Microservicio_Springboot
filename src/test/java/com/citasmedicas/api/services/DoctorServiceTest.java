package com.citasmedicas.api.services;

import com.citasmedicas.api.dtos.DoctorDTO;
import com.citasmedicas.api.dtos.DoctorResponseDTO;
import com.citasmedicas.api.enums.RolNombre;
import com.citasmedicas.api.exceptions.ResourceNotFoundException;
import com.citasmedicas.api.mappers.DoctorMapper;
import com.citasmedicas.api.models.*;
import com.citasmedicas.api.repositories.DoctorRepository;
import com.citasmedicas.api.repositories.EspecialidadRepository;
import com.citasmedicas.api.repositories.RolRepository;
import com.citasmedicas.api.repositories.UsuarioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Pruebas del Servicio de Doctores (DoctorService)")
class DoctorServiceTest {

    @Mock
    private DoctorRepository doctorRepository;
    @Mock
    private UsuarioRepository usuarioRepository;
    @Mock
    private RolRepository rolRepository;
    @Mock
    private EspecialidadRepository especialidadRepository;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private DoctorMapper doctorMapper;

    @InjectMocks
    private DoctorService doctorService;

    // --- Datos de prueba reutilizables ---
    private DoctorDTO doctorDTO;
    private Doctor doctor;
    private Especialidad especialidad;
    private Rol rolDoctor;
    private DoctorResponseDTO doctorResponseDTO;


    @BeforeEach
    void setUp() {
        especialidad = new Especialidad(1L, "Cardiología", "Estudio del corazón");
        rolDoctor = new Rol(2L, RolNombre.DOCTOR);

        doctorDTO = new DoctorDTO();
        doctorDTO.setNombreCompleto("Dr. Gregory House");
        doctorDTO.setEspecialidadId(especialidad.getId());
        doctorDTO.setCedulaProfesional("123456");

        doctor = new Doctor();
        doctor.setId(1L);
        doctor.setNombreCompleto(doctorDTO.getNombreCompleto());
        doctor.setEspecialidad(especialidad);
        doctor.setCitas(Collections.emptyList()); // Inicialmente sin citas

        doctorResponseDTO = new DoctorResponseDTO();
        doctorResponseDTO.setId(doctor.getId());
        doctorResponseDTO.setNombreCompleto(doctor.getNombreCompleto());
    }

    @Nested
    @DisplayName("Pruebas para Registrar Doctor")
    class RegistrarDoctorTests {

        @Test
        @DisplayName("Debería registrar un nuevo doctor exitosamente")
        void registrarDoctor_Success() {
            // Arrange
            when(especialidadRepository.findById(especialidad.getId())).thenReturn(Optional.of(especialidad));
            when(rolRepository.findByNombre(RolNombre.DOCTOR)).thenReturn(Optional.of(rolDoctor));
            when(usuarioRepository.existsByEmail(anyString())).thenReturn(false);
            when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");
            when(doctorMapper.toEntity(any(DoctorDTO.class))).thenReturn(new Doctor());
            when(doctorRepository.save(any(Doctor.class))).thenReturn(doctor);
            when(doctorMapper.toResponseDto(any(Doctor.class))).thenReturn(doctorResponseDTO);

            // Act
            DoctorResponseDTO resultado = doctorService.registrarDoctor(doctorDTO);

            // Assert
            assertNotNull(resultado);
            assertNotNull(resultado.getPassword()); // La contraseña generada debe estar en la respuesta
            assertEquals(doctor.getId(), resultado.getId());
            verify(doctorRepository).save(any(Doctor.class));
        }

        @Test
        @DisplayName("Debería lanzar ResourceNotFoundException si la especialidad no existe")
        void registrarDoctor_ShouldThrowException_WhenEspecialidadNotFound() {
            // Arrange
            when(especialidadRepository.findById(anyLong())).thenReturn(Optional.empty());

            // Act & Assert
            ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class,
                    () -> doctorService.registrarDoctor(doctorDTO));
            assertEquals("Especialidad no encontrada con id: " + doctorDTO.getEspecialidadId(), exception.getMessage());
        }

        @Test
        @DisplayName("Debería lanzar RuntimeException si el rol de DOCTOR no existe")
        void registrarDoctor_ShouldThrowException_WhenRoleNotFound() {
            // Arrange
            when(especialidadRepository.findById(especialidad.getId())).thenReturn(Optional.of(especialidad));
            when(rolRepository.findByNombre(RolNombre.DOCTOR)).thenReturn(Optional.empty());

            // Act & Assert
            RuntimeException exception = assertThrows(RuntimeException.class,
                    () -> doctorService.registrarDoctor(doctorDTO));
            assertEquals("Error: Rol de doctor no encontrado.", exception.getMessage());
        }
    }

    @Nested
    @DisplayName("Pruebas para Obtener Doctores")
    class ObtenerDoctoresTests {

        @Test
        @DisplayName("Debería devolver una lista de todos los doctores")
        void obtenerTodosLosDoctores_Success() {
            // Arrange
            when(doctorRepository.findAll()).thenReturn(List.of(doctor));
            when(doctorMapper.toResponseDto(any(Doctor.class))).thenReturn(doctorResponseDTO);

            // Act
            List<DoctorResponseDTO> resultados = doctorService.obtenerTodosLosDoctores();

            // Assert
            assertNotNull(resultados);
            assertFalse(resultados.isEmpty());
            assertEquals(1, resultados.size());
        }

        @Test
        @DisplayName("Debería devolver una lista vacía si no hay doctores")
        void obtenerTodosLosDoctores_ShouldReturnEmptyList() {
            // Arrange
            when(doctorRepository.findAll()).thenReturn(Collections.emptyList());

            // Act
            List<DoctorResponseDTO> resultados = doctorService.obtenerTodosLosDoctores();

            // Assert
            assertNotNull(resultados);
            assertTrue(resultados.isEmpty());
        }

        @Test
        @DisplayName("Debería obtener un doctor por su ID")
        void obtenerDoctorPorId_Success() {
            // Arrange
            when(doctorRepository.findById(doctor.getId())).thenReturn(Optional.of(doctor));
            when(doctorMapper.toResponseDto(any(Doctor.class))).thenReturn(doctorResponseDTO);

            // Act
            DoctorResponseDTO resultado = doctorService.obtenerDoctorPorId(doctor.getId());

            // Assert
            assertNotNull(resultado);
            assertEquals(doctor.getId(), resultado.getId());
        }

        @Test
        @DisplayName("Debería lanzar ResourceNotFoundException si el doctor no se encuentra por ID")
        void obtenerDoctorPorId_ShouldThrowException_WhenNotFound() {
            // Arrange
            when(doctorRepository.findById(anyLong())).thenReturn(Optional.empty());

            // Act & Assert
            assertThrows(ResourceNotFoundException.class, () -> doctorService.obtenerDoctorPorId(999L));
        }
    }

    @Nested
    @DisplayName("Pruebas para Eliminar Doctor")
    class EliminarDoctorTests {

        @Test
        @DisplayName("Debería eliminar un doctor exitosamente si no tiene citas")
        void eliminarDoctor_Success() {
            // Arrange
            when(doctorRepository.findById(doctor.getId())).thenReturn(Optional.of(doctor));
            doNothing().when(doctorRepository).delete(doctor);

            // Act & Assert
            assertDoesNotThrow(() -> doctorService.eliminarDoctor(doctor.getId()));
            verify(doctorRepository).delete(doctor);
        }

        @Test
        @DisplayName("Debería lanzar IllegalStateException si el doctor a eliminar tiene citas asociadas")
        void eliminarDoctor_ShouldThrowException_WhenDoctorHasCitas() {
            // Arrange
            doctor.setCitas(List.of(new Cita())); // Asociar una cita al doctor
            when(doctorRepository.findById(doctor.getId())).thenReturn(Optional.of(doctor));

            // Act & Assert
            IllegalStateException exception = assertThrows(IllegalStateException.class,
                    () -> doctorService.eliminarDoctor(doctor.getId()));
            assertEquals("No se puede eliminar un doctor con citas asociadas. Por favor, reasigne o cancele las citas primero.", exception.getMessage());
        }

        @Test
        @DisplayName("Debería lanzar ResourceNotFoundException si el doctor a eliminar no existe")
        void eliminarDoctor_ShouldThrowException_WhenNotFound() {
            // Arrange
            when(doctorRepository.findById(anyLong())).thenReturn(Optional.empty());

            // Act & Assert
            assertThrows(ResourceNotFoundException.class, () -> doctorService.eliminarDoctor(999L));
        }
    }
}