package com.citasmedicas.api.services;

import com.citasmedicas.api.dtos.HistorialMedicoDTO;
import com.citasmedicas.api.enums.EstadoCita;
import com.citasmedicas.api.exceptions.ResourceNotFoundException;
import com.citasmedicas.api.mappers.HistorialMedicoMapper;
import com.citasmedicas.api.models.*;
import com.citasmedicas.api.repositories.CitaRepository;
import com.citasmedicas.api.repositories.HistorialMedicoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class HistorialMedicoService {

    private final HistorialMedicoRepository historialMedicoRepository;
    private final CitaRepository citaRepository;
    private final HistorialMedicoMapper historialMedicoMapper;

    @Transactional
    public HistorialMedicoDTO crearHistorial(HistorialMedicoDTO historialDTO, Usuario usuarioDoctor) {
        // 1. Validar que la cita exista y esté completada
        Cita cita = citaRepository.findById(historialDTO.getCitaId())
                .orElseThrow(() -> new ResourceNotFoundException("Cita no encontrada con id: " + historialDTO.getCitaId()));

        if (cita.getEstado() != EstadoCita.COMPLETADA) {
            throw new IllegalStateException("Solo se puede crear un historial para citas completadas.");
        }

        // 2. Verificar que el doctor que crea el historial es el mismo de la cita
        if (!cita.getDoctor().getUsuario().getId().equals(usuarioDoctor.getId())) {
            throw new AccessDeniedException("No tiene permiso para crear un historial para esta cita.");
        }

        // 3. Crear y guardar el historial
        HistorialMedico historial = historialMedicoMapper.toEntity(historialDTO);
        historial.setCita(cita);
        historial.setPaciente(cita.getPaciente()); // Asignar el paciente desde la cita

        HistorialMedico historialGuardado = historialMedicoRepository.save(historial);
        return historialMedicoMapper.toDto(historialGuardado);
    }
}