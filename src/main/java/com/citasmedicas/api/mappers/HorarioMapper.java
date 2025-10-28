package com.citasmedicas.api.mappers;

import com.citasmedicas.api.dtos.HorarioDTO;
import com.citasmedicas.api.models.Horario;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface HorarioMapper {

    HorarioMapper INSTANCE = Mappers.getMapper(HorarioMapper.class);

    @Mapping(source = "doctor.id", target = "doctorId")
    HorarioDTO toDto(Horario horario);

    @Mapping(source = "doctorId", target = "doctor.id")
    Horario toEntity(HorarioDTO horarioDTO);
}