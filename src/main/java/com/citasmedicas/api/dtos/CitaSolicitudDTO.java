package com.citasmedicas.api.dtos;

import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class CitaSolicitudDTO {

    @NotNull(message = "La fecha no puede ser nula.")
    @FutureOrPresent(message = "La fecha de la cita debe ser hoy o en el futuro.")
    private LocalDate fecha; // El paciente solo envía la fecha

    private String motivoConsulta;

    @NotNull(message = "El id del doctor es requerido.")
    private Long doctorId;

    // El ID del paciente lo obtendremos del token, así que no es necesario aquí.
}