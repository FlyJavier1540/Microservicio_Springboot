package citasmedicas.service;

import citasmedicas.model.Cita;

import java.util.List;
import java.util.Optional;

public interface CitaService {
    List<Cita> listarTodas();
    Optional<Cita> buscarPorId(Long id);
    Cita crear(Cita cita);
    Cita actualizar(Long id, Cita datos);
    void eliminar(Long id);
}
