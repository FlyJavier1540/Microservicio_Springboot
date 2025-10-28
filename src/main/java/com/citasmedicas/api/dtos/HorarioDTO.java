package com.citasmedicas.api.dtos;

import com.citasmedicas.api.enums.DiaDeLaSemana;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalTime;

@Getter
@Setter
public class HorarioDTO {
    private Long id;

    @NotNull(message = "El día de la semana es requerido.")
    private DiaDeLaSemana diaDeLaSemana;

    @NotNull(message = "La hora de inicio es requerida.")
    private LocalTime horaInicio;

    @NotNull(message = "La hora de fin es requerida.")
    private LocalTime horaFin;

    private Long doctorId;
}