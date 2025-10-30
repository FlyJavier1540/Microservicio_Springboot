package com.citasmedicas.api.controllers;

import com.citasmedicas.api.dtos.DoctorDTO;
import com.citasmedicas.api.dtos.DoctorResponseDTO;
import com.citasmedicas.api.services.DoctorService;
import io.swagger.v3.oas.annotations.Operation;
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

    @Operation(summary = "Registra un nuevo doctor en el sistema (Rol: ADMIN)")
    @PostMapping("/register")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<DoctorResponseDTO> registerDoctor(@Valid @RequestBody DoctorDTO doctorDTO) {
        return new ResponseEntity<>(doctorService.registrarDoctor(doctorDTO), HttpStatus.CREATED);
    }


    @Operation(summary = "Obtiene una lista de todos los doctores")
    @GetMapping
    public ResponseEntity<List<DoctorResponseDTO>> getAllDoctores() {
        return ResponseEntity.ok(doctorService.obtenerTodosLosDoctores());
    }

    @Operation(summary = "Obtiene los detalles de un doctor por su ID")
    @GetMapping("/{id}")
    public ResponseEntity<DoctorResponseDTO> getDoctorById(@PathVariable Long id) {
        return ResponseEntity.ok(doctorService.obtenerDoctorPorId(id));
    }

    @Operation(summary = "Elimina un doctor del sistema (Rol: ADMIN)")
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> eliminarDoctor(@PathVariable Long id) {
        doctorService.eliminarDoctor(id);
        return ResponseEntity.noContent().build();
    }
}