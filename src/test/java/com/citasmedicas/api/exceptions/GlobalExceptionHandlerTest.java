package com.citasmedicas.api.exceptions;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.core.MethodParameter;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@DisplayName("Pruebas del Manejador Global de Excepciones (GlobalExceptionHandler)")
class GlobalExceptionHandlerTest {

    private GlobalExceptionHandler globalExceptionHandler;

    @BeforeEach
    void setUp() {
        globalExceptionHandler = new GlobalExceptionHandler();
    }

    @Nested
    @DisplayName("Pruebas para cada tipo de Excepción")
    class ExceptionHandlingTests {

        @Test
        @DisplayName("Debería manejar ResourceNotFoundException y devolver un estado 404 NOT_FOUND")
        void handleResourceNotFoundException() {
            // Arrange
            String errorMessage = "Recurso no encontrado";
            ResourceNotFoundException ex = new ResourceNotFoundException(errorMessage);

            // Act
            ResponseEntity<String> response = globalExceptionHandler.handleResourceNotFoundException(ex);

            // Assert
            assertNotNull(response);
            assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
            assertEquals(errorMessage, response.getBody());
        }

        @Test
        @DisplayName("Debería manejar MethodArgumentNotValidException y devolver un estado 400 BAD_REQUEST con los errores")
        void handleMethodArgumentNotValid() {
            // Arrange
            // Simular el error de validación
            BindingResult bindingResult = mock(BindingResult.class);
            FieldError fieldError = new FieldError("objectName", "fieldName", "El campo no puede ser nulo");
            when(bindingResult.getFieldErrors()).thenReturn(java.util.List.of(fieldError));
            MethodParameter methodParameter = mock(MethodParameter.class);

            MethodArgumentNotValidException ex = new MethodArgumentNotValidException(methodParameter, bindingResult);

            // Act
            ResponseEntity<Object> response = globalExceptionHandler.handleMethodArgumentNotValid(ex);

            // Assert
            assertNotNull(response);
            assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
            assertInstanceOf(Map.class, response.getBody());

            @SuppressWarnings("unchecked")
            Map<String, String> errors = (Map<String, String>) response.getBody();
            assertTrue(errors.containsKey("fieldName"));
            assertEquals("El campo no puede ser nulo", errors.get("fieldName"));
        }

        @Test
        @DisplayName("Debería manejar una Excepción genérica y devolver un estado 500 INTERNAL_SERVER_ERROR")
        void handleGlobalException() {
            // Arrange
            String errorMessage = "Error inesperado en el sistema";
            Exception ex = new Exception(errorMessage);

            // Act
            ResponseEntity<String> response = globalExceptionHandler.handleGlobalException(ex);

            // Assert
            assertNotNull(response);
            assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
            assertTrue(response.getBody().contains(errorMessage));
        }
    }
}