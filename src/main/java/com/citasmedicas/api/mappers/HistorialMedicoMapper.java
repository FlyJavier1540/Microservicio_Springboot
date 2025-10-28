package com.citasmedicas.api.mappers;

import com.citasmedicas.api.dtos.HistorialMedicoDTO;
import com.citasmedicas.api.models.HistorialMedico;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface HistorialMedicoMapper {

    HistorialMedicoMapper INSTANCE = Mappers.getMapper(HistorialMedicoMapper.class);

    @Mapping(source = "cita.id", target = "citaId")
    @Mapping(source = "doctor.nombreCompleto", target = "doctorNombre")
    HistorialMedicoDTO toDto(HistorialMedico historialMedico);

    @Mapping(source = "citaId", target = "cita.id")
    HistorialMedico toEntity(HistorialMedicoDTO historialMedicoDTO);
}