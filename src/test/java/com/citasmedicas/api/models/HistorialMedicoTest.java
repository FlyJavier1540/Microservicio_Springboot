package com.citasmedicas.api.models;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Pruebas para el Modelo de Entidad HistorialMedico")
class HistorialMedicoTest {

    private HistorialMedico historialMedico;

    @BeforeEach
    void setUp() {
        historialMedico = new HistorialMedico();
    }

    @Test
    @DisplayName("Debería instanciar un HistorialMedico usando el constructor sin argumentos")
    void testNoArgsConstructor() {
        assertNotNull(historialMedico, "La instancia no debería ser nula.");
    }

    @Test
    @DisplayName("Debería instanciar un HistorialMedico usando el constructor con todos los argumentos")
    void testAllArgsConstructor() {
        // Arrange
        Cita cita = new Cita();
        Paciente paciente = new Paciente();
        Doctor doctor = new Doctor();

        // Act
        HistorialMedico historialCompleto = new HistorialMedico(1L, "Diagnóstico de prueba", "Tratamiento de prueba", "Notas adicionales", cita, paciente, doctor);

        // Assert
        assertNotNull(historialCompleto);
        assertEquals(1L, historialCompleto.getId());
        assertEquals("Diagnóstico de prueba", historialCompleto.getDiagnostico());
        assertEquals("Tratamiento de prueba", historialCompleto.getTratamiento());
        assertEquals("Notas adicionales", historialCompleto.getNotas());
        assertEquals(cita, historialCompleto.getCita());
        assertEquals(paciente, historialCompleto.getPaciente());
        assertEquals(doctor, historialCompleto.getDoctor());
    }

    @Test
    @DisplayName("Debería establecer y obtener el ID correctamente")
    void shouldSetAndGetId() {
        // Arrange
        Long id = 40L;

        // Act
        historialMedico.setId(id);

        // Assert
        assertEquals(id, historialMedico.getId());
    }

    @Test
    @DisplayName("Debería establecer y obtener el diagnóstico correctamente")
    void shouldSetAndGetDiagnostico() {
        // Arrange
        String diagnostico = "Hipertensión";

        // Act
        historialMedico.setDiagnostico(diagnostico);

        // Assert
        assertEquals(diagnostico, historialMedico.getDiagnostico());
    }

    @Test
    @DisplayName("Debería establecer y obtener el tratamiento correctamente")
    void shouldSetAndGetTratamiento() {
        // Arrange
        String tratamiento = "Dieta baja en sodio y ejercicio.";

        // Act
        historialMedico.setTratamiento(tratamiento);

        // Assert
        assertEquals(tratamiento, historialMedico.getTratamiento());
    }

    @Test
    @DisplayName("Debería establecer y obtener las notas correctamente")
    void shouldSetAndGetNotas() {
        // Arrange
        String notas = "El paciente debe monitorear su presión arterial diariamente.";

        // Act
        historialMedico.setNotas(notas);

        // Assert
        assertEquals(notas, historialMedico.getNotas());
    }

    @Test
    @DisplayName("Debería establecer y obtener la Cita correctamente")
    void shouldSetAndGetCita() {
        // Arrange
        Cita cita = new Cita();

        // Act
        historialMedico.setCita(cita);

        // Assert
        assertEquals(cita, historialMedico.getCita());
    }

    @Test
    @DisplayName("Debería establecer y obtener el Paciente correctamente")
    void shouldSetAndGetPaciente() {
        // Arrange
        Paciente paciente = new Paciente();

        // Act
        historialMedico.setPaciente(paciente);

        // Assert
        assertEquals(paciente, historialMedico.getPaciente());
    }

    @Test
    @DisplayName("Debería establecer y obtener el Doctor correctamente")
    void shouldSetAndGetDoctor() {
        // Arrange
        Doctor doctor = new Doctor();

        // Act
        historialMedico.setDoctor(doctor);

        // Assert
        assertEquals(doctor, historialMedico.getDoctor());
    }
}