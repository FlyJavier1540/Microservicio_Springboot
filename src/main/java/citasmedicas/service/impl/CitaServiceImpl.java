package citasmedicas.service.impl;

import citasmedicas.model.Cita;
import citasmedicas.repository.CitaRepository;
import citasmedicas.service.CitaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CitaServiceImpl implements CitaService {

    @Autowired
    private CitaRepository repository;

    @Override
    public List<Cita> listarTodas() {
        return repository.findAll();
    }

    @Override
    public Optional<Cita> buscarPorId(Long id) {
        return repository.findById(id);
    }

    @Override
    public Cita crear(Cita cita) {
        return repository.save(cita);
    }

    @Override
    public Cita actualizar(Long id, Cita datos) {
        return repository.findById(id).map(c -> {
            c.setPaciente(datos.getPaciente());
            c.setDoctor(datos.getDoctor());
            c.setFechaHora(datos.getFechaHora());
            c.setEstado(datos.getEstado());
            c.setNotas(datos.getNotas());
            return repository.save(c);
        }).orElseThrow(() -> new RuntimeException("Cita no encontrado"));
    }

    @Override
    public void eliminar(Long id) {
        repository.deleteById(id);
    }
}
