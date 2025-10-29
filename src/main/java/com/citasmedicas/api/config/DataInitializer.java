package com.citasmedicas.api.config;

import com.citasmedicas.api.enums.RolNombre;
import com.citasmedicas.api.models.Rol;
import com.citasmedicas.api.models.Usuario;
import com.citasmedicas.api.repositories.RolRepository;
import com.citasmedicas.api.repositories.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Set;

@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final RolRepository rolRepository;
    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {

        if (rolRepository.findByNombre(RolNombre.ADMIN).isEmpty()) {
            rolRepository.save(new Rol(null, RolNombre.ADMIN));
            System.out.println("Rol ADMIN creado.");
        }

        if (rolRepository.findByNombre(RolNombre.DOCTOR).isEmpty()) {
            rolRepository.save(new Rol(null, RolNombre.DOCTOR));
            System.out.println("Rol DOCTOR creado.");
        }

        if (rolRepository.findByNombre(RolNombre.PACIENTE).isEmpty()) {
            rolRepository.save(new Rol(null, RolNombre.PACIENTE));
            System.out.println("Rol PACIENTE creado.");
        }

        if (usuarioRepository.findByEmail("admin@citasmedicas.com").isEmpty()) {
            Rol rolAdmin = rolRepository.findByNombre(RolNombre.ADMIN)
                    .orElseThrow(() -> new RuntimeException("Rol ADMIN no encontrado"));

            Usuario admin = new Usuario();
            admin.setEmail("admin@citasmedicas.com");
            admin.setPassword(passwordEncoder.encode("admin123"));
            admin.setRoles(Set.of(rolAdmin));

            usuarioRepository.save(admin);
            System.out.println("Usuario Administrador creado.");
        }
    }
}