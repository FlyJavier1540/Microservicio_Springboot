package com.citasmedicas.api.repositories;

import com.citasmedicas.api.models.Horario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface HorarioRepository extends JpaRepository<Horario, Long> {

    List<Horario> findAllByDoctorId(Long doctorId);
}