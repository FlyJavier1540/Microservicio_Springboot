package citasmedicas.service.impl;

import citasmedicas.model.Doctor;
import citasmedicas.repository.DoctorRepository;
import citasmedicas.service.DoctorService;
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
        return repository.findById(id).map(d -> {
            d.setNombre(datos.getNombre());
            d.setEspecialidad(datos.getEspecialidad());
            d.setCorreo(datos.getCorreo());
            d.setTelefono(datos.getTelefono());
            return repository.save(d);
        }).orElseThrow(() -> new RuntimeException("Doctor no encontrado"));
    }

    @Override
    public void eliminar(Long id) {
        repository.deleteById(id);
    }
}
