package com.citasmedicas.api.models;

import com.citasmedicas.api.enums.DiaDeLaSemana;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalTime;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Pruebas para el Modelo de Entidad Horario")
class HorarioTest {

    private Horario horario;

    @BeforeEach
    void setUp() {
        horario = new Horario();
    }

    @Test
    @DisplayName("Debería instanciar un Horario usando el constructor sin argumentos")
    void testNoArgsConstructor() {
        assertNotNull(horario, "La instancia no debería ser nula.");
    }

    @Test
    @DisplayName("Debería instanciar un Horario usando el constructor con todos los argumentos")
    void testAllArgsConstructor() {
        // Arrange
        Doctor doctor = new Doctor();
        LocalTime inicio = LocalTime.of(8, 0);
        LocalTime fin = LocalTime.of(12, 0);

        // Act
        Horario horarioCompleto = new Horario(1L, DiaDeLaSemana.MIERCOLES, inicio, fin, doctor);

        // Assert
        assertNotNull(horarioCompleto);
        assertEquals(1L, horarioCompleto.getId());
        assertEquals(DiaDeLaSemana.MIERCOLES, horarioCompleto.getDiaDeLaSemana());
        assertEquals(inicio, horarioCompleto.getHoraInicio());
        assertEquals(fin, horarioCompleto.getHoraFin());
        assertEquals(doctor, horarioCompleto.getDoctor());
    }

    @Test
    @DisplayName("Debería establecer y obtener el ID correctamente")
    void shouldSetAndGetId() {
        // Arrange
        Long id = 50L;

        // Act
        horario.setId(id);

        // Assert
        assertEquals(id, horario.getId());
    }

    @Test
    @DisplayName("Debería establecer y obtener el día de la semana correctamente")
    void shouldSetAndGetDiaDeLaSemana() {
        // Arrange
        DiaDeLaSemana dia = DiaDeLaSemana.VIERNES;

        // Act
        horario.setDiaDeLaSemana(dia);

        // Assert
        assertEquals(dia, horario.getDiaDeLaSemana());
    }

    @Test
    @DisplayName("Debería establecer y obtener la hora de inicio correctamente")
    void shouldSetAndGetHoraInicio() {
        // Arrange
        LocalTime inicio = LocalTime.of(9, 30);

        // Act
        horario.setHoraInicio(inicio);

        // Assert
        assertEquals(inicio, horario.getHoraInicio());
    }

    @Test
    @DisplayName("Debería establecer y obtener la hora de fin correctamente")
    void shouldSetAndGetHoraFin() {
        // Arrange
        LocalTime fin = LocalTime.of(17, 45);

        // Act
        horario.setHoraFin(fin);

        // Assert
        assertEquals(fin, horario.getHoraFin());
    }

    @Test
    @DisplayName("Debería establecer y obtener el Doctor correctamente")
    void shouldSetAndGetDoctor() {
        // Arrange
        Doctor doctor = new Doctor();

        // Act
        horario.setDoctor(doctor);

        // Assert
        assertEquals(doctor, horario.getDoctor());
    }
}