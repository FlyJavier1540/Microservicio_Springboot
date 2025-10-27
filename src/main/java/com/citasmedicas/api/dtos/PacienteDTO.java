package com.citasmedicas.api.dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Past;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class PacienteDTO {
    private Long id;

    @NotBlank(message = "El nombre del paciente no puede estar vacío.")
    private String nombreCompleto;

    @Past(message = "La fecha de nacimiento debe ser una fecha en el pasado.")
    private LocalDate fechaNacimiento;

    private String telefono;
    private String direccion;
}