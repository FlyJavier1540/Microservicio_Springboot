package com.citasmedicas.api.controllers;

import com.citasmedicas.api.dtos.HorarioDTO;
import com.citasmedicas.api.models.Usuario;
import com.citasmedicas.api.services.HorarioService;
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

    // Endpoint para que un doctor añada un nuevo bloque a su horario
    @PostMapping
    @PreAuthorize("hasRole('DOCTOR')")
    public ResponseEntity<HorarioDTO> addHorario(@Valid @RequestBody HorarioDTO horarioDTO, Authentication authentication) {
        Usuario usuarioDoctor = (Usuario) authentication.getPrincipal();
        return new ResponseEntity<>(horarioService.agregarHorario(horarioDTO, usuarioDoctor), HttpStatus.CREATED);
    }

    // Endpoint para que un doctor consulte su propio horario
    @GetMapping("/mi-horario")
    @PreAuthorize("hasRole('DOCTOR')")
    public ResponseEntity<List<HorarioDTO>> getMiHorario(Authentication authentication) {
        Usuario usuarioDoctor = (Usuario) authentication.getPrincipal();
        return ResponseEntity.ok(horarioService.obtenerHorariosPorDoctor(usuarioDoctor));
    }
}