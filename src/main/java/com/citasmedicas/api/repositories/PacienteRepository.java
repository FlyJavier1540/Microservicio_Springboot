package com.citasmedicas.api.repositories;

import com.citasmedicas.api.models.Paciente;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PacienteRepository extends JpaRepository<Paciente, Long> {
    Optional<Paciente> findByUsuarioId(Long usuarioId);
}