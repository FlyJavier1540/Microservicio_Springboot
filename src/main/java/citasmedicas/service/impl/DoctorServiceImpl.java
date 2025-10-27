package citasmedicas.service.impl;

import citasmedicas.model.Doctor;
import citasmedicas.repository.DoctorRepository;
import citasmedicas.service.DoctorService;
import citasmedicas.exception.ResourceNotFoundException; // ¡Importante!
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class DoctorServiceImpl implements DoctorService {

    @Autowired
    private DoctorRepository repository;

    @Override
    public List<Doctor> listarTodos() {
        return repository.findAll();
    }

    @Override
    public Optional<Doctor> buscarPorId(Long id) {
        return repository.findById(id);
    }

    @Override
    public Doctor crear(Doctor doctor) {
        return repository.save(doctor);
    }

    @Override
    public Doctor actualizar(Long id, Doctor datos) {
        // Buscamos el doctor por ID, si no existe, lanzamos nuestra excepción personalizada
        Doctor doctorExistente = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Doctor no encontrado con id: " + id));

        doctorExistente.setNombre(datos.getNombre());
        doctorExistente.setEspecialidad(datos.getEspecialidad());
        doctorExistente.setCorreo(datos.getCorreo());
        doctorExistente.setTelefono(datos.getTelefono());
        return repository.save(doctorExistente);
    }

    @Override
    public void eliminar(Long id) {
        // Verificamos que el doctor exista antes de intentar borrarlo
        if (!repository.existsById(id)) {
            throw new ResourceNotFoundException("Doctor no encontrado con id: " + id);
        }
        repository.deleteById(id);
    }
}