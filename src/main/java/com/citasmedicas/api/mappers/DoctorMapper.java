package com.citasmedicas.api.mappers;

import com.citasmedicas.api.dtos.DoctorDTO;
import com.citasmedicas.api.models.Doctor;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring", uses = {EspecialidadMapper.class})
public interface DoctorMapper {

    DoctorMapper INSTANCE = Mappers.getMapper(DoctorMapper.class);

    @Mapping(source = "especialidad.id", target = "especialidadId")
    @Mapping(source = "especialidad.nombre", target = "especialidadNombre")
    DoctorDTO toDto(Doctor doctor);

    @Mapping(source = "especialidadId", target = "especialidad.id")
    Doctor toEntity(DoctorDTO doctorDTO);
}