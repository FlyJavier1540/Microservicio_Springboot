package citasmedicas.controller;

import citasmedicas.dto.DoctorDTO;
import citasmedicas.model.Doctor;
import citasmedicas.service.DoctorService;
import citasmedicas.exception.ResourceNotFoundException;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/doctores")
public class DoctorController {

    @Autowired
    private DoctorService service;

    // Métodos de Mapeo (puedes moverlos a una clase Mapper dedicada después)
    private DoctorDTO convertirADto(Doctor doctor) {
        DoctorDTO dto = new DoctorDTO();
        dto.setId(doctor.getId());
        dto.setNombre(doctor.getNombre());
        dto.setEspecialidad(doctor.getEspecialidad());
        dto.setCorreo(doctor.getCorreo());
        dto.setTelefono(doctor.getTelefono());
        return dto;
    }

    private Doctor convertirAEntidad(DoctorDTO dto) {
        Doctor doctor = new Doctor();
        // No seteamos el ID al crear, la BD lo genera
        doctor.setNombre(dto.getNombre());
        doctor.setEspecialidad(dto.getEspecialidad());
        doctor.setCorreo(dto.getCorreo());
        doctor.setTelefono(dto.getTelefono());
        return doctor;
    }

    @GetMapping
    public ResponseEntity<List<DoctorDTO>> listarTodos() {
        List<DoctorDTO> listaDtos = service.listarTodos().stream()
                .map(this::convertirADto)
                .collect(Collectors.toList());
        return ResponseEntity.ok(listaDtos);
    }

    @GetMapping("/{id}")
    public ResponseEntity<DoctorDTO> buscarPorId(@PathVariable Long id) {
        return service.buscarPorId(id)
                .map(doctor -> ResponseEntity.ok(convertirADto(doctor)))
                .orElseThrow(() -> new ResourceNotFoundException("Doctor no encontrado con id: " + id));
    }

    @PostMapping
    public ResponseEntity<DoctorDTO> crear(@Valid @RequestBody DoctorDTO doctorDTO) {
        Doctor nuevoDoctor = service.crear(convertirAEntidad(doctorDTO));
        return new ResponseEntity<>(convertirADto(nuevoDoctor), HttpStatus.CREATED); // Usamos 201 Created
    }

    @PutMapping("/{id}")
    public ResponseEntity<DoctorDTO> actualizar(@PathVariable Long id, @Valid @RequestBody DoctorDTO doctorDTO) {
        Doctor doctorActualizado = service.actualizar(id, convertirAEntidad(doctorDTO));
        return ResponseEntity.ok(convertirADto(doctorActualizado));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        service.eliminar(id);
        return ResponseEntity.noContent().build(); // Usamos 204 No Content
    }
}