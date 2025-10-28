package com.citasmedicas.api.services;

import com.citasmedicas.api.dtos.HorarioDTO;
import com.citasmedicas.api.exceptions.ResourceNotFoundException;
import com.citasmedicas.api.mappers.HorarioMapper;
import com.citasmedicas.api.models.Doctor;
import com.citasmedicas.api.models.Horario;
import com.citasmedicas.api.models.Usuario;
import com.citasmedicas.api.repositories.DoctorRepository;
import com.citasmedicas.api.repositories.HorarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class HorarioService {

    private final HorarioRepository horarioRepository;
    private final DoctorRepository doctorRepository;
    private final HorarioMapper horarioMapper;

    public HorarioDTO agregarHorario(HorarioDTO horarioDTO, Usuario usuarioDoctor) {
        Doctor doctor = doctorRepository.findByUsuarioId(usuarioDoctor.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Perfil de Doctor no encontrado para el usuario."));

        // Validar que la hora de fin sea posterior a la hora de inicio
        if (horarioDTO.getHoraInicio().isAfter(horarioDTO.getHoraFin())) {
            throw new IllegalArgumentException("La hora de inicio no puede ser posterior a la hora de fin.");
        }

        Horario nuevoHorario = horarioMapper.toEntity(horarioDTO);
        nuevoHorario.setDoctor(doctor);

        Horario horarioGuardado = horarioRepository.save(nuevoHorario);
        return horarioMapper.toDto(horarioGuardado);
    }

    public List<HorarioDTO> obtenerHorariosPorDoctor(Usuario usuarioDoctor) {
        Doctor doctor = doctorRepository.findByUsuarioId(usuarioDoctor.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Perfil de Doctor no encontrado para el usuario."));

        return horarioRepository.findAllByDoctorId(doctor.getId()).stream()
                .map(horarioMapper::toDto)
                .collect(Collectors.toList());
    }

    @Transactional
    public void eliminarHorario(Long id, Usuario usuarioDoctor) {
        Horario horario = horarioRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Horario no encontrado con id: " + id));

        Doctor doctor = doctorRepository.findByUsuarioId(usuarioDoctor.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Perfil de Doctor no encontrado para el usuario."));

        // Verificamos que el horario pertenezca al doctor que lo quiere eliminar
        if (!horario.getDoctor().getId().equals(doctor.getId())) {
            throw new AccessDeniedException("No tiene permiso para eliminar este horario.");
        }

        horarioRepository.deleteById(id);
    }
}