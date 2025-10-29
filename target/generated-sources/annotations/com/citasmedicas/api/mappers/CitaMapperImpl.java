package com.citasmedicas.api.mappers;

import com.citasmedicas.api.dtos.CitaDTO;
import com.citasmedicas.api.models.Cita;
import com.citasmedicas.api.models.Doctor;
import com.citasmedicas.api.models.Paciente;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2025-10-28T21:49:06-0600",
    comments = "version: 1.5.5.Final, compiler: javac, environment: Java 17.0.12 (Oracle Corporation)"
)
@Component
public class CitaMapperImpl implements CitaMapper {

    @Override
    public CitaDTO toDto(Cita cita) {
        if ( cita == null ) {
            return null;
        }

        CitaDTO citaDTO = new CitaDTO();

        citaDTO.setDoctorId( citaDoctorId( cita ) );
        citaDTO.setDoctorNombre( citaDoctorNombreCompleto( cita ) );
        citaDTO.setPacienteId( citaPacienteId( cita ) );
        citaDTO.setPacienteNombre( citaPacienteNombreCompleto( cita ) );
        citaDTO.setId( cita.getId() );
        citaDTO.setFechaHora( cita.getFechaHora() );
        citaDTO.setEstado( cita.getEstado() );
        citaDTO.setMotivoConsulta( cita.getMotivoConsulta() );

        return citaDTO;
    }

    @Override
    public Cita toEntity(CitaDTO citaDTO) {
        if ( citaDTO == null ) {
            return null;
        }

        Cita cita = new Cita();

        cita.setDoctor( citaDTOToDoctor( citaDTO ) );
        cita.setPaciente( citaDTOToPaciente( citaDTO ) );
        cita.setId( citaDTO.getId() );
        cita.setFechaHora( citaDTO.getFechaHora() );
        cita.setEstado( citaDTO.getEstado() );
        cita.setMotivoConsulta( citaDTO.getMotivoConsulta() );

        return cita;
    }

    private Long citaDoctorId(Cita cita) {
        if ( cita == null ) {
            return null;
        }
        Doctor doctor = cita.getDoctor();
        if ( doctor == null ) {
            return null;
        }
        Long id = doctor.getId();
        if ( id == null ) {
            return null;
        }
        return id;
    }

    private String citaDoctorNombreCompleto(Cita cita) {
        if ( cita == null ) {
            return null;
        }
        Doctor doctor = cita.getDoctor();
        if ( doctor == null ) {
            return null;
        }
        String nombreCompleto = doctor.getNombreCompleto();
        if ( nombreCompleto == null ) {
            return null;
        }
        return nombreCompleto;
    }

    private Long citaPacienteId(Cita cita) {
        if ( cita == null ) {
            return null;
        }
        Paciente paciente = cita.getPaciente();
        if ( paciente == null ) {
            return null;
        }
        Long id = paciente.getId();
        if ( id == null ) {
            return null;
        }
        return id;
    }

    private String citaPacienteNombreCompleto(Cita cita) {
        if ( cita == null ) {
            return null;
        }
        Paciente paciente = cita.getPaciente();
        if ( paciente == null ) {
            return null;
        }
        String nombreCompleto = paciente.getNombreCompleto();
        if ( nombreCompleto == null ) {
            return null;
        }
        return nombreCompleto;
    }

    protected Doctor citaDTOToDoctor(CitaDTO citaDTO) {
        if ( citaDTO == null ) {
            return null;
        }

        Doctor doctor = new Doctor();

        doctor.setId( citaDTO.getDoctorId() );

        return doctor;
    }

    protected Paciente citaDTOToPaciente(CitaDTO citaDTO) {
        if ( citaDTO == null ) {
            return null;
        }

        Paciente paciente = new Paciente();

        paciente.setId( citaDTO.getPacienteId() );

        return paciente;
    }
}
