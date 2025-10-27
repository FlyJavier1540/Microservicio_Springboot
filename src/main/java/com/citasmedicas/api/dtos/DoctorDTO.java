package com.citasmedicas.api.dtos;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DoctorDTO {
    private Long id;

    @NotBlank(message = "El nombre del doctor no puede estar vacío.")
    private String nombreCompleto;

    private String telefono;
    private String cedulaProfesional;
    private Long especialidadId; // Para las peticiones
    private String especialidadNombre; // Para las respuestas
}