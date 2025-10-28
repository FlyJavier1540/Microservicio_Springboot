package com.citasmedicas.api.dtos;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class CitaPospuestaDTO {

    @NotNull(message = "La nueva fecha y hora no pueden ser nulas.")
    @Future(message = "La nueva fecha y hora deben ser en el futuro.")
    private LocalDateTime nuevaFechaHora;
}