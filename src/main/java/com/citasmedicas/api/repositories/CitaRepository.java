package com.citasmedicas.api.repositories;

import com.citasmedicas.api.models.Cita;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CitaRepository extends JpaRepository<Cita, Long> {

    List<Cita> findAllByPacienteId(Long pacienteId);

    List<Cita> findAllByDoctorId(Long doctorId);
}