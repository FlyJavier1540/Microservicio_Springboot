package com.citasmedicas.api.models;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Pruebas para el Modelo de Entidad Especialidad")
class EspecialidadTest {

    private Especialidad especialidad;

    @BeforeEach
    void setUp() {
        especialidad = new Especialidad();
    }

    @Test
    @DisplayName("Debería instanciar una Especialidad usando el constructor sin argumentos")
    void testNoArgsConstructor() {
        assertNotNull(especialidad, "La instancia no debería ser nula.");
    }

    @Test
    @DisplayName("Debería instanciar una Especialidad usando el constructor con todos los argumentos")
    void testAllArgsConstructor() {
        // Arrange y Act
        Especialidad especialidadCompleta = new Especialidad(1L, "Dermatología", "Cuidado de la piel.");

        // Assert
        assertNotNull(especialidadCompleta);
        assertEquals(1L, especialidadCompleta.getId());
        assertEquals("Dermatología", especialidadCompleta.getNombre());
        assertEquals("Cuidado de la piel.", especialidadCompleta.getDescripcion());
    }

    @Test
    @DisplayName("Debería establecer y obtener el ID correctamente")
    void shouldSetAndGetId() {
        // Arrange
        Long id = 30L;

        // Act
        especialidad.setId(id);

        // Assert
        assertEquals(id, especialidad.getId());
    }

    @Test
    @DisplayName("Debería establecer y obtener el nombre correctamente")
    void shouldSetAndGetNombre() {
        // Arrange
        String nombre = "Oncología";

        // Act
        especialidad.setNombre(nombre);

        // Assert
        assertEquals(nombre, especialidad.getNombre());
    }

    @Test
    @DisplayName("Debería establecer y obtener la descripción correctamente")
    void shouldSetAndGetDescripcion() {
        // Arrange
        String descripcion = "Tratamiento del cáncer.";

        // Act
        especialidad.setDescripcion(descripcion);

        // Assert
        assertEquals(descripcion, especialidad.getDescripcion());
    }
}