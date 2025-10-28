package com.citasmedicas.api.services;

import com.citasmedicas.api.dtos.CitaDTO;
import com.citasmedicas.api.enums.EstadoCita;
import com.citasmedicas.api.exceptions.ResourceNotFoundException;
import com.citasmedicas.api.mappers.CitaMapper;
import com.citasmedicas.api.models.Cita;
import com.citasmedicas.api.models.Doctor;
import com.citasmedicas.api.models.Paciente;
import com.citasmedicas.api.repositories.CitaRepository;
import com.citasmedicas.api.repositories.DoctorRepository;
import com.citasmedicas.api.repositories.PacienteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CitaService {

    private final CitaRepository citaRepository;
    private final PacienteRepository pacienteRepository;
    private final DoctorRepository doctorRepository;
    private final CitaMapper citaMapper;

    @Transactional
    public CitaDTO solicitarCita(CitaDTO citaDTO) {
        // 1. Validar que el paciente y el doctor existan
        Paciente paciente = pacienteRepository.findById(citaDTO.getPacienteId())
                .orElseThrow(() -> new ResourceNotFoundException("Paciente no encontrado con id: " + citaDTO.getPacienteId()));
        Doctor doctor = doctorRepository.findById(citaDTO.getDoctorId())
                .orElseThrow(() -> new ResourceNotFoundException("Doctor no encontrado con id: " + citaDTO.getDoctorId()));

        // 2. Crear la entidad Cita y establecer su estado inicial
        Cita nuevaCita = citaMapper.toEntity(citaDTO);
        nuevaCita.setPaciente(paciente);
        nuevaCita.setDoctor(doctor);
        nuevaCita.setEstado(EstadoCita.SOLICITADA); // ¡Estado inicial clave!

        // 3. Guardar en la base de datos
        Cita citaGuardada = citaRepository.save(nuevaCita);

        // 4. Devolver el DTO de la cita recién creada
        return citaMapper.toDto(citaGuardada);
    }

    @Transactional
    public CitaDTO cambiarEstadoCita(Long id, EstadoCita nuevoEstado) {
        Cita cita = citaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Cita no encontrada con id: " + id));

        cita.setEstado(nuevoEstado);
        Cita citaActualizada = citaRepository.save(cita);
        return citaMapper.toDto(citaActualizada);
    }

    public List<CitaDTO> obtenerCitasPorEstado(EstadoCita estado) {
        // En un sistema real, esto podría necesitar paginación.
        // También se podrían añadir filtros por doctor, etc.
        return citaRepository.findAll().stream()
                .filter(cita -> cita.getEstado() == estado)
                .map(citaMapper::toDto)
                .collect(Collectors.toList());
    }

    public List<CitaDTO> obtenerCitasPorPaciente(Long pacienteId) {
        if (!pacienteRepository.existsById(pacienteId)) {
            throw new ResourceNotFoundException("Paciente no encontrado con id: " + pacienteId);
        }
        return citaRepository.findAllByPacienteId(pacienteId).stream()
                .map(citaMapper::toDto)
                .collect(Collectors.toList());
    }
}