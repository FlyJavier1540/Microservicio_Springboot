package com.citasmedicas.api.services;

import com.citasmedicas.api.dtos.CitaAprobacionDTO;
import com.citasmedicas.api.dtos.CitaDTO;
import com.citasmedicas.api.dtos.CitaPospuestaDTO;
import com.citasmedicas.api.dtos.CitaSolicitudDTO;
import com.citasmedicas.api.enums.DiaDeLaSemana;
import com.citasmedicas.api.enums.EstadoCita;
import com.citasmedicas.api.exceptions.ResourceNotFoundException;
import com.citasmedicas.api.mappers.CitaMapper;
import com.citasmedicas.api.models.*;
import com.citasmedicas.api.repositories.CitaRepository;
import com.citasmedicas.api.repositories.DoctorRepository;
import com.citasmedicas.api.repositories.HorarioRepository;
import com.citasmedicas.api.repositories.PacienteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CitaService {

    private final CitaRepository citaRepository;
    private final PacienteRepository pacienteRepository;
    private final DoctorRepository doctorRepository;
    private final HorarioRepository horarioRepository;
    private final CitaMapper citaMapper;
    // La inyección de EmailService ha sido eliminada
    private static final int DURACION_CITA_MINUTOS = 30;

    private void validarDisponibilidad(Doctor doctor, LocalDateTime fechaHoraCita) {
        DayOfWeek diaDeLaSemanaJava = fechaHoraCita.getDayOfWeek();
        LocalTime horaCita = fechaHoraCita.toLocalTime();

        DiaDeLaSemana diaDeLaSemanaApp;
        try {
            diaDeLaSemanaApp = DiaDeLaSemana.valueOf(diaDeLaSemanaJava.name());
        } catch (IllegalArgumentException e) {
            switch(diaDeLaSemanaJava) {
                case SUNDAY: diaDeLaSemanaApp = DiaDeLaSemana.DOMINGO; break;
                case MONDAY: diaDeLaSemanaApp = DiaDeLaSemana.LUNES; break;
                case TUESDAY: diaDeLaSemanaApp = DiaDeLaSemana.MARTES; break;
                case WEDNESDAY: diaDeLaSemanaApp = DiaDeLaSemana.MIERCOLES; break;
                case THURSDAY: diaDeLaSemanaApp = DiaDeLaSemana.JUEVES; break;
                case FRIDAY: diaDeLaSemanaApp = DiaDeLaSemana.VIERNES; break;
                case SATURDAY: diaDeLaSemanaApp = DiaDeLaSemana.SABADO; break;
                default: throw new IllegalStateException("Día de la semana no mapeado: " + diaDeLaSemanaJava);
            }
        }

        final DiaDeLaSemana finalDiaDeLaSemanaApp = diaDeLaSemanaApp;

        List<Horario> horariosDoctor = horarioRepository.findAllByDoctorId(doctor.getId());

        boolean dentroDeHorario = horariosDoctor.stream().anyMatch(h ->
                h.getDiaDeLaSemana() == finalDiaDeLaSemanaApp &&
                        !horaCita.isBefore(h.getHoraInicio()) &&
                        !horaCita.isAfter(h.getHoraFin().minusMinutes(DURACION_CITA_MINUTOS))
        );

        if (!dentroDeHorario) {
            throw new IllegalArgumentException("El horario solicitado está fuera de las horas de trabajo del doctor.");
        }

        LocalDateTime horaFinCita = fechaHoraCita.plusMinutes(DURACION_CITA_MINUTOS);
        List<Cita> citasSolapadas = citaRepository.findByDoctorIdAndFechaHoraBetween(
                doctor.getId(),
                fechaHoraCita.minusMinutes(DURACION_CITA_MINUTOS - 1),
                horaFinCita.minusMinutes(1)
        );

        if (!citasSolapadas.isEmpty()) {
            throw new IllegalArgumentException("Ya existe una cita programada en el horario seleccionado.");
        }
    }

    @Transactional
    public CitaDTO solicitarCita(CitaSolicitudDTO solicitudDTO, Usuario usuarioPaciente) {
        Paciente paciente = pacienteRepository.findByUsuarioId(usuarioPaciente.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Perfil de Paciente no encontrado para el usuario."));
        Doctor doctor = doctorRepository.findById(solicitudDTO.getDoctorId())
                .orElseThrow(() -> new ResourceNotFoundException("Doctor no encontrado con id: " + solicitudDTO.getDoctorId()));

        Cita nuevaCita = new Cita();
        nuevaCita.setPaciente(paciente);
        nuevaCita.setDoctor(doctor);
        nuevaCita.setMotivoConsulta(solicitudDTO.getMotivoConsulta());
        nuevaCita.setFechaHora(solicitudDTO.getFecha().atStartOfDay());
        nuevaCita.setEstado(EstadoCita.SOLICITADA);

        Cita citaGuardada = citaRepository.save(nuevaCita);
        return citaMapper.toDto(citaGuardada);
    }

    @Transactional
    public CitaDTO aprobarCita(Long id, CitaAprobacionDTO aprobacionDTO, Usuario usuarioAutenticado) {
        Cita cita = citaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Cita no encontrada con id: " + id));

        boolean isAdmin = usuarioAutenticado.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
        if (!isAdmin) {
            Doctor doctor = doctorRepository.findByUsuarioId(usuarioAutenticado.getId()).orElseThrow(() -> new ResourceNotFoundException("Perfil de Doctor no encontrado."));
            if (!cita.getDoctor().getId().equals(doctor.getId())) {
                throw new AccessDeniedException("No tiene permiso para modificar esta cita.");
            }
        }

        LocalDateTime fechaHoraFinal = cita.getFechaHora().toLocalDate().atTime(aprobacionDTO.getHora());
        validarDisponibilidad(cita.getDoctor(), fechaHoraFinal);

        cita.setFechaHora(fechaHoraFinal);
        cita.setEstado(EstadoCita.PROGRAMADA);

        Cita citaActualizada = citaRepository.save(cita);
        // La llamada a emailService ha sido eliminada
        return citaMapper.toDto(citaActualizada);
    }

    @Transactional
    public CitaDTO posponerCita(Long id, CitaPospuestaDTO pospuestaDTO, Usuario usuarioAutenticado) {
        Cita cita = citaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Cita no encontrada con id: " + id));

        boolean isAdmin = usuarioAutenticado.getAuthorities().stream().anyMatch(grantedAuthority -> grantedAuthority.getAuthority().equals("ROLE_ADMIN"));
        if (!isAdmin) {
            Doctor doctor = doctorRepository.findByUsuarioId(usuarioAutenticado.getId())
                    .orElseThrow(() -> new ResourceNotFoundException("Perfil de Doctor no encontrado para el usuario."));
            if (!cita.getDoctor().getId().equals(doctor.getId())) {
                throw new AccessDeniedException("No tiene permiso para posponer esta cita.");
            }
        }

        validarDisponibilidad(cita.getDoctor(), pospuestaDTO.getNuevaFechaHora());

        cita.setFechaHora(pospuestaDTO.getNuevaFechaHora());
        cita.setEstado(EstadoCita.PROGRAMADA);

        Cita citaActualizada = citaRepository.save(cita);
        // La llamada a emailService ha sido eliminada
        return citaMapper.toDto(citaActualizada);
    }

    @Transactional
    public CitaDTO cambiarEstadoCita(Long id, EstadoCita nuevoEstado, Usuario usuarioAutenticado) {
        Cita cita = citaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Cita no encontrada con id: " + id));

        boolean isAdmin = usuarioAutenticado.getAuthorities().stream()
                .anyMatch(grantedAuthority -> grantedAuthority.getAuthority().equals("ROLE_ADMIN"));

        if (!isAdmin) {
            Doctor doctor = doctorRepository.findByUsuarioId(usuarioAutenticado.getId())
                    .orElseThrow(() -> new ResourceNotFoundException("Perfil de Doctor no encontrado para el usuario."));

            if (!cita.getDoctor().getId().equals(doctor.getId())) {
                throw new AccessDeniedException("No tiene permiso para modificar esta cita.");
            }
        }

        cita.setEstado(nuevoEstado);
        Cita citaActualizada = citaRepository.save(cita);

        // La llamada a emailService ha sido eliminada

        return citaMapper.toDto(citaActualizada);
    }

    // ... (El resto de los métodos no necesitan cambios)
    public List<CitaDTO> obtenerSolicitudesPorDoctor(Usuario usuarioAutenticado) {
        Doctor doctor = doctorRepository.findByUsuarioId(usuarioAutenticado.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Perfil de Doctor no encontrado para el usuario."));

        return citaRepository.findAllByDoctorIdAndEstado(doctor.getId(), EstadoCita.SOLICITADA).stream()
                .map(citaMapper::toDto)
                .collect(Collectors.toList());
    }

    public List<CitaDTO> obtenerCitasCompletadasPorDoctor(Usuario usuarioAutenticado) {
        Doctor doctor = doctorRepository.findByUsuarioId(usuarioAutenticado.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Perfil de Doctor no encontrado para el usuario."));

        return citaRepository.findAllByDoctorIdAndEstado(doctor.getId(), EstadoCita.COMPLETADA).stream()
                .map(citaMapper::toDto)
                .collect(Collectors.toList());
    }

    public List<CitaDTO> obtenerTodasLasCitasCompletadas() {
        return citaRepository.findAllByEstado(EstadoCita.COMPLETADA).stream()
                .map(citaMapper::toDto)
                .collect(Collectors.toList());
    }

    public List<CitaDTO> obtenerCitasDelPacienteAutenticado(Usuario usuarioAutenticado) {
        Paciente paciente = pacienteRepository.findByUsuarioId(usuarioAutenticado.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Perfil de Paciente no encontrado para el usuario."));

        return citaRepository.findAllByPacienteId(paciente.getId()).stream()
                .map(citaMapper::toDto)
                .collect(Collectors.toList());
    }
}