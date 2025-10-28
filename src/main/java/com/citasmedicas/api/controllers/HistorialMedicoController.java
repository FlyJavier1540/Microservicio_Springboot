package com.citasmedicas.api.controllers;

import com.citasmedicas.api.dtos.HistorialMedicoDTO;
import com.citasmedicas.api.models.Usuario;
import com.citasmedicas.api.services.HistorialMedicoService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/historial")
@RequiredArgsConstructor
public class HistorialMedicoController {

    private final HistorialMedicoService historialMedicoService;

    @Operation(summary = "Crea un nuevo registro en el historial médico para una cita completada (Rol: DOCTOR)")
    @PostMapping
    @PreAuthorize("hasRole('DOCTOR')")
    public ResponseEntity<HistorialMedicoDTO> crearHistorial(
            @Valid @RequestBody HistorialMedicoDTO historialDTO,
            Authentication authentication) {
        Usuario usuarioDoctor = (Usuario) authentication.getPrincipal();
        return new ResponseEntity<>(historialMedicoService.crearHistorial(historialDTO, usuarioDoctor), HttpStatus.CREATED);
    }

    @Operation(summary = "Obtiene el historial médico completo de un paciente por su ID (Roles: ADMIN, DOCTOR)")
    @GetMapping("/paciente/{pacienteId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'DOCTOR')")
    public ResponseEntity<List<HistorialMedicoDTO>> getHistorialPorPaciente(@PathVariable Long pacienteId) {
        return ResponseEntity.ok(historialMedicoService.obtenerHistorialPorPaciente(pacienteId));
    }
}