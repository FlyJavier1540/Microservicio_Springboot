package com.citasmedicas.api.mappers;

import com.citasmedicas.api.dtos.HorarioDTO;
import com.citasmedicas.api.models.Doctor;
import com.citasmedicas.api.models.Horario;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2025-10-28T18:02:18-0600",
    comments = "version: 1.5.5.Final, compiler: javac, environment: Java 17.0.12 (Oracle Corporation)"
)
@Component
public class HorarioMapperImpl implements HorarioMapper {

    @Override
    public HorarioDTO toDto(Horario horario) {
        if ( horario == null ) {
            return null;
        }

        HorarioDTO horarioDTO = new HorarioDTO();

        horarioDTO.setDoctorId( horarioDoctorId( horario ) );
        horarioDTO.setId( horario.getId() );
        horarioDTO.setDiaDeLaSemana( horario.getDiaDeLaSemana() );
        horarioDTO.setHoraInicio( horario.getHoraInicio() );
        horarioDTO.setHoraFin( horario.getHoraFin() );

        return horarioDTO;
    }

    @Override
    public Horario toEntity(HorarioDTO horarioDTO) {
        if ( horarioDTO == null ) {
            return null;
        }

        Horario horario = new Horario();

        horario.setDoctor( horarioDTOToDoctor( horarioDTO ) );
        horario.setId( horarioDTO.getId() );
        horario.setDiaDeLaSemana( horarioDTO.getDiaDeLaSemana() );
        horario.setHoraInicio( horarioDTO.getHoraInicio() );
        horario.setHoraFin( horarioDTO.getHoraFin() );

        return horario;
    }

    private Long horarioDoctorId(Horario horario) {
        if ( horario == null ) {
            return null;
        }
        Doctor doctor = horario.getDoctor();
        if ( doctor == null ) {
            return null;
        }
        Long id = doctor.getId();
        if ( id == null ) {
            return null;
        }
        return id;
    }

    protected Doctor horarioDTOToDoctor(HorarioDTO horarioDTO) {
        if ( horarioDTO == null ) {
            return null;
        }

        Doctor doctor = new Doctor();

        doctor.setId( horarioDTO.getDoctorId() );

        return doctor;
    }
}
