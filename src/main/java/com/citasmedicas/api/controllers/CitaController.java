package com.citasmedicas.api.controllers;

import com.citasmedicas.api.dtos.CitaDTO;
import com.citasmedicas.api.enums.EstadoCita;
import com.citasmedicas.api.services.CitaService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import com.citasmedicas.api.models.Usuario;
import org.springframework.security.core.Authentication;
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

    @GetMapping("/solicitudes")
    @PreAuthorize("hasAnyRole('ADMIN', 'DOCTOR')")
    public ResponseEntity<List<CitaDTO>> getCitasSolicitadas(Authentication authentication) {
        // Obtenemos el usuario autenticado desde el contexto de seguridad
        Usuario usuarioAutenticado = (Usuario) authentication.getPrincipal();

        // Si es ADMIN, podría ver todas (lógica a futuro).
        // Si es DOCTOR, solo verá las suyas.
        // Por ahora, asumimos que si no es admin, es doctor.
        // TODO: Añadir lógica para que el admin vea todas las solicitudes

        return ResponseEntity.ok(citaService.obtenerSolicitudesPorDoctor(usuarioAutenticado));
    }

    // --- NUEVO ENDPOINT SEGURO PARA PACIENTES ---
    @GetMapping("/mis-citas")
    @PreAuthorize("hasRole('PACIENTE')")
    public ResponseEntity<List<CitaDTO>> getMisCitas(Authentication authentication) {
        Usuario usuarioAutenticado = (Usuario) authentication.getPrincipal();
        return ResponseEntity.ok(citaService.obtenerCitasDelPacienteAutenticado(usuarioAutenticado));
    }

    // Endpoint para aprobar una cita
    @PatchMapping("/{id}/aprobar")
    @PreAuthorize("hasAnyRole('ADMIN', 'DOCTOR')")
    public ResponseEntity<CitaDTO> aprobarCita(@PathVariable Long id, Authentication authentication) {
        Usuario usuarioAutenticado = (Usuario) authentication.getPrincipal();
        return ResponseEntity.ok(citaService.cambiarEstadoCita(id, EstadoCita.PROGRAMADA, usuarioAutenticado));
    }

    // Endpoint para rechazar una cita
    @PatchMapping("/{id}/rechazar")
    @PreAuthorize("hasAnyRole('ADMIN', 'DOCTOR')")
    public ResponseEntity<CitaDTO> rechazarCita(@PathVariable Long id, Authentication authentication) {
        Usuario usuarioAutenticado = (Usuario) authentication.getPrincipal();
        return ResponseEntity.ok(citaService.cambiarEstadoCita(id, EstadoCita.RECHAZADA, usuarioAutenticado));
    }

    // Endpoint para que un paciente vea sus propias citas
    @GetMapping("/mis-citas/{pacienteId}")
    @PreAuthorize("hasRole('PACIENTE')") // Aquí se podría añadir validación para que solo vea las suyas
    public ResponseEntity<List<CitaDTO>> getMisCitas(@PathVariable Long pacienteId) {
        return ResponseEntity.ok(citaService.obtenerCitasPorPaciente(pacienteId));
    }

    @PatchMapping("/{id}/completar")
    @PreAuthorize("hasAnyRole('ADMIN', 'DOCTOR')")
    public ResponseEntity<CitaDTO> completarCita(@PathVariable Long id, Authentication authentication) {
        Usuario usuarioAutenticado = (Usuario) authentication.getPrincipal();
        return ResponseEntity.ok(citaService.cambiarEstadoCita(id, EstadoCita.COMPLETADA, usuarioAutenticado));
    }
}