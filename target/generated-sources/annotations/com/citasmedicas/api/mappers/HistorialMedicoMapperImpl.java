package com.citasmedicas.api.mappers;

import com.citasmedicas.api.dtos.HistorialMedicoDTO;
import com.citasmedicas.api.models.Cita;
import com.citasmedicas.api.models.Doctor;
import com.citasmedicas.api.models.HistorialMedico;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2025-10-28T18:02:17-0600",
    comments = "version: 1.5.5.Final, compiler: javac, environment: Java 17.0.12 (Oracle Corporation)"
)
@Component
public class HistorialMedicoMapperImpl implements HistorialMedicoMapper {

    @Override
    public HistorialMedicoDTO toDto(HistorialMedico historialMedico) {
        if ( historialMedico == null ) {
            return null;
        }

        HistorialMedicoDTO historialMedicoDTO = new HistorialMedicoDTO();

        historialMedicoDTO.setCitaId( historialMedicoCitaId( historialMedico ) );
        historialMedicoDTO.setDoctorNombre( historialMedicoDoctorNombreCompleto( historialMedico ) );
        historialMedicoDTO.setId( historialMedico.getId() );
        historialMedicoDTO.setDiagnostico( historialMedico.getDiagnostico() );
        historialMedicoDTO.setTratamiento( historialMedico.getTratamiento() );
        historialMedicoDTO.setNotas( historialMedico.getNotas() );

        return historialMedicoDTO;
    }

    @Override
    public HistorialMedico toEntity(HistorialMedicoDTO historialMedicoDTO) {
        if ( historialMedicoDTO == null ) {
            return null;
        }

        HistorialMedico historialMedico = new HistorialMedico();

        historialMedico.setCita( historialMedicoDTOToCita( historialMedicoDTO ) );
        historialMedico.setId( historialMedicoDTO.getId() );
        historialMedico.setDiagnostico( historialMedicoDTO.getDiagnostico() );
        historialMedico.setTratamiento( historialMedicoDTO.getTratamiento() );
        historialMedico.setNotas( historialMedicoDTO.getNotas() );

        return historialMedico;
    }

    private Long historialMedicoCitaId(HistorialMedico historialMedico) {
        if ( historialMedico == null ) {
            return null;
        }
        Cita cita = historialMedico.getCita();
        if ( cita == null ) {
            return null;
        }
        Long id = cita.getId();
        if ( id == null ) {
            return null;
        }
        return id;
    }

    private String historialMedicoDoctorNombreCompleto(HistorialMedico historialMedico) {
        if ( historialMedico == null ) {
            return null;
        }
        Doctor doctor = historialMedico.getDoctor();
        if ( doctor == null ) {
            return null;
        }
        String nombreCompleto = doctor.getNombreCompleto();
        if ( nombreCompleto == null ) {
            return null;
        }
        return nombreCompleto;
    }

    protected Cita historialMedicoDTOToCita(HistorialMedicoDTO historialMedicoDTO) {
        if ( historialMedicoDTO == null ) {
            return null;
        }

        Cita cita = new Cita();

        cita.setId( historialMedicoDTO.getCitaId() );

        return cita;
    }
}
