package com.citasmedicas.api.services;

import com.citasmedicas.api.dtos.DoctorDTO;
import com.citasmedicas.api.enums.RolNombre;
import com.citasmedicas.api.exceptions.ResourceNotFoundException;
import com.citasmedicas.api.mappers.DoctorMapper;
import com.citasmedicas.api.models.Doctor;
import com.citasmedicas.api.models.Especialidad;
import com.citasmedicas.api.models.Rol;
import com.citasmedicas.api.models.Usuario;
import com.citasmedicas.api.repositories.DoctorRepository;
import com.citasmedicas.api.repositories.EspecialidadRepository;
import com.citasmedicas.api.repositories.RolRepository;
import com.citasmedicas.api.repositories.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DoctorService {

    private final DoctorRepository doctorRepository;
    private final UsuarioRepository usuarioRepository;
    private final RolRepository rolRepository;
    private final EspecialidadRepository especialidadRepository;
    private final PasswordEncoder passwordEncoder;
    private final DoctorMapper doctorMapper;

    @Transactional
    public DoctorDTO registrarDoctor(DoctorDTO doctorDTO, String rawPassword) {
        // 1. Validar que la especialidad exista
        Especialidad especialidad = especialidadRepository.findById(doctorDTO.getEspecialidadId())
                .orElseThrow(() -> new ResourceNotFoundException("Especialidad no encontrada con id: " + doctorDTO.getEspecialidadId()));

        // 2. Crear y configurar el Usuario
        Rol rolDoctor = rolRepository.findByNombre(RolNombre.DOCTOR)
                .orElseThrow(() -> new RuntimeException("Error: Rol de doctor no encontrado."));

        Usuario usuario = new Usuario();
        // Generaremos un email provisional, que el doctor podrá cambiar luego.
        String email = doctorDTO.getNombreCompleto().replaceAll("\\s+", ".").toLowerCase() + "@citasmedicas.com";
        if(usuarioRepository.existsByEmail(email)){
            email = doctorDTO.getNombreCompleto().replaceAll("\\s+", ".").toLowerCase() + "." + System.currentTimeMillis() + "@citasmedicas.com";
        }

        // --- LÍNEA AÑADIDA PARA DEPURACIÓN ---
        System.out.println("Email generado para el doctor: " + email);
        // ------------------------------------

        usuario.setEmail(email);
        usuario.setPassword(passwordEncoder.encode(rawPassword));
        usuario.setRoles(Set.of(rolDoctor));

        // 3. Crear y configurar el Doctor
        Doctor doctor = doctorMapper.toEntity(doctorDTO);
        doctor.setUsuario(usuario);
        doctor.setEspecialidad(especialidad);

        // 4. Guardar y devolver el DTO
        Doctor nuevoDoctor = doctorRepository.save(doctor);
        return doctorMapper.toDto(nuevoDoctor);
    }

    public List<DoctorDTO> obtenerTodosLosDoctores() {
        return doctorRepository.findAll()
                .stream()
                .map(doctorMapper::toDto)
                .collect(Collectors.toList());
    }

    public DoctorDTO obtenerDoctorPorId(Long id) {
        Doctor doctor = doctorRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Doctor no encontrado con id: " + id));
        return doctorMapper.toDto(doctor);
    }
}