package com.citasmedicas.api.models;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Pruebas para el Modelo de Entidad Doctor")
class DoctorTest {

    private Doctor doctor;

    @BeforeEach
    void setUp() {
        doctor = new Doctor();
    }

    @Test
    @DisplayName("Debería instanciar un Doctor usando el constructor sin argumentos")
    void testNoArgsConstructor() {
        assertNotNull(doctor, "La instancia no debería ser nula.");
    }

    @Test
    @DisplayName("Debería instanciar un Doctor usando el constructor con todos los argumentos")
    void testAllArgsConstructor() {
        // Arrange
        Usuario usuario = new Usuario();
        Especialidad especialidad = new Especialidad();
        List<Cita> citas = new ArrayList<>();

        // Act
        Doctor doctorCompleto = new Doctor(1L, "Dr. John Smith", "555-0101", "12345XYZ", usuario, especialidad, citas);

        // Assert
        assertNotNull(doctorCompleto);
        assertEquals(1L, doctorCompleto.getId());
        assertEquals("Dr. John Smith", doctorCompleto.getNombreCompleto());
        assertEquals("555-0101", doctorCompleto.getTelefono());
        assertEquals("12345XYZ", doctorCompleto.getCedulaProfesional());
        assertEquals(usuario, doctorCompleto.getUsuario());
        assertEquals(especialidad, doctorCompleto.getEspecialidad());
        assertEquals(citas, doctorCompleto.getCitas());
    }

    @Test
    @DisplayName("Debería establecer y obtener el ID correctamente")
    void shouldSetAndGetId() {
        // Arrange
        Long id = 20L;

        // Act
        doctor.setId(id);

        // Assert
        assertEquals(id, doctor.getId());
    }

    @Test
    @DisplayName("Debería establecer y obtener el nombre completo correctamente")
    void shouldSetAndGetNombreCompleto() {
        // Arrange
        String nombre = "Dr. Allison Cameron";

        // Act
        doctor.setNombreCompleto(nombre);

        // Assert
        assertEquals(nombre, doctor.getNombreCompleto());
    }

    @Test
    @DisplayName("Debería establecer y obtener el teléfono correctamente")
    void shouldSetAndGetTelefono() {
        // Arrange
        String telefono = "555-0102";

        // Act
        doctor.setTelefono(telefono);

        // Assert
        assertEquals(telefono, doctor.getTelefono());
    }

    @Test
    @DisplayName("Debería establecer y obtener la cédula profesional correctamente")
    void shouldSetAndGetCedulaProfesional() {
        // Arrange
        String cedula = "67890ABC";

        // Act
        doctor.setCedulaProfesional(cedula);

        // Assert
        assertEquals(cedula, doctor.getCedulaProfesional());
    }

    @Test
    @DisplayName("Debería establecer y obtener el Usuario correctamente")
    void shouldSetAndGetUsuario() {
        // Arrange
        Usuario usuario = new Usuario();

        // Act
        doctor.setUsuario(usuario);

        // Assert
        assertEquals(usuario, doctor.getUsuario());
    }

    @Test
    @DisplayName("Debería establecer y obtener la Especialidad correctamente")
    void shouldSetAndGetEspecialidad() {
        // Arrange
        Especialidad especialidad = new Especialidad();

        // Act
        doctor.setEspecialidad(especialidad);

        // Assert
        assertEquals(especialidad, doctor.getEspecialidad());
    }

    @Test
    @DisplayName("Debería establecer y obtener la lista de Citas correctamente")
    void shouldSetAndGetCitas() {
        // Arrange
        List<Cita> citas = new ArrayList<>();
        citas.add(new Cita());

        // Act
        doctor.setCitas(citas);

        // Assert
        assertEquals(citas, doctor.getCitas());
        assertFalse(doctor.getCitas().isEmpty());
    }
}