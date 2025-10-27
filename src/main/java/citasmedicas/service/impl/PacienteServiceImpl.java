package citasmedicas.service.impl;

import citasmedicas.model.Paciente;
import citasmedicas.repository.PacienteRepository;
import citasmedicas.service.PacienteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


import java.util.List;
import java.util.Optional;

@Service
public class PacienteServiceImpl implements PacienteService{
    @Autowired
    private PacienteRepository repository;

    @Override
    public List<Paciente> listarTodos(){
    return repository.findAll();
    }

    @Override
    public Optional<Paciente> buscarPorId(Long id){
        return repository.findById(id);
    }

    @Override
    public Paciente crear(Paciente paciente){
        return repository.save(paciente);
    }

    @Override
    public Paciente actualizar(Long id, Paciente datos){
        return repository.findById(id).map(p -> {
            p.setNombre(datos.getNombre());
            p.setCorreo(datos.getCorreo());
            p.setTelefono(datos.getTelefono());
            p.setFechaNacimiento(datos.getFechaNacimiento());
            return repository.save(p);
        }).orElseThrow(() -> new RuntimeException("Paciente no encontrado"));
    }

    @Override
    public void eliminar(Long id) {
        repository.deleteById(id);
    }
}