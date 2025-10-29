package com.citasmedicas.api.repositories;

import com.citasmedicas.api.enums.EstadoCita;
import com.citasmedicas.api.models.Cita;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface CitaRepository extends JpaRepository<Cita, Long> {

    List<Cita> findAllByPacienteId(Long pacienteId);
    List<Cita> findAllByDoctorIdAndEstado(Long doctorId, EstadoCita estado);
    List<Cita> findAllByEstado(EstadoCita estado);
    List<Cita> findByDoctorIdAndFechaHoraBetween(Long doctorId, LocalDateTime start, LocalDateTime end);
}