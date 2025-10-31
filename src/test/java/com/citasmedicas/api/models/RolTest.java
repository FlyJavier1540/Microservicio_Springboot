package com.citasmedicas.api.models;

import com.citasmedicas.api.enums.RolNombre;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Pruebas para el Modelo de Entidad Rol")
class RolTest {

    private Rol rol;

    @BeforeEach
    void setUp() {
        rol = new Rol();
    }

    @Test
    @DisplayName("Debería instanciar un Rol usando el constructor sin argumentos")
    void testNoArgsConstructor() {
        assertNotNull(rol, "La instancia no debería ser nula.");
    }

    @Test
    @DisplayName("Debería instanciar un Rol usando el constructor con todos los argumentos")
    void testAllArgsConstructor() {
        // Arrange y Act
        Rol rolCompleto = new Rol(1L, RolNombre.ADMIN);

        // Assert
        assertNotNull(rolCompleto);
        assertEquals(1L, rolCompleto.getId());
        assertEquals(RolNombre.ADMIN, rolCompleto.getNombre());
    }

    @Test
    @DisplayName("Debería establecer y obtener el ID correctamente")
    void shouldSetAndGetId() {
        // Arrange
        Long id = 70L;

        // Act
        rol.setId(id);

        // Assert
        assertEquals(id, rol.getId());
    }

    @Test
    @DisplayName("Debería establecer y obtener el nombre (RolNombre) correctamente")
    void shouldSetAndGetNombre() {
        // Arrange
        RolNombre nombre = RolNombre.PACIENTE;

        // Act
        rol.setNombre(nombre);

        // Assert
        assertEquals(nombre, rol.getNombre());
    }
}