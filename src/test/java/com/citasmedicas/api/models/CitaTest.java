package com.citasmedicas.api.models;

import com.citasmedicas.api.enums.EstadoCita;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Pruebas para el Modelo de Entidad Cita")
class CitaTest {

    private Cita cita;
    private final LocalDateTime fechaHora = LocalDateTime.of(2025, 10, 31, 10, 30);
    private final Doctor doctor = new Doctor();
    private final Paciente paciente = new Paciente();

    @BeforeEach
    void setUp() {
        cita = new Cita();
    }

    @Test
    @DisplayName("Debería instanciar una Cita usando el constructor sin argumentos")
    void testNoArgsConstructor() {
        assertNotNull(cita, "La instancia no debería ser nula.");
    }

    @Test
    @DisplayName("Debería instanciar una Cita usando el constructor con todos los argumentos")
    void testAllArgsConstructor() {
        // Arrange y Act
        Cita citaCompleta = new Cita(1L, fechaHora, EstadoCita.PROGRAMADA, "Chequeo general", doctor, paciente);

        // Assert
        assertNotNull(citaCompleta);
        assertEquals(1L, citaCompleta.getId());
        assertEquals(fechaHora, citaCompleta.getFechaHora());
        assertEquals(EstadoCita.PROGRAMADA, citaCompleta.getEstado());
        assertEquals("Chequeo general", citaCompleta.getMotivoConsulta());
        assertEquals(doctor, citaCompleta.getDoctor());
        assertEquals(paciente, citaCompleta.getPaciente());
    }

    @Test
    @DisplayName("Debería establecer y obtener el ID correctamente")
    void shouldSetAndGetId() {
        // Arrange
        Long id = 10L;

        // Act
        cita.setId(id);

        // Assert
        assertEquals(id, cita.getId());
    }

    @Test
    @DisplayName("Debería establecer y obtener la fecha y hora correctamente")
    void shouldSetAndGetFechaHora() {
        // Act
        cita.setFechaHora(fechaHora);

        // Assert
        assertEquals(fechaHora, cita.getFechaHora());
    }

    @Test
    @DisplayName("Debería establecer y obtener el estado correctamente")
    void shouldSetAndGetEstado() {
        // Arrange
        EstadoCita estado = EstadoCita.COMPLETADA;

        // Act
        cita.setEstado(estado);

        // Assert
        assertEquals(estado, cita.getEstado());
    }

    @Test
    @DisplayName("Debería establecer y obtener el motivo de la consulta correctamente")
    void shouldSetAndGetMotivoConsulta() {
        // Arrange
        String motivo = "Dolor de espalda";

        // Act
        cita.setMotivoConsulta(motivo);

        // Assert
        assertEquals(motivo, cita.getMotivoConsulta());
    }

    @Test
    @DisplayName("Debería establecer y obtener el Doctor correctamente")
    void shouldSetAndGetDoctor() {
        // Act
        cita.setDoctor(doctor);

        // Assert
        assertEquals(doctor, cita.getDoctor());
    }

    @Test
    @DisplayName("Debería establecer y obtener el Paciente correctamente")
    void shouldSetAndGetPaciente() {
        // Act
        cita.setPaciente(paciente);

        // Assert
        assertEquals(paciente, cita.getPaciente());
    }
}