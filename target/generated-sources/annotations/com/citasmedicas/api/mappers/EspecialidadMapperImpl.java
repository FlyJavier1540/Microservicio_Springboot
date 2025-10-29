package com.citasmedicas.api.mappers;

import com.citasmedicas.api.dtos.EspecialidadDTO;
import com.citasmedicas.api.models.Especialidad;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2025-10-28T18:02:17-0600",
    comments = "version: 1.5.5.Final, compiler: javac, environment: Java 17.0.12 (Oracle Corporation)"
)
@Component
public class EspecialidadMapperImpl implements EspecialidadMapper {

    @Override
    public EspecialidadDTO toDto(Especialidad especialidad) {
        if ( especialidad == null ) {
            return null;
        }

        EspecialidadDTO especialidadDTO = new EspecialidadDTO();

        especialidadDTO.setId( especialidad.getId() );
        especialidadDTO.setNombre( especialidad.getNombre() );
        especialidadDTO.setDescripcion( especialidad.getDescripcion() );

        return especialidadDTO;
    }

    @Override
    public Especialidad toEntity(EspecialidadDTO especialidadDTO) {
        if ( especialidadDTO == null ) {
            return null;
        }

        Especialidad especialidad = new Especialidad();

        especialidad.setId( especialidadDTO.getId() );
        especialidad.setNombre( especialidadDTO.getNombre() );
        especialidad.setDescripcion( especialidadDTO.getDescripcion() );

        return especialidad;
    }
}
