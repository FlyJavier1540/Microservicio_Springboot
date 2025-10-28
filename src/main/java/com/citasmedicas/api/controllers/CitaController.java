package com.citasmedicas.api.controllers;

import com.citasmedicas.api.dtos.CitaDTO;
import com.citasmedicas.api.enums.EstadoCita;
import com.citasmedicas.api.services.CitaService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/citas")
@RequiredArgsConstructor
public class CitaController {

    private final CitaService citaService;

    // Endpoint para que un paciente solicite una nueva cita
    @PostMapping("/solicitar")
    @PreAuthorize("hasRole('PACIENTE')")
    public ResponseEntity<CitaDTO> solicitarCita(@Valid @RequestBody CitaDTO citaDTO) {
        return new ResponseEntity<>(citaService.solicitarCita(citaDTO), HttpStatus.CREATED);
    }

    // Endpoint para que un admin o doctor vea las citas solicitadas
    @GetMapping("/solicitudes")
    @PreAuthorize("hasAnyRole('ADMIN', 'DOCTOR')")
    public ResponseEntity<List<CitaDTO>> getCitasSolicitadas() {
        return ResponseEntity.ok(citaService.obtenerCitasPorEstado(EstadoCita.SOLICITADA));
    }

    // Endpoint para aprobar una cita
    @PatchMapping("/{id}/aprobar")
    @PreAuthorize("hasAnyRole('ADMIN', 'DOCTOR')")
    public ResponseEntity<CitaDTO> aprobarCita(@PathVariable Long id) {
        return ResponseEntity.ok(citaService.cambiarEstadoCita(id, EstadoCita.PROGRAMADA));
    }

    // Endpoint para rechazar una cita
    @PatchMapping("/{id}/rechazar")
    @PreAuthorize("hasAnyRole('ADMIN', 'DOCTOR')")
    public ResponseEntity<CitaDTO> rechazarCita(@PathVariable Long id) {
        return ResponseEntity.ok(citaService.cambiarEstadoCita(id, EstadoCita.RECHAZADA));
    }

    // Endpoint para que un paciente vea sus propias citas
    @GetMapping("/mis-citas/{pacienteId}")
    @PreAuthorize("hasRole('PACIENTE')") // Aquí se podría añadir validación para que solo vea las suyas
    public ResponseEntity<List<CitaDTO>> getMisCitas(@PathVariable Long pacienteId) {
        return ResponseEntity.ok(citaService.obtenerCitasPorPaciente(pacienteId));
    }
}