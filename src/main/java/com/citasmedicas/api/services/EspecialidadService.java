package com.citasmedicas.api.services;

import com.citasmedicas.api.dtos.EspecialidadDTO;
import com.citasmedicas.api.exceptions.ResourceNotFoundException;
import com.citasmedicas.api.mappers.EspecialidadMapper;
import com.citasmedicas.api.models.Especialidad;
import com.citasmedicas.api.repositories.EspecialidadRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class EspecialidadService {

    private final EspecialidadRepository especialidadRepository;
    private final EspecialidadMapper especialidadMapper;

    public List<EspecialidadDTO> obtenerTodas() {
        return especialidadRepository.findAll()
                .stream()
                .map(especialidadMapper::toDto)
                .collect(Collectors.toList());
    }

    public EspecialidadDTO crearEspecialidad(EspecialidadDTO especialidadDTO) {
        Especialidad especialidad = especialidadMapper.toEntity(especialidadDTO);
        Especialidad nuevaEspecialidad = especialidadRepository.save(especialidad);
        return especialidadMapper.toDto(nuevaEspecialidad);
    }

    public EspecialidadDTO obtenerPorId(Long id) {
        Especialidad especialidad = especialidadRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Especialidad no encontrada con id: " + id));
        return especialidadMapper.toDto(especialidad);
    }

    public void eliminarEspecialidad(Long id) {
        if (!especialidadRepository.existsById(id)) {
            throw new ResourceNotFoundException("Especialidad no encontrada con id: " + id);
        }
        especialidadRepository.deleteById(id);
    }
}