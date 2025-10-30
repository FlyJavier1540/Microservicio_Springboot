package com.citasmedicas.api.controllers;

import com.citasmedicas.api.dtos.AdminRegisterRequestDTO;
import com.citasmedicas.api.dtos.AuthRequestDTO;
import com.citasmedicas.api.dtos.AuthResponseDTO;
import com.citasmedicas.api.dtos.RegisterRequestDTO;
import com.citasmedicas.api.services.AuthService;
import io.swagger.v3.oas.annotations.Operation; // <-- ¡IMPORTA ESTO!
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @Operation(summary = "Registra un nuevo usuario como paciente")
    @PostMapping("/register")
    public ResponseEntity<AuthResponseDTO> register(
            @Valid @RequestBody RegisterRequestDTO request
    ) {
        return ResponseEntity.ok(authService.register(request));
    }

    @Operation(summary = "Inicia sesión para obtener un token JWT")
    @PostMapping("/login")
    public ResponseEntity<AuthResponseDTO> login(
            @Valid @RequestBody AuthRequestDTO request
    ) {
        return ResponseEntity.ok(authService.login(request));
    }

    @Operation(summary = "Registra el primer usuario administrador del sistema usando una clave secreta")
    @PostMapping("/register-admin")
    public ResponseEntity<AuthResponseDTO> registerAdmin(
            @Valid @RequestBody AdminRegisterRequestDTO request
    ) {
        return ResponseEntity.ok(authService.registerAdmin(request));
    }
}