package com.citasmedicas.api.mappers;

import com.citasmedicas.api.dtos.EspecialidadDTO;
import com.citasmedicas.api.models.Especialidad;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface EspecialidadMapper {

    EspecialidadMapper INSTANCE = Mappers.getMapper(EspecialidadMapper.class);

    EspecialidadDTO toDto(Especialidad especialidad);

    Especialidad toEntity(EspecialidadDTO especialidadDTO);
}