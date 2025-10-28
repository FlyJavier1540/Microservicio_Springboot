package com.citasmedicas.api.controllers;

import com.citasmedicas.api.dtos.CitaAprobacionDTO;
import com.citasmedicas.api.dtos.CitaDTO;
import com.citasmedicas.api.dtos.CitaPospuestaDTO;
import com.citasmedicas.api.dtos.CitaSolicitudDTO;
import com.citasmedicas.api.enums.EstadoCita;
import com.citasmedicas.api.models.Usuario;
import com.citasmedicas.api.services.CitaService;
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
@RequestMapping("/api/citas")
@RequiredArgsConstructor
public class CitaController {

    private final CitaService citaService;

    @Operation(summary = "Permite a un paciente solicitar una nueva cita (Rol: PACIENTE)")
    @PostMapping("/solicitar")
    @PreAuthorize("hasRole('PACIENTE')")
    public ResponseEntity<CitaDTO> solicitarCita(@Valid @RequestBody CitaSolicitudDTO solicitudDTO, Authentication authentication) {
        Usuario usuarioPaciente = (Usuario) authentication.getPrincipal();
        return new ResponseEntity<>(citaService.solicitarCita(solicitudDTO, usuarioPaciente), HttpStatus.CREATED);
    }

    @Operation(summary = "Obtiene la lista de citas solicitadas pendientes (Roles: ADMIN, DOCTOR)")
    @GetMapping("/solicitudes")
    @PreAuthorize("hasAnyRole('ADMIN', 'DOCTOR')")
    public ResponseEntity<List<CitaDTO>> getCitasSolicitadas(Authentication authentication) {
        Usuario usuarioAutenticado = (Usuario) authentication.getPrincipal();
        return ResponseEntity.ok(citaService.obtenerSolicitudesPorDoctor(usuarioAutenticado));
    }

    @Operation(summary = "Obtiene todas las citas del paciente autenticado (Rol: PACIENTE)")
    @GetMapping("/mis-citas")
    @PreAuthorize("hasRole('PACIENTE')")
    public ResponseEntity<List<CitaDTO>> getMisCitas(Authentication authentication) {
        Usuario usuarioAutenticado = (Usuario) authentication.getPrincipal();
        return ResponseEntity.ok(citaService.obtenerCitasDelPacienteAutenticado(usuarioAutenticado));
    }

    @Operation(summary = "Aprueba una cita y establece la hora (Roles: ADMIN, DOCTOR)")
    @PatchMapping("/{id}/aprobar")
    @PreAuthorize("hasAnyRole('ADMIN', 'DOCTOR')")
    public ResponseEntity<CitaDTO> aprobarCita(@PathVariable Long id,
                                               @Valid @RequestBody CitaAprobacionDTO aprobacionDTO,
                                               Authentication authentication) {
        Usuario usuarioAutenticado = (Usuario) authentication.getPrincipal();
        return ResponseEntity.ok(citaService.aprobarCita(id, aprobacionDTO, usuarioAutenticado));
    }

    @Operation(summary = "Rechaza una solicitud de cita (Roles: ADMIN, DOCTOR)")
    @PatchMapping("/{id}/rechazar")
    @PreAuthorize("hasAnyRole('ADMIN', 'DOCTOR')")
    public ResponseEntity<CitaDTO> rechazarCita(@PathVariable Long id, Authentication authentication) {
        Usuario usuarioAutenticado = (Usuario) authentication.getPrincipal();
        return ResponseEntity.ok(citaService.cambiarEstadoCita(id, EstadoCita.RECHAZADA, usuarioAutenticado));
    }

    @Operation(summary = "Pospone o reagenda una cita a una nueva fecha y hora (Roles: ADMIN, DOCTOR)")
    @PatchMapping("/{id}/posponer")
    @PreAuthorize("hasAnyRole('ADMIN', 'DOCTOR')")
    public ResponseEntity<CitaDTO> posponerCita(@PathVariable Long id,
                                                @Valid @RequestBody CitaPospuestaDTO pospuestaDTO,
                                                Authentication authentication) {
        Usuario usuarioAutenticado = (Usuario) authentication.getPrincipal();
        return ResponseEntity.ok(citaService.posponerCita(id, pospuestaDTO, usuarioAutenticado));
    }

    @Operation(summary = "Marca una cita como completada (Roles: ADMIN, DOCTOR)")
    @PatchMapping("/{id}/completar")
    @PreAuthorize("hasAnyRole('ADMIN', 'DOCTOR')")
    public ResponseEntity<CitaDTO> completarCita(@PathVariable Long id, Authentication authentication) {
        Usuario usuarioAutenticado = (Usuario) authentication.getPrincipal();
        return ResponseEntity.ok(citaService.cambiarEstadoCita(id, EstadoCita.COMPLETADA, usuarioAutenticado));
    }

    @Operation(summary = "Obtiene el historial de citas completadas del doctor autenticado (Rol: DOCTOR)")
    @GetMapping("/mi-historial")
    @PreAuthorize("hasRole('DOCTOR')")
    public ResponseEntity<List<CitaDTO>> getMiHistorialDeCitas(Authentication authentication) {
        Usuario usuarioAutenticado = (Usuario) authentication.getPrincipal();
        return ResponseEntity.ok(citaService.obtenerCitasCompletadasPorDoctor(usuarioAutenticado));
    }

    @Operation(summary = "Obtiene el historial completo de todas las citas completadas en el sistema (Rol: ADMIN)")
    @GetMapping("/historial-completo")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<CitaDTO>> getHistorialCompletoDeCitas() {
        return ResponseEntity.ok(citaService.obtenerTodasLasCitasCompletadas());
    }

    @Operation(summary = "Elimina una cita del sistema (Rol: ADMIN)")
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> eliminarCita(@PathVariable Long id) {
        citaService.eliminarCita(id);
        return ResponseEntity.noContent().build();
    }
}