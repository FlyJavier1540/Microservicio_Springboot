package com.citasmedicas.api.controllers;

import com.citasmedicas.api.dtos.DoctorDTO;
import com.citasmedicas.api.services.DoctorService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/doctores")
@RequiredArgsConstructor
public class DoctorController {

    private final DoctorService doctorService;

    // Endpoint para registrar un nuevo doctor. Solo accesible por administradores.
    @PostMapping("/register")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<DoctorDTO> registerDoctor(@Valid @RequestBody DoctorDTO doctorDTO) {
        // En un sistema real, la contraseña inicial se generaría de forma segura
        // y se enviaría al doctor por un canal seguro.
        String temporaryPassword = "password123";
        return new ResponseEntity<>(doctorService.registrarDoctor(doctorDTO, temporaryPassword), HttpStatus.CREATED);
    }

    // Endpoint para obtener todos los doctores. Accesible por cualquier usuario autenticado.
    @GetMapping
    public ResponseEntity<List<DoctorDTO>> getAllDoctores() {
        return ResponseEntity.ok(doctorService.obtenerTodosLosDoctores());
    }

    // Endpoint para obtener un doctor por su ID.
    @GetMapping("/{id}")
    public ResponseEntity<DoctorDTO> getDoctorById(@PathVariable Long id) {
        return ResponseEntity.ok(doctorService.obtenerDoctorPorId(id));
    }
}