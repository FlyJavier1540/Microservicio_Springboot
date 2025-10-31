package com.citasmedicas.api.models;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Pruebas para el Modelo de Entidad Paciente")
class PacienteTest {

    private Paciente paciente;

    @BeforeEach
    void setUp() {
        paciente = new Paciente();
    }

    @Test
    @DisplayName("Debería instanciar un Paciente usando el constructor sin argumentos")
    void testNoArgsConstructor() {
        assertNotNull(paciente, "La instancia no debería ser nula.");
    }

    @Test
    @DisplayName("Debería instanciar un Paciente usando el constructor con todos los argumentos")
    void testAllArgsConstructor() {
        // Arrange
        Usuario usuario = new Usuario();
        List<Cita> citas = new ArrayList<>();
        LocalDate fechaNacimiento = LocalDate.of(1995, 8, 24);

        // Act
        Paciente pacienteCompleto = new Paciente(1L, "Juan Pérez", fechaNacimiento, "555-1234", "Av. Siempre Viva 123", usuario, citas);

        // Assert
        assertNotNull(pacienteCompleto);
        assertEquals(1L, pacienteCompleto.getId());
        assertEquals("Juan Pérez", pacienteCompleto.getNombreCompleto());
        assertEquals(fechaNacimiento, pacienteCompleto.getFechaNacimiento());
        assertEquals("555-1234", pacienteCompleto.getTelefono());
        assertEquals("Av. Siempre Viva 123", pacienteCompleto.getDireccion());
        assertEquals(usuario, pacienteCompleto.getUsuario());
        assertEquals(citas, pacienteCompleto.getCitas());
    }

    @Test
    @DisplayName("Debería establecer y obtener el ID correctamente")
    void shouldSetAndGetId() {
        // Arrange
        Long id = 60L;

        // Act
        paciente.setId(id);

        // Assert
        assertEquals(id, paciente.getId());
    }

    @Test
    @DisplayName("Debería establecer y obtener el nombre completo correctamente")
    void shouldSetAndGetNombreCompleto() {
        // Arrange
        String nombre = "Maria García";

        // Act
        paciente.setNombreCompleto(nombre);

        // Assert
        assertEquals(nombre, paciente.getNombreCompleto());
    }

    @Test
    @DisplayName("Debería establecer y obtener la fecha de nacimiento correctamente")
    void shouldSetAndGetFechaNacimiento() {
        // Arrange
        LocalDate fechaNacimiento = LocalDate.of(2000, 1, 1);

        // Act
        paciente.setFechaNacimiento(fechaNacimiento);

        // Assert
        assertEquals(fechaNacimiento, paciente.getFechaNacimiento());
    }

    @Test
    @DisplayName("Debería establecer y obtener el teléfono correctamente")
    void shouldSetAndGetTelefono() {
        // Arrange
        String telefono = "555-5678";

        // Act
        paciente.setTelefono(telefono);

        // Assert
        assertEquals(telefono, paciente.getTelefono());
    }

    @Test
    @DisplayName("Debería establecer y obtener la dirección correctamente")
    void shouldSetAndGetDireccion() {
        // Arrange
        String direccion = "Calle Falsa 456";

        // Act
        paciente.setDireccion(direccion);

        // Assert
        assertEquals(direccion, paciente.getDireccion());
    }

    @Test
    @DisplayName("Debería establecer y obtener el Usuario correctamente")
    void shouldSetAndGetUsuario() {
        // Arrange
        Usuario usuario = new Usuario();

        // Act
        paciente.setUsuario(usuario);

        // Assert
        assertEquals(usuario, paciente.getUsuario());
    }

    @Test
    @DisplayName("Debería establecer y obtener la lista de Citas correctamente")
    void shouldSetAndGetCitas() {
        // Arrange
        List<Cita> citas = new ArrayList<>();
        citas.add(new Cita());

        // Act
        paciente.setCitas(citas);

        // Assert
        assertEquals(citas, paciente.getCitas());
        assertFalse(paciente.getCitas().isEmpty());
    }
}