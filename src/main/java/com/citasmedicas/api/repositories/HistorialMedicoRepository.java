package com.citasmedicas.api.repositories;

import com.citasmedicas.api.models.HistorialMedico;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface HistorialMedicoRepository extends JpaRepository<HistorialMedico, Long> {

    List<HistorialMedico> findAllByPacienteId(Long pacienteId);
}