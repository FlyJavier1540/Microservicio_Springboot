package com.citasmedicas.api.mappers;

import com.citasmedicas.api.dtos.CitaDTO;
import com.citasmedicas.api.models.Cita;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface CitaMapper {

    CitaMapper INSTANCE = Mappers.getMapper(CitaMapper.class);

    @Mapping(source = "doctor.id", target = "doctorId")
    @Mapping(source = "doctor.nombreCompleto", target = "doctorNombre")
    @Mapping(source = "paciente.id", target = "pacienteId")
    @Mapping(source = "paciente.nombreCompleto", target = "pacienteNombre")
    CitaDTO toDto(Cita cita);

    @Mapping(source = "doctorId", target = "doctor.id")
    @Mapping(source = "pacienteId", target = "paciente.id")
    Cita toEntity(CitaDTO citaDTO);
}