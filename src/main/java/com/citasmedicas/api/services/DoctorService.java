package com.citasmedicas.api.services;

import com.citasmedicas.api.dtos.DoctorDTO;
import com.citasmedicas.api.dtos.DoctorResponseDTO; // Importa el nuevo DTO
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

import java.security.SecureRandom;
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

    private String generateRandomPassword() {
        final String CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        final int PASSWORD_LENGTH = 10;
        SecureRandom random = new SecureRandom();
        StringBuilder sb = new StringBuilder(PASSWORD_LENGTH);
        for (int i = 0; i < PASSWORD_LENGTH; i++) {
            sb.append(CHARACTERS.charAt(random.nextInt(CHARACTERS.length())));
        }
        return sb.toString();
    }

    @Transactional
    public DoctorResponseDTO registrarDoctor(DoctorDTO doctorDTO) {
        Especialidad especialidad = especialidadRepository.findById(doctorDTO.getEspecialidadId())
                .orElseThrow(() -> new ResourceNotFoundException("Especialidad no encontrada con id: " + doctorDTO.getEspecialidadId()));

        Rol rolDoctor = rolRepository.findByNombre(RolNombre.DOCTOR)
                .orElseThrow(() -> new RuntimeException("Error: Rol de doctor no encontrado."));

        String generatedPassword = generateRandomPassword();
        String email = doctorDTO.getNombreCompleto()
                .trim() // Quita espacios al inicio y al final
                .replaceAll("[^a-zA-Z0-9]", ".") // Reemplaza todo lo que no sea letra/número por un punto
                .replaceAll("\\.+", ".") // Reemplaza múltiples puntos seguidos por uno solo
                .toLowerCase() + "@citasmedicas.com";
        //String email = doctorDTO.getNombreCompleto().replaceAll("\\s+", ".").toLowerCase() + "@citasmedicas.com";
        if(usuarioRepository.existsByEmail(email)){
            email = doctorDTO.getNombreCompleto().replaceAll("\\s+", ".").toLowerCase() + "." + System.currentTimeMillis() + "@citasmedicas.com";
        }

        Usuario usuario = new Usuario();
        usuario.setEmail(email);
        usuario.setPassword(passwordEncoder.encode(generatedPassword));
        usuario.setRoles(Set.of(rolDoctor));

        Doctor doctor = doctorMapper.toEntity(doctorDTO);
        doctor.setUsuario(usuario);
        doctor.setEspecialidad(especialidad);

        Doctor nuevoDoctor = doctorRepository.save(doctor);

        DoctorResponseDTO responseDTO = doctorMapper.toResponseDto(nuevoDoctor);
        responseDTO.setPassword(generatedPassword); // Llenamos el campo de la contraseña manualmente

        return responseDTO;
    }

    public List<DoctorResponseDTO> obtenerTodosLosDoctores() {
        return doctorRepository.findAll()
                .stream()
                // El password aquí será null y no se mostrará en el JSON
                .map(doctorMapper::toResponseDto)
                .collect(Collectors.toList());
    }

    public DoctorResponseDTO obtenerDoctorPorId(Long id) {
        Doctor doctor = doctorRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Doctor no encontrado con id: " + id));
        // El password aquí será null y no se mostrará en el JSON
        return doctorMapper.toResponseDto(doctor);
    }

    @Transactional
    public void eliminarDoctor(Long id) {
        Doctor doctor = doctorRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Doctor no encontrado con id: " + id));

        // Verificamos si el doctor tiene citas asociadas
        if (!doctor.getCitas().isEmpty()) {
            throw new IllegalStateException("No se puede eliminar un doctor con citas asociadas. Por favor, reasigne o cancele las citas primero.");
        }

        doctorRepository.delete(doctor);
    }
}