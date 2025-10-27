package citasmedicas.service;

import citasmedicas.model.Doctor;

import java.util.List;
import java.util.Optional;

public interface DoctorService {
    List<Doctor> listarTodos();
    Optional<Doctor> buscarPorId(Long id);
    Doctor crear(Doctor doctor);
    Doctor actualizar(Long id, Doctor datos);
    void eliminar(Long id);
}
