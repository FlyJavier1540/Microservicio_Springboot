package com.citasmedicas.api.dtos;

import com.citasmedicas.api.enums.EstadoCita;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class CitaDTO {
    private Long id;

    @NotNull(message = "La fecha y hora no pueden ser nulas.")
    @Future(message = "La fecha y hora de la cita deben ser en el futuro.")
    private LocalDateTime fechaHora;

    private EstadoCita estado;
    private String motivoConsulta;

    @NotNull(message = "El id del doctor es requerido.")
    private Long doctorId;
    private String doctorNombre; // Para respuestas

    @NotNull(message = "El id del paciente es requerido.")
    private Long pacienteId;
    private String pacienteNombre; // Para respuestas
}