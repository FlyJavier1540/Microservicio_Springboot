package com.citasmedicas.api.controllers;

import com.citasmedicas.api.dtos.CitaAprobacionDTO;
import com.citasmedicas.api.dtos.CitaDTO;
import com.citasmedicas.api.dtos.CitaPospuestaDTO;
import com.citasmedicas.api.dtos.CitaSolicitudDTO;
import com.citasmedicas.api.enums.EstadoCita;
import com.citasmedicas.api.models.Usuario;
import com.citasmedicas.api.services.CitaService;
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

    @PostMapping("/solicitar")
    @PreAuthorize("hasRole('PACIENTE')")
    public ResponseEntity<CitaDTO> solicitarCita(@Valid @RequestBody CitaSolicitudDTO solicitudDTO, Authentication authentication) {
        Usuario usuarioPaciente = (Usuario) authentication.getPrincipal();
        return new ResponseEntity<>(citaService.solicitarCita(solicitudDTO, usuarioPaciente), HttpStatus.CREATED);
    }

    // El endpoint de aprobación ahora recibe un cuerpo (Body) con la hora
    @PatchMapping("/{id}/aprobar")
    @PreAuthorize("hasAnyRole('ADMIN', 'DOCTOR')")
    public ResponseEntity<CitaDTO> aprobarCita(@PathVariable Long id,
                                               @Valid @RequestBody CitaAprobacionDTO aprobacionDTO,
                                               Authentication authentication) {
        Usuario usuarioAutenticado = (Usuario) authentication.getPrincipal();
        return ResponseEntity.ok(citaService.aprobarCita(id, aprobacionDTO, usuarioAutenticado));
    }

    @GetMapping("/solicitudes")
    @PreAuthorize("hasAnyRole('ADMIN', 'DOCTOR')")
    public ResponseEntity<List<CitaDTO>> getCitasSolicitadas(Authentication authentication) {
        Usuario usuarioAutenticado = (Usuario) authentication.getPrincipal();
        return ResponseEntity.ok(citaService.obtenerSolicitudesPorDoctor(usuarioAutenticado));
    }

    @GetMapping("/mis-citas")
    @PreAuthorize("hasRole('PACIENTE')")
    public ResponseEntity<List<CitaDTO>> getMisCitas(Authentication authentication) {
        Usuario usuarioAutenticado = (Usuario) authentication.getPrincipal();
        return ResponseEntity.ok(citaService.obtenerCitasDelPacienteAutenticado(usuarioAutenticado));
    }

    @PatchMapping("/{id}/rechazar")
    @PreAuthorize("hasAnyRole('ADMIN', 'DOCTOR')")
    public ResponseEntity<CitaDTO> rechazarCita(@PathVariable Long id, Authentication authentication) {
        Usuario usuarioAutenticado = (Usuario) authentication.getPrincipal();
        return ResponseEntity.ok(citaService.cambiarEstadoCita(id, EstadoCita.RECHAZADA, usuarioAutenticado));
    }

    @PatchMapping("/{id}/completar")
    @PreAuthorize("hasAnyRole('ADMIN', 'DOCTOR')")
    public ResponseEntity<CitaDTO> completarCita(@PathVariable Long id, Authentication authentication) {
        Usuario usuarioAutenticado = (Usuario) authentication.getPrincipal();
        return ResponseEntity.ok(citaService.cambiarEstadoCita(id, EstadoCita.COMPLETADA, usuarioAutenticado));
    }

    @PatchMapping("/{id}/posponer")
    @PreAuthorize("hasAnyRole('ADMIN', 'DOCTOR')")
    public ResponseEntity<CitaDTO> posponerCita(@PathVariable Long id,
                                                @Valid @RequestBody CitaPospuestaDTO pospuestaDTO,
                                                Authentication authentication) {
        Usuario usuarioAutenticado = (Usuario) authentication.getPrincipal();
        return ResponseEntity.ok(citaService.posponerCita(id, pospuestaDTO, usuarioAutenticado));
    }

    @GetMapping("/mi-historial")
    @PreAuthorize("hasRole('DOCTOR')")
    public ResponseEntity<List<CitaDTO>> getMiHistorialDeCitas(Authentication authentication) {
        Usuario usuarioAutenticado = (Usuario) authentication.getPrincipal();
        return ResponseEntity.ok(citaService.obtenerCitasCompletadasPorDoctor(usuarioAutenticado));
    }

    // Endpoint para que un admin vea el historial completo de citas
    @GetMapping("/historial-completo")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<CitaDTO>> getHistorialCompletoDeCitas() {
        return ResponseEntity.ok(citaService.obtenerTodasLasCitasCompletadas());
    }
}