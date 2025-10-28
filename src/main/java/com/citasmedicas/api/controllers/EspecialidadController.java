package com.citasmedicas.api.controllers;

import com.citasmedicas.api.dtos.EspecialidadDTO;
import com.citasmedicas.api.services.EspecialidadService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/especialidades")
@RequiredArgsConstructor
public class EspecialidadController {

    private final EspecialidadService especialidadService;

    @Operation(summary = "Obtiene una lista de todas las especialidades médicas")
    @GetMapping
    public ResponseEntity<List<EspecialidadDTO>> getAllEspecialidades() {
        return ResponseEntity.ok(especialidadService.obtenerTodas());
    }

    @Operation(summary = "Crea una nueva especialidad médica (Rol: ADMIN)")
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<EspecialidadDTO> createEspecialidad(@Valid @RequestBody EspecialidadDTO especialidadDTO) {
        return new ResponseEntity<>(especialidadService.crearEspecialidad(especialidadDTO), HttpStatus.CREATED);
    }

    @Operation(summary = "Obtiene los detalles de una especialidad por su ID")
    @GetMapping("/{id}")
    public ResponseEntity<EspecialidadDTO> getEspecialidadById(@PathVariable Long id) {
        return ResponseEntity.ok(especialidadService.obtenerPorId(id));
    }

    @Operation(summary = "Elimina una especialidad por su ID (Rol: ADMIN)")
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteEspecialidad(@PathVariable Long id) {
        especialidadService.eliminarEspecialidad(id);
        return ResponseEntity.noContent().build();
    }
}