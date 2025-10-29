package com.citasmedicas.api.controllers;

import com.citasmedicas.api.dtos.PacienteDTO;
import com.citasmedicas.api.services.PacienteService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/pacientes")
@RequiredArgsConstructor
public class PacienteController {

    private final PacienteService pacienteService;

    @Operation(summary = "Obtiene una lista de todos los pacientes del sistema (Rol: ADMIN)")
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<PacienteDTO>> getAllPacientes() {
        return ResponseEntity.ok(pacienteService.obtenerTodosLosPacientes());
    }

    @Operation(summary = "Obtiene los detalles de un paciente por su ID (Roles: ADMIN)")
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or @securityService.esPropietarioDeCuentaPaciente(authentication, #id)")
    public ResponseEntity<PacienteDTO> getPacienteById(@PathVariable Long id) {
        return ResponseEntity.ok(pacienteService.obtenerPacientePorId(id));
    }

    @Operation(summary = "Elimina un paciente del sistema (Rol: ADMIN)")
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> eliminarPaciente(@PathVariable Long id) {
        pacienteService.eliminarPaciente(id);
        return ResponseEntity.noContent().build();
    }
}