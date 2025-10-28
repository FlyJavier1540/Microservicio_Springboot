package com.citasmedicas.api.services;

import com.citasmedicas.api.dtos.HistorialMedicoDTO;
import com.citasmedicas.api.enums.EstadoCita;
import com.citasmedicas.api.exceptions.ResourceNotFoundException;
import com.citasmedicas.api.mappers.HistorialMedicoMapper;
import com.citasmedicas.api.models.*;
import com.citasmedicas.api.repositories.CitaRepository;
import com.citasmedicas.api.repositories.DoctorRepository;
import com.citasmedicas.api.repositories.HistorialMedicoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class HistorialMedicoService {

    private final HistorialMedicoRepository historialMedicoRepository;
    private final CitaRepository citaRepository;
    private final DoctorRepository doctorRepository; // <-- AÑADE ESTA INYECCIÓN
    private final HistorialMedicoMapper historialMedicoMapper;

    @Transactional
    public HistorialMedicoDTO crearHistorial(HistorialMedicoDTO historialDTO, Usuario usuarioDoctor) {
        Cita cita = citaRepository.findById(historialDTO.getCitaId())
                .orElseThrow(() -> new ResourceNotFoundException("Cita no encontrada con id: " + historialDTO.getCitaId()));

        if (cita.getEstado() != EstadoCita.COMPLETADA) {
            throw new IllegalStateException("Solo se puede crear un historial para citas completadas.");
        }

        Doctor doctor = doctorRepository.findByUsuarioId(usuarioDoctor.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Perfil de Doctor no encontrado para el usuario."));

        if (!cita.getDoctor().getId().equals(doctor.getId())) {
            throw new AccessDeniedException("No tiene permiso para crear un historial para esta cita.");
        }

        HistorialMedico historial = historialMedicoMapper.toEntity(historialDTO);
        historial.setCita(cita);
        historial.setPaciente(cita.getPaciente());
        historial.setDoctor(doctor); // <-- AÑADIDO: Asignamos el doctor que crea el registro

        HistorialMedico historialGuardado = historialMedicoRepository.save(historial);
        return historialMedicoMapper.toDto(historialGuardado);
    }

    public List<HistorialMedicoDTO> obtenerHistorialPorPaciente(Long pacienteId) {
        // Usamos el método que ya habíamos creado en el HistorialMedicoRepository
        return historialMedicoRepository.findAllByPacienteId(pacienteId).stream()
                .map(historialMedicoMapper::toDto)
                .collect(Collectors.toList());
    }
}