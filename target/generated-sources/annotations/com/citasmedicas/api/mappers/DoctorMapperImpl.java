package com.citasmedicas.api.mappers;

import com.citasmedicas.api.dtos.DoctorDTO;
import com.citasmedicas.api.models.Doctor;
import com.citasmedicas.api.models.Especialidad;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2025-10-28T21:49:06-0600",
    comments = "version: 1.5.5.Final, compiler: javac, environment: Java 17.0.12 (Oracle Corporation)"
)
@Component
public class DoctorMapperImpl implements DoctorMapper {

    @Override
    public DoctorDTO toDto(Doctor doctor) {
        if ( doctor == null ) {
            return null;
        }

        DoctorDTO doctorDTO = new DoctorDTO();

        doctorDTO.setEspecialidadId( doctorEspecialidadId( doctor ) );
        doctorDTO.setEspecialidadNombre( doctorEspecialidadNombre( doctor ) );
        doctorDTO.setId( doctor.getId() );
        doctorDTO.setNombreCompleto( doctor.getNombreCompleto() );
        doctorDTO.setTelefono( doctor.getTelefono() );
        doctorDTO.setCedulaProfesional( doctor.getCedulaProfesional() );

        return doctorDTO;
    }

    @Override
    public Doctor toEntity(DoctorDTO doctorDTO) {
        if ( doctorDTO == null ) {
            return null;
        }

        Doctor doctor = new Doctor();

        doctor.setEspecialidad( doctorDTOToEspecialidad( doctorDTO ) );
        doctor.setId( doctorDTO.getId() );
        doctor.setNombreCompleto( doctorDTO.getNombreCompleto() );
        doctor.setTelefono( doctorDTO.getTelefono() );
        doctor.setCedulaProfesional( doctorDTO.getCedulaProfesional() );

        return doctor;
    }

    private Long doctorEspecialidadId(Doctor doctor) {
        if ( doctor == null ) {
            return null;
        }
        Especialidad especialidad = doctor.getEspecialidad();
        if ( especialidad == null ) {
            return null;
        }
        Long id = especialidad.getId();
        if ( id == null ) {
            return null;
        }
        return id;
    }

    private String doctorEspecialidadNombre(Doctor doctor) {
        if ( doctor == null ) {
            return null;
        }
        Especialidad especialidad = doctor.getEspecialidad();
        if ( especialidad == null ) {
            return null;
        }
        String nombre = especialidad.getNombre();
        if ( nombre == null ) {
            return null;
        }
        return nombre;
    }

    protected Especialidad doctorDTOToEspecialidad(DoctorDTO doctorDTO) {
        if ( doctorDTO == null ) {
            return null;
        }

        Especialidad especialidad = new Especialidad();

        especialidad.setId( doctorDTO.getEspecialidadId() );

        return especialidad;
    }
}
