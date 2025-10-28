package com.citasmedicas.api.controllers;

import com.citasmedicas.api.dtos.EspecialidadDTO;
import com.citasmedicas.api.services.EspecialidadService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/especialidades")
@RequiredArgsConstructor
public class EspecialidadController {

    private final EspecialidadService especialidadService;

    @GetMapping
    public ResponseEntity<List<EspecialidadDTO>> getAllEspecialidades() {
        return ResponseEntity.ok(especialidadService.obtenerTodas());
    }

    @PostMapping
    public ResponseEntity<EspecialidadDTO> createEspecialidad(@Valid @RequestBody EspecialidadDTO especialidadDTO) {
        return new ResponseEntity<>(especialidadService.crearEspecialidad(especialidadDTO), HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<EspecialidadDTO> getEspecialidadById(@PathVariable Long id) {
        return ResponseEntity.ok(especialidadService.obtenerPorId(id));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteEspecialidad(@PathVariable Long id) {
        especialidadService.eliminarEspecialidad(id);
        return ResponseEntity.noContent().build();
    }
}