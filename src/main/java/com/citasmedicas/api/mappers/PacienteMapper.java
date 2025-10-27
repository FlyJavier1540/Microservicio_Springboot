package com.citasmedicas.api.mappers;

import com.citasmedicas.api.dtos.PacienteDTO;
import com.citasmedicas.api.models.Paciente;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface PacienteMapper {

    PacienteMapper INSTANCE = Mappers.getMapper(PacienteMapper.class);

    PacienteDTO toDto(Paciente paciente);

    Paciente toEntity(PacienteDTO pacienteDTO);
}