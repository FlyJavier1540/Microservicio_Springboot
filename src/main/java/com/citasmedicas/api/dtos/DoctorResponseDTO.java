package com.citasmedicas.api.dtos;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.Setter;

// Este DTO se usará para las respuestas generales
@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL) // No incluirá campos nulos en el JSON
public class DoctorResponseDTO {
    private Long id;
    private String nombreCompleto;
    private String telefono;
    private String cedulaProfesional;
    private String especialidadNombre;
    private String password; // Campo que solo se llenará al crear
}