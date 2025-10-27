package citasmedicas.service;

import citasmedicas.model.Paciente;

import java.util.List;
import java.util.Optional;

public interface PacienteService {
    List<Paciente> listarTodos();
    Optional<Paciente> buscarPorId(Long id);
    Paciente crear(Paciente paciente);
    Paciente actualizar(Long id, Paciente datos);
    void eliminar(Long id);
}
