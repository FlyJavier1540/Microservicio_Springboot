package com.citasmedicas.api.dtos;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalTime;

@Getter
@Setter
public class CitaAprobacionDTO {

    @NotNull(message = "La hora de la cita es requerida.")
    private LocalTime hora; // El doctor envía la hora
}