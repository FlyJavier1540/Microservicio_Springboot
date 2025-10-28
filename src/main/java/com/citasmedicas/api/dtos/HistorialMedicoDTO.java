package com.citasmedicas.api.dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class HistorialMedicoDTO {
    private Long id;

    @NotNull(message = "El ID de la cita es requerido.")
    private Long citaId;

    @NotBlank(message = "El diagnóstico no puede estar vacío.")
    private String diagnostico;

    private String tratamiento;
    private String notas;
}