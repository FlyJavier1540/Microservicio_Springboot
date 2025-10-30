package com.citasmedicas.api.services;

import com.citasmedicas.api.dtos.AdminRegisterRequestDTO;
import com.citasmedicas.api.dtos.AuthRequestDTO;
import com.citasmedicas.api.dtos.AuthResponseDTO;
import com.citasmedicas.api.dtos.RegisterRequestDTO;
import com.citasmedicas.api.enums.RolNombre;
import com.citasmedicas.api.models.Paciente;
import com.citasmedicas.api.models.Rol;
import com.citasmedicas.api.models.Usuario;
import com.citasmedicas.api.repositories.*;
import com.citasmedicas.api.repositories.RolRepository;
import com.citasmedicas.api.repositories.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;

@Service
@RequiredArgsConstructor
public class AuthService {

    @Value("${admin.secret-key}") // Lee la clave secreta desde las variables de entorno
    private String adminSecretKey;

    private final UsuarioRepository usuarioRepository;
    private final PacienteRepository pacienteRepository;
    private final RolRepository rolRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    @Transactional
    public AuthResponseDTO register(RegisterRequestDTO request) {
        if (usuarioRepository.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException("El email ya está en uso.");
        }

        Rol rolPaciente = rolRepository.findByNombre(RolNombre.PACIENTE)
                .orElseThrow(() -> new RuntimeException("Error: Rol de paciente no encontrado."));

        Usuario usuario = new Usuario();
        usuario.setEmail(request.getEmail());
        usuario.setPassword(passwordEncoder.encode(request.getPassword()));
        usuario.setRoles(Set.of(rolPaciente));

        Paciente paciente = new Paciente();
        paciente.setNombreCompleto(request.getNombreCompleto());
        paciente.setUsuario(usuario);

        pacienteRepository.save(paciente);

        String jwtToken = jwtService.generateToken(usuario);
        return new AuthResponseDTO(jwtToken);
    }

    public AuthResponseDTO login(AuthRequestDTO request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()
                )
        );

        Usuario usuario = usuarioRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado."));

        String jwtToken = jwtService.generateToken(usuario);
        return new AuthResponseDTO(jwtToken);
    }

    @Transactional
    public AuthResponseDTO registerAdmin(AdminRegisterRequestDTO request) {
        // 1. Verificar la clave secreta
        if (!adminSecretKey.equals(request.getSecretKey())) {
            throw new AccessDeniedException("Clave secreta inválida.");
        }

        // 2. Verificar que el email no esté en uso
        if (usuarioRepository.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException("El email ya está en uso.");
        }

        // 3. Buscar el rol de ADMIN
        Rol rolAdmin = rolRepository.findByNombre(RolNombre.ADMIN)
                .orElseThrow(() -> new RuntimeException("Error: Rol de ADMIN no encontrado."));

        // 4. Crear y guardar el nuevo usuario administrador
        Usuario usuario = new Usuario();
        usuario.setEmail(request.getEmail());
        usuario.setPassword(passwordEncoder.encode(request.getPassword()));
        usuario.setRoles(Set.of(rolAdmin));

        usuarioRepository.save(usuario);

        // 5. Generar y devolver el token
        String jwtToken = jwtService.generateToken(usuario);
        return new AuthResponseDTO(jwtToken);
    }
}