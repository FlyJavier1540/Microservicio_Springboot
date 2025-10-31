package com.citasmedicas.api.dtos;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Pruebas de Validación para CitaPospuestaDTO")
class CitaPospuestaDTOTest {

    private static Validator validator;

    @BeforeAll
    static void setUp() {
        // Se crea una única instancia del validador para todas las pruebas
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Nested
    @DisplayName("Pruebas para el campo 'nuevaFechaHora'")
    class NuevaFechaHoraValidationTests {

        @Test
        @DisplayName("Debería ser válido si la fecha está en el futuro")
        void shouldBeValidWhenDateIsInFuture() {
            // Arrange
            CitaPospuestaDTO dto = new CitaPospuestaDTO();
            dto.setNuevaFechaHora(LocalDateTime.now().plusDays(1)); // Fecha futura

            // Act
            Set<ConstraintViolation<CitaPospuestaDTO>> violations = validator.validate(dto);

            // Assert
            assertTrue(violations.isEmpty(), "No debería haber violaciones de validación");
        }

        @Test
        @DisplayName("Debería ser inválido si la fecha es nula")
        void shouldBeInvalidWhenDateIsNull() {
            // Arrange
            CitaPospuestaDTO dto = new CitaPospuestaDTO();
            dto.setNuevaFechaHora(null); // Valor nulo

            // Act
            Set<ConstraintViolation<CitaPospuestaDTO>> violations = validator.validate(dto);

            // Assert
            assertFalse(violations.isEmpty(), "Debería haber una violación de validación");
            ConstraintViolation<CitaPospuestaDTO> violation = violations.iterator().next();
            assertEquals("La nueva fecha y hora no pueden ser nulas.", violation.getMessage());
            assertEquals("nuevaFechaHora", violation.getPropertyPath().toString());
        }

        @Test
        @DisplayName("Debería ser inválido si la fecha está en el pasado")
        void shouldBeInvalidWhenDateIsInPast() {
            // Arrange
            CitaPospuestaDTO dto = new CitaPospuestaDTO();
            dto.setNuevaFechaHora(LocalDateTime.now().minusDays(1)); // Fecha pasada

            // Act
            Set<ConstraintViolation<CitaPospuestaDTO>> violations = validator.validate(dto);

            // Assert
            assertFalse(violations.isEmpty(), "Debería haber una violación de validación");
            ConstraintViolation<CitaPospuestaDTO> violation = violations.iterator().next();
            assertEquals("La nueva fecha y hora deben ser en el futuro.", violation.getMessage());
        }

        @Test
        @DisplayName("Debería ser inválido si la fecha es el presente exacto (no futuro)")
        void shouldBeInvalidWhenDateIsPresent() {
            // Arrange
            CitaPospuestaDTO dto = new CitaPospuestaDTO();
            dto.setNuevaFechaHora(LocalDateTime.now()); // Fecha presente

            // Act
            Set<ConstraintViolation<CitaPospuestaDTO>> violations = validator.validate(dto);

            // Assert
            assertFalse(violations.isEmpty());
            ConstraintViolation<CitaPospuestaDTO> violation = violations.iterator().next();
            assertEquals("La nueva fecha y hora deben ser en el futuro.", violation.getMessage());
        }
    }
}