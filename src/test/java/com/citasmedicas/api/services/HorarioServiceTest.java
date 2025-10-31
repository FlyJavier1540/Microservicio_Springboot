package com.citasmedicas.api.services;

import com.citasmedicas.api.dtos.HorarioDTO;
import com.citasmedicas.api.enums.DiaDeLaSemana;
import com.citasmedicas.api.enums.RolNombre;
import com.citasmedicas.api.exceptions.ResourceNotFoundException;
import com.citasmedicas.api.mappers.HorarioMapper;
import com.citasmedicas.api.models.Doctor;
import com.citasmedicas.api.models.Horario;
import com.citasmedicas.api.models.Rol;
import com.citasmedicas.api.models.Usuario;
import com.citasmedicas.api.repositories.DoctorRepository;
import com.citasmedicas.api.repositories.HorarioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.access.AccessDeniedException;

import java.time.LocalTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Pruebas del Servicio de Horarios (HorarioService)")
class HorarioServiceTest {

    @Mock
    private HorarioRepository horarioRepository;
    @Mock
    private DoctorRepository doctorRepository;
    @Mock
    private HorarioMapper horarioMapper;

    @InjectMocks
    private HorarioService horarioService;

    // --- Datos de prueba reutilizables ---
    private Usuario usuarioDoctor;
    private Doctor doctor;
    private Horario horario;
    private HorarioDTO horarioDTO;

    @BeforeEach
    void setUp() {
        usuarioDoctor = new Usuario(1L, "doctor@test.com", "pass", Set.of(new Rol(2L, RolNombre.DOCTOR)));
        doctor = new Doctor();
        doctor.setId(1L);
        doctor.setUsuario(usuarioDoctor);

        horarioDTO = new HorarioDTO();
        horarioDTO.setDiaDeLaSemana(DiaDeLaSemana.LUNES);
        horarioDTO.setHoraInicio(LocalTime.of(9, 0));
        horarioDTO.setHoraFin(LocalTime.of(13, 0));
        horarioDTO.setDoctorId(doctor.getId());

        horario = new Horario();
        horario.setId(1L);
        horario.setDoctor(doctor);
        horario.setDiaDeLaSemana(horarioDTO.getDiaDeLaSemana());
        horario.setHoraInicio(horarioDTO.getHoraInicio());
        horario.setHoraFin(horarioDTO.getHoraFin());
    }

    @Nested
    @DisplayName("Pruebas para Agregar Horario")
    class AgregarHorarioTests {

        @Test
        @DisplayName("Debería agregar un nuevo bloque de horario exitosamente")
        void agregarHorario_Success() {
            // Arrange
            when(doctorRepository.findByUsuarioId(usuarioDoctor.getId())).thenReturn(Optional.of(doctor));
            when(horarioMapper.toEntity(any(HorarioDTO.class))).thenReturn(new Horario());
            when(horarioRepository.save(any(Horario.class))).thenReturn(horario);
            when(horarioMapper.toDto(any(Horario.class))).thenReturn(horarioDTO);

            // Act
            HorarioDTO resultado = horarioService.agregarHorario(horarioDTO, usuarioDoctor);

            // Assert
            assertNotNull(resultado);
            assertEquals(horarioDTO.getDiaDeLaSemana(), resultado.getDiaDeLaSemana());
            verify(horarioRepository).save(any(Horario.class));
        }

        @Test
        @DisplayName("Debería lanzar IllegalArgumentException si la hora de inicio es posterior a la hora de fin")
        void agregarHorario_ShouldThrowException_WhenStartTimeIsAfterEndTime() {
            // Arrange
            horarioDTO.setHoraInicio(LocalTime.of(14, 0));
            horarioDTO.setHoraFin(LocalTime.of(10, 0));
            when(doctorRepository.findByUsuarioId(usuarioDoctor.getId())).thenReturn(Optional.of(doctor));

            // Act & Assert
            IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                    () -> horarioService.agregarHorario(horarioDTO, usuarioDoctor));
            assertEquals("La hora de inicio no puede ser posterior a la hora de fin.", exception.getMessage());
        }

