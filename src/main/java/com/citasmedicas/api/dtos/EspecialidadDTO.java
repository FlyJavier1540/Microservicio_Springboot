package com.citasmedicas.api.dtos;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EspecialidadDTO {
    private Long id;

    @NotBlank(message = "El nombre de la especialidad no puede estar vacío.")
    private String nombre;

    private String descripcion;
}