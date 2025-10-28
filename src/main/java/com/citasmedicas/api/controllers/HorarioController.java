package com.citasmedicas.api.controllers;

import com.citasmedicas.api.dtos.HorarioDTO;
import com.citasmedicas.api.models.Usuario;
import com.citasmedicas.api.services.HorarioService;
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
@RequestMapping("/api/horarios")
@RequiredArgsConstructor
public class HorarioController {

    private final HorarioService horarioService;

    @Operation(summary = "Permite a un doctor añadir un nuevo bloque a su horario de trabajo (Rol: DOCTOR)")
    @PostMapping
    @PreAuthorize("hasRole('DOCTOR')")
    public ResponseEntity<HorarioDTO> addHorario(@Valid @RequestBody HorarioDTO horarioDTO, Authentication authentication) {
        Usuario usuarioDoctor = (Usuario) authentication.getPrincipal();
        return new ResponseEntity<>(horarioService.agregarHorario(horarioDTO, usuarioDoctor), HttpStatus.CREATED);
    }

    @Operation(summary = "Permite a un doctor consultar su propio horario de trabajo (Rol: DOCTOR)")
    @GetMapping("/mi-horario")
    @PreAuthorize("hasRole('DOCTOR')")
    public ResponseEntity<List<HorarioDTO>> getMiHorario(Authentication authentication) {
        Usuario usuarioDoctor = (Usuario) authentication.getPrincipal();
        return ResponseEntity.ok(horarioService.obtenerHorariosPorDoctor(usuarioDoctor));
    }

    @Operation(summary = "Elimina un bloque del horario de trabajo del doctor autenticado (Rol: DOCTOR)")
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('DOCTOR')")
    public ResponseEntity<Void> eliminarHorario(@PathVariable Long id, Authentication authentication) {
        Usuario usuarioDoctor = (Usuario) authentication.getPrincipal();
        horarioService.eliminarHorario(id, usuarioDoctor);
        return ResponseEntity.noContent().build();
    }
}