        @Test
        @DisplayName("Debería lanzar ResourceNotFoundException si el perfil del doctor no existe")
        void agregarHorario_ShouldThrowException_WhenDoctorProfileNotFound() {
            // Arrange
            when(doctorRepository.findByUsuarioId(anyLong())).thenReturn(Optional.empty());

            // Act & Assert
            assertThrows(ResourceNotFoundException.class, () -> horarioService.agregarHorario(horarioDTO, usuarioDoctor));
        }
    }

    @Nested
    @DisplayName("Pruebas para Obtener Horarios")
    class ObtenerHorariosTests {

        @Test
        @DisplayName("Debería obtener los horarios del doctor autenticado")
        void obtenerHorariosPorDoctor_Success() {
            // Arrange
            when(doctorRepository.findByUsuarioId(usuarioDoctor.getId())).thenReturn(Optional.of(doctor));
            when(horarioRepository.findAllByDoctorId(doctor.getId())).thenReturn(List.of(horario));
            when(horarioMapper.toDto(any(Horario.class))).thenReturn(horarioDTO);

            // Act
            List<HorarioDTO> resultados = horarioService.obtenerHorariosPorDoctor(usuarioDoctor);

            // Assert
            assertNotNull(resultados);
            assertFalse(resultados.isEmpty());
            assertEquals(1, resultados.size());
        }

        @Test
        @DisplayName("Debería devolver una lista vacía si el doctor no tiene horarios")
        void obtenerHorariosPorDoctor_ShouldReturnEmptyList() {
            // Arrange
            when(doctorRepository.findByUsuarioId(usuarioDoctor.getId())).thenReturn(Optional.of(doctor));
            when(horarioRepository.findAllByDoctorId(doctor.getId())).thenReturn(Collections.emptyList());

            // Act
            List<HorarioDTO> resultados = horarioService.obtenerHorariosPorDoctor(usuarioDoctor);

            // Assert
            assertNotNull(resultados);
            assertTrue(resultados.isEmpty());
        }
    }

    @Nested
    @DisplayName("Pruebas para Eliminar Horario")
    class EliminarHorarioTests {

        @Test
        @DisplayName("Debería eliminar un horario exitosamente")
        void eliminarHorario_Success() {
            // Arrange
            when(horarioRepository.findById(horario.getId())).thenReturn(Optional.of(horario));
            when(doctorRepository.findByUsuarioId(usuarioDoctor.getId())).thenReturn(Optional.of(doctor));
            doNothing().when(horarioRepository).deleteById(horario.getId());

            // Act & Assert
            assertDoesNotThrow(() -> horarioService.eliminarHorario(horario.getId(), usuarioDoctor));
            verify(horarioRepository).deleteById(horario.getId());
        }

        @Test
        @DisplayName("Debería lanzar ResourceNotFoundException si el horario a eliminar no existe")
        void eliminarHorario_ShouldThrowException_WhenHorarioNotFound() {
            // Arrange
            when(horarioRepository.findById(anyLong())).thenReturn(Optional.empty());

            // Act & Assert
            assertThrows(ResourceNotFoundException.class, () -> horarioService.eliminarHorario(999L, usuarioDoctor));
        }

        @Test
        @DisplayName("Debería lanzar AccessDeniedException si un doctor intenta eliminar un horario que no le pertenece")
        void eliminarHorario_ShouldThrowException_WhenHorarioDoesNotBelongToDoctor() {
            // Arrange
            Doctor otroDoctor = new Doctor();
            otroDoctor.setId(2L);
            horario.setDoctor(otroDoctor); // El horario pertenece a otro doctor

            when(horarioRepository.findById(horario.getId())).thenReturn(Optional.of(horario));
            when(doctorRepository.findByUsuarioId(usuarioDoctor.getId())).thenReturn(Optional.of(doctor));

            // Act & Assert
            AccessDeniedException exception = assertThrows(AccessDeniedException.class,
                    () -> horarioService.eliminarHorario(horario.getId(), usuarioDoctor));
            assertEquals("No tiene permiso para eliminar este horario.", exception.getMessage());
        }
    }
}