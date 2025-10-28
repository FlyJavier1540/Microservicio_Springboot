package com.citasmedicas.api.services;

import com.citasmedicas.api.dtos.PacienteDTO;
import com.citasmedicas.api.exceptions.ResourceNotFoundException;
import com.citasmedicas.api.mappers.PacienteMapper;
import com.citasmedicas.api.models.Paciente;
import com.citasmedicas.api.repositories.PacienteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PacienteService {

    private final PacienteRepository pacienteRepository;
    private final PacienteMapper pacienteMapper;

    public List<PacienteDTO> obtenerTodosLosPacientes() {
        return pacienteRepository.findAll()
                .stream()
                .map(pacienteMapper::toDto)
                .collect(Collectors.toList());
    }

    public PacienteDTO obtenerPacientePorId(Long id) {
        Paciente paciente = pacienteRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Paciente no encontrado con id: " + id));
        return pacienteMapper.toDto(paciente);
    }

    @Transactional
    public void eliminarPaciente(Long id) {
        Paciente paciente = pacienteRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Paciente no encontrado con id: " + id));

        // Verificamos si el paciente tiene citas
        if (!paciente.getCitas().isEmpty()) {
            throw new IllegalStateException("No se puede eliminar un paciente con citas asociadas.");
        }

        pacienteRepository.delete(paciente);
    }
